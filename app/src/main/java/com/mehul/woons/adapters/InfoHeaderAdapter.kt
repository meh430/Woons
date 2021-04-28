package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.databinding.InfoHeaderBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonChapters

class InfoHeaderAdapter(val onLibraryClick: () -> Unit, val onResumeClick: () -> Unit) :
    RecyclerView.Adapter<InfoHeaderAdapter.InfoViewHolder>() {
    var webtoonInfo: Resource<WebtoonChapters> = Resource.loading()
    var inLibrary = false

    fun updateInfo(info: Resource<WebtoonChapters>) {
        webtoonInfo = info
        notifyItemChanged(0)
    }

    fun updateInLibrary(inLib: Boolean) {
        inLibrary = inLib
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder =
        InfoViewHolder(
            InfoHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) = holder.bind(webtoonInfo)


    override fun getItemCount(): Int = 1

    inner class InfoViewHolder(val binding: InfoHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: Resource<WebtoonChapters>) {
            when (info.status) {
                Resource.Status.SUCCESS -> {
                    hideAll()
                    binding.infoHeaderContent.visibility = View.VISIBLE
                    val currWebtoon = info.data!!.webtoon
                    // Set all the text
                    binding.apply {
                        webtoonTitle.text = currWebtoon.name
                        author.text = currWebtoon.author
                        artist.text = currWebtoon.artist
                        rating.text = currWebtoon.rating
                        summary.text = currWebtoon.summary
                    }

                    // change button text
                    val libraryButtonText = if (inLibrary) {
                        "Remove from library"
                    } else {
                        "Add to library'"
                    }
                    binding.libraryButton.text = libraryButtonText

                    // add listeners to the 2 buttons
                    binding.libraryButton.setOnClickListener {
                        onLibraryClick()
                    }
                    binding.resumeButton.setOnClickListener {
                        onResumeClick()
                    }
                }
                Resource.Status.LOADING -> {
                    hideAll()
                    binding.infoHeaderLoading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    hideAll()
                    binding.error.error.visibility = View.VISIBLE
                    binding.error.errorLabel.text = info.message!!
                }
            }
        }

        fun hideAll() {
            binding.error.error.visibility = View.GONE
            binding.infoHeaderLoading.visibility = View.GONE
            binding.infoHeaderContent.visibility = View.GONE
        }
    }
}