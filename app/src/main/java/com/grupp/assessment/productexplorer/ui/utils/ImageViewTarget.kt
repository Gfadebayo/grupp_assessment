package com.grupp.assessment.productexplorer.ui.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import androidx.core.view.get
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import com.grupp.assessment.productexplorer.R

class ImageViewTarget(
    private val imageView: ImageView,
): ImageViewTarget<Drawable>(imageView) {

    private val progressDrawable by lazy { setProgressDrawable() }

     override fun setResource(resource: Drawable?) {
        imageView.setImageDrawable(resource)
    }

     override fun onLoadFailed(errorDrawable: Drawable?) {
         if(errorDrawable != null) imageView.setImageDrawable(errorDrawable)
         else imageView.setImageResource(R.drawable.ic_error)
    }

     override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        imageView.setImageDrawable(resource)
    }

     override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        imageView.setImageDrawable(progressDrawable)
    }

    private fun setProgressDrawable(): CircularProgressDrawable {
        return CircularProgressDrawable(imageView.context).also {
            it.bounds = Rect(0, 0, imageView.width, imageView.height)

            it.setStyle(CircularProgressDrawable.DEFAULT)
            it.arrowEnabled = false
            it.backgroundColor = Color.TRANSPARENT
            it.setColorSchemeColors(imageView.context.colorPrimary)
            it.strokeWidth = imageView.context.dpToPx(4).toFloat()
            it.start()
        }
    }

    private val Context.colorPrimary: Int
        get() {
            val resId = com.google.android.material.R.attr.colorPrimary

            val typedValue = TypedValue()
            val isSuccess = theme.resolveAttribute(resId, typedValue, true)

            return if (isSuccess) typedValue.data else -1
        }

    fun Context.dpToPx(dp: Float): Float {
        return (dp * resources.displayMetrics.density)
    }
}