package com.uni.link.data.responses

import com.uni.link.data.Status
import com.uni.link.data.models.User

data class UserResponse (
        val status: Status,
        val user: User?,
        val error: String?
) {
    companion object {
        fun loading(): UserResponse = UserResponse(Status.LOADING, null, null)

        fun success(user: User): UserResponse
                = UserResponse(Status.SUCCESS, user, null)

        fun error(error: String): UserResponse = UserResponse(Status.ERROR, null, error)
    }
}