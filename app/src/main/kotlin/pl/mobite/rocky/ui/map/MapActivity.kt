package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_map.*
import pl.mobite.rocky.R
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.models.PlaceCords
import pl.mobite.rocky.ui.map.MapIntent.*
import pl.mobite.rocky.utils.ToMuchDataToFetchException
import pl.mobite.rocky.utils.dpToPx


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private val mapReadyRelay = PublishRelay.create<MapReadyIntent>()
    private val errorDisplayedRelay = PublishRelay.create<ErrorDisplayedIntent>()
    private val allPlacesGoneRelay = PublishRelay.create<AllPlacesGoneIntent>()

    private val handler = Handler()
    private lateinit var disposable: CompositeDisposable

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }

    override fun onStop() {
        disposable.dispose()
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }

    private fun intents(): Observable<MapIntent> {
        return Observable.merge(listOf(
                mapReadyRelay,
                searchIntent(),
                errorDisplayedRelay,
                allPlacesGoneRelay))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.setOnMarkerClickListener { marker ->
            (marker.tag as? Place?)?.let { place ->
                Toast.makeText(this@MapActivity, place.name, Toast.LENGTH_SHORT).show()
            }
            true
        }
        mapReadyRelay.accept(MapReadyIntent)
    }

    private fun searchIntent(): Observable<SearchPlacesIntent> {
        return RxTextView.editorActionEvents(queryInput) { action -> action.actionId() == EditorInfo.IME_ACTION_SEARCH }
                .map { SearchPlacesIntent(queryInput.text.toString()) }
    }

    private fun render(state: MapViewState) {
        with(state) {

            queryInput.isEnabled = googleMap != null && !isLoading

            /* Handle error - display message and clear error */
            if (error != null) {
                val messageRes = if (error is ToMuchDataToFetchException) {
                    R.string.map_api_to_much_data_error
                } else {
                    R.string.map_api_loading_error
                }
                Toast.makeText(this@MapActivity, messageRes, Toast.LENGTH_SHORT).show()
                errorDisplayedRelay.accept(ErrorDisplayedIntent)
                return
            }

            /* Do not change markers if no timestamp or if loading */
            if (placesTimestamp == null || isLoading) {
                return
            }

            /* Put markers on the map */
            googleMap?.let { map ->
                handler.removeCallbacksAndMessages(null)
                map.clear()
                // TODO: if places to display list is empty show message
                val displayingOffset = System.currentTimeMillis() - placesTimestamp!!
                val placesToDisplay = places.filter { place -> place.displayingTime() - displayingOffset > 0 }
                if (placesToDisplay.isEmpty()) {
                    allPlacesGoneRelay.accept(AllPlacesGoneIntent)
                    return
                }
                val boundsBuilder by lazy { LatLngBounds.builder() }
                placesToDisplay.forEach { place ->
                    val marker = map.addMarker(place.toMarker())
                    boundsBuilder.include(marker.position)
                    marker.tag = place
                    handler.postDelayed({
                        marker.remove()
                    }, place.displayingTime() - displayingOffset)
                }

                /* Update camera only if we are displaying places right after search */
                if (places.size == placesToDisplay.size) {
                    val cameraUpdate: CameraUpdate? = when {
                        placesToDisplay.size == 1 -> CameraUpdateFactory.newLatLng(placesToDisplay.first().toMarker().position)
                        placesToDisplay.size > 1 -> CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dpToPx(36).toInt())
                        else -> null
                    }
                    cameraUpdate?.let { map.moveCamera(it) }
                }
            }
        }
    }
}

fun Place.toMarker(): MarkerOptions {
    return MarkerOptions().position(cords.toLatLng()).title(name)
}

fun PlaceCords.toLatLng(): LatLng {
    return LatLng(lat, lng)
}

fun Place.displayingTime(): Long {
    return (openYear - 1990) * 1000L
}
