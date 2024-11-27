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
import com.grupp.assessment.productexplorer.ui.list.mapper.ListUi

class ProductListAdapter: ListAdapter<ListUi, ProductListAdapter.ViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<ListUi>() {
            override fun areItemsTheSame(oldItem: ListUi, newItem: ListUi) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListUi, newItem: ListUi) = oldItem == newItem
        }
    }

    var onItemClick: (ListUi) -> Unit = {}

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

            Glide.with(imageProduct)
                .load(item.image)
                .into(imageProduct)
                .request?.also { if(!it.isRunning) it.begin() }
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemProductBinding.bind(view).apply {
            root.setOnClickListener {
                onItemClick(currentList[bindingAdapterPosition])
            }
        }
    }
}