package com.grupp.assessment.productexplorer.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.databinding.ActivityMainBinding
import com.grupp.assessment.productexplorer.core.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            binding = this
            setContentView(root)
        }
    }

    private val loadingLifecycleObserver by lazy {
        object: DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                dismissLoadingDialog()
                owner.lifecycle.removeObserver(this)
            }
        }
    }

    val isLoading: Boolean get() = binding.layoutLoading.isVisible

    fun showLoading(
        @StringRes textRes: Int? = null,
        text: String? = null,
        owner: LifecycleOwner? = null
    ) {
        runOnUiThread {
            binding.apply {
                layoutLoading.animate().cancel()

                val dialogText = textRes?.run { getString(this) } ?: text

                textLoading.text = dialogText

                if(layoutLoading.isVisible) return@runOnUiThread

                layoutLoading.animate()
                    .setDuration(450)
                    .withStartAction {
                        layoutLoading.isVisible = true
                        layoutLoading.translationY = layoutLoading.height.toFloat()
                    }
                    .withEndAction { layoutLoading.isVisible = true }
                    .translationY(0f)
                    .alpha(1f)
                    .setInterpolator(DecelerateInterpolator())
                    .start()

                owner?.lifecycle?.addObserver(loadingLifecycleObserver)
            }
        }
    }

    fun dismissLoadingDialog() {
        runOnUiThread {
            binding.apply {
                layoutLoading.animate().cancel()

                if(!layoutLoading.isVisible) return@runOnUiThread

                layoutLoading.animate()
                    .setDuration(450)
                    .withEndAction { layoutLoading.isVisible = false }
                    .translationY(layoutLoading.height * 1f)
                    .alpha(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    fun handleError(
        error: Result.Error,
        useToast: Boolean = true,
        @StringRes retryTitle: Int? = null,
        onDismiss: () -> Unit = {},
        onRetryClicked: (() -> Unit)? = null
    ) {
        dismissLoadingDialog()

        val errorPair = when (error) {
            is Result.NetworkError -> getString(R.string.error_network_title) to getString(R.string.error_network_message)

            is Result.TimeoutError -> getString(R.string.error_timeout_title) to getString(R.string.error_timeout_message)

            is Result.UnknownError -> getString(R.string.error_unknown_title) to getString(R.string.error_unknown_message)

            is Result.EmptyError -> getString(R.string.error_unknown_title) to getString(R.string.error_unknown_message)

            else -> (error as Result.ApiError).let { it.title to it.message }
        }

        runOnUiThread {
            if (useToast && onRetryClicked == null) {
                Toast.makeText(this, errorPair.second, Toast.LENGTH_SHORT).show()
            }
            else {
                val errorDialog = MaterialAlertDialogBuilder(this).run {
                    setTitle(errorPair.first)
                    setMessage(errorPair.second)

                    setCancelable(false)

                    if (onRetryClicked != null) {
                        setPositiveButton(retryTitle ?: R.string.retry) { dia, _ ->
                            onRetryClicked()
                            dia.dismiss()
                        }
                    }

                    setOnDismissListener { onDismiss() }

                    setNegativeButton(R.string.close) { dia, _ -> dia.dismiss() }

                    create()
                }

                errorDialog.show()
            }
        }
    }

    fun navigate(
        @IdRes id: Int,
        args: Bundle = Bundle.EMPTY,
        popupTo: Int? = null,
        inclusive: Boolean = false,
        vararg sharedElements: Pair<View, String>
    ) {
        val navOptions = navOptions {
            if(popupTo != null) {
                this.popUpTo(popupTo) {
                    this.inclusive = inclusive
                }
            }

            this.anim {
                this.enter = R.anim.appear_from_right
                this.exit = R.anim.exit_to_left
                this.popEnter = R.anim.appear_from_left
                this.popExit = R.anim.exit_to_right
            }
        }


        val navExtras = FragmentNavigatorExtras(*sharedElements)

        navController.navigate(id, args, null, navigatorExtras = navExtras)
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}