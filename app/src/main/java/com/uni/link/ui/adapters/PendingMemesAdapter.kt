package com.uni.link.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.uni.link.R
import com.uni.link.data.models.PendingMeme
import com.uni.link.databinding.ItemPendingMemeBinding
import com.uni.link.ui.callbacks.PendingMemesCallback
import com.uni.link.utils.AppUtils
import com.uni.link.utils.inflate
import com.mikepenz.ionicons_typeface_library.Ionicons

class PendingMemesAdapter(private val callback: PendingMemesCallback): PagedListAdapter<PendingMeme,
        PendingMemesAdapter.MemeHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PendingMeme>() {
            override fun areItemsTheSame(oldItem: PendingMeme, newItem: PendingMeme): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PendingMeme, newItem: PendingMeme): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeHolder {
        return MemeHolder(parent.inflate(R.layout.item_pending_meme), callback)
    }

    override fun onBindViewHolder(holder: MemeHolder, position: Int) {
        val meme = getItem(position)
        holder.bind(meme!!)
    }

    inner class MemeHolder(private val binding: ItemPendingMemeBinding, private val callback: PendingMemesCallback):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(meme: PendingMeme) {
            binding.meme = meme
            binding.callback = callback

            binding.delete.setImageDrawable(AppUtils.getDrawable(binding.root.context, Ionicons.Icon.ion_android_delete, R.color.color_text_secondary, 22))
        }
    }
}