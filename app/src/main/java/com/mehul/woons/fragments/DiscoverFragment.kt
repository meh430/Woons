package com.mehul.woons.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mehul.woons.adapters.DiscoverAdapter
import com.mehul.woons.databinding.FragmentDiscoverBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.viewmodels.DiscoverViewModel
import timber.log.Timber

class DiscoverFragment : Fragment() {
    private val discoverViewModel: DiscoverViewModel by viewModels()
    private lateinit var discoverAdapter: DiscoverAdapter
    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onWebtoonClick = { webtoon: Webtoon -> Timber.e(webtoon.toString()) }
        val onDiscoverClick = { category: String -> Timber.e(category) }
        // Do all observe stuff here
        discoverAdapter = DiscoverAdapter(onWebtoonClick, onDiscoverClick)
        binding.discoverScroll.adapter = discoverAdapter

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
}