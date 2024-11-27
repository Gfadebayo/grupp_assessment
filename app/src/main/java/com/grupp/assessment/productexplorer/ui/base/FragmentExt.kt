package com.grupp.assessment.productexplorer.ui.base

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.AnyThread
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.grupp.assessment.productexplorer.ui.MainActivity
import com.grupp.assessment.productexplorer.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.grupp.assessment.productexplorer.core.Result

val Fragment.navController: NavController
    get() = findNavController()

val Fragment.isLoading: Boolean
    get() = (requireActivity() as? MainActivity)?.isLoading ?: false

fun Fragment.showLoading(@StringRes textRes: Int? = null, text: String? = null) {
    (requireActivity() as? MainActivity)?.also {
        it.showLoading(textRes, text, owner = this)
    }
}

fun Fragment.dismissLoadingDialog() {
    (requireActivity() as? MainActivity)?.also {
        it.dismissLoadingDialog()
    }
}

fun Fragment.handleError(
    error: Result.Error,
    useToast: Boolean = true,
    @StringRes retryTitle: Int? = null,
    onDismiss: () -> Unit = {},
    onRetryClicked: (() -> Unit)? = null
) {
    if(isAdded) {
        (requireActivity() as? MainActivity)?.also {
            it.handleError(error, useToast, retryTitle, onDismiss, onRetryClicked)
        }
    }
}

fun Fragment.handleError(title: String, message: String,
                         useToast: Boolean = true,
                         @StringRes retryTitle: Int? = null,
                         onDismiss: () -> Unit = {},
                         onRetryClicked: (() -> Unit)? = null) {
    handleError(Result.ApiError(title, message), useToast, retryTitle, onDismiss, onRetryClicked)
}

@MainThread
fun Fragment.onBackPressed() {
    if(isAdded) requireActivity().onBackPressedDispatcher.onBackPressed()
}

@AnyThread
fun Fragment.runOnUiThread(runnable: () -> Unit) {
    if(isAdded) ContextCompat.getMainExecutor(requireContext()).execute(runnable)
}

fun Fragment.navigate(@IdRes id: Int, args: Bundle = Bundle.EMPTY, popupTo: Int? = null, inclusive: Boolean = false) {
    if(isAdded) (requireActivity() as MainActivity).navigate(id, args, popupTo, inclusive)
}

fun Fragment.addBackCallback(isEnabled: Boolean = true, callback: OnBackPressedCallback.() -> Unit): OnBackPressedCallback {
    return requireActivity().onBackPressedDispatcher.addCallback(owner = this, onBackPressed = callback, enabled = isEnabled)
}