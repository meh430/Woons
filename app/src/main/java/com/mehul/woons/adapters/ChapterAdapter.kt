package com.mehul.woons.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mehul.woons.R
import com.mehul.woons.databinding.ChapterItemBinding
import com.mehul.woons.databinding.ChapterLoadingBinding
import com.mehul.woons.databinding.ChapterSheetBinding
import com.mehul.woons.databinding.ErrorBinding
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import timber.log.Timber

class ChapterAdapter(val listener: ChapterAdapter.ChapterItemListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chapterItems: Resource<List<Chapter>> = Resource.loading()

    fun updateChapterItems(items: Resource<List<Chapter>>) {
        chapterItems = items
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when (chapterItems.status) {
        Resource.Status.SUCCESS -> {
            ChapterViewHolder(
                ChapterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        Resource.Status.LOADING -> {
            ChapterLoadingViewHolder(
                ChapterLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        Resource.Status.ERROR -> {
            ChapterErrorViewHolder(
                ErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (chapterItems.status) {
            Resource.Status.SUCCESS -> {
                (holder as ChapterViewHolder).bind(chapterItems.data!![position], position)
            }
            Resource.Status.LOADING -> {
                (holder as ChapterLoadingViewHolder).binding.chapterLoading.visibility =
                    View.VISIBLE
            }
            Resource.Status.ERROR -> {
                (holder as ChapterErrorViewHolder).bind(chapterItems.message!!)
            }
        }

    override fun getItemCount(): Int = when (chapterItems.status) {
        Resource.Status.SUCCESS -> chapterItems.data!!.size
        Resource.Status.LOADING, Resource.Status.ERROR -> 1
    }

    interface ChapterItemListener {
        fun onChapterClick(chapter: Chapter)
        fun markSingle(position: Int)
        fun markManyRead(position: Int)
        fun markManyUnread(position: Int)
    }

    fun showSheet(context: Context, chapter: Chapter, position: Int) {
        val sheet = BottomSheetDialog(context)
        val binding = ChapterSheetBinding.inflate(LayoutInflater.from(context))
        sheet.setContentView(binding.root)
        binding.markSingle.text = if (chapter.hasRead) "Mark as unread" else "Mark as read"
        binding.markSingle.setOnClickListener {
            listener.markSingle(position)
        }
        binding.markManyRead.setOnClickListener {
            listener.markManyRead(position)
        }
        binding.markManyUnread.setOnClickListener {
            listener.markManyUnread(position)
        }
        sheet.show()
    }

    inner class ChapterViewHolder(val binding: ChapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapter: Chapter, position: Int) {
            // change color to indicate whether read or not
            val itemTextColor = ContextCompat.getColor(
                binding.root.context,
                if (chapter.hasRead) android.R.color.darker_gray else R.color.textColor
            )

            // bind text
            binding.apply {
                chapterName.setTextColor(itemTextColor)
                chapterName.text = chapter.chapterNumber
                uploadDate.setTextColor(itemTextColor)
                uploadDate.text = chapter.uploadDate
            }

            binding.root.setOnClickListener {
                listener.onChapterClick(chapter)
            }

            // open up bottom sheet with options on marking chapters as read
            binding.root.setOnLongClickListener {
                Timber.e("LONG CLICK")
                showSheet(binding.root.context, chapter, position)
                true
            }
        }
    }

    inner class ChapterLoadingViewHolder(val binding: ChapterLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ChapterErrorViewHolder(val binding: ErrorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.error.visibility = View.VISIBLE
            binding.errorLabel.text = message
        }
    }
}
