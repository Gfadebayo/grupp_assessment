package com.grupp.assessment.productexplorer.ui.detail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.databinding.FragmentProductDetailBinding
import com.grupp.assessment.productexplorer.ui.base.navController
import com.grupp.assessment.productexplorer.ui.utils.delegates.viewBinding
import com.grupp.assessment.productexplorer.ui.utils.spacedBy
import com.grupp.assessment.productexplorer.ui.utils.textAppearance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private val binding by viewBinding { FragmentProductDetailBinding.bind(it) }

    private val viewModel by viewModels<ProductDetailViewModel>()

    private val args by navArgs<ProductDetailFragmentArgs>()

    private val transition by lazy { TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = transition

        viewModel.fetchDetail(args.id)
        viewModel.stateFlow
            .flowWithLifecycle(lifecycle)
            .onEach { if(it is Result.Success) setDetail(it.data) }
            .launchIn(lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            viewModel.stateFlow.value.also {
                if(it is Result.Success) setDetail(it.data)
            }

            ViewCompat.setOnApplyWindowInsetsListener(root) { v, w ->
                val inset = w.getInsets(WindowInsetsCompat.Type.systemBars())

                Timber.d("Inset is $inset")

                layoutToolbar.updatePadding(top = inset.top)

                textPrice.updatePadding(bottom = inset.bottom)

                w.inset(inset)
            }
        }
    }

    private fun setDetail(detail: ProductDetailModel) {
        binding.apply {
            imageProduct.post {
                Timber.d("Image view width and height: ${imageProduct.width} and ${imageProduct.height}")
                Glide.with(imageProduct)
                    .load(detail.imageUrl)
                    .override(imageProduct.width, imageProduct.height)
                    .into(imageProduct)
                    .request?.also { if(!it.isRunning) it.begin() }
            }

            buttonUp.setOnClickListener { navController.popBackStack() }

            textTitle.text = detail.title

            textCategory.text = detail.category.toDetailCategory(requireContext())

            textDescription.text = detail.description

            textRating.text = detail.rating

            textPrice.text = SpannableStringBuilder()
                .textAppearance(requireContext(), textRes = R.string.total_price, res = R.style.TextAppearance_Detail_TotalPrice)
                .spacedBy(4)
                .append(detail.price)

            startPostponedEnterTransition()
        }
    }
}
