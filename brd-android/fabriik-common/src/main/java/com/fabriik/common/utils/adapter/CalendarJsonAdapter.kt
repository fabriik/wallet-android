package com.fabriik.common.utils.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.json.JSONObject.NULL
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CalendarJsonAdapter : JsonAdapter<Calendar>() {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Calendar? {
        if (reader.peek() == NULL) {
            return reader.nextNull()
        }
        val string = reader.nextString()
        val date = simpleDateFormat.parse(string)
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        }
        return null
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Calendar?) {
        if (value == null) {
            writer.nullValue()
        } else {
            val string = simpleDateFormat.format(value.time)
            writer.value(string)
        }
    }
}