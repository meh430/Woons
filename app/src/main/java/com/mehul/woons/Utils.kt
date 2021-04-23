package com.mehul.woons

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.mehul.woons.entities.Resource
import kotlinx.coroutines.Dispatchers

fun <T> loadLocalData(localCall: suspend () -> LiveData<T>): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        val result = kotlin.runCatching { localCall().map { Resource.success(it) } }
        result.onSuccess {
            emitSource(it)
        }
        result.onFailure {
            emit(Resource.error(it.message, null))
        }
    }


fun <T> loadRemoteData(remoteCall: suspend () -> T): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        val result = kotlin.runCatching { remoteCall() }
        result.onSuccess {
            emit(Resource.success(it))
        }
        result.onFailure {
            emit(Resource.error(it.message, null))
        }
    }