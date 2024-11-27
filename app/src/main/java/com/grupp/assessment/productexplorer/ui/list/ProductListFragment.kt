package com.grupp.assessment.productexplorer.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.databinding.FragmentProductListBinding
import com.grupp.assessment.productexplorer.ui.base.dismissLoadingDialog
import com.grupp.assessment.productexplorer.ui.base.handleError
import com.grupp.assessment.productexplorer.ui.base.navigate
import com.grupp.assessment.productexplorer.ui.utils.delegates.viewBinding
import com.grupp.assessment.productexplorer.ui.utils.itemdecoration.BoxDecoration
import com.grupp.assessment.productexplorer.ui.utils.itemdecoration.BoxDecoration.Companion.setBoxDecoration
import com.grupp.assessment.productexplorer.ui.utils.itemdecoration.InBetweenDecoration.Companion.setInBetweenDecoration
import com.grupp.assessment.productexplorer.ui.utils.itemdecoration.getItemDecoration
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
                binding.apply {
                    root.isRefreshing = it.loadState is Result.Loading

                    (recyclerView.adapter as ProductListAdapter).submitList(it.products)

                    if(it.loadState is Result.Error) handleError(it.loadState)
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            recyclerView.adapter = ProductListAdapter().apply {
                submitList(viewModel.listFlow.value.products)

                onItemClick = { index ->
                    val item = currentList[index]
                    val itemBinding = (recyclerView.findViewHolderForAdapterPosition(index) as ProductListAdapter.ViewHolder).binding

                    navigate(
                        id = R.id.frag_product_detail,
                        args = bundleOf("id" to item.id),
                        sharedElements = arrayOf(
                            itemBinding.imageProduct to "image_product",
                            itemBinding.textPrice to "price",
                            itemBinding.textRating to "rating",
                            itemBinding.textTitle to "title"

                        )
                    )
                }
            }

            val horizontal = resources.getDimension(R.dimen.padding_horizontal_screen).toInt()
            recyclerView.setBoxDecoration(
                start = horizontal,
                end = horizontal
            )

            recyclerView.setInBetweenDecoration(padding = 8)

            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, w ->
                val inset = w.getInsets(WindowInsetsCompat.Type.systemBars())

                recyclerView.getItemDecoration<BoxDecoration>()?.also {
                    it.paddingTop = inset.top
                    it.paddingBottom = inset.bottom

                    recyclerView.invalidateItemDecorations()
                }

                w
            }

            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }

            root.setOnRefreshListener { viewModel.fetchProducts() }
        }
    }
}