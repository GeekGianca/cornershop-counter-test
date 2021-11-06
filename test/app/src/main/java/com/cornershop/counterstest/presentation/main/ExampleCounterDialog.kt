package com.cornershop.counterstest.presentation.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.core.MainInteraction
import com.cornershop.counterstest.data.models.ExampleModel
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.databinding.DialogExampleCounterBinding
import com.cornershop.counterstest.presentation.adapter.AdapterExample

class ExampleCounterDialog : DialogFragment(), AdapterExample.ExampleInteraction {
    private var _binding: DialogExampleCounterBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var interaction: MainInteraction
    private lateinit var adapterExample: AdapterExample

    companion object {
        @JvmStatic
        fun newInstance() = ExampleCounterDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            if (activity is MainActivity) {
                try {
                    interaction = context as MainInteraction
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExampleCounterBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        val generateExamples = generateExp()
        binding.topAppBar.setNavigationOnClickListener {
            this.dismiss()
        }
        adapterExample = AdapterExample(generateExamples, this)
        binding.exampleList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = adapterExample
        }
    }

    private fun generateExp(): List<ExampleModel> {
        val resourceDrinks = resources.getStringArray(R.array.drinks_array).toList()
        val resourceFoods = resources.getStringArray(R.array.food_array).toList()
        val resourceMisc = resources.getStringArray(R.array.misc_array).toList()
        return mutableListOf(
            ExampleModel(getString(R.string.drinks), resourceDrinks),
            ExampleModel(getString(R.string.food), resourceFoods),
            ExampleModel(getString(R.string.misc), resourceMisc)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity(), R.style.AppTheme_FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), android.R.color.white)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onSelectExample(eObject: String) {
        this.dismiss()
        interaction.onCreateCounter(TitleCounterModel(eObject))
    }
}