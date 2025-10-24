package com.hutian.dogquiz.domain.flashcard.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.hutian.dogquiz.data.flashcard.repositories.BreedRepository
import com.hutian.dogquiz.data.flashcard.repositories.DogImageRepository
import com.hutian.dogquiz.domain.flashcard.models.Breed
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val dogImageRepository: DogImageRepository,
    private val breedRepository: BreedRepository): ViewModel() {

    suspend fun getRandomCard(): Card {
        return try {
            val breed = breedRepository.getRandomBreed()
                ?: return ErrorLoadingCards("Could not find breeds")
            val imageUrl =
                dogImageRepository.getRandomDogImageUri(breed.name, breed.subBreed)
            return DogFlashCard(breed, imageUrl)
        } catch (e: Exception) {
            ErrorLoadingCards(e.message ?: "Unknown error")
        }
    }

}

sealed class Card

data class DogFlashCard(
    private val breed: Breed,
    private val imageUrl: String,
): Card() {
    val name: String get() = if (breed.subBreed == null) breed.name else "${breed.name} ${breed.subBreed}"
    val uri: String get() = imageUrl
}

data class ErrorLoadingCards(val errorMessage: String): Card()
