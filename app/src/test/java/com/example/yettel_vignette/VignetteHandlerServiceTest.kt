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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
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
    fun `test getHighwayVignetteInfo calls repository method`() = runBlocking {
        // GIVEN
        val dummyPayload = Payload(
            highwayVignettes = emptyList(),
            vehicleCategories = emptyList(),
            counties = emptyList()
        )

        val dummyResponse = Response.success(
            HighwayVignetteResponse(
                requestId = "dummyId",
                statusCode = "OK",
                payload = dummyPayload,
                dataType = "dummyType"
            )
        )

        `when`(repository.getHighwayVignetteInfo()).thenReturn(dummyResponse)

        // WHEN
        val result = service.getHighwayVignetteInfo()

        // THEN
        verify(repository).getHighwayVignetteInfo()
        assertEquals(dummyResponse, result)
    }

    @Test
    fun `test getVehicleInfo calls repository method`() = runBlocking {
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
        assertEquals(dummyResponse, result)
    }

    @Test
    fun `test placeVignetteOrder calls repository method`() = runBlocking {
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
        assertEquals(dummyResponse, result)
    }
}