package com.example.weatherjourney.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.datastore.model.UserData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : Serializer<UserData> {

    override val defaultValue = UserData.default

    override suspend fun readFrom(input: InputStream): UserData {
        try {
            return Json.decodeFromString(
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read UserPreferences.", exception)
        }
    }

    override suspend fun writeTo(
        t: UserData,
        output: OutputStream
    ) = withContext(ioDispatcher) {
        output.write(
            Json.encodeToString(t).encodeToByteArray()
        )
    }
}
