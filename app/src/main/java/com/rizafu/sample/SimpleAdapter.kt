package com.rizafu.sample

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rizafu.sample.databinding.ItemMainBinding

import java.util.ArrayList
import java.util.HashMap


/**
 * Created by RizaFu on 1/2/17.
 */

internal class SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.MainViewHolder>() {
    private val items: ArrayList<HashMap<String, String>> = ArrayList()
    private var context: Context? = null

    private var onItemClick: ((View,Int) -> Unit)? = null

    fun addItem(title: String, subTitle: String?) {
        val map = HashMap<String, String>()
        map.put(TITLE, title)
        map.put(SUBTITLE, subTitle?:"")
        items.add(map)
        notifyDataSetChanged()
    }

    interface OnItemClick {
        fun onClick(view: View, position: Int)
    }

    fun setOnItemClick(onItemClick: (View, Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemMainBinding>(LayoutInflater.from(parent.context), R.layout.item_main, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.binding.root.setOnClickListener { view -> onItemClick?.invoke(view, position) }

        holder.binding.title.text = getItem(position)[TITLE]

        if (!TextUtils.isEmpty(getItem(position)[SUBTITLE])) {
            holder.binding.subTitle.text = getItem(position)[SUBTITLE]
            holder.binding.subTitle.visibility = View.VISIBLE
        } else {
            holder.binding.subTitle.visibility = View.GONE
        }
        if (position == itemCount - 1) {
            val margin = context?.resources?.getDimension(R.dimen.margin_item)?.toInt()?:0
            val params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(margin, margin, margin, margin)
            holder.binding.container.layoutParams = params
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): HashMap<String, String> {
        return items[position]
    }

    internal class MainViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val TITLE = "title"
        private val SUBTITLE = "subTitle"
    }
}
