package com.uni.link.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.uni.link.data.Status
import com.uni.link.data.models.Fave
import com.uni.link.data.repositories.MemesRepository
import com.uni.link.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class FavesDataSource constructor(private val repository: MemesRepository,
                                  private val scope: CoroutineScope,
                                  private val status: (Status) -> Unit): ItemKeyedDataSource<String, Fave>(), KoinComponent {

    private val sessionManager: SessionManager by inject()
    private val userId = sessionManager.getUserId()

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val status: (Status) -> Unit): DataSource.Factory<String, Fave>() {
        override fun create(): DataSource<String, Fave> {
            return FavesDataSource(repository, scope, status)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Fave>) {
        Timber.e("Initialisation...")

        scope.launch {
            val memes = repository.fetchFaves(userId)

            if (memes.isEmpty()) status(Status.ERROR) else status (Status.SUCCESS)
            callback.onResult(memes)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Fave>) {
        scope.launch {
            val memes = repository.fetchFaves(userId = userId, loadAfter = params.key)
            Timber.e("Faves fetched: ${memes.size}")
            callback.onResult(memes)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Fave>) {
        scope.launch {
            val memes = repository.fetchFaves(userId = userId, loadBefore = params.key)
            Timber.e("Faves fetched: ${memes.size}")
            callback.onResult(memes)
        }
    }

    override fun getKey(item: Fave): String = item.id!!
}