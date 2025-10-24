package com.hutian.dogquiz.data.flashcard.repositories

import com.hutian.dogquiz.data.flashcard.datasources.BreedDataSource
import com.hutian.dogquiz.data.flashcard.models.BreedResponse
import com.hutian.dogquiz.domain.flashcard.models.Breed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.DisplayName



class BreedRepositoryTest {

    private val breedDataSource: BreedDataSource = mockk()

    private val breedRepository: BreedRepository = BreedRepository(breedDataSource)

    @Test
    @DisplayName("Given data source returns valid data, When getBreeds is called, Then return a correctly mapped list of breeds")
    fun `should return mapped breeds when data source is successful`() = runTest {
        // GIVEN: A fake response from the data source
        val fakeApiResponse = BreedResponse(
            message = mapOf(
                "bulldog" to listOf("boston", "english", "french"),
                "affenpinscher" to emptyList(),
                "african" to listOf("wild")
            ),
            status = "success"
        )

        // Configure the mock to return our fake data when getBreeds is called
        coEvery { breedDataSource.getAllBreeds() } returns fakeApiResponse

        // WHEN: The method under test is called
        val breeds = breedRepository.getRandomBreed()

        // THEN: Assert that the output is what we expect
        val expectedBreeds = listOf(Breed("bulldog", "boston"),
            Breed("bulldog", "english"),
            Breed("bulldog", "french"),
            Breed("affenpinscher", null),
            Breed("african", "wild"))

        Assert.assertTrue(breeds in expectedBreeds)
    }

    @Test
    @DisplayName("Given data source returns an empty map, When getBreeds is called, Then return an empty list")
    fun `should return empty list when data source provides no breeds`() = runTest {
        // GIVEN: An empty but valid response from the data source
        val emptyResponse = BreedResponse(message = emptyMap(), status = "success")
        coEvery { breedDataSource.getAllBreeds() } returns emptyResponse

        // WHEN: The method is called
        val breeds = breedRepository.getRandomBreed()

        // THEN: Assert that the result is null
        Assert.assertNull(breeds)
    }

}