package com.hutian.dogquiz.data.flashcard.repositories

import com.hutian.dogquiz.data.flashcard.datasources.DogImageDataSource
import javax.inject.Inject

class DogImageRepository @Inject constructor(private val dogImageDataSource: DogImageDataSource) {
    suspend fun getRandomDogImageUri(breed: String, subBreed: String?): String {
            return if (subBreed != null) {
                dogImageDataSource.getRandomDogImage(breed, subBreed).message
            } else {
                dogImageDataSource.getRandomDogImage(breed).message
            }
        }
}