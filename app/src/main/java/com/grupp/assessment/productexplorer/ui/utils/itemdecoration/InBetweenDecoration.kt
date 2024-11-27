package com.grupp.assessment.productexplorer.ui.utils.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grupp.assessment.productexplorer.ui.utils.dpToPx

/**
 * The Space between two items
 */
class InBetweenDecoration(
    private val padding: Int,
    private val addToLast: Boolean = false
) : RecyclerView.ItemDecoration() {
        companion object {
            fun RecyclerView.setInBetweenDecoration(padding: Int, isDp: Boolean = true, addToLast: Boolean = false): InBetweenDecoration {
                val realValue = if(isDp) context.dpToPx(padding) else padding

                getItemDecoration<InBetweenDecoration>()?.let { removeItemDecoration(it) }

                return InBetweenDecoration(realValue, addToLast).also {
                    addItemDecoration(it)
                }
            }
        }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.layoutManager?.also {
            if(it is GridLayoutManager) parent.handleGridOffsets(outRect, view)
            else if(it is LinearLayoutManager) parent.handleLinearOffsets(outRect, view)
        }
    }

    private fun RecyclerView.handleLinearOffsets(outRect: Rect, view: View) {
        val layoutManager = layoutManager as LinearLayoutManager

        val isVertical = layoutManager.orientation == LinearLayoutManager.VERTICAL


        val pos = getChildAdapterPosition(view)
        val count = adapter?.itemCount ?: 0

        if (pos != 0 && (pos < count - 1 || addToLast)) outRect.set(if(isVertical) 0 else padding, if(isVertical) padding else 0, 0, 0)
    }

    private fun RecyclerView.handleGridOffsets(outRect: Rect, view: View) {
        val layoutManager = layoutManager as GridLayoutManager
        val pos = getChildAdapterPosition(view)
        val spanSize = layoutManager.spanCount
        val groupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(pos, spanSize)
        val itemCount = adapter?.itemCount ?: 0
        val groupCount = ((itemCount) / spanSize).let {
            if(it * spanSize == itemCount) it
            else it+1
        }

        //For row, the left side of the first item will be skip, as well as the right side of the last item
        //the padding will be added to the right side of the first item, left side of the last item and left+right side of items inbetween
        val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(pos, layoutManager.spanCount)

        val addToLeft = if(spanIndex == 0) /*only add for right*/{ false }
        else if(spanIndex == spanSize-1) /*only add for left*/{ true }
        else /*add for both*/{ true }

        val addToRight = if(spanIndex == 0) /*only add for right*/{ true }
        else if(spanIndex == spanSize-1) /*only add for left*/{ false }
        else /*add for both*/{ true }

        val addToTop = if(groupIndex == 0) false
        else if(groupIndex < groupCount-1) true
        else true

        outRect.set(
            if (addToLeft) padding else 0,
            if(addToTop) padding else 0,
            if(addToRight) padding else 0,
            0
        )
    }
}