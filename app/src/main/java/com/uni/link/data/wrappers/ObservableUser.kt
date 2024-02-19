package com.uni.link.data.wrappers

import com.uni.link.data.models.User
import io.reactivex.Observable

data class ObservableUser (
        override val id: String,
        val user: Observable<User>
): ItemViewModel {}