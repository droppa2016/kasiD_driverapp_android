package co.za.kasi.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC") // Assume UTC if missing
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return json?.asString?.let {
            try {
                dateFormat.parse(it) ?: throw JsonParseException("Invalid date format: $it")
            } catch (e: ParseException) {
                throw JsonParseException("Failed to parse date: $it", e)
            }
        } ?: throw JsonParseException("Date string is null")
    }
}