package com.example.ytaudio.search


import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.TransitionManager
import com.example.ytaudio.R
import com.example.ytaudio.databinding.SearchFragmentBinding
import com.example.ytaudio.fragment.ActionModeFragment
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.utils.extensions.hideKeyboard


class SearchFragment : ActionModeFragment() {

    private lateinit var binding: SearchFragmentBinding

    lateinit var viewModel: SearchViewModel
        private set

    private val videoItemAdapter = SearchAdapter(this) {
        Toast.makeText(this@SearchFragment.context, it.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(activity).application
        viewModel =
            ViewModelProvider(this, FactoryUtils.provideSearchViewModel(application))
                .get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater)

        binding.apply {
            viewModel = this@SearchFragment.viewModel
            videoItemsView.adapter = videoItemAdapter
            videoItemsView.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
            lifecycleOwner = this@SearchFragment
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = getString(R.string.search)
        searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
            .threshold = 1

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_suggest)
        val cursorAdapter = SimpleCursorAdapter(
            context, R.layout.item_search_suggest,
            null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView.suggestionsAdapter = cursorAdapter

        viewModel.autoComplete.observe(viewLifecycleOwner, Observer {
            val cursor =
                MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
            it.forEachIndexed { index, s ->
                cursor.addRow(arrayOf(index, s))
            }
            cursorAdapter.changeCursor(cursor)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.hideKeyboard(context)
                query?.let {
                    viewModel.setResponse(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.setAutoComplete(it)
                }
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean = false

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.setQuery(selection, false)

                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.isIconified = false
                searchView.requestFocusFromTouch()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?) = true
        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                if (!viewModel.searchItemList.value.isNullOrEmpty()) {
                    videoItemAdapter.startActionMode()
                }
                true
            }
            R.id.action_search -> {
                activity?.let {
                    TransitionManager.beginDelayedTransition(it.findViewById(R.id.toolbar))
                    MenuItemCompat.expandActionView(item)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        binding.root.hideKeyboard(context)
        super.onPause()
    }
}