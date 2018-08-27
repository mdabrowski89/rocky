package pl.mobite.rocky


fun dpToPx(dp: Int) = dp * RockyApp.instance.resources.displayMetrics.density
