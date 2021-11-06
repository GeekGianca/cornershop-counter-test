package com.cornershop.counterstest.presentation.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.data.models.ExampleModel
import com.cornershop.counterstest.databinding.LayoutItemDetailBinding

class AdapterExample(
    private val list: List<ExampleModel>,
    private val listener: ExampleInteraction
) :
    RecyclerView.Adapter<AdapterExample.ExampleViewHolder>() {
    private lateinit var context: Context
    private val rPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_item_detail, parent, false)
        val binding = LayoutItemDetailBinding.bind(view)
        return ExampleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item) // Bind title
    }

    override fun getItemCount(): Int = list.size

    inner class ExampleViewHolder(private val binding: LayoutItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExampleModel) {
            binding.titleList.text = item.title
            // Create layout from sub recycler
            binding.detailList.apply {
                val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                manager.initialPrefetchItemCount = item.listData.size
                layoutManager = manager
                val adapterItem = AdapterExampleItem(item.listData, listener = listener)
                adapter = adapterItem
                setRecycledViewPool(rPool)
            }
        }
    }

    interface ExampleInteraction {
        fun onSelectExample(eObject: String)
    }
}