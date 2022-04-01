package com.example.floatplayer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class MyContentProvider: ContentProvider() {

    val SP_NAME = "spFloatTest"
    private var spEditor: SharedPreferences.Editor? = null
    private val authority  = "com.example.floatplayer"
    private val path = "xmlData"
    private val URI_XML = Uri.parse("content://".plus(authority).plus("/").plus(path))
    private val codeXmlData = 0
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    //初始化存储SP
    private fun initProviderData() {
        uriMatcher.addURI(authority, path, codeXmlData)
    }

    override fun onCreate(): Boolean {
        return context?.let {
            spEditor = it.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
            initProviderData()
            true
        } ?: false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")

    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}