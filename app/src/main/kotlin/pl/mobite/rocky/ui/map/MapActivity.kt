package pl.mobite.rocky.ui.map

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLngBounds
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_map.*
import pl.mobite.rocky.R
import pl.mobite.rocky.data.model.MarkerData
import pl.mobite.rocky.ui.map.MapIntent.*
import pl.mobite.rocky.utils.CustomTextWatcher
import pl.mobite.rocky.utils.RockyViewModelFactory
import pl.mobite.rocky.utils.dpToPx
import pl.mobite.rocky.utils.setVisibleOrGone


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private val mapReadyRelay = PublishRelay.create<MapReadyIntent>()
    private val errorDisplayedRelay = PublishRelay.create<ErrorDisplayedIntent>()
    private val allMarkersGoneRelay = PublishRelay.create<AllMarkersGoneIntent>()
    private val markerClickedRelay = PublishRelay.create<String>()

    private val handler = Handler()
    private lateinit var disposable: CompositeDisposable
    private var lastViewState: MapViewState? = null

    private lateinit var viewModel: MapViewModel

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

        /* When developer option "Do not keep activities" is checked and we move app to recent screen and open app again,
        the ViewModel is destroyed and recreated, and our view is in default state.
        In order to prevent it the last ViewState is saved in activity bundle and it is used
        as initial state when new ViewModel is created  */
        val initialViewState: MapViewState? = savedInstanceState?.getParcelable(MapViewState.PARCEL_KEY)
        viewModel = ViewModelProviders.of(this,
                RockyViewModelFactory.getInstance(initialViewState))
                .get(MapViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())

        disposable.addAll(markerClickedRelay.subscribe { description -> showMarkerDescription(description)})
    }

    override fun onStop() {
        disposable.dispose()
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        lastViewState?.let { mapViewState ->
            outState?.putParcelable(MapViewState.PARCEL_KEY, mapViewState)
        }
    }

    private fun intents(): Observable<MapIntent> {
        return Observable.merge(listOf(
                mapReadyRelay,
                searchIntent(),
                errorDisplayedRelay,
                allMarkersGoneRelay))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.setOnMarkerClickListener { marker ->
            (marker.tag as? String?)?.let { placeDesc ->
                markerClickedRelay.accept(placeDesc)
            }
            true
        }

        /*
        If we send intent immediately and in view state we have a list of places to display on map than app will crash
        with an exception:

        "Map size can't be 0. Most likely, layout has not yet occured for the map view.  Either wait until layout has
        occurred or use newLatLngBounds(LatLngBounds, int, int, int) which allows you to specify the map's dimensions."

        Looks like when onMapReady callback is invoked map is still not yet ready for updating its camera with
        LatLngBounds update
         */
        handler.post { mapReadyRelay.accept(MapReadyIntent) }
    }

    private fun searchIntent(): Observable<SearchPlacesIntent> {
        return RxTextView.editorActionEvents(queryInput) { action ->
            action.actionId() == EditorInfo.IME_ACTION_SEARCH && queryInput.text.toString().isNotBlank()}
                .map { SearchPlacesIntent(queryInput.text.toString()) }
    }

    private fun render(state: MapViewState) {
        saveViewStateState(state)
        with(state) {

            refreshSearchView(isLoading)

            /* Handle error - display message and clear error */
            error?.let {
                showErrorMessage(it)
                errorDisplayedRelay.accept(ErrorDisplayedIntent)
                return
            }

            /* Do not change markers on map if no data or if loading */
            if (dataCreationTimestamp == null || isLoading) {
                return
            }

            /* Show message if list is empty */
            if (markerDataList.isEmpty()) {
                showEmptyListMessage()
                allMarkersGoneRelay.accept(AllMarkersGoneIntent)
                return
            }

            /* Put markerDataList on the map */
            googleMap?.let { map ->

                clearMap(map)

                /* Create list of markers which should be displayed on map */
                val markersToDisplay = getMarkersToDisplay(markerDataList, dataCreationTimestamp)
                if (markersToDisplay.isEmpty()) {
                    allMarkersGoneRelay.accept(AllMarkersGoneIntent)
                    return
                }

                /* Display markers on map */
                val boundsBuilder by lazy { LatLngBounds.builder() }
                markersToDisplay.forEach { markerToDisplay ->
                    displayMarkerOnMap(map, markerToDisplay)
                    boundsBuilder.include(markerToDisplay.data.markerOptions.position)
                }

                /* Update camera only if we are displaying places right after search,
                filter out places which will not be displayed anyway  */
                if (markerDataList.filter { it.timeToLive > 0 }.size == markersToDisplay.size) {
                    updateMapCamera(map, markerDataList, boundsBuilder)
                }
            }
        }
    }

    private fun saveViewStateState(mapViewState: MapViewState) {
        /* Save state ony if data is not loading, because in order to properly handle ViewModel recreation
        with loading state as its initial state we need to also send search intent and search intents should
        not be send based on current ViewState but rather based on user action */
        if (!mapViewState.isLoading) {
            lastViewState = mapViewState
        }
    }

    private fun refreshSearchView(isLoading: Boolean) {
        queryInput.isEnabled = googleMap != null && !isLoading
        queryInput.text.toString().let {queryText ->
            clearButton.setVisibleOrGone(queryText.isNotEmpty() && !isLoading)
            queryProgress.setVisibleOrGone(queryText.isNotEmpty() && isLoading)
        }
    }

    private fun clearMap(map: GoogleMap) {
        handler.removeCallbacksAndMessages(null)
        map.clear()
    }

    private fun getMarkersToDisplay(markerDataList: List<MarkerData>, dataCreationTimestamp: Long): List<MarkerToDisplay> {
        /* Calculate how much time markers are already displayed on the map */
        val passedTime = System.currentTimeMillis() - dataCreationTimestamp
        return markerDataList
                .map { markerData -> MarkerToDisplay(markerData, markerData.timeToLive - passedTime) }
                .filter { markerToDisplay -> markerToDisplay.remainingTime > 0 }
    }

    private fun displayMarkerOnMap(map: GoogleMap, markerToDisplay: MarkerToDisplay) {
        with(markerToDisplay) {
            val marker = map.addMarker(data.markerOptions)
            marker.tag = data.description
            handler.postDelayed({ marker.remove() }, remainingTime)
        }
    }

    private fun updateMapCamera(map: GoogleMap, markerDataList: List<MarkerData>, boundsBuilder: LatLngBounds.Builder) {
        val cameraUpdate: CameraUpdate? = when {
            markerDataList.size == 1 -> CameraUpdateFactory.newLatLng(markerDataList.first().markerOptions.position)
            markerDataList.size > 1 -> CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dpToPx(36).toInt())
            else -> null
        }
        cameraUpdate?.let { map.moveCamera(it) }
    }

    private fun showMarkerDescription(description: String) {
        Toast.makeText(this@MapActivity, description, Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyListMessage() {
        Toast.makeText(this@MapActivity, R.string.map_api_empty_list, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(error: Throwable) {
        Toast.makeText(this@MapActivity, R.string.map_api_loading_error, Toast.LENGTH_SHORT).show()
    }

    private data class MarkerToDisplay(val data: MarkerData, val remainingTime: Long)
}
