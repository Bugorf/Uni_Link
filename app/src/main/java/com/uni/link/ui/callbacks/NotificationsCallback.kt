package com.uni.link.ui.callbacks

import android.view.View
import com.uni.link.data.models.Notification

interface NotificationsCallback {
    fun onNotificationClicked(view: View, notification: Notification)
}