package com.breadwallet.ext

import okhttp3.Request

fun Request.Builder.addUniqueHeader(request:Request, name: String, value: String) = apply {
    if (request.headers[name] != null) {
        removeHeader(name)
    }
    addHeader(name, value)
}

