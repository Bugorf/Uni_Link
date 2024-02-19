package com.uni.link.ui.callbacks

import android.view.View
import com.uni.link.data.models.Comment

interface CommentsCallback {
    fun onCommentClicked(view: View, comment: Comment, longClick: Boolean)
}