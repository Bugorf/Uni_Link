package com.uni.link.data.wrappers

import com.uni.link.data.models.Comment
import io.reactivex.Observable

data class ObservableComment(
        val id: String,
        val comment: Observable<Comment>
) {}