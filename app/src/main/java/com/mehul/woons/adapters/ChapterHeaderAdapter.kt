package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.databinding.ChaptersHeaderBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonChapters

class ChapterHeaderAdapter : RecyclerView.Adapter<ChapterHeaderAdapter.ChapterHeaderViewHolder>() {
    var webtoonInfo: Resource<WebtoonChapters> = Resource.loading()

    fun updateInfo(info: Resource<WebtoonChapters>) {
        webtoonInfo = info
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChapterHeaderAdapter.ChapterHeaderViewHolder = ChapterHeaderViewHolder(
        ChaptersHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(
        holder: ChapterHeaderAdapter.ChapterHeaderViewHolder,
        position: Int
    ) {
        holder.bind(webtoonInfo)
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = 1

    inner class ChapterHeaderViewHolder(val binding: ChaptersHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: Resource<WebtoonChapters>) {
            binding.numChapters.text = when (info.status) {
                Resource.Status.SUCCESS -> "${info.data!!.webtoon.numChapters} Chapters"
                Resource.Status.LOADING -> "Loading..."
                Resource.Status.ERROR -> "Error loading"
            }
        }
    }
}