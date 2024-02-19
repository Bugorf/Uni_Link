package com.uni.link.ui.viewmodels

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.uni.link.R
import com.uni.link.data.models.User

class UsersViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {
            findViewById<TextView>(R.id.tv_count).isVisible = false
            findViewById<TextView>(R.id.tv_time).isVisible = false

            findViewById<TextView>(R.id.tv_title).text = user.userName
            findViewById<TextView>(R.id.tv_subtitle).text = user.userBio

            val ivUserImage = findViewById<ShapeableImageView>(R.id.iv_user_image)
//            Log.d("DP", user.thumbImage)
            Picasso.get()
                .load(user.userAvatar)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(ivUserImage)

            setOnClickListener {
                onClick.invoke(user.userName!!, user.userAvatar!!, user.userId!!)
            }
        }
}