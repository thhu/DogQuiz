package com.hutian.dogquiz.data.flashcard.repositories

import com.hutian.dogquiz.data.flashcard.datasources.DogImageDataSource
import com.hutian.dogquiz.data.flashcard.models.DogImageResponse
import com.hutian.dogquiz.domain.flashcard.models.Breed
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows


class DogImageRepositoryTest {

    private val dogImageDataSource: DogImageDataSource = mockk()

    // The class under test
    private val dogImageRepository: DogImageRepository = DogImageRepository(dogImageDataSource)

    @Test
    @DisplayName("Given API returns a successful response, When called, Then return the image URL")
    fun `should return image url when api is successful`() = runTest {
        // GIVEN: A specific breed and a fake successful API response
        val breed = Breed("corgi", "cardigan")
        val fakeImageUrl = "https://images.dog.ceo/breeds/corgi-cardigan/n02113799_4387.jpg"
        val fakeResponse = DogImageResponse(message = fakeImageUrl, status = "success")

        // Configure the mock to return the fake response when the specific API method is called
        coEvery { dogImageDataSource.getRandomDogImage(breed.name, breed.subBreed) } returns fakeResponse

        // WHEN: The repository method is called
        val imageUrl = dogImageRepository.getRandomDogImageUri("corgi", "cardigan")

        // THEN: Assert that the returned URL matches the one from the fake response
        assertEquals(fakeImageUrl, imageUrl)
    }

    @Test
    @DisplayName("Given API returns a successful response for a breed with no sub-breed, When called, Then return the image URL")
    fun `should return image url for breed with no sub-breed`() = runTest {
        // GIVEN: A breed with no sub-breed
        val fakeImageUrl = "https://images.dog.ceo/breeds/akita/Akita_inu_blanc.jpg"
        val fakeResponse = DogImageResponse(message = fakeImageUrl, status = "success")

        // Configure the mock for a call with a null subBreed
        coEvery { dogImageDataSource.getRandomDogImage("akita") } returns fakeResponse

        // WHEN: The repository method is called
        val imageUrl = dogImageRepository.getRandomDogImageUri("akita", null)

        // THEN: Assert that the correct URL is returned
        assertEquals(fakeImageUrl, imageUrl)
        coVerify(exactly = 0) { dogImageDataSource.getRandomDogImage(any(), any()) }
    }


    @Test
    @DisplayName("Given API call throws an exception, When called, Then return null")
    fun `should return null when api throws an exception`() = runTest {
        // GIVEN: The mock is configured to throw an exception
        coEvery { dogImageDataSource.getRandomDogImage(any(), any()) } throws RuntimeException("Network failed")

        // WHEN: The repository method is called assert it throws runtime exception
        assertThrows<RuntimeException> { dogImageRepository.getRandomDogImageUri("corgi", "cardigan") }
    }

}