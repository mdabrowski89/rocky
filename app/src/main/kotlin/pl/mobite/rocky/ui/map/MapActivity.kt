package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
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
import pl.mobite.rocky.ui.map.MapIntent.*
import pl.mobite.rocky.utils.CustomTextWatcher
import pl.mobite.rocky.utils.RockyViewModelFactory
import pl.mobite.rocky.utils.dpToPx
import pl.mobite.rocky.utils.setVisibleOrGone


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private val mapReadyRelay = PublishRelay.create<MapReadyIntent>()
    private val errorDisplayedRelay = PublishRelay.create<ErrorDisplayedIntent>()
    private val allPlacesGoneRelay = PublishRelay.create<AllPlacesGoneIntent>()
    private val placeClickedRelay = PublishRelay.create<Place>()

    private val handler = Handler()
    private lateinit var disposable: CompositeDisposable

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this, RockyViewModelFactory.instance).get(MapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        clearButton.setOnClickListener { queryInput.setText("") }
        queryInput.addTextChangedListener(object : CustomTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                clearButton.setVisibleOrGone(queryInput.text.isNotEmpty())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())

        disposable.addAll(placeClickedRelay.subscribe { place -> showPlaceDetails(place)})
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.setOnMarkerClickListener { marker ->
            (marker.tag as? Place?)?.let { place ->
                placeClickedRelay.accept(place)
            }
            true
        }
        mapReadyRelay.accept(MapReadyIntent)
    }

    private fun searchIntent(): Observable<SearchPlacesIntent> {
        return RxTextView.editorActionEvents(queryInput) { action ->
            action.actionId() == EditorInfo.IME_ACTION_SEARCH && queryInput.text.toString().isNotBlank()}
                .map { SearchPlacesIntent(queryInput.text.toString()) }
    }

    private fun render(state: MapViewState) {
        with(state) {

            queryInput.isEnabled = googleMap != null && !isLoading
            queryInput.text.toString().let {queryText ->
                clearButton.setVisibleOrGone(queryText.isNotEmpty() && !isLoading)
                queryProgress.setVisibleOrGone(queryText.isNotEmpty() && isLoading)
            }

            /* Handle error - display message and clear error */
            error?.let {
                showErrorMessage(it)
                errorDisplayedRelay.accept(ErrorDisplayedIntent)
                return
            }

            /* Do not change markers if no timestamp or if loading */
            if (placesTimestamp == null || isLoading) {
                return
            }

            /* Put markers on the map */
            googleMap?.let { map ->
                if (places.isEmpty()) {
                    showEmptyListMessage()
                    allPlacesGoneRelay.accept(AllPlacesGoneIntent)
                    return
                }

                handler.removeCallbacksAndMessages(null)
                map.clear()

                val displayingOffset = System.currentTimeMillis() - placesTimestamp!!
                val placesToDisplay = places
                        .map { place -> PlaceToDisplay(place, place.getDisplayingTime(displayingOffset)) }
                        .filter { placeToDisplay -> placeToDisplay.displayingTimeInMills > 0 }

                if (placesToDisplay.isEmpty()) {
                    allPlacesGoneRelay.accept(AllPlacesGoneIntent)
                    return
                }

                val boundsBuilder by lazy { LatLngBounds.builder() }
                placesToDisplay.forEach { with(it) {
                    val marker = map.addMarker(place.toMarker())
                    marker.tag = place
                    handler.postDelayed({ marker.remove() }, displayingTimeInMills)
                    boundsBuilder.include(marker.position)
                } }

                /* Update camera only if we are displaying places right after search,
                 * filter out places which will not be displayed anyway  */
                if (places.filter { it.getDisplayingTime() > 0 }.size == placesToDisplay.size) {
                    val cameraUpdate: CameraUpdate? = when {
                        places.size == 1 -> CameraUpdateFactory.newLatLng(places.first().toMarker().position)
                        places.size > 1 -> CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dpToPx(36).toInt())
                        else -> null
                    }
                    cameraUpdate?.let { map.moveCamera(it) }
                }
            }
        }
    }

    private fun showPlaceDetails(place: Place) {
        with(place) {
            Toast.makeText(this@MapActivity, "$name, $openYear", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEmptyListMessage() {
        Toast.makeText(this@MapActivity, R.string.map_api_empty_list, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(error: Throwable) {
        Toast.makeText(this@MapActivity, R.string.map_api_loading_error, Toast.LENGTH_SHORT).show()
    }

    private fun Place.toMarker(): MarkerOptions {
        return MarkerOptions().position(LatLng(cords.lat, cords.lng)).title(name)
    }

    private fun Place.getDisplayingTime(displayOffset: Long = 0): Long {
        return (openYear - 1990) * 1000L - displayOffset
    }

    private data class PlaceToDisplay(val place: Place, val displayingTimeInMills: Long)
}
