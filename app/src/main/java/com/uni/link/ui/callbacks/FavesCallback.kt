package com.uni.link.ui.callbacks

import com.uni.link.data.models.Fave
import com.makeramen.roundedimageview.RoundedImageView

interface FavesCallback {
    fun onFaveClick(view: RoundedImageView, meme: Fave, longClick: Boolean)
}