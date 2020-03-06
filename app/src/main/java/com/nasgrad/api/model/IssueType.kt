package com.nasgrad.api.model

import androidx.annotation.Keep

@Keep
data class IssueType(
    val id: String,
    val name: String,
    val description: String,
    val categories: List<String>
)