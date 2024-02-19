package com.uni.link.ui.callbacks

import android.view.View
import com.uni.link.data.models.PendingMeme

interface PendingMemesCallback {
    fun onPendingMemeClicked(view: View, meme: PendingMeme)
}