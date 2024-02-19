package com.uni.link.data.responses

import com.uni.link.data.Status
import com.uni.link.data.models.Comment

data class CommentsResponse(
        val status: Status,
        val data: List<Comment>?,
        val error: String?
) {
    companion object {
        fun loading(): CommentsResponse = CommentsResponse(Status.LOADING, null, null)

        fun success(comments: List<Comment>): CommentsResponse
                = CommentsResponse(Status.SUCCESS, comments, null)

        fun error(error: String): CommentsResponse = CommentsResponse(Status.ERROR, null, error)
    }
}