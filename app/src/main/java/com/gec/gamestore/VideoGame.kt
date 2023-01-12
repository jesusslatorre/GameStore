package com.gec.gamestore

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class VideoGame(
    val name: String? = null,
    val date: String? = null,
    val price: String? = null,
    val description: String? = null,
    val url: String? = null,
    @Exclude val key: String? = null
)