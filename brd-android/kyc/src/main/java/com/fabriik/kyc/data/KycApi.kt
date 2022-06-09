package com.fabriik.kyc.data

import android.content.Context
import androidx.core.net.toFile
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.kyc.R
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.data.model.DocumentData
import com.fabriik.kyc.data.requests.CompleteLevel1VerificationRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class KycApi(
    private val context: Context,
    private val service: KycService
) {
    suspend fun getCountries(): Resource<List<Country>?> {
        return try {
            val response = service.getCountries(Locale.getDefault().language)
            Resource.success(data = response.countries)
        } catch (ex: Exception) {
            Resource.error(message = getErrorMessage(ex))
        }
    }

    suspend fun getDocuments(): Resource<List<DocumentType>?> {
        return try {
            val response = service.getDocuments()
            Resource.success(data = response.documents)
        } catch (ex: Exception) {
            Resource.error(message = getErrorMessage(ex))
        }
    }

    suspend fun completeLevel1Verification(firstName: String, lastName: String, country: Country, dateOfBirth: Date): Resource<ResponseBody?> {
        return try {
            val response = service.completeLevel1Verification(
                CompleteLevel1VerificationRequest(
                    firstName = firstName,
                    lastName = lastName,
                    country = country.code,
                    dateOfBirth = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dateOfBirth),
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            Resource.error(message = getErrorMessage(ex))
        }
    }

    suspend fun uploadPhotos(type: String, documentType: DocumentType, documentData: List<DocumentData>) : Resource<ResponseBody?> {
        return try {
            val bodyType = type.toRequestBody()
            val bodyDocumentType = documentType.id.toRequestBody()

            val imagesParts = mutableListOf<MultipartBody.Part>()
            for (data in documentData) {
                val imageFile = data.imageUri.toFile()
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)

                val partName = when (data.documentSide) {
                    DocumentSide.FRONT -> "front"
                    DocumentSide.BACK -> "back"
                }

                imagesParts.add(
                    MultipartBody.Part.createFormData(
                        partName, partName, requestBody
                    )
                )
            }

            val response = service.uploadPhotos(
                type = bodyType,
                documentType = bodyDocumentType,
                images = imagesParts.toTypedArray()
            )

            Resource.success(response)
        } catch (ex: Exception) {
            Resource.error(message = getErrorMessage(ex))
        }
    }

    suspend fun submitPhotosForVerification(): Resource<ResponseBody?> {
        return try {
            val response = service.submitPhotosForVerification()
            Resource.success(data = response)
        } catch (ex: Exception) {
            Resource.error(message = getErrorMessage(ex))
        }
    }

    private fun getErrorMessage(ex: Exception): String {
        return ex.message ?: context.getString(R.string.FabriikApi_DefaultError)
    }

    companion object {

        const val DATE_FORMAT = "yyyy-MM-dd"

        fun create(context: Context) = KycApi(
            context = context,
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(KycApiInterceptor())
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_KYC_API)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
                .create(KycService::class.java)
        )
    }
}