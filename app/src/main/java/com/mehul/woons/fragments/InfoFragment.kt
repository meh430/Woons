package com.mehul.woons.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import com.mehul.woons.MainActivity
import com.mehul.woons.adapters.ChapterAdapter
import com.mehul.woons.adapters.ChapterHeaderAdapter
import com.mehul.woons.adapters.InfoHeaderAdapter
import com.mehul.woons.databinding.FragmentInfoBinding
import com.mehul.woons.entities.Chapter
import com.mehul.woons.viewmodels.WebtoonInfoViewModel
import kotlinx.coroutines.launch


class InfoFragment : Fragment(), ChapterAdapter.ChapterItemListener {
    private val infoArgs: InfoFragmentArgs by navArgs()
    private val infoViewModel: WebtoonInfoViewModel by viewModels()

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Do all observe stuff here
        (activity as MainActivity).supportActionBar?.title = infoArgs.name
        (activity as MainActivity).binding.appbar.setExpanded(true, true)
        val onLibraryClick = {
            if (infoViewModel.inLibrary) {
                infoViewModel.removeFromLibrary()
            } else {
                infoViewModel.addToLibrary()
            }
        }
        val onResumeClick: () -> Unit = {
            lifecycleScope.launch {
                val resumeCh = infoViewModel.getResumeChapter()
                val toReader = InfoFragmentDirections.actionInfoFragmentToReaderFragment(
                    infoArgs.internalName,
                    resumeCh?.internalChapterReference
                        ?: infoViewModel.getLastChapter().internalChapterReference
                )
                findNavController().navigate(toReader)
            }
        }
        val infoHeaderAdapter = InfoHeaderAdapter(onLibraryClick, onResumeClick)
        val chapterHeaderAdapter = ChapterHeaderAdapter()
        val chapterAdapter = ChapterAdapter(this)
        val mainAdapter = ConcatAdapter(infoHeaderAdapter, chapterHeaderAdapter, chapterAdapter)

        // Observing changes to read chapters, so we can refresh view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChapterClick(chapter: Chapter) {
        TODO("Not yet implemented")
    }

    override fun markSingle(position: Int) {
        TODO("Not yet implemented")
    }

    override fun markManyRead(position: Int) {
        TODO("Not yet implemented")
    }

    override fun markManyUnread(position: Int) {
        TODO("Not yet implemented")
    }
}