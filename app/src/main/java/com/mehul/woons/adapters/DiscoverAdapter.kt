package com.mehul.woons.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mehul.woons.databinding.DiscoverItemBinding
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonsPage

// onWebtoonClick will be used to navigate to info fragment
// onDiscoverClick will be used to navigate to the browse fragment
class DiscoverAdapter(
    val onWebtoonClick: (Webtoon) -> Unit,
    val onDiscoverClick: (String) -> Unit
) :
    RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder>() {

    var discoverItems: List<Resource<WebtoonsPage>> = ArrayList()

    fun updateDiscoverItems(l: List<Resource<WebtoonsPage>>) {
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

        fun bind(discoverItem: Resource<WebtoonsPage>) {
            when (discoverItem.status) {
                Resource.Status.SUCCESS -> {
                    binding.discoverItemLayout.visibility = View.VISIBLE
                    binding.loadingCategory.visibility = View.GONE
                    binding.error.error.visibility = View.GONE

                    binding.categoryTitleBar.setOnClickListener {
                        onDiscoverClick(discoverItem.data!!.category)
                    }

                    binding.category.text = discoverItem.data!!.category

                    binding.categoryItems.visibility = View.VISIBLE
                    val webtoonAdapter = WebtoonAdapter(false) {
                        onWebtoonClick(it)
                    }
                    binding.categoryItems.adapter = webtoonAdapter
                    webtoonAdapter.updateWebtoons(discoverItem.data.items)
                }
                Resource.Status.LOADING -> {
                    binding.discoverItemLayout.visibility = View.VISIBLE
                    binding.categoryItems.visibility = View.GONE
                    binding.error.error.visibility = View.GONE
                    binding.loadingCategory.visibility = View.VISIBLE

                    binding.category.text = discoverItem.data!!.category
                }
                Resource.Status.ERROR -> {
                    binding.discoverItemLayout.visibility = View.GONE
                    binding.error.error.visibility = View.VISIBLE
                    binding.error.errorLabel.text = discoverItem.message!!
                }
            }
        }
    }
}