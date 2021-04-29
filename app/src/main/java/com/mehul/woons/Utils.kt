package com.mehul.woons

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.repositories.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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

// Should be called from main coroutine context since db calls are in IO context
// Add or remove webtoon from library depending on whether it is in db
suspend fun addOrRemoveFromLibrary(
    context: Context,
    libraryRepository: LibraryRepository,
    webtoon: Webtoon
) {

    val inLibrary = libraryRepository.webtoonWithNameExists(webtoon.name)
    if (inLibrary) {
        val deleteId = libraryRepository.getWebtoonIdFromName(webtoon.name)
        libraryRepository.deleteWebtoon(deleteId)
        Toast.makeText(context, "Removed ${webtoon.name} from library", Toast.LENGTH_SHORT).show()
    } else {
        libraryRepository.insertWebtoon(
            Webtoon(
                name = webtoon.name,
                internalName = webtoon.internalName,
                coverImage = webtoon.coverImage
            )
        )
        Toast.makeText(context, "Added ${webtoon.name} to library", Toast.LENGTH_SHORT).show()
    }
}

suspend fun getUpdatedAllChapters(
    chRep: LibraryRepository,
    inLibrary: Boolean,
    webtoonId: Long,
    ac: List<Chapter>
) =
    withContext(Dispatchers.Default) {
        val readChs = if (inLibrary) {
            chRep.getNonLiveReadChapters(webtoonId)
        } else {
            ArrayList()
        }

        //Timber.e(readChs.toString())
        // Create map with name as key and chapter as value
        val readNamesMap =
            readChs.map { it.chapterNumber to it }.toMap()
        val currAllChapters = ac.map { it.copy() }
        // Update whether has read and add ids to available ones
        for (ch in currAllChapters) {
            val hasRead = readNamesMap.containsKey(ch.chapterNumber)
            ch.hasRead = hasRead
            // If in map (read chapter), then update id for future reference (deletions)
            ch.id = if (hasRead) {
                readNamesMap[ch.chapterNumber]!!.id
            } else {
                0
            }
        }
        //Timber.e(currAllChapters.toString())
        currAllChapters
    }
