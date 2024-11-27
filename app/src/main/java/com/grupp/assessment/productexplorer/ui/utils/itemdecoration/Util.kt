package com.grupp.assessment.productexplorer.ui.utils.itemdecoration

import androidx.recyclerview.widget.RecyclerView

internal inline fun <reified T: RecyclerView.ItemDecoration> RecyclerView.getItemDecoration(): T? {
    for(i in 0 until itemDecorationCount) {
        if(getItemDecorationAt(i) is T) return getItemDecorationAt(i) as T
    }

    return null
}