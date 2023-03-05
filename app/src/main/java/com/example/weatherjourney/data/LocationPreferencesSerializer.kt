package com.example.weatherjourney.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.weatherejourney.LocationPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object LocationPreferencesSerializer : Serializer<LocationPreferences> {

    override val defaultValue: LocationPreferences = LocationPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LocationPreferences {
        try {
            return LocationPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: LocationPreferences, output: OutputStream) = t.writeTo(output)
}
