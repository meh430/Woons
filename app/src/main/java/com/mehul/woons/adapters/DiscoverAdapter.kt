package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.Constants
import com.mehul.woons.databinding.DiscoverItemBinding
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonsPage

// onWebtoonClick will be used to navigate to info fragment
// onDiscoverClick will be used to navigate to the browse fragment
class DiscoverAdapter(
    val onWebtoonClick: (Webtoon) -> Unit,
    val onWebtoonLongClick: (Webtoon) -> Unit,
    val onDiscoverClick: (String) -> Unit
) :
    RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder>() {

    var discoverItems: List<WebtoonsPage> = ArrayList()

    fun updateDiscoverItems(l: List<WebtoonsPage>) {
        discoverItems = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverViewHolder {
        val binding =
            DiscoverItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) =
        holder.bind(discoverItems[position])


    override fun getItemCount() = discoverItems.size

    inner class DiscoverViewHolder(val binding: DiscoverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(discoverItem: WebtoonsPage) {
            binding.categoryTitleBar.setOnClickListener {
                onDiscoverClick(discoverItem.category)
            }
            binding.category.text = Constants.getDisplayCategory(discoverItem.category)
            val webtoonAdapter = WebtoonAdapter(false, onWebtoonClick, onWebtoonLongClick)
            binding.categoryItems.adapter = webtoonAdapter
            webtoonAdapter.updateWebtoons(discoverItem.items)

        }
    }
}