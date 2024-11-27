package com.grupp.assessment.productexplorer.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.databinding.ItemProductBinding
import com.grupp.assessment.productexplorer.ui.utils.ImageViewTarget

class ProductListAdapter: ListAdapter<ProductItem, ProductListAdapter.ViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<ProductItem>() {
            override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem) = oldItem == newItem
        }
    }

    var onItemClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false).run {
            ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = currentList[position]

            textTitle.text = item.title

            textPrice.text = item.price

            textRating.text = item.rating

            imageProduct.transitionName = "image_product_${item.id}"
            textRating.transitionName = "rating_${item.id}"
            textTitle.transitionName = "title_${item.id}"
            textPrice.transitionName = "price_${item.id}"

            imageProduct.post {
                Glide.with(imageProduct)
                    .load(item.image)
                    .override(imageProduct.width, imageProduct.height)
                    .into(ImageViewTarget(imageProduct))
                    .request?.also { if(!it.isRunning) it.begin() }
            }
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemProductBinding.bind(view).apply {
            root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }
    }
}