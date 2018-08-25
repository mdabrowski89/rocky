package pl.mobite.rocky.ui.map

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import pl.mobite.rocky.R
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.repositories.PlacesRepository
import pl.mobite.rocky.ui.map.MapAction.*
import pl.mobite.rocky.ui.map.MapIntent.*
import pl.mobite.rocky.ui.map.MapResult.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val mapReadyRelay = PublishRelay.create<MapReadyIntent>()
    private val placeExpiredRelay = PublishRelay.create<PlaceExpiredIntent>()
    private val placeDetailsRelay = PublishRelay.create<PlaceDetailsIntent>()
    private val errorDisplayedRelay = PublishRelay.create<ErrorDisplayedIntent>()
    private val placeDisplayedRelay = PublishRelay.create<PlaceDisplayedIntent>()

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        intents().map { intent -> actionFromIntent(intent)}
                .compose(actionProcessor)
                .scan(MapViewState.default(), reducer)
                .subscribe { state -> render(state) }
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
        mMap = googleMap
        mMap.setOnMarkerClickListener { marker ->
            (marker.tag as? Place?)?.let { place ->
                placeDetailsRelay.accept(PlaceDetailsIntent(place))
            }
            true
        }
        mapReadyRelay.accept(MapReadyIntent)
    }

    private fun render(state: MapViewState) {
        with(state) {
            queryInput.isEnabled = isMapReady && !isLoading
            if (isMapReady) {
                handler.removeCallbacksAndMessages(null)
                mMap.clear()
                val boundsBuilder = LatLngBounds.builder()
                places.forEach {place ->
                    handler.postDelayed({
                        placeExpiredRelay.accept(PlaceExpiredIntent(place))
                    }, 50000)
                    val marker = place.toMarker()
                    boundsBuilder.include(marker.position)
                    mMap.addMarker(marker).tag = place
                }
                if (places.size == 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(places.first().toMarker().position))
                } else if (places.size > 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 2))
                }
            }
            if (error != null) {
                Toast.makeText(this@MapActivity, "Unable to load places", Toast.LENGTH_SHORT).show()
                errorDisplayedRelay.accept(ErrorDisplayedIntent)
            }
            if (placeToDisplay != null) {
                // TODO: change do dialog with info
                Toast.makeText(this@MapActivity, placeToDisplay.name, Toast.LENGTH_SHORT).show()
                placeDisplayedRelay.accept(PlaceDisplayedIntent)
            }
        }
    }

    private fun intents(): Observable<MapIntent> {
        return Observable.merge(listOf(
                initialIntent(),
                mapReadyRelay,
                searchIntent(),
                placeExpiredRelay,
                placeDetailsRelay,
                errorDisplayedRelay,
                placeDisplayedRelay))
    }

    private fun initialIntent(): Observable<InitialIntent> {
        return Observable.just(InitialIntent)
    }

    private fun searchIntent(): Observable<SearchPlacesIntent> {
        return RxTextView.editorActionEvents(queryInput) { action -> action.actionId() == EditorInfo.IME_ACTION_SEARCH }
                .map { SearchPlacesIntent(queryInput.text.toString()) }
    }
}

fun Place.toMarker(): MarkerOptions {
    return MarkerOptions().position(LatLng(lat, lng)).title(name)
}

private fun actionFromIntent(intent: MapIntent): MapAction {
    return when(intent) {
        is InitialIntent -> LoadMapAction
        is MapReadyIntent -> MapReadyAction
        is SearchPlacesIntent -> LoadPlacesAction(intent.query)
        is PlaceExpiredIntent -> RemovePlaceAction(intent.place)
        is PlaceDetailsIntent -> DisplayPlaceAction(intent.place)
        is ErrorDisplayedIntent -> ErrorDisplayedAction
        is PlaceDisplayedIntent -> PlaceDisplayedAction
    }
}

private val placesRepository = PlacesRepository()

var actionProcessor: ObservableTransformer<MapAction, MapResult> =
        ObservableTransformer<MapAction, MapResult> { actions: Observable<MapAction> ->
            actions.publish { shared ->
                Observable.merge(listOf(
                        shared.ofType(LoadMapAction::class.java).compose(loadMapProcessor),
                        shared.ofType(MapReadyAction::class.java).compose(mapReadyProcessor),
                        shared.ofType(LoadPlacesAction::class.java).compose(loadPlacesProcessor),
                        shared.ofType(RemovePlaceAction::class.java).compose(removePlaceProcessor),
                        shared.ofType(DisplayPlaceAction::class.java).compose(displayPlaceProcessor),
                        shared.ofType(ErrorDisplayedAction::class.java).compose(errorDisplayedProcessor),
                        shared.ofType(PlaceDisplayedAction::class.java).compose(placeDisplayedProcessor)
                ))
            }
        }

val loadMapProcessor = ObservableTransformer { actions: Observable<LoadMapAction> ->
    actions.switchMap {
        Observable.just(LoadMapResult)
    }
}

val mapReadyProcessor = ObservableTransformer { actions: Observable<MapReadyAction> ->
    actions.switchMap {
        Observable.just(MapReadyResult)
    }
}

val loadPlacesProcessor = ObservableTransformer { actions: Observable<LoadPlacesAction> ->
    actions.switchMap {
        placesRepository.getPlaces()
                .map { places -> LoadPlacesResult.Success(places) }
                .cast(LoadPlacesResult::class.java)
                .onErrorReturn { t -> LoadPlacesResult.Failure(t) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(LoadPlacesResult.InFlight)
    }
}

val removePlaceProcessor = ObservableTransformer { actions: Observable<RemovePlaceAction> ->
    actions.switchMap { action ->
        Observable.just(RemovePlaceResult(action.place))
    }
}

val displayPlaceProcessor = ObservableTransformer { actions: Observable<DisplayPlaceAction> ->
    actions.switchMap { action ->
        Observable.just(DisplayPlaceResult(action.place))
    }
}

val errorDisplayedProcessor = ObservableTransformer { actions: Observable<ErrorDisplayedAction> ->
    actions.switchMap {
        Observable.just(ErrorDisplayedResult)
    }
}

val placeDisplayedProcessor = ObservableTransformer { actions: Observable<PlaceDisplayedAction> ->
    actions.switchMap {
        Observable.just(PlaceDisplayedResult)
    }
}

sealed class MapIntent {
    object InitialIntent: MapIntent()
    object MapReadyIntent: MapIntent()
    data class SearchPlacesIntent(val query: String): MapIntent()
    data class PlaceExpiredIntent(val place: Place): MapIntent()
    data class PlaceDetailsIntent(val place: Place): MapIntent()
    object ErrorDisplayedIntent: MapIntent()
    object PlaceDisplayedIntent: MapIntent()
}

sealed class MapAction {
    object LoadMapAction: MapAction()
    object MapReadyAction: MapAction()
    data class LoadPlacesAction(val query: String): MapAction()
    data class RemovePlaceAction(val place: Place): MapAction()
    data class DisplayPlaceAction(val place: Place): MapAction()
    object ErrorDisplayedAction: MapAction()
    object PlaceDisplayedAction: MapAction()
}

sealed class MapResult {
    object LoadMapResult: MapResult()
    object MapReadyResult: MapResult()
    sealed class LoadPlacesResult: MapResult() {
        object InFlight: LoadPlacesResult()
        data class Failure(val throwable: Throwable): LoadPlacesResult()
        data class Success(val places: List<Place>): LoadPlacesResult()
    }
    data class RemovePlaceResult(val place: Place): MapResult()
    data class DisplayPlaceResult(val place: Place): MapResult()
    object ErrorDisplayedResult: MapResult()
    object PlaceDisplayedResult: MapResult()
}

data class MapViewState(
        val isMapReady: Boolean,
        val isLoading: Boolean,
        val places: List<Place>,
        val error: Throwable?,
        val placeToDisplay: Place?
) {
    companion object Factory {
        fun default() = MapViewState(
                isMapReady = false,
                isLoading = false,
                places = emptyList(),
                error = null,
                placeToDisplay = null)
    }
}

val reducer = BiFunction {
    prevState: MapViewState, result: MapResult ->
    when(result) {
        is LoadMapResult -> prevState
        is MapReadyResult -> prevState.copy(isMapReady = true)
        is LoadPlacesResult ->
            when(result) {
                is LoadPlacesResult.InFlight -> prevState.copy(isLoading = true)
                is LoadPlacesResult.Success -> prevState.copy(isLoading = false, places = result.places)
                is LoadPlacesResult.Failure -> prevState.copy(isLoading = false, error = result.throwable)
            }
        is RemovePlaceResult -> prevState.copy(places = prevState.places.filterNot { it == result.place })
        is DisplayPlaceResult -> prevState.copy(placeToDisplay = result.place)
        is ErrorDisplayedResult -> prevState.copy(error = null)
        is PlaceDisplayedResult -> prevState.copy(placeToDisplay = null)
    }
}
