package com.mobiledev.swipedb.unsplash.models

data class SearchImage (
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashImage>
)