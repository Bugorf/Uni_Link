package com.uni.link.data.models

import com.uni.link.utils.Constants
import java.io.Serializable

/**
 *  Définir le structure des utilisateurs
 **/

data class User(
        var userId: String? = null,
        var userName: String? = null,
        var userToken: String? = null,
        var userAvatar: String? = null,
        var userEmail: String? = null,
        var dateCreated: String? = null,
        var dateUpdated: String? = null,
        var userBio: String? = null,
        var admin: Int? = 0,
        var posts: Int? = 0,
        var muted: Boolean = false,
        var followers: MutableMap<String, Boolean> = mutableMapOf(),
        var following: MutableMap<String, Boolean> = mutableMapOf()
): Serializable {
    val hasAdminStatus: Boolean
        get() = admin == Constants.ADMIN || admin == Constants.SUPER_ADMIN
}