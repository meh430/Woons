package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.loadLocalData
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import javax.inject.Inject

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    val initialLoading: MutableLiveData<Boolean> = MutableLiveData()
    val libraryWebtoons: LiveData<Resource<List<Webtoon>>> =
        loadLocalData { libraryRepository.getLibraryWebtoons() }

    init {
        (application as WoonsApplication).appComponent.injectIntoLibrary(this)
        initialLoading.value = true
    }

    // For testing
    suspend fun populateTable() {
        val test = listOf(
            Webtoon(
                name = "The God of High School",
                internalName = "the-god-of-high-school",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/02/thumb_5e5a30621e9dd.jpg"
            ),
            Webtoon(
                name = "Tower of God",
                internalName = "tower-of-god",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e5ad223a5412.jpg"
            ),
            Webtoon(
                name = "Noblesse", internalName = "noblesse", coverImage =
                "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e61329ca4268.jpg"
            ),
            Webtoon(
                name = "The Gamer",
                internalName = "the-gamer",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e5b349450ef8.jpg"
            ),
            Webtoon(
                name = "Lady Baby",
                internalName = "lady-baby",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/01/thumb_5e0c84e82ad4d.jpg"
            ),
            Webtoon(
                numRead = 2,
                name = "Killing Stalking",
                internalName = "killing-stalking",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e690e8a765a9.jpg"
            ),
            Webtoon(
                numRead = 2,
                name = "Dimensional Mercenary",
                internalName = "dimensional-mercenary",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/01/Dimensional-Mercenary.jpg"
            ),
            Webtoon(
                name = "i am The Sorcerer King",
                internalName = "i-am-the-sorcerer-king",
                coverImage = "https://mangakomi.com/wp-content/uploads/2019/12/i-am-The-Sorcerer-King.jpg"
            ),
            Webtoon(
                name = "bj Alex",
                internalName = "bj-alex",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/bj-Alex.jpg"
            ),
            Webtoon(
                name = "Perfect Half",
                internalName = "perfect-half",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/02/thumb_5e58e4b783e5b.jpg"
            ),
            Webtoon(
                name = "The Scholar’s Reincarnation",
                internalName = "the-scholars-reincarnation",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e62f6c92b26c.jpg"
            ),
            Webtoon(
                name = "The Abandoned Empress",
                internalName = "the-abandoned-empress",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/04/thumb_5e92f96be2194.jpg"
            ),
            Webtoon(
                name = "Gosu",
                internalName = "gosu",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e65f0979083d.jpg"
            ),
            Webtoon(
                name = "The Legendary Moonlight Sculptor",
                internalName = "the-legendary-moonlight-sculptor",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e65a4a213048.jpg"
            ),
            Webtoon(
                name = "Lookism",
                internalName = "lookism",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e64ca586eb85.jpg"
            ),
            Webtoon(
                name = "Close as Neighbors",
                internalName = "close-as-neighbors",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e5b773f164a8.jpg"
            ),
            Webtoon(
                name = "Love is an Illusion",
                internalName = "love-is-an-illusion",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/03/thumb_5e79b1209f09a.jpg"
            ),
            Webtoon(
                name = "Sweet Guy",
                internalName = "sweet-guy",
                coverImage = "https://mangakomi.com/wp-content/uploads/2020/04/thumb_5e8ed2809d7f1.jpg"
            )
        )

        test.forEach {
            libraryRepository.insertWebtoon(it)
        }
    }
}