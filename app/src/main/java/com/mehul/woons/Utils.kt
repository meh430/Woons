package com.mehul.woons

import android.content.Context
import android.util.DisplayMetrics
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun calculateNoOfColumns(
    context: Context,
    columnWidthDp: Float
): Int { // For example columnWidthdp=180
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / columnWidthDp + 0.5).toInt()
}