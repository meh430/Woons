package com.mehul.woons.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.Constants
import com.mehul.woons.MainActivity
import com.mehul.woons.adapters.WebtoonAdapter
import com.mehul.woons.addOrRemoveFromLibrary
import com.mehul.woons.calculateNoOfColumns
import com.mehul.woons.databinding.FragmentBrowseBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.viewmodels.BrowseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber


class BrowseFragment : Fragment() {
    private val browseArgs: BrowseFragmentArgs by navArgs()
    private val browseViewModel: BrowseViewModel by viewModels()
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).binding.appbar.setExpanded(true, true)

        val onWebtoonClick = { webtoon: Webtoon ->
            Timber.e(webtoon.toString())
        }
        val browseAdapter = WebtoonAdapter(true, onWebtoonClick) {
            // long click so change library
            lifecycleScope.launch {
                addOrRemoveFromLibrary(requireContext(), browseViewModel.libraryRepository, it)
            }
        }
        binding.browseScroll.layoutManager =
            GridLayoutManager(
                requireContext(),
                calculateNoOfColumns(requireContext(), Constants.WEBTOON_ITEM_WIDTH)
            )
        binding.browseScroll.adapter = browseAdapter

        // Show search bar since we are browsing search
        if (browseArgs.isSearch) {
            binding.browseSearch.visibility = View.VISIBLE
            binding.browseSearch.setText(browseArgs.category)
            // Ensure that no data has been loaded already
            if (browseViewModel.webtoons.value?.isEmpty() == true) {
                browseViewModel.performSearch(browseArgs.category)
                binding.browseSearch.setOnEditorActionListener { textView, i, keyEvent ->
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        // hide kb
                        binding.browseSearch.clearFocus()
                        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .hideSoftInputFromWindow(binding.browseSearch.windowToken, 0)
                        // Search with new query
                        browseViewModel.performSearch(binding.browseSearch.text.toString())
                        true
                    } else {
                        false
                    }
                }
            }
        } else {
            binding.browseSearch.visibility = View.GONE
            if (browseViewModel.webtoons.value?.isEmpty() == true) {
                browseViewModel.performFetch(browseArgs.category)
            }
        }

        browseViewModel.currentPage.observe(viewLifecycleOwner) {
            if (browseArgs.isSearch) {
                (activity as MainActivity).supportActionBar?.title = "Search"
            } else {
                (activity as MainActivity).supportActionBar?.title =
                    Constants.getDisplayCategory(browseArgs.category)
            }

            when (it.status) {
                Resource.Status.LOADING -> binding.loading.visibility = View.VISIBLE
                Resource.Status.SUCCESS -> {
                    hideAll()
                    if (browseArgs.isSearch) {
                        binding.browseSearch.visibility = View.VISIBLE
                    }
                    binding.browseScroll.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    hideAll()
                    binding.error.errorLabel.text = it.message!!
                    binding.error.error.visibility = View.VISIBLE
                }
            }
        }

        browseViewModel.webtoons.observe(viewLifecycleOwner) {
            if (browseViewModel.currentPage.value!!.status == Resource.Status.ERROR) {
                return@observe
            }

            if (it.isEmpty()) {
                if (browseViewModel.currentPage.value!!.status == Resource.Status.LOADING) {
                    return@observe
                }
                hideAll()
                binding.empty.empty.visibility = View.VISIBLE
                if (browseArgs.isSearch) {
                    binding.browseSearch.visibility = View.VISIBLE
                    binding.empty.emptyLabel.text = "No search results found :("
                } else {
                    binding.empty.emptyLabel.text = "No webtoons found :("
                }
            } else {
                browseAdapter.updateWebtoons(it)
            }
        }

        binding.browseScroll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // Recycler view is at the bottom. If search, no more pages
                if (!recyclerView.canScrollVertically(1)) {
                    if (browseArgs.isSearch) {
                        return
                    }
                    val currentStatus = browseViewModel.currentPage.value!!.status
                    // Only paginate if there were no issues previously?
                    if (currentStatus == Resource.Status.SUCCESS) {
                        browseViewModel.performFetch(browseArgs.category)
                    }
                }
            }
        })
    }

    private fun hideAll() {
        binding.loading.visibility = View.GONE
        binding.browseScroll.visibility = View.GONE
        binding.empty.empty.visibility = View.GONE
        binding.error.error.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}