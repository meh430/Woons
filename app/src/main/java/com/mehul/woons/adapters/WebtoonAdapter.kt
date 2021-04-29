package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehul.woons.databinding.WebtoonItem2Binding
import com.mehul.woons.databinding.WebtoonItemBinding
import com.mehul.woons.entities.Webtoon

class WebtoonAdapter(
    private val isGrid: Boolean,
    val onClick: (Webtoon) -> Unit,
    val onLongClick: (Webtoon) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var webtoons: List<Webtoon> = ArrayList()

    fun updateWebtoons(l: List<Webtoon>) {
        webtoons = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == WEBTOON_GRID) {
            WebtoonGridViewHolder(
                WebtoonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            WebtoonHorizontalViewHolder(
                WebtoonItem2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }


    override fun getItemViewType(position: Int) = if (isGrid) WEBTOON_GRID else WEBTOON_HORIZONTAL


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == WEBTOON_GRID) {
            (holder as WebtoonGridViewHolder).bind(webtoons[position])
        } else {
            (holder as WebtoonHorizontalViewHolder).bind(webtoons[position])
        }
    }

    override fun getItemCount() = webtoons.size

    inner class WebtoonGridViewHolder(private val binding: WebtoonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wt: Webtoon) {
            binding.root.setOnLongClickListener {
                onLongClick(wt)
                true
            }
            binding.root.setOnClickListener {
                onClick(wt)
            }
            binding.title.text = if (wt.name.length >= 20) {
                wt.name.slice(0..16) + "..."
            } else {
                wt.name
            }
            Glide.with(binding.coverImage.context).load(wt.coverImage).centerCrop()
                .into(binding.coverImage)
        }
    }

    inner class WebtoonHorizontalViewHolder(private val binding: WebtoonItem2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wt: Webtoon) {
            binding.root.setOnLongClickListener {
                onLongClick(wt)
                true
            }
            binding.root.setOnClickListener {
                onClick(wt)
            }
            binding.title.text = if (wt.name.length >= 20) {
                wt.name.slice(0..16) + "..."
            } else {
                wt.name
            }
            Glide.with(binding.coverImage.context).load(wt.coverImage).centerCrop()
                .into(binding.coverImage)
        }
    }


    companion object {
        const val WEBTOON_GRID = 0
        const val WEBTOON_HORIZONTAL = 1
    }
}