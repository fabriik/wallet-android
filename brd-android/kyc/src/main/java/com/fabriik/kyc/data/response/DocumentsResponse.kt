package com.fabriik.kyc.data.response

import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.data.model.Country
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentsResponse(
    @Json(name = "supportedDocuments")
    val documents: List<DocumentType>
)