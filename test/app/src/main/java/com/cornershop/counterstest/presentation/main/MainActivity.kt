package com.cornershop.counterstest.presentation.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.core.*
import com.cornershop.counterstest.core.Util.EMPTY_LIST
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.databinding.ActivityMainBinding
import com.cornershop.counterstest.presentation.adapter.AdapterCounter
import com.cornershop.counterstest.presentation.states.MainEventState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterCounter.CounterInteraction, View.OnClickListener,
    MainInteraction {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var viewModel: MainViewModel
    private var errorStatusEvent: Int = -2
    private var incrementOrDecrement: Int = 0
    private var counterEntity: CounterEntity? = null
    private var isContextualModeEnable = false
    private val contextualCountersSelected = mutableListOf<CounterEntity>()
    private var actionMode: ActionMode? = null
    private var dialog: AddCounterDialog? = null
    private lateinit var adapterCounter: AdapterCounter
    private var query: String? = ""
    private val counters = mutableListOf<CounterEntity>()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        query?.let {
            if (it.isNotBlank()) {
                viewModel.clearErrorStack() // if offline, avoid the Job stop
                viewModel.searchCounterListByQuery(it)
            } else {
                restoreInitialStateView()
            }
        }
    }

    private val task: Long = 600
    private val actionCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.contextual_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_share -> {
                    onShareCounter(contextualCountersSelected.toShare())
                    true
                }
                R.id.action_delete -> {
                    displayAlert(message = getString(
                        R.string.delete_x_question,
                        contextualCountersSelected.toDelete()
                    ),
                        titlePos = getString(R.string.delete), pos = { dialogInterface, _ ->
                            onDeleteSelected()
                            dialogInterface.dismiss()
                        }, titleNeg = getString(R.string.cancel), neg = { dialog, _ ->
                            dialog.dismiss()
                        })
                    true
                }
                else -> {
                    false
                }
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            Log.d(TAG, "Mode: $mode")
            Log.d(TAG, "from: onDestroyActionMode")
            removeElementsSelected()
            finishContextualActionMode()
        }

    }

    private fun onDeleteSelected() {
        Log.d(TAG, "from: onDeleteSelected")
        contextualCountersSelected.forEach {
            viewModel.setStateEvent(MainEventState.DeleteCounterEvent(it))
        }
        removeElementsSelected()
        finishContextualActionMode()
    }

    private fun removeElementsSelected() {
        Log.d(TAG, "removeElementSelected")
        if (contextualCountersSelected.isNotEmpty()) {
            isContextualModeEnable = false
            contextualCountersSelected.forEachIndexed { index, obj ->
                adapterCounter.removeElementSelected(obj)
            }
            contextualCountersSelected.clear()
            counters.forEachIndexed { index, _ ->
                adapterCounter.notifyItemChanged(index)
            }
        } else {
            contextualCountersSelected.clear() // Only Safe
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        subscribeObservables()
    }

    private fun initView() {
        // Uses by viewModels() or by activityViewModels()
        // or ViewModelFactory class
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)// Simple way
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.searchCardView.setOnClickListener(this)
        binding.addCounter.setOnClickListener(this)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                this@MainActivity.query = query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                query = newText
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, task)
                return true
            }

        })
        binding.refreshCounter.setOnRefreshListener {

            if (isContextualModeEnable) {
                Log.d(TAG, "from: setOnRefreshListener")
                removeElementsSelected()
            }
            restoreInitialStateView()
        }
        viewModel.setupChannel()
    }

    private fun initRecyclerView(data: List<CounterEntity>) {
        adapterCounter = AdapterCounter(data, this)
        val timesAndItems = data.toDetail()
        binding.items.text = getString(R.string.n_items, timesAndItems.first)
        binding.times.text = getString(R.string.n_times, timesAndItems.second)
        binding.counterList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterCounter
        }
    }

    private fun subscribeObservables() {
        viewModel.viewSate.observe(this) { viewState ->
            if (viewState != null) {
                binding.loadingCounters.hide()
                viewState.listCounterView.listCounters?.let {
                    if (!isContextualModeEnable) {
                        Log.d(TAG, "ShowResults: $it")
                        counters.clear()
                        counters.addAll(it)
                        initRecyclerView(it)
                        checkEmptyState(it)
                    }
                }

                viewState.searchCounterView.listCounters?.let {
                    if (!isContextualModeEnable) {
                        Log.d(TAG, "Search Counter: $it")
                        counters.clear()
                        counters.addAll(it)
                        initRecyclerView(it)
                        checkEmptySearch(it)
                    }
                }

                viewState.deleteCounterView.counter?.let {
                    displayAlert(title = getString(R.string.counter_deleted),
                        getString(R.string.counter_deleted_title, it.title),
                        titlePos = getString(R.string.ok), pos = { dialogInterface, _ ->
                            restoreInitialStateView()
                            dialogInterface.dismiss()
                        })
                }
            }
        }

        viewModel.shouldDisplayProgressBar.observe(this) {
            binding.refreshCounter.isRefreshing = it
        }

        viewModel.errorState.observe(this) { errorState ->
            errorState?.let {
                Log.d(TAG, "$it")
                onErrorReceived(
                    errorState = it,
                    errorStateCallback = object : ErrorStateCallback {
                        override fun removeErrorFromStack() {
                            viewModel.clearErrorState(0)
                        }

                    }
                )
            }
        }
    }

    private fun checkEmptySearch(data: List<CounterEntity>) {
        if (data.isEmpty()) {
            binding.layoutEmptyState.show()
            binding.title.hide()
            binding.subTitle.text = getString(R.string.no_results)
            binding.groupContentDataList.hide()
        } else {
            binding.layoutEmptyState.hide()
            binding.title.hide()
            binding.groupContentDataList.show()
        }
    }

    private fun checkEmptyState(data: List<CounterEntity>) {
        if (data.isEmpty()) {
            binding.loadingCounters.hide()
            binding.layoutEmptyState.show()
            binding.title.show()
            binding.title.text = getString(R.string.no_counters)
            binding.subTitle.text = getString(R.string.no_counters_phrase)
            binding.groupContentDataList.hide()
        } else {
            binding.loadingCounters.hide()
            binding.layoutEmptyState.hide()
            binding.groupContentDataList.show()
        }
    }

    private fun showAppBarSearchView() {
        binding.searchCardView.hide()
        binding.appBar.show()
        if (contextualCountersSelected.isEmpty()) {
            binding.searchView.setQuery("", false)
            binding.searchView.requestFocus(0)
        }
    }

    private fun hideAppBarSearch() {
        binding.searchCardView.show()
        binding.appBar.hide()
    }

    private fun restoreInitialStateView() {
        viewModel.clearErrorStack()
        viewModel.clearViews()
        viewModel.setStateEvent(MainEventState.GetListCountersEvent())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideAppBarSearch()
                isContextualModeEnable = false
                contextualCountersSelected.clear()
                restoreInitialStateView()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClickIncreaseCounterListener(index: Int, element: CounterEntity) {
        incrementOrDecrement = 0
        counterEntity = element
        viewModel.clearErrorStack()
        viewModel.clearViews()
        viewModel.setStateEvent(MainEventState.EditCounterEvent(element, 0))
        errorStatusEvent = 1
    }

    override fun onClickDecreaseCounterListener(index: Int, element: CounterEntity) {
        if (element.count > 0) {
            incrementOrDecrement = 1
            counterEntity = element
            viewModel.clearErrorStack()
            viewModel.clearViews()
            viewModel.setStateEvent(MainEventState.EditCounterEvent(element, 1))
            errorStatusEvent = -1
        }
    }

    override fun onContextAddCounterListener(index: Int, elements: List<CounterEntity>) {
        contextualCountersSelected.clear()
        contextualCountersSelected.addAll(elements)
        adapterCounter.notifyItemChanged(index)
        counters.forEachIndexed { idx, _ ->
            if (idx != index)
                adapterCounter.notifyItemChanged(idx)
        }
        if (!isContextualModeEnable) {
            isContextualModeEnable = true
            enableContextualActionMode()
            binding.addCounter.hide()
        } else {
            updateTitleContextual()
        }
    }

    override fun onContextRemoveCounterListener(index: Int, elements: List<CounterEntity>) {
        contextualCountersSelected.clear()
        contextualCountersSelected.addAll(elements)
        adapterCounter.notifyItemChanged(index)
        updateTitleContextual()
        if (elements.isEmpty()) {
            finishContextualActionMode()
        } else {
            binding.addCounter.hide()
        }
    }

    private fun enableContextualActionMode() {
        showAppBarSearchView()
        actionMode = startSupportActionMode(actionCallback)
        updateTitleContextual()
    }

    private fun updateTitleContextual() {
        actionMode?.title = getString(R.string.n_selected, contextualCountersSelected.size)
    }

    private fun finishContextualActionMode() {
        isContextualModeEnable = false
        binding.addCounter.show()
        actionMode?.finish()
        hideAppBarSearch()
    }

    override fun onStart() {
        super.onStart()
        viewModel.setStateEvent(MainEventState.GetListCountersEvent())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addCounter -> {
                dialog = AddCounterDialog.newInstance()
                dialog?.show(supportFragmentManager, "counterDialog")
            }
            R.id.searchCardView -> {
                showAppBarSearchView()
            }
        }
    }

    override fun onCreateCounter(model: TitleCounterModel) {
        dialog?.dismiss()
        dialog = null
        viewModel.clearErrorStack()
        viewModel.createCounter(model)
        errorStatusEvent = 0
    }

    override fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback) {
        binding.loadingCounters.hide()
        if (errorState.message.contains("Network error", ignoreCase = true)) {
            if (!this::adapterCounter.isInitialized) {
                binding.layoutEmptyState.show()
            }
            return
        }
        if (errorState.code == 500) {
            when (errorStatusEvent) {
                0 -> {
                    displayAlert(
                        title = getString(R.string.error_creating_counter_title),
                        message = getString(R.string.connection_error_description),
                        titlePos = getString(R.string.ok),
                        pos = { dialog, _ ->
                            dialog.dismiss()
                        })
                }
                1, -1 -> {
                    displayAlert(
                        title = getString(
                            R.string.error_updating_counter_title,
                            counterEntity?.title,
                            counterEntity?.count?.plus(errorStatusEvent)
                        ),
                        message = getString(R.string.connection_error_description),
                        titlePos = getString(R.string.dismiss),
                        pos = { dialog, _ ->
                            dialog.dismiss()
                        },
                        titleNeg = getString(R.string.retry),
                        neg = { dialog, _ ->
                            if (incrementOrDecrement == 0)
                                onClickIncreaseCounterListener(0, counterEntity!!)
                            else
                                onClickDecreaseCounterListener(0, counterEntity!!)
                            dialog.dismiss()
                        })
                }
                else -> {

                }
            }
            errorStatusEvent = -2
            return
        }
        if (!errorState.message.equals(EMPTY_LIST, ignoreCase = false)) {
            Toast.makeText(this, errorState.message, Toast.LENGTH_LONG).show()
            return
        } else {
            counters.clear()
            checkEmptyState(counters)
            return
        }
    }

    override fun onBackPressed() {
        if (contextualCountersSelected.isNotEmpty()) {
            Log.d(TAG, "from: onBackPressed")
            removeElementsSelected()
        } else {
            super.onBackPressed()
        }
    }
}