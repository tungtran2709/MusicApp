package com.example.musicapp.data

interface OnDataLocalCallback<T> {
    fun onSucceed(data: T?)
    fun onFailed(e: Exception?)
}
