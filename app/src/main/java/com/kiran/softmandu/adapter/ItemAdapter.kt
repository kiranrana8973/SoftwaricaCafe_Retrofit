package com.kiran.softmandu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kiran.softmandu.R
import com.kiran.softmandu.model.Item

class ItemAdapter(
    private val context: Context,
    private val lstItems: MutableList<Item>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = lstItems[position]
        holder.tvItemName.text = item.itemName
        holder.tvItemPrice.text = item.price.toString()
        Glide.with(context)
            .load(item.imageUrl)
            .fitCenter()
            .circleCrop()
            .placeholder(R.drawable.index)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgItem)

    }

    override fun getItemCount(): Int {
        return lstItems.size
    }


}