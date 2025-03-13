package com.colman.mobilePostsApp.data.book

data class BookResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publishedDate: String?
)
