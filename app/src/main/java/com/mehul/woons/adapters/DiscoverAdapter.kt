package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.Constants
import com.mehul.woons.databinding.DiscoverItemBinding
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonsPage

// onWebtoonClick will be used to navigate to info fragment
// longClick will be used to add or remove from library
// onDiscoverClick will be used to navigate to the browse fragment
class DiscoverAdapter(val listener: DiscoverAdapter.DiscoverListener) :
    RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder>() {

    private var discoverItems: List<WebtoonsPage> = ArrayList()

    fun updateDiscoverItems(l: List<WebtoonsPage>) {
        discoverItems = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DiscoverViewHolder(
        DiscoverItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) =
        holder.bind(discoverItems[position])


    override fun getItemCount() = discoverItems.size

    interface DiscoverListener {
        fun onWebtoonClick(webtoon: Webtoon)
        fun onWebtoonLongClick(webtoon: Webtoon)
        fun onDiscoverClick(category: String)
    }

    inner class DiscoverViewHolder(val binding: DiscoverItemBinding) :
        RecyclerView.ViewHolder(binding.root), WebtoonAdapter.WebtoonItemListener {

        fun bind(discoverItem: WebtoonsPage) {
            binding.categoryTitleBar.setOnClickListener {
                listener.onDiscoverClick(discoverItem.category)
            }

            binding.category.text = Constants.getDisplayCategory(discoverItem.category)
            val webtoonAdapter =
                WebtoonAdapter(false, this)
            binding.categoryItems.adapter = webtoonAdapter
            webtoonAdapter.updateWebtoons(discoverItem.items)
        }

        override fun onClick(webtoon: Webtoon) = listener.onWebtoonClick(webtoon)
        override fun onLongClick(webtoon: Webtoon) = listener.onWebtoonLongClick(webtoon)
    }
}