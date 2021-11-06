package com.cornershop.counterstest.presentation.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.cornershop.counterstest.R
import com.cornershop.counterstest.core.MainInteraction
import com.cornershop.counterstest.core.show
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.databinding.DialogCounterBinding

class AddCounterDialog : DialogFragment() {
    private var _binding: DialogCounterBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var interaction: MainInteraction

    companion object {
        @JvmStatic
        fun newInstance() = AddCounterDialog()
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
        _binding = DialogCounterBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            this.dismiss()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add_counter -> {
                    binding.topAppBar.menu?.clear()
                    binding.loadingCounter.show()
                    interaction.onCreateCounter(TitleCounterModel(binding.inputCounter.text.toString()))
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
        binding.exampleText.setOnClickListener {
            val dialog = ExampleCounterDialog.newInstance()
            dialog.show(parentFragmentManager, "exampleDialog")
        }
        binding.layoutInputName.requestFocus(0)
        return binding.root
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

}