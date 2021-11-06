package com.cornershop.counterstest.presentation.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.core.dismiss
import com.cornershop.counterstest.core.hide
import com.cornershop.counterstest.core.show
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.databinding.LayoutCounterItemBinding

class AdapterCounter(
    private val list: List<CounterEntity>,
    private val interaction: CounterInteraction
) :
    RecyclerView.Adapter<AdapterCounter.CounterViewHolder>() {
    private var _context: Context? = null
    private val ctx
        get() = _context!!

    private var longPressEnable = false
    private val selectedItems = mutableListOf<CounterEntity>()

    fun removeElementSelected(index: CounterEntity) {
        Log.d("AdapterCounter", "Remove $index")
        selectedItems.remove(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterViewHolder {
        _context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCounterItemBinding.inflate(inflater, parent, false)
        return CounterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CounterViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class CounterViewHolder(private val binding: LayoutCounterItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        fun bind(item: CounterEntity) {
            binding.title.text = item.title
            binding.counter.text = item.count.toString()
            binding.viewContent.setOnClickListener(this)
            binding.selectedItem.setOnClickListener(this)
            binding.viewContent.setOnLongClickListener(this)
            binding.ibDecrease.setOnClickListener(this)
            binding.ibIncrease.setOnClickListener(this)
            if (item.count > 0) {
                binding.counter.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            } else {
                binding.counter.setTextColor(ContextCompat.getColor(ctx, R.color.gray))
            }
            if (longPressEnable) {
                if (selectedItems.isNotEmpty()) {
                    binding.groupAddOrRemove.hide()
                    if (selectedItems.contains(item)) {
                        binding.selectedItem.show()
                        binding.viewDataContent.background =
                            ContextCompat.getDrawable(ctx, R.drawable.bg_selected_item)
                    } else {
                        binding.selectedItem.hide()
                        binding.viewDataContent.background = null
                    }
                } else {
                    binding.selectedItem.hide()
                    binding.groupAddOrRemove.show()
                    binding.viewDataContent.background = null
                }
            } else {
                binding.groupAddOrRemove.show()
                binding.selectedItem.hide()
            }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.viewContent, R.id.selectedItem -> {
                    if (longPressEnable) {
                        if (selectedItems.contains(list[adapterPosition])) {
                            selectedItems.remove(list[adapterPosition])
                            interaction.onContextRemoveCounterListener(
                                adapterPosition,
                                selectedItems
                            )
                        } else {
                            selectedItems.add(list[adapterPosition])
                            interaction.onContextAddCounterListener(adapterPosition, selectedItems)
                        }
                        if (selectedItems.size == 0)
                            longPressEnable = false
                    }
                }
                R.id.ibDecrease -> {
                    interaction.onClickDecreaseCounterListener(
                        adapterPosition,
                        list[adapterPosition]
                    )
                }
                R.id.ibIncrease -> {
                    interaction.onClickIncreaseCounterListener(
                        adapterPosition,
                        list[adapterPosition]
                    )
                }
            }
        }

        override fun onLongClick(v: View): Boolean {
            when (v.id) {
                R.id.viewContent -> {
                    longPressEnable = true
                    if (!selectedItems.contains(list[adapterPosition])) {
                        selectedItems.add(list[adapterPosition])
                        interaction.onContextAddCounterListener(adapterPosition, selectedItems)
                    } else {
                        selectedItems.remove(list[adapterPosition])
                        interaction.onContextRemoveCounterListener(adapterPosition, selectedItems)
                    }
                    if (selectedItems.size == 0)
                        longPressEnable = false
                    return true
                }
            }
            return false
        }
    }

    interface CounterInteraction {
        fun onClickIncreaseCounterListener(index: Int, element: CounterEntity)
        fun onClickDecreaseCounterListener(index: Int, element: CounterEntity)
        fun onContextAddCounterListener(index: Int, elements: List<CounterEntity>)
        fun onContextRemoveCounterListener(index: Int, elements: List<CounterEntity>)
    }

}