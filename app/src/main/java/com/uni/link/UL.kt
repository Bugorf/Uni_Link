package com.uni.link

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.uni.link.di.*
import com.uni.link.utils.Constants
import com.uni.link.utils.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 *  Activité pour initialiser tout les paramètres de l'application
 *  et bibliothèques utilisées
 */

class UL : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        // Initialiser theme
        updateTheme(this)

        // Initialiser application
        if (FirebaseApp.getApps(this).isEmpty())
            FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        CaocConfig.Builder.create()
                .enabled(true)
                .showErrorDetails(false)
                .errorDrawable(com.uni.link.R.mipmap.ic_launcher)
                .apply()

        // Initialiser Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        com.uni.link.UL.Companion.updateNotificationToken(this)

        // Initialisation
        startKoin {
            androidLogger()
            androidContext(this@UL)
            modules(listOf(
                    firebaseModule,
                    repositoriesModule,
                    viewModelsModule,
                    sessionManagerModule
            ))
        }

        RxJavaPlugins.setErrorHandler {
            Timber.e("RxJava error: ${it.localizedMessage}")
        }

    }

    companion object {
        @JvmStatic
        fun updateNotificationToken(context: Context) {
            val sessionManager = SessionManager(context)

            if (sessionManager.isLoggedIn() && !sessionManager.isFirstLaunch()) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.e("Error initiating new token")
                        return@addOnCompleteListener
                    }

                    // Obtenir token id
                    if (sessionManager.getUserId().isNotEmpty()) {
                        val dbRef = FirebaseFirestore.getInstance()
                        dbRef.collection(Constants.USERS).document(sessionManager.getUserId()).update(Constants.USER_TOKEN, task.result)
                    }
                }
            }
        }

        /**
         *  Mettre à jour le thème actuel
         */
        fun updateTheme(context: Context) {
            AppCompatDelegate.setDefaultNightMode(SessionManager(context).themeMode())
        }
    }
}