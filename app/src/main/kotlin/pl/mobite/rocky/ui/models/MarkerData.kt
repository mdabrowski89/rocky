package pl.mobite.rocky.ui.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.MarkerOptions

/**
 * @param timeToLive in milliseconds
 */
data class MarkerData(val markerOptions: MarkerOptions, val description: String, val timeToLive: Long): Parcelable {
    constructor(source: Parcel): this(
        source.readParcelable(MarkerOptions::class.java.classLoader)!!,
        source.readString()!!,
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(markerOptions, 0)
        writeString(description)
        writeLong(timeToLive)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MarkerData> = object: Parcelable.Creator<MarkerData> {
            override fun createFromParcel(source: Parcel): MarkerData =
                MarkerData(source)

            override fun newArray(size: Int): Array<MarkerData?> = arrayOfNulls(size)
        }
    }
}