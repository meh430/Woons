package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehul.woons.databinding.WebtoonItemBinding
import com.mehul.woons.entities.Webtoon

class WebtoonAdapter(val onClick: (Webtoon) -> Unit) :
    RecyclerView.Adapter<WebtoonAdapter.WebtoonViewHolder>() {
    var webtoons: List<Webtoon> = ArrayList<Webtoon>()

    fun updateWebtoons(l: List<Webtoon>) {
        webtoons = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebtoonViewHolder {
        val binding = WebtoonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WebtoonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WebtoonViewHolder, position: Int) {
        holder.bind(webtoons[position])
    }

    override fun getItemCount(): Int {
        return webtoons.size
    }

    inner class WebtoonViewHolder(val binding: WebtoonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wt: Webtoon) {
            binding.root.setOnClickListener {
                onClick(wt)
            }
            binding.title.text = wt.name
            Glide.with(binding.coverImage.context).load(wt.coverImage).centerCrop()
                .into(binding.coverImage)

        }
    }
}