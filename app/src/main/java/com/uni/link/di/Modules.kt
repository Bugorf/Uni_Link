package com.uni.link.di

import com.uni.link.data.repositories.*
import com.uni.link.ui.viewmodels.*
import com.uni.link.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *  Initialisation
 **/


val firebaseModule = module {
    single { FirebaseDatabase.getInstance().reference }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance().reference }
    single { FirebaseAuth.getInstance() }
}

val repositoriesModule = module {
    single { UsersRepository(get(), get(), get()) }
    single { MemesRepository(get(), get()) }
    single { NotificationsRepository(get()) }
    single { ReportsRepository(get()) }
    single { CommentsRepository(get()) }
}

val viewModelsModule = module {
    viewModel { UsersViewModel(get()) }
    viewModel { MemesViewModel(get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { ReportsViewModel(get()) }
    viewModel { CommentsViewModel(get()) }
}

val sessionManagerModule = module {
    single { SessionManager(androidApplication()) }
}
