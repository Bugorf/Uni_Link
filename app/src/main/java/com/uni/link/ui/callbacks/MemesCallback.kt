package com.uni.link.ui.callbacks

import android.view.View
import com.uni.link.data.models.Meme
import com.uni.link.data.models.User

interface MemesCallback {
    fun onMemeClicked(view: View, meme: Meme)
    fun onProfileClicked(view: View, user: User)
}