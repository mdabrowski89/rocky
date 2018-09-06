package pl.mobite.rocky.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.mobite.rocky.RockyApp


fun dpToPx(dp: Int) = dp * RockyApp.instance.resources.displayMetrics.density

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
