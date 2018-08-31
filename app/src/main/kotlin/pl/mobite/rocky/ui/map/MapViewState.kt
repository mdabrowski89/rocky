package pl.mobite.rocky.ui.map

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.rocky.data.model.MarkerData

data class MapViewState(
        val reRenderFlag: Boolean,
        val isLoading: Boolean,
        val markerDataList: List<MarkerData>,
        val dataCreationTimestamp: Long?,
        val error: Throwable?
) : Parcelable {

    constructor(source: Parcel) : this(
            1 == source.readInt(),
            1 == source.readInt(),
            mutableListOf<MarkerData>().apply { source.readList(this, MarkerData::class.java.classLoader) },
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readSerializable() as Throwable?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (reRenderFlag) 1 else 0))
        writeInt((if (isLoading) 1 else 0))
        writeList(markerDataList)
        writeValue(dataCreationTimestamp)
        writeSerializable(error)
    }

    companion object {
        fun default() = MapViewState(
                reRenderFlag = false,
                isLoading = false,
                markerDataList = emptyList(),
                dataCreationTimestamp = null,
                error = null)

        @JvmField
        val CREATOR: Parcelable.Creator<MapViewState> = object : Parcelable.Creator<MapViewState> {
            override fun createFromParcel(source: Parcel): MapViewState = MapViewState(source)
            override fun newArray(size: Int): Array<MapViewState?> = arrayOfNulls(size)
        }

        val PARCEL_KEY = MapViewState.toString()
    }
}
