package com.github.siela1915.bootcamp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoredActivity (
    @PrimaryKey
    val activity: String,
    val type: String,
    val participants: Int,
    val price: Int,
    val link: String,
    val key: String,
    val accessibility: Double,
)
