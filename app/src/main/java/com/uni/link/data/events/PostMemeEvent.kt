package com.uni.link.data.events

/**
 *  Enregistrement du nouveau meme et meme de favorite
 **/
data class PostMemeEvent(
        val type: TYPE
) {
    enum class TYPE {
        NEW_POST,
        FAVORITE
    }
}