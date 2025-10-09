package com.example.dnbn_friend.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

object JsonCache {
    private val gson = Gson()

    fun <T> writeList(file: File, list: List<T>) {
        runCatching {
            file.parentFile?.mkdirs()
            file.writeText(gson.toJson(list))
        }
    }

    fun <T> readList(file: File, type: Type): List<T> {
        return runCatching {
            if (!file.exists()) return emptyList()
            val text = file.readText()
            @Suppress("UNCHECKED_CAST")
            gson.fromJson<List<T>>(text, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    inline fun <reified T> listType(): Type = object : TypeToken<List<T>>() {}.type
}


