package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehul.woons.databinding.InfoHeaderBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonChapters

class InfoHeaderAdapter(val listener: InfoHeaderListener) :
    RecyclerView.Adapter<InfoHeaderAdapter.InfoViewHolder>() {
    private var webtoonInfo: Resource<WebtoonChapters> = Resource.loading()
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

    interface InfoHeaderListener {
        fun onLibraryClick()
        fun onResumeClick()
    }

    inner class InfoViewHolder(val binding: InfoHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun setNotEmpty(original: String, replace: String) = if (original.isEmpty()) {
            "No $replace found!"
        } else {
            original
        }

        fun bind(info: Resource<WebtoonChapters>) {
            when (info.status) {
                Resource.Status.SUCCESS -> {
                    hideAll()
                    binding.infoHeaderContent.visibility = View.VISIBLE
                    val currWebtoon = info.data!!.webtoon
                    // Set all the text
                    binding.apply {
                        webtoonTitle.text = setNotEmpty(currWebtoon.name, "title")
                        author.text = setNotEmpty(currWebtoon.author, "author")
                        artist.text = setNotEmpty(currWebtoon.artist, "artist")
                        rating.text = if (currWebtoon.rating.isNotEmpty()) {
                            currWebtoon.rating + "/5"
                        } else {
                            setNotEmpty("", "rating")
                        }
                        summary.text = setNotEmpty(currWebtoon.summary, "summary")
                    }

                    Glide.with(binding.coverImage.context).load(info.data.webtoon.coverImage)
                        .centerCrop()
                        .into(binding.coverImage)

                    // change button text
                    val libraryButtonText = if (inLibrary) {
                        "Remove from library"
                    } else {
                        "Add to library"
                    }
                    binding.libraryButton.text = libraryButtonText

                    // add listeners to the 2 buttons
                    binding.libraryButton.setOnClickListener {
                        listener.onLibraryClick()
                    }
                    binding.resumeButton.setOnClickListener {
                        listener.onResumeClick()
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

        private fun hideAll() {
            binding.error.error.visibility = View.GONE
            binding.infoHeaderLoading.visibility = View.GONE
            binding.infoHeaderContent.visibility = View.GONE
        }
    }
}