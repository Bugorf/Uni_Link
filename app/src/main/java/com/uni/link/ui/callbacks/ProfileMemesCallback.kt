package com.uni.link.ui.callbacks

import android.view.View
import com.uni.link.data.models.Meme
import com.uni.link.data.models.User

interface ProfileMemesCallback : MemesCallback {
    fun onMemeLongClicked(meme: Meme)

    override fun onMemeClicked(view: View, meme: Meme)

    override fun onProfileClicked(view: View, user: User)
}