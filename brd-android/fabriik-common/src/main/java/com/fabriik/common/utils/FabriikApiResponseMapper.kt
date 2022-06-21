package com.fabriik.common.utils

import android.content.Context
import com.fabriik.common.R
import com.fabriik.common.data.FabriikApiResponse
import com.fabriik.common.data.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException

class FabriikApiResponseMapper {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun <T> mapSuccess(response: FabriikApiResponse<T?>): Resource<T?> {
        return when {
            response.result == "ok" ->
                Resource.success(response.data)
            response.result == "error" && response.error != null ->
                Resource.error(message = response.error.code)
            else ->
                Resource.error(message = "Unknown error - invalid state")
        }
    }

    fun <T> mapError(context: Context, exception: Exception) : Resource<T?> {
        var errorMessage: String? = null

        if (exception is HttpException) {
            exception.response()?.errorBody()?.let {
                val responseType = Types.newParameterizedType(FabriikApiResponse::class.java, Any::class.java)
                val responseAdapter = moshi.adapter<FabriikApiResponse<Any>>(responseType)

                val response = responseAdapter.fromJson(
                    it.source()
                )

                errorMessage = response?.error?.message
            }
        }

        return Resource.error(
            message = errorMessage ?: context.getString(R.string.FabriikApi_DefaultError)
        )
    }
}