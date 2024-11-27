package com.grupp.assessment.productexplorer.ui.utils.span

import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TextAppearanceSpan
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use

/**
 * [TextAppearanceSpan] seems to have an issue where on some device the fontFamily is ignored.
 * After some investigation it seems it was not able to properly load the font from the font folder.
 * So the job of this class is to fix that by using [ResourcesCompat] to load the font given it's ID
 * then overwriting the typeface in the [updateMeasureState]
 */
class TextAppearanceSpan2: TextAppearanceSpan {

    private var typeface: Typeface? = null

    constructor(context: Context, appearance: Int): this(context, appearance, -1)

    constructor(context: Context, appearance: Int, colorList: Int): super(context, appearance, colorList) {
        context.obtainStyledAttributes(appearance, intArrayOf(android.R.attr.fontFamily)).use {
            val tv = TypedValue()
            it.getValue(it.getIndex(0), tv)

            typeface = ResourcesCompat.getFont(context, tv.resourceId)
        }
    }

    override fun updateMeasureState(ds: TextPaint) {
        super.updateMeasureState(ds)
        ds.setTypeface(typeface)
    }
}