package com.uni.link.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.uni.link.data.Status
import com.uni.link.data.repositories.MemesRepository
import com.uni.link.data.wrappers.ItemViewModel
import com.uni.link.data.wrappers.ObservableUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MemesDataSource constructor(private val repository: MemesRepository,
                                  private val scope: CoroutineScope,
                                  private val user: ObservableUser? = null,
                                  private val status: (Status) -> Unit): ItemKeyedDataSource<String, ItemViewModel>() {

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val user: ObservableUser? = null,
                  private val status: (Status) -> Unit): DataSource.Factory<String, ItemViewModel>() {

        override fun create(): DataSource<String, ItemViewModel> {
            return MemesDataSource(repository, scope, user, status)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<ItemViewModel>) {
        Timber.e("initialisation...")
        status(Status.LOADING)

        scope.launch {
            if (user == null) {
                val memes = repository.fetchMemes()

                status(if (memes.isEmpty()) Status.ERROR else Status.SUCCESS)
                callback.onResult(memes)
            } else {
                val memes = repository.fetchMemesByUser(user.id)
                memes.add(0, user)

                if (memes.size == 1) status(Status.ERROR) else status (Status.SUCCESS)
                callback.onResult(memes)
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {
        if (params.key != user?.id) {
            scope.launch {
                if (user == null) {
                    val memes =  repository.fetchMemes(loadAfter = params.key)
                    callback.onResult(memes)
                } else {
                    val memes = repository.fetchMemesByUser(userId = user.id, loadAfter = params.key)
                    callback.onResult(memes)
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {}

    override fun getKey(item: ItemViewModel): String = item.id

}