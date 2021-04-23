package com.mehul.woons.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

// All the ignored fields are pulled in with an api call
// Should update all the fields on startup in vm
// Update reading counts in repo with dao sql query!
// In libraryvm, list of webtoons with transform for numRead and numChapters checks
// keep count in infovm? update using dao, no need to pull whole object
@Entity(tableName = "library")
data class Webtoon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var internalName: String?,
    var coverImage: String,
    @Ignore var summary: String?,
    @Ignore var rating: String?,
    @Ignore var author: String?,
    @Ignore var artist: String?,
    var numChapters: Int = 0,
    var numRead: Int = 0
)