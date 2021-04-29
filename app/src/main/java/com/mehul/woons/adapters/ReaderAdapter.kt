package com.mehul.woons.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.mehul.woons.databinding.ReaderItemBinding


class ReaderAdapter : RecyclerView.Adapter<ReaderAdapter.ReaderViewHolder>() {
    var pages: List<String> = ArrayList()

    fun updatePages(p: List<String>) {
        pages = p
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReaderAdapter.ReaderViewHolder = ReaderViewHolder(
        ReaderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ReaderAdapter.ReaderViewHolder, position: Int) =
        holder.bind(pages[position])

    override fun getItemCount() = pages.size

    inner class ReaderViewHolder(val binding: ReaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pageUrl: String) {
            Glide.with(binding.root.context)
                .asBitmap()
                .load(pageUrl)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        binding.readerImage.setImage(ImageSource.bitmap(resource))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            //Glide.with(binding.readerImage.context).load(pageUrl).fitCenter()
            //   .into(binding.readerImage)
        }
    }
}