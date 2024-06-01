package com.example.dnote

import android.content.Context
import android.content.SharedPreferences

object FolderManager {
    private const val PREFS_NAME = "folders_prefs"
    private const val KEY_FOLDERS = "folders"

    fun getFolders(context: Context): MutableList<String> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val folderSet = sharedPreferences.getStringSet(KEY_FOLDERS, mutableSetOf("Diary")) // Ensure Diary folder exists
        return folderSet?.toMutableList() ?: mutableListOf("Diary")
    }

    fun saveFolders(context: Context, folders: MutableList<String>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putStringSet(KEY_FOLDERS, folders.toMutableSet())
            apply()
        }
    }
}

