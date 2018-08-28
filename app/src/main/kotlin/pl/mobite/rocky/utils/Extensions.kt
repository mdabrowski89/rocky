package pl.mobite.rocky.utils

import android.view.View
import pl.mobite.rocky.RockyApp


fun dpToPx(dp: Int) = dp * RockyApp.instance.resources.displayMetrics.density

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
