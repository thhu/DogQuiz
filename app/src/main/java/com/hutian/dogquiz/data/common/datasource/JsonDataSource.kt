package com.hutian.dogquiz.data.common.datasource

import android.app.Application
import javax.inject.Inject

class JsonDataSource @Inject constructor(
    private val application: Application
) {
    /**
     * Reads a JSON file from the assets folder and parses it into a list of objects of type T.
     * @param fileName The name of the JSON file in the 'assets' folder (e.g., "dogs.json").
     * @return A list of parsed objects, or an empty list if an error occurs.
     */
    fun getJsonDataFromFile(fileName: String): String {
        return application.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}