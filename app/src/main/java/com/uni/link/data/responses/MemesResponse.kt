package com.uni.link.data.responses

import com.uni.link.data.Status
import com.uni.link.data.wrappers.ObservableMeme

data class MemesResponse(
        val status: Status,
        val data: ObservableMeme?,
        val error: String?
) {
    companion object {
        fun loading(): MemesResponse = MemesResponse(Status.LOADING, null, null)

        fun success(meme: ObservableMeme): MemesResponse
                = MemesResponse(Status.SUCCESS, meme, null)

        fun error(error: String): MemesResponse = MemesResponse(Status.ERROR, null, error)
    }
}