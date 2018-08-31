package pl.mobite.rocky.ui.map

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.rocky.data.models.Place

data class MapViewState(
        val reRenderFlag: Boolean,
        val isLoading: Boolean,
        val places: List<Place>,
        val placesTimestamp: Long?,
        val error: Throwable?
) : Parcelable {

    constructor(source: Parcel) : this(
            1 == source.readInt(),
            1 == source.readInt(),
            ArrayList<Place>().apply { source.readList(this, Place::class.java.classLoader) },
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readSerializable() as Throwable?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (reRenderFlag) 1 else 0))
        writeInt((if (isLoading) 1 else 0))
        writeList(places)
        writeValue(placesTimestamp)
        writeSerializable(error)
    }

    companion object {
        fun default() = MapViewState(
                reRenderFlag = false,
                isLoading = false,
                places = emptyList(),
                placesTimestamp = null,
                error = null)

        @JvmField
        val CREATOR: Parcelable.Creator<MapViewState> = object : Parcelable.Creator<MapViewState> {
            override fun createFromParcel(source: Parcel): MapViewState = MapViewState(source)
            override fun newArray(size: Int): Array<MapViewState?> = arrayOfNulls(size)
        }

        val PARCEL_KEY = MapViewState.toString()
    }
}
