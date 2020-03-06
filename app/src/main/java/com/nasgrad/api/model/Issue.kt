package com.nasgrad.api.model

import androidx.annotation.Keep

@Keep
data class Issue(
    val id: String,
    val ownerId: String,
    var title: String?,
    var description: String?,
    var issueType: String?,
    var categories: List<String>?,
    var location: Location?,
    var state: String?,
    var picturePreview: String?,
    val submittedCount: Int?,
    var address: String?
)
