package com.sandystudios.talky.models

import android.content.Context
import com.uni.link.utils.formatAsHeader
import java.util.*

/**
 *   Définir struacture de Chat
 **/

interface ChatEvent {
    val sentAt: Date
}

data class Message(
    val msg: String,
    val senderId: String,
    val msgId: String,
    val type: String = "TEXT",
    val status: Int = 1,
    val liked: Boolean = false,
    override val sentAt: Date = Date()
) : ChatEvent {

    /** Empty [Constructor] for Firebase */
    constructor() : this("", "", "", "", 1, false, Date(0L))
}

data class DateHeader(override val sentAt: Date, val context: Context) : ChatEvent {
    val date: String = sentAt.formatAsHeader(context)
}