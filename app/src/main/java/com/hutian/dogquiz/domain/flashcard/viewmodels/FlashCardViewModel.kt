package com.hutian.dogquiz.domain.flashcard.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hutian.dogquiz.data.flashcard.repositories.BreedRepository
import com.hutian.dogquiz.data.flashcard.repositories.DogImageRepository
import com.hutian.dogquiz.domain.flashcard.models.Breed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val dogImageRepository: DogImageRepository,
    private val breedRepository: BreedRepository): ViewModel() {

    private val _uiState = MutableStateFlow(FlashCardUiState())
    val uiState: StateFlow<FlashCardUiState> = _uiState.asStateFlow()

    fun loadNextCard() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, showAnswer = false, feedbackMessage = null, userGuess = "") }
        val card = getRandomCard()
        _uiState.update { it.copy(currentCard = card, isLoading = false) }
    }

    fun onUserGuessChanged(newGuess: String) {
        _uiState.update { it.copy(userGuess = newGuess) }
    }

    fun submitGuess() = viewModelScope.launch {
        val state = _uiState.value
        val current = state.currentCard as? DogFlashCard ?: return@launch
        if (state.userGuess.equals(current.name, ignoreCase = true)) {
            _uiState.update { it.copy(feedbackMessage = "Correct!", showAnswer = true) }
            delay(2000)
            loadNextCard()
        } else {
            _uiState.update { it.copy(feedbackMessage = "Try Again!") }
            delay(1500)
            _uiState.update { it.copy(feedbackMessage = null) }
        }
    }

    private suspend fun getRandomCard(): Card {
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

data class FlashCardUiState(
    val currentCard: Card? = null,
    val isLoading: Boolean = false,
    val userGuess: String = "",
    val feedbackMessage: String? = null,
    val showAnswer: Boolean = false
)

sealed class Card

data class DogFlashCard(
    private val breed: Breed,
    private val imageUrl: String,
): Card() {
    val name: String get() = if (breed.subBreed == null) breed.name else "${breed.name} ${breed.subBreed}"
    val uri: String get() = imageUrl
}

data class ErrorLoadingCards(val errorMessage: String): Card()
