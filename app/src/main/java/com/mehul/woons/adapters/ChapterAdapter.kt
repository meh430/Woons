package com.mehul.woons.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mehul.woons.R
import com.mehul.woons.databinding.ChapterItemBinding
import com.mehul.woons.databinding.ChapterSheetBinding
import com.mehul.woons.entities.Chapter

class ChapterAdapter(val listener: ChapterItemListener) :
    RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private var chapterItems: List<Chapter> = ArrayList()
    var inLibrary = false

    fun updateChapterItems(items: List<Chapter>) {
        chapterItems = items
        notifyDataSetChanged()
    }

    fun updateInLibrary(inLib: Boolean) {
        inLibrary = inLib
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChapterAdapter.ChapterViewHolder = ChapterViewHolder(
        ChapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) =
        holder.bind(chapterItems[position], position)


    override fun getItemCount(): Int = chapterItems.size

    interface ChapterItemListener {
        fun onChapterClick(chapter: Chapter)
        fun markSingle(chapter: Chapter, position: Int)
        fun markManyRead(position: Int)
        fun markManyUnread(position: Int)
    }

    fun showSheet(context: Context, chapter: Chapter, position: Int) {
        val sheet = BottomSheetDialog(context)
        val sheetBinding = ChapterSheetBinding.inflate(LayoutInflater.from(context))
        sheet.setContentView(sheetBinding.root)
        sheetBinding.markSingle.text = if (chapter.hasRead) "Mark as unread" else "Mark as read"
        sheetBinding.markSingle.setOnClickListener {
            listener.markSingle(chapter, position)
            sheet.dismiss()
        }
        sheetBinding.markManyRead.setOnClickListener {
            listener.markManyRead(position)
            sheet.dismiss()
        }
        sheetBinding.markManyUnread.setOnClickListener {
            listener.markManyUnread(position)
            sheet.dismiss()
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
                if (inLibrary) {
                    showSheet(binding.root.context, chapter, position)
                }
                true
            }
        }
    }
}
