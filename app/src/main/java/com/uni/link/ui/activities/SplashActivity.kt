package com.uni.link.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.uni.link.utils.AppUtils
import com.uni.link.utils.SessionManager
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isFirstLaunch()) {
                launch(LoginActivity::class.java)
            } else {
                when (sessionManager.isLoggedIn()) {
                    true -> launch(MainActivity::class.java)
                    else -> launch(LoginActivity::class.java)
                }
            }
        }, 1500)
    }

    private fun launch(activity: Class<*>)  {
        startActivity(Intent(this, activity))
        AppUtils.slideRight(this)
        finish()
    }
}
