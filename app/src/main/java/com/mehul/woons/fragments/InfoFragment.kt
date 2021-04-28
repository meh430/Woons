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
import com.mehul.woons.entities.Resource
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.viewmodels.WebtoonInfoViewModel
import kotlinx.coroutines.launch
import timber.log.Timber


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
        binding.infoScroll.adapter = mainAdapter

        if (infoViewModel.webtoonInfo.value!!.status != Resource.Status.SUCCESS) {
            infoViewModel.initializeInfo(infoArgs.name, infoArgs.internalName)
        }

        // Observing changes to read chapters, so we can refresh all chapters
        infoViewModel.changedRead.observe(viewLifecycleOwner) {
            (activity as MainActivity).supportActionBar?.title = infoArgs.name
            (activity as MainActivity).binding.appbar.setExpanded(true, true)
            Timber.e("CHANGED READ")
            if (infoViewModel.webtoonInfo.value!!.status != Resource.Status.SUCCESS) {
                return@observe
            }
            chapterAdapter.updateChapterItems(ArrayList())
            infoViewModel.updateAllChapters()
        }
        infoViewModel.allChapters.observe(viewLifecycleOwner) {
            chapterAdapter.updateChapterItems(if (it.status == Resource.Status.SUCCESS) it.data!! else ArrayList())
        }
        infoViewModel.webtoonInfo.observe(viewLifecycleOwner) {
            infoHeaderAdapter.updateInfo(it)
            chapterHeaderAdapter.updateInfo(it)
        }
        infoViewModel.webtoonIdLive.observe(viewLifecycleOwner) {
            val inLibrary = it != LibraryRepository.NOT_IN_LIBRARY
            chapterAdapter.updateInLibrary(inLibrary)
            infoHeaderAdapter.updateInLibrary(inLibrary)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChapterClick(chapter: Chapter) {
        val toReader = InfoFragmentDirections.actionInfoFragmentToReaderFragment(
            infoArgs.internalName,
            chapter.internalChapterReference
        )
        findNavController().navigate(toReader)
    }

    override fun markSingle(chapter: Chapter, position: Int) {
        Timber.e(chapter.toString())
        if (chapter.hasRead) {
            infoViewModel.markSingleUnread(position)
        } else {
            infoViewModel.markSingleRead(position)
        }
    }

    override fun markManyRead(position: Int) = infoViewModel.markManyRead(position)

    override fun markManyUnread(position: Int) = infoViewModel.markManyUnread(position)
}