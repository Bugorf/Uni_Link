package com.uni.link.ui.viewmodels

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.uni.link.R
import com.uni.link.data.models.Inbox
import com.uni.link.utils.formatAsListItem

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {
            findViewById<TextView>(R.id.tv_count).apply {
                isVisible = item.count > 0
                text = item.count.toString()
            }

            findViewById<TextView>(R.id.tv_time).text = item.time.formatAsListItem(context)
            findViewById<TextView>(R.id.tv_title).text = item.name
            findViewById<TextView>(R.id.tv_subtitle).text = item.msg
            val ivUserImage = findViewById<ShapeableImageView>(R.id.iv_user_image)
            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(ivUserImage)
            setOnClickListener {
                onClick.invoke(item.name, item.image, item.from)
            }
        }
}