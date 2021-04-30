package com.mehul.woons.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mehul.woons.Constants
import com.mehul.woons.adapters.WebtoonAdapter
import com.mehul.woons.addOrRemoveFromLibrary
import com.mehul.woons.calculateNoOfColumns
import com.mehul.woons.databinding.FragmentLibraryBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.viewmodels.LibraryViewModel
import kotlinx.coroutines.launch


class LibraryFragment : Fragment(), WebtoonAdapter.WebtoonItemListener {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var libraryAdapter: WebtoonAdapter
    private val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()

        libraryViewModel.libraryWebtoons.observe(viewLifecycleOwner) {
            hideAll()
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    libraryAdapter.updateWebtoons(it.data!!)
                    if (it.data.isEmpty()) {
                        binding.empty.emptyLabel.text = "Library is empty :("
                        binding.empty.empty.visibility = View.VISIBLE
                    } else {
                        binding.webtoonScroll.visibility = View.VISIBLE
                    }
                }
                Resource.Status.LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    binding.error.errorLabel.text = it.message!!
                    binding.error.error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initializeAdapter() {
        libraryAdapter = WebtoonAdapter(true, this)
        binding.webtoonScroll.layoutManager =
            GridLayoutManager(
                requireContext(),
                calculateNoOfColumns(requireContext(), Constants.WEBTOON_ITEM_WIDTH)
            )
        binding.webtoonScroll.adapter = libraryAdapter
    }

    private fun hideAll() {
        binding.empty.empty.visibility = View.GONE
        binding.error.error.visibility = View.GONE
        binding.loading.visibility = View.GONE
        binding.webtoonScroll.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(webtoon: Webtoon) {
        val toInfo = LibraryFragmentDirections.actionLibraryFragmentToInfoFragment(
            webtoon.name,
            webtoon.internalName
        )
        findNavController().navigate(toInfo)
    }

    override fun onLongClick(webtoon: Webtoon) {
        lifecycleScope.launch {
            addOrRemoveFromLibrary(requireContext(), libraryViewModel.libraryRepository, webtoon)
        }
    }
}