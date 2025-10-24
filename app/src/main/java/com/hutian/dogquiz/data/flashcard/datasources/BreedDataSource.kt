package com.hutian.dogquiz.data.flashcard.datasources

import com.hutian.dogquiz.data.common.datasource.JsonDataSource
import com.hutian.dogquiz.data.flashcard.models.BreedResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BreedDataSource @Inject constructor(
    private val jsonDataSource: JsonDataSource
) {
    private val json = Json { isLenient = true; ignoreUnknownKeys = true }

   fun getAllBreeds(): BreedResponse {
       val jsonData = jsonDataSource.getJsonDataFromFile("dog_breeds.json")
       return json.decodeFromString(jsonData)
   }

}