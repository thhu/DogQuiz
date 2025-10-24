package com.hutian.dogquiz.data.flashcard.repositories

import com.hutian.dogquiz.data.flashcard.datasources.BreedDataSource
import com.hutian.dogquiz.domain.flashcard.models.Breed
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedRepository @Inject constructor(
    private val breedDataSource: BreedDataSource
) {
    private val allBreeds: List<Breed> by lazy {
        breedDataSource.getAllBreeds()
            .message
            .asSequence()
            .map {
                listOf(Breed(it.key, null)) + it.value.map { subBreed ->
                    Breed(it.key, subBreed)
                }
            }
            .flatten()
            .toList()
    }

    fun getRandomBreed(): Breed? {
        return allBreeds.randomOrNull()
    }
}



