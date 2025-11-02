package com.mobiledev.swipedb.viewModels

import android.app.Application
import android.content.ContentValues
import android.provider.BaseColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobiledev.swipedb.unsplash.api.APIClient
import com.mobiledev.swipedb.unsplash.api.APIInterface
import com.mobiledev.swipedb.database.DbContract
import com.mobiledev.swipedb.database.DbHelper
import com.mobiledev.swipedb.database.models.ImageCard
import com.mobiledev.swipedb.unsplash.models.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val imageList = ArrayList<UnsplashImage>()
    private val liveImages = MutableLiveData<List<UnsplashImage>>()
    private var dbHelper: DbHelper = DbHelper(application.applicationContext)
    private val compositeDisposable = CompositeDisposable()

    val liveData: LiveData<List<UnsplashImage>> get() = liveImages

    fun searchImages(query: String) {
        val api: APIInterface = APIClient.getClient().create(APIInterface::class.java)
        val observable: Observable<SearchImage> = api.searchPhotos(query, 1, 20)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        compositeDisposable.add(observable.subscribe(
            { images ->
                imageList.clear()
                imageList.addAll(images.results)
                liveImages.postValue(imageList)
            },
            { error ->
                error.printStackTrace() // Logs error for debugging
                liveImages.postValue(emptyList()) // Ensures UI updates properly even on failure
            }
        ))
    }

    fun addLike(imageCard: ImageCard) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbContract.Entry.COLUMN_NAME_IMAGE_ID, imageCard.imageId)
            put(DbContract.Entry.COLUMN_NAME_IMAGE_URL, imageCard.url)
        }

        db.insert(DbContract.Entry.TABLE_NAME, null, values)
    }

    fun getLikes(): MutableList<ImageCard> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DbContract.Entry.COLUMN_NAME_IMAGE_ID,
            DbContract.Entry.COLUMN_NAME_IMAGE_URL
        )

        val sortOrder = "${BaseColumns._ID} DESC"
        val cursor = db.query(
            DbContract.Entry.TABLE_NAME,
            projection,
            null, null,
            null, null,
            sortOrder
        )

        val likes = mutableListOf<ImageCard>()
        with(cursor) {
            while (moveToNext()) {
                likes.add(
                    ImageCard(
                        imageId = getString(getColumnIndexOrThrow(DbContract.Entry.COLUMN_NAME_IMAGE_ID)),
                        url = getString(getColumnIndexOrThrow(DbContract.Entry.COLUMN_NAME_IMAGE_URL))
                    )
                )
            }
        }
        cursor.close()

        return likes
    }
}
