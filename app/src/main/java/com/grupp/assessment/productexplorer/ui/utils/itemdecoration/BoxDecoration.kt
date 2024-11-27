package com.grupp.assessment.productexplorer.ui.utils.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BoxDecoration(
    var paddingTop: Int = 0,
    var paddingBottom: Int = 0,
    var paddingStart: Int = 0,
    var paddingEnd: Int = 0
) : RecyclerView.ItemDecoration() {
    companion object {

        /**
         * Create a BoxDecoration that sets it's parameters from the paddings of the
         * RecyclerView. Then it removes the padding in the RecyclerView.
         *
         * Calling this ensures there is only 1 instance of [BoxDecoration] present in a recycler view
         * if any of the specified params is null, it's equivalent padding from the recycler view is used instead.
         * for example, is start is null, then paddingStart from the recycler view is used instead
         */
        fun RecyclerView.setBoxDecoration(start: Int? = null, end: Int? = null, top: Int? = null, bottom: Int? = null): BoxDecoration {
            val decoration = BoxDecoration(
                paddingStart = start ?: paddingStart,
                paddingEnd = end ?: paddingEnd,
                paddingBottom = bottom ?: paddingBottom,
                paddingTop = top ?: paddingTop)

            val currentBox = getItemDecoration<BoxDecoration>()

            if(currentBox!= null) removeItemDecoration(currentBox)

            addItemDecoration(decoration)

            updatePadding(left = 0, right = 0, bottom = 0, top = 0)

            return decoration
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.layoutManager?.also {
            if(it is GridLayoutManager) parent.handleGrid(outRect, view)
            else if(it is LinearLayoutManager) parent.handleList(outRect, view)
        }
    }

    private fun RecyclerView.handleList(outRect: Rect, view: View) {

        val pos = getChildAdapterPosition(view)
        val count = adapter?.itemCount ?: 0

        outRect.set(paddingStart, if (pos == 0) paddingTop else 0, paddingEnd, if (pos == count - 1) paddingBottom else 0)
    }

    private fun RecyclerView.handleGrid(outRect: Rect, view: View) {
        val layoutManager = layoutManager as GridLayoutManager
        val pos = getChildAdapterPosition(view)
        val spanSize = layoutManager.spanCount
        val groupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(pos, spanSize)
        val itemCount = adapter?.itemCount ?: 0
        val groupCount = ((itemCount) / spanSize).let {
            if(it * spanSize == itemCount) it
            else it+1
        }

        val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(pos, layoutManager.spanCount)

        val addToLeft = spanIndex == 0 /*only add for right*/

        val addToRight = spanIndex == spanSize-1

        val addToTop = groupIndex == 0

        val addToBottom = groupIndex == groupCount-1

        outRect.set(
            if (addToLeft) this@BoxDecoration.paddingStart else 0,
            if(addToTop) this@BoxDecoration.paddingTop else 0,
            if(addToRight) this@BoxDecoration.paddingEnd else 0,
            if(addToBottom) this@BoxDecoration.paddingBottom else 0
        )
    }
}