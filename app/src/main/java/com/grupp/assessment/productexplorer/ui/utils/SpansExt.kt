package com.grupp.assessment.productexplorer.ui.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.grupp.assessment.productexplorer.ui.utils.span.TextAppearanceSpan2

fun SpannableStringBuilder.textAppearance(context: Context, text: CharSequence?, @StyleRes res: Int): SpannableStringBuilder {
    return append(text, TextAppearanceSpan2(context, res), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.textAppearance(context: Context, @StringRes textRes: Int, @StyleRes res: Int): SpannableStringBuilder {
    return append(context.getString(textRes), TextAppearanceSpan2(context, res), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.absSize(text: CharSequence, size: Int, isDp: Boolean = true): SpannableStringBuilder {
    return append(text, AbsoluteSizeSpan(size, isDp), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.spacedBy(size: Int, isDp: Boolean = true): SpannableStringBuilder {
    //First and last \n are important as the whitespace in the middle is ignored otherwise.
    //Also size 0 for 1 and 3 is important because simply appending will cause it to use the default
    //text size in the TextView which may lead to undefined behaviour
    return append("\n", AbsoluteSizeSpan(0), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        .append(" ", AbsoluteSizeSpan(size, isDp), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        .append("\n", AbsoluteSizeSpan(0), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}