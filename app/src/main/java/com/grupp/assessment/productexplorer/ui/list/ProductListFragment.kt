package com.grupp.assessment.productexplorer.ui.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.databinding.FragmentProductListBinding
import com.grupp.assessment.productexplorer.ui.base.dismissLoadingDialog
import com.grupp.assessment.productexplorer.ui.base.handleError
import com.grupp.assessment.productexplorer.ui.base.navigate
import com.grupp.assessment.productexplorer.ui.base.showLoading
import com.grupp.assessment.productexplorer.ui.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class ProductListFragment: Fragment(R.layout.fragment_product_list) {

    private val binding by viewBinding { FragmentProductListBinding.bind(it) }

    private val viewModel by viewModels<ProductListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchProducts()

        viewModel.listFlow
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach {
                Timber.d(it.toString())

//                if(it.loadState is Result.Loading) showLoading(textRes = R.string.fetching_products)
//                else dismissLoadingDialog()

                binding.apply {
                    when(it.loadState) {
                        is Result.None -> {}
                        is Result.Success -> {
                            (recyclerView.adapter as ProductListAdapter).submitList(it.products)
                        }
                        is Result.Error -> handleError(it.loadState)

                        else -> {}
                    }
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            recyclerView.adapter = ProductListAdapter().apply {
                submitList(viewModel.listFlow.value.products)

                onItemClick = {
                    navigate(
                        id = R.id.frag_product_detail,
                        args = bundleOf("id" to it.id),
                    )
                }
            }
        }
    }
}