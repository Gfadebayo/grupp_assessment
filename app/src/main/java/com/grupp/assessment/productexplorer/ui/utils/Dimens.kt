package com.grupp.assessment.productexplorer.ui.utils

import android.content.Context

fun Context.dpToPx(dp: Int): Int {
    return dpToPx(dp.toFloat()).toInt()
}

fun Context.dpToPx(dp: Float): Float {
    return (dp * resources.displayMetrics.density)
}