package com.mehul.woons.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.mehul.woons.MainActivity
import com.mehul.woons.adapters.ReaderAdapter
import com.mehul.woons.databinding.FragmentReaderBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.viewmodels.ReaderViewModel
import timber.log.Timber

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
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun changeChapter(modifyCurrentChapter: (String) -> Boolean) {
        val waitText = "Please wait..."
        val noMoreText = "No more chapters..."
        if (readerViewModel.chapterPages.value!!.status == Resource.Status.SUCCESS) {
            Timber.e("Changing chapter")

            if (!modifyCurrentChapter(readerArgs.internalName)) {
                Toast.makeText(requireContext(), noMoreText, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), waitText, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val readerAdapter = ReaderAdapter()
        binding.readerScroll.adapter = readerAdapter

        // If already success, then data was loaded previously, avoid re fetch
        if (readerViewModel.chapterPages.value!!.status != Resource.Status.SUCCESS) {
            readerViewModel.initializeReader(
                readerArgs.name,
                readerArgs.internalName,
                readerArgs.internalChapterReference
            )
        }

        binding.prevChapter.setOnClickListener {
            changeChapter { readerViewModel.prevChapter(it) }
        }

        binding.nextChapter.setOnClickListener {
            changeChapter { readerViewModel.nextChapter(it) }
        }

        readerViewModel.chapterPages.observe(viewLifecycleOwner) {
            (activity as MainActivity).binding.appbar.setExpanded(true, true)

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideAll()
                    (activity as MainActivity).supportActionBar?.title = readerArgs.name
                    binding.chapterName.text =
                        readerViewModel.getChapterName()
                    binding.readerScroll.scrollToPosition(0)
                    binding.readerScroll.visibility = View.VISIBLE
                    readerAdapter.updatePages(it.data!!)
                }
                Resource.Status.LOADING -> {
                    hideAll()
                    val loadingText = "Loading..."
                    binding.chapterName.text = loadingText
                    (activity as MainActivity).supportActionBar?.title = loadingText
                    binding.loading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    hideAll()
                    val errorText = "Error"
                    binding.chapterName.text = errorText
                    binding.error.errorLabel.text = it.message!!
                    binding.error.error.visibility = View.VISIBLE
                    (activity as MainActivity).supportActionBar?.title = errorText
                }
            }
        }
    }

    private fun hideAll() {
        binding.readerScroll.visibility = View.GONE
        binding.error.error.visibility = View.GONE
        binding.loading.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}