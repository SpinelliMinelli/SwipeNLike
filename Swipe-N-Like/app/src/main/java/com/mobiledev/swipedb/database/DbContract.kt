package com.mobiledev.swipedb.database

import android.provider.BaseColumns

object DbContract {
    object Entry : BaseColumns {
        const val TABLE_NAME = "LikedImages"
        const val COLUMN_NAME_IMAGE_ID = "Id"
        const val COLUMN_NAME_IMAGE_URL = "Url"
    }
}