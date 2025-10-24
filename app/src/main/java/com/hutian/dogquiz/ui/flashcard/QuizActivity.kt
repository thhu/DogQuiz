package com.hutian.dogquiz.ui.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hutian.dogquiz.domain.flashcard.viewmodels.Card
import com.hutian.dogquiz.domain.flashcard.viewmodels.DogFlashCard
import com.hutian.dogquiz.domain.flashcard.viewmodels.FlashCardViewModel
import com.hutian.dogquiz.ui.theme.DogQuizTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlashCardScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FlashCardScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    // Get an instance of the ViewModel using Hilt
    val viewModel: FlashCardViewModel = hiltViewModel()

    // State for the current dog being displayed
    var currentCard by remember { mutableStateOf<Card?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var userGuess by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var showAnswer by remember { mutableStateOf(false) }

    val loadNextCard = {
        scope.launch {
            isLoading = true
            userGuess = "" // Clear previous guess
            feedbackMessage = null // Clear previous feedback
            showAnswer = false
            currentCard = viewModel.getRandomCard()
            isLoading = false
        }
    }

    // Effect to load the first dog when the screen appears
    LaunchedEffect(Unit) {
        loadNextCard()
    }

    // Main layout for the screen
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Display the flashcard if a dog has been loaded
            currentCard?.let { card ->
                FlashCard(card = card, flipped = showAnswer)
            }
        }

        Spacer(Modifier.height(24.dp))

        feedbackMessage?.let { message ->
            Text(
                text = message,
                color = if (message == "Correct!") Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
        }

        TextField(
            value = userGuess,
            onValueChange = { userGuess = it },
            label = { Text("Guess the dog breed") },
            singleLine = true,
            enabled = !isLoading && currentCard != null // Disable when loading
        )

        Spacer(Modifier.height(16.dp))

        // Button to load the next card
        Button(onClick = {
            scope.launch {
                if (currentCard is DogFlashCard) {
                    val card = currentCard as DogFlashCard
                    if (userGuess.equals(card.name, ignoreCase = true)) {
                        feedbackMessage = "Correct!"
                        showAnswer = true
                        delay(2000) // Wait a second before loading the next card
                        loadNextCard()
                    } else {
                        feedbackMessage = "Try Again!"
                        delay(1500)
                        feedbackMessage = null
                    }
                }
            }
        },
            enabled = userGuess.isNotBlank()) {
            Text("Submit Guess")
        }
    }
}

@Composable
fun FlashCard(card: Card, flipped: Boolean = false, modifier: Modifier = Modifier) {
    // State to track if the card is flipped
    var isFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(card, flipped) {
        isFlipped = flipped
    }

    // Animation for the flip rotation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "flipRotation"
    )

    Card(
        modifier = modifier
            .size(width = 300.dp, height = 400.dp)
            // Apply the rotation effect
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { isFlipped = !isFlipped }, // Toggle flip state on click
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (card is DogFlashCard) {
            // Show either the front or the back based on the rotation
            if (rotation < 90f) {
                FlashCardFront(imageUrl = card.uri)
            } else {
                // The back is rotated 180 degrees to appear correctly
                FlashCardBack(breedName = card.name, modifier = Modifier.graphicsLayer { rotationY = 180f })
            }
        }
    }
}

@Composable
fun FlashCardFront(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Dog image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun FlashCardBack(breedName: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = breedName, style = MaterialTheme.typography.headlineMedium)
    }
}