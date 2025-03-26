package com.example.yettel_vignette

import com.example.yettel_vignette.interfaces.VignetteRepository
import com.example.yettel_vignette.models.HighwayVignetteResponse
import com.example.yettel_vignette.models.LanguageNames
import com.example.yettel_vignette.models.Payload
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrder
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import com.example.yettel_vignette.services.VignetteHandlerService
import com.example.yettel_vignette.models.Result
import com.example.yettel_vignette.models.VignetteResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class VignetteHandlerServiceTest {

    @Mock
    lateinit var repository: VignetteRepository

    private lateinit var service: VignetteHandlerService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        service = VignetteHandlerService(repository)
    }

    @Test
    fun `test getHighwayVignetteInfo returns success`() = runBlocking {
        // GIVEN
        val dummyPayload = Payload(
            highwayVignettes = emptyList(),
            vehicleCategories = emptyList(),
            counties = emptyList()
        )

        val dummyResponse = HighwayVignetteResponse(
            requestId = "dummyId",
            statusCode = "OK",
            payload = dummyPayload,
            dataType = "dummyType"
        )

        `when`(repository.getHighwayVignetteInfo()).thenReturn(Response.success(dummyResponse))

        val expectedResult = VignetteResult(
            vignettes = emptyList(),
            countyVignettes = emptyList()
        )

        // WHEN
        val result = service.getHighwayVignetteInfo()

        // THEN
        verify(repository).getHighwayVignetteInfo()
        assertTrue(result is Result.Success)
        assertEquals(expectedResult, (result as Result.Success).data)
    }


    @Test
    fun `test getVehicleInfo returns success`() = runBlocking {
        // Given
        val dummyVehicle = Vehicle(
            requestId = "vehicleId",
            statusCode = "OK",
            internationalRegistrationCode = "IRCode",
            type = "Car",
            name = "Test Car",
            plate = "ABC123",
            country = LanguageNames(hu = "Magyar", en = "Hungarian"),
            vignetteType = "Standard"
        )
        val dummyResponse = Response.success(dummyVehicle)
        `when`(repository.getVehicleInfo()).thenReturn(dummyResponse)

        // When
        val result = service.getVehicleInfo()

        // Then
        verify(repository).getVehicleInfo()
        assertTrue(result is Result.Success)
        assertEquals(dummyVehicle, (result as Result.Success).data)
    }

    @Test
    fun `test getVehicleInfo returns error when API call fails`() = runBlocking {
        // Given
        `when`(repository.getVehicleInfo()).thenReturn(Response.error(500, "".toResponseBody()))

        // When
        val result = service.getVehicleInfo()

        // Then
        verify(repository).getVehicleInfo()
        assertTrue(result is Result.Error)
        assertEquals("API call failed with code 500", (result as Result.Error).message)
    }

    @Test
    fun `test placeVignetteOrder returns success`() = runBlocking {
        // Given
        val dummyOrder = VignetteOrder(
            type = "DAY",
            category = "CAR",
            cost = 5000f
        )
        val orderRequest = VignetteOrderRequest(highwayOrders = listOf(dummyOrder))
        val dummyOrderResponse = VignetteOrderResponse(
            requestId = "orderId",
            statusCode = "OK",
            receivedOrders = listOf(dummyOrder)
        )
        val dummyResponse = Response.success(dummyOrderResponse)
        `when`(repository.placeVignetteOrder(orderRequest)).thenReturn(dummyResponse)

        // When
        val result = service.placeVignetteOrder(orderRequest)

        // Then
        verify(repository).placeVignetteOrder(orderRequest)
        assertTrue(result is Result.Success)
        assertEquals(dummyOrderResponse, (result as Result.Success).data)
    }

    @Test
    fun `test placeVignetteOrder returns error when API call fails`() = runBlocking {
        // Given
        val orderRequest = VignetteOrderRequest(highwayOrders = listOf())
        `when`(repository.placeVignetteOrder(orderRequest)).thenReturn(Response.error(400, "".toResponseBody()))

        // When
        val result = service.placeVignetteOrder(orderRequest)

        // Then
        verify(repository).placeVignetteOrder(orderRequest)
        assertTrue(result is Result.Error)
        assertEquals("API call failed with code 400", (result as Result.Error).message)
    }
}