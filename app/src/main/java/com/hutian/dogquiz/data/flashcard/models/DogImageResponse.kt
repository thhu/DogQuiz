package com.hutian.dogquiz.data.flashcard.models

import kotlinx.serialization.Serializable

@Serializable
data class DogImageResponse(val message: String, val status: String)