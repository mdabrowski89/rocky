package pl.mobite.rocky.utils

import pl.mobite.rocky.RockyApp


fun dpToPx(dp: Int) = dp * RockyApp.instance.resources.displayMetrics.density
