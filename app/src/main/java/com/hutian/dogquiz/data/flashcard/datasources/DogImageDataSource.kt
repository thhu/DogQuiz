package com.hutian.dogquiz.data.flashcard.datasources

import com.hutian.dogquiz.data.common.datasource.NetworkDataSource
import com.hutian.dogquiz.data.flashcard.models.DogImageResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DogImageDataSource @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) {
    private val json = Json { isLenient = true; ignoreUnknownKeys = true }
    suspend fun getRandomDogImage(breed: String, subBreed: String?): DogImageResponse {
        val jsonData = networkDataSource.getJsonString("https://dog.ceo/api/breed/$breed/$subBreed/images/random")
        return json.decodeFromString(jsonData)
    }

    suspend fun getRandomDogImage(breed: String): DogImageResponse {
        val jsonData = networkDataSource.getJsonString("https://dog.ceo/api/breed/$breed/images/random")
        return json.decodeFromString(jsonData)
    }
}