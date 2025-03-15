package com.colman.mobilePostsApp.data.book

data class BookResponse(
    val docs: List<BookItem>?
)

data class BookItem(
    val title: String
)