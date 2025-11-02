package com.mobiledev.swipedb.database.models

data class ImageCard(
    val id: Long = counter++,
    val imageId: String,
    val url: String
) {
    companion object {
        private var counter = 0L
    }
}
