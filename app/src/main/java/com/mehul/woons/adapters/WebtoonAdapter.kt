package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehul.woons.databinding.WebtoonItem2Binding
import com.mehul.woons.databinding.WebtoonItemBinding
import com.mehul.woons.entities.Webtoon

class WebtoonAdapter(val isGrid: Boolean, val onClick: (Webtoon) -> Unit) :
    RecyclerView.Adapter<WebtoonAdapter.WebtoonViewHolder>() {
    var webtoons: List<Webtoon> = ArrayList()

    fun updateWebtoons(l: List<Webtoon>) {
        webtoons = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebtoonViewHolder {
        return if (isGrid) {
            WebtoonViewHolder(
                WebtoonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                null
            )
        } else {
            WebtoonViewHolder(
                null,
                WebtoonItem2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: WebtoonViewHolder, position: Int) =
        holder.bind(webtoons[position])


    override fun getItemCount() = webtoons.size


    inner class WebtoonViewHolder(
        private val binding: WebtoonItemBinding?,
        private val binding2: WebtoonItem2Binding?
    ) :
        RecyclerView.ViewHolder(binding?.root ?: binding2!!.root) {

        fun bind(wt: Webtoon) {
            if (binding != null) {
                binding.root.setOnClickListener {
                    onClick(wt)
                }
                binding.title.text = wt.name
                Glide.with(binding.coverImage.context).load(wt.coverImage).centerCrop()
                    .into(binding.coverImage)
            } else {
                binding2!!.root.setOnClickListener {
                    onClick(wt)
                }

                binding2.title.text = if (wt.name.length >= 20) {
                    wt.name.slice(0..16) + "..."
                } else {
                    wt.name
                }
                Glide.with(binding2.coverImage.context).load(wt.coverImage).centerCrop()
                    .into(binding2.coverImage)
            }
        }
    }
}