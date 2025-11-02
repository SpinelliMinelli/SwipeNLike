package com.mobiledev.swipedb.unsplash.api

import com.mobiledev.swipedb.unsplash.models.SearchImage
import com.mobiledev.swipedb.unsplash.models.UnsplashImage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {

    @GET("search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): Observable<SearchImage>

    @GET("photos")
    fun getPhoto(
        @Query("id") id: String,
    ): Observable<UnsplashImage>
}
