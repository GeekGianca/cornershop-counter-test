package com.cornershop.counterstest.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.LayoutSubItemDetailExampleBinding

class AdapterExampleItem(
    private val list: List<String>,
    private val listener: AdapterExample.ExampleInteraction
) :
    RecyclerView.Adapter<AdapterExampleItem.ExampleItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_sub_item_detail_example, parent, false)
        val binding = LayoutSubItemDetailExampleBinding.bind(view)
        return ExampleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExampleItemViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ExampleItemViewHolder(private val binding: LayoutSubItemDetailExampleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.chipItem.text = item
            binding.chipItem.setOnClickListener {
                listener.onSelectExample(item)
            }
        }
    }
}