package com.example.mgapp.data.util

import android.content.Context
import com.example.mgapp.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SchemaLoader {
    fun loadSchemas(context: Context): List<HotspotSchema> {
        val json = context.assets.open("schema.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<HotspotSchema>>() {}.type
        return Gson().fromJson(json, type)
    }
}
