package com.uni.link.data.wrappers

import com.uni.link.data.models.Meme
import io.reactivex.Observable

data class ObservableMeme(
        override val id: String,
        val meme: Observable<Meme>
): ItemViewModel {
}