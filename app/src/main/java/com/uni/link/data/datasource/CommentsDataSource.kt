package com.uni.link.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.uni.link.data.repositories.CommentsRepository
import com.uni.link.data.wrappers.ObservableComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class CommentsDataSource constructor(private val repository: CommentsRepository,
                                     private val scope: CoroutineScope,
                                     private val memeId: String,
                                     private val onEmptyAction: (Boolean) -> Unit) : ItemKeyedDataSource<String, ObservableComment>() {

    class Factory(private val repository: CommentsRepository,
                  private val scope: CoroutineScope,
                  private val memeId: String,
                  private val onEmptyAction: (Boolean) -> Unit) : DataSource.Factory<String, ObservableComment>() {

        override fun create(): DataSource<String, ObservableComment> {
            return CommentsDataSource(repository, scope, memeId, onEmptyAction)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<ObservableComment>) {
        Timber.e("Initialisation...")

        scope.launch {
            val comments = repository.fetchComments(memeId)
            onEmptyAction(comments.isEmpty())
            callback.onResult(comments)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<ObservableComment>) {

    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<ObservableComment>) {
//        scope.launch {
//            val memes = if (user == null) {
//                repository.fetchMemes(loadBefore = params.key)
//            } else {
//                repository.fetchMemesByUser(userId = user.userId!!, loadBefore = params.key)
//            }
//
//            callback.onResult(memes)
//        }
    }

    override fun getKey(item: ObservableComment): String = item.id
}