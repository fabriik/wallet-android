package com.fabriik.common.utils

import com.fabriik.common.data.FabriikApiResponse
import com.fabriik.common.data.FabriikApiResponseError
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FabriikApiResponseMapperTest {

    @Mock lateinit var moshi: Moshi

    private lateinit var mapper: FabriikApiResponseMapper

    @Before
    fun setUp() {
        mapper = FabriikApiResponseMapper(moshi)
    }

    @Test
    fun mapFabriikApiResponseSuccess_responseOk_returnSuccess() {
        val response: FabriikApiResponse<String?> = FabriikApiResponse(
            result = "ok",
            error = null,
            data = "test"
        )

        val actual: Resource<String?> = mapper.mapFabriikApiResponseSuccess(response)

        assertEquals(response.data, actual.data)
        assertEquals(Status.SUCCESS, actual.status)
    }

    @Test
    fun mapFabriikApiResponseSuccess_responseErrorWithErrorObject_returnError() {
        val response: FabriikApiResponse<String?> = FabriikApiResponse(
            result = "error",
            error = FabriikApiResponseError(
                code = "2100",
                message = "Test error code 2100"
            ),
            data = null
        )

        val actual: Resource<String?> = mapper.mapFabriikApiResponseSuccess(response)

        assertEquals("2100", actual.message)
        assertEquals(Status.ERROR, actual.status)
    }

    @Test
    fun mapFabriikApiResponseSuccess_responseErrorWithoutErrorObject_returnUnknownError() {
        val response: FabriikApiResponse<String?> = FabriikApiResponse(
            result = "error",
            error = null,
            data = null
        )

        val actual: Resource<String?> = mapper.mapFabriikApiResponseSuccess(response)

        assertEquals("Unknown error - invalid state", actual.message)
        assertEquals(Status.ERROR, actual.status)
    }
}