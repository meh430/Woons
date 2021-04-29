package com.mehul.woons.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mehul.woons.MainActivity
import com.mehul.woons.adapters.ReaderAdapter
import com.mehul.woons.databinding.FragmentReaderBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.viewmodels.ReaderViewModel

// Fragment needs to receives name, internal name, and chapter reference
class ReaderFragment : Fragment() {
    private val readerArgs: ReaderFragmentArgs by navArgs()
    private val readerViewModel: ReaderViewModel by viewModels()
    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val readerAdapter = ReaderAdapter()
        binding.readerScroll.adapter = readerAdapter

        if (readerViewModel.chapterPages.value!!.status != Resource.Status.SUCCESS) {
            readerViewModel.initializeReader(
                readerArgs.name,
                readerArgs.internalName,
                readerArgs.internalChapterReference
            )
        }

        readerViewModel.chapterPages.observe(viewLifecycleOwner) {
            (activity as MainActivity).supportActionBar?.show()
            (activity as MainActivity).supportActionBar?.title = readerArgs.name
            (activity as MainActivity).supportActionBar?.subtitle = ""

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideAll()
                    (activity as MainActivity).supportActionBar?.subtitle =
                        readerViewModel.getChapterName()
                    binding.readerScroll.visibility = View.VISIBLE
                    readerAdapter.updatePages(it.data!!)
                }
                Resource.Status.LOADING -> {
                    hideAll()
                    binding.loading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    hideAll()
                    binding.error.error.visibility = View.VISIBLE
                    binding.error.errorLabel.text = it.message!!
                }
            }
        }
    }

    fun hideAll() {
        binding.readerScroll.visibility = View.GONE
        binding.error.error.visibility = View.GONE
        binding.loading.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}