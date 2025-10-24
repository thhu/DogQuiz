package com.hutian.dogquiz.data.flashcard.models

import kotlinx.serialization.Serializable

@Serializable
data class BreedResponse(val message: Map<String, List<String>>, val status: String)