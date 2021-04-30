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
import androidx.navigation.fragment.findNavController
import com.mehul.woons.adapters.DiscoverAdapter
import com.mehul.woons.addOrRemoveFromLibrary
import com.mehul.woons.databinding.FragmentDiscoverBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.viewmodels.DiscoverViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class DiscoverFragment : Fragment(), DiscoverAdapter.DiscoverListener {
    private val discoverViewModel: DiscoverViewModel by viewModels()
    private lateinit var discoverAdapter: DiscoverAdapter
    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discoverAdapter = DiscoverAdapter(this)
        binding.discoverScroll.adapter = discoverAdapter

        binding.searchBar.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                // hide kb
                binding.searchBar.clearFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                // navigate to search browse
                val toSearchBrowse =
                    DiscoverFragmentDirections.actionDiscoverFragmentToBrowseFragment(
                        true,
                        binding.searchBar.text.toString()
                    )

                findNavController().navigate(toSearchBrowse)
                true
            } else {
                false
            }
        }


        discoverViewModel.discoverLists.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.LOADING -> {
                    binding.discoverScroll.visibility = View.GONE
                    binding.error.error.visibility = View.GONE

                    binding.discoverLoading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    binding.discoverScroll.visibility = View.GONE
                    binding.discoverLoading.visibility = View.GONE
                    binding.error.error.visibility = View.VISIBLE
                    binding.error.errorLabel.text = it.message!!
                }
                Resource.Status.SUCCESS -> {
                    binding.discoverLoading.visibility = View.GONE
                    binding.error.error.visibility = View.GONE
                    binding.discoverScroll.visibility = View.VISIBLE
                    discoverAdapter.updateDiscoverItems(it.data!!)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Discover listener overrides

    override fun onWebtoonClick(webtoon: Webtoon) {
        Timber.e(webtoon.toString())
        val toInfo = DiscoverFragmentDirections.actionDiscoverFragmentToInfoFragment(
            webtoon.name,
            webtoon.internalName
        )
        findNavController().navigate(toInfo)
    }

    override fun onWebtoonLongClick(webtoon: Webtoon) {
        // long clicked so we update library
        lifecycleScope.launch {
            addOrRemoveFromLibrary(
                requireContext(),
                discoverViewModel.libraryRepository,
                webtoon
            )
        }
    }

    override fun onDiscoverClick(category: String) {
        Timber.e(category)
        val toBrowse =
            DiscoverFragmentDirections.actionDiscoverFragmentToBrowseFragment(false, category)
        findNavController().navigate(toBrowse)
    }
}