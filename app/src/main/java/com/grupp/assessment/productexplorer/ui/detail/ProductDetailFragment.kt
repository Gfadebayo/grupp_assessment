package com.grupp.assessment.productexplorer.ui.detail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.databinding.FragmentProductDetailBinding
import com.grupp.assessment.productexplorer.ui.utils.delegates.viewBinding
import com.grupp.assessment.productexplorer.ui.utils.spacedBy
import com.grupp.assessment.productexplorer.ui.utils.textAppearance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private val binding by viewBinding { FragmentProductDetailBinding.bind(it) }

    private val viewModel by viewModels<ProductDetailViewModel>()

    private val args by navArgs<ProductDetailFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        viewModel.fetchDetail(args.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            viewModel.stateFlow.value.also {
                if(it is Result.Success) setDetail(it.data)
            }
        }
    }

    private fun setDetail(detail: ProductDetailModel) {
        binding.apply {
            Glide.with(imageProduct)
                .load(detail.imageUrl)
                .into(imageProduct)
                .request?.begin()

            textTitle.text = detail.title

            textCategory.text = detail.category.toDetailCategory(requireContext())

            textDescription.text = detail.description

            textRating.text = detail.rating

            textPrice.text = SpannableStringBuilder()
                .textAppearance(requireContext(), textRes = R.string.total_price, res = R.style.TextAppearance_Detail_TotalPrice)
                .spacedBy(4)
                .append(detail.price)
        }
    }
}
