package com.example.weatherjourney.core.model.serializer

import com.example.weatherjourney.core.model.Coordinate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object CoordinateSerializer : KSerializer<Coordinate> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Coordinate") {
            element<Float>("latitude")
            element<Float>("longitude")
        }

    override fun serialize(encoder: Encoder, value: Coordinate) =
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.latitude)
            encodeFloatElement(descriptor, 1, value.longitude)
        }

    override fun deserialize(decoder: Decoder): Coordinate =
        decoder.decodeStructure(descriptor) {
            var latitude = -1f
            var longitude = -1f
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> latitude = decodeFloatElement(descriptor, 0)
                    1 -> longitude = decodeFloatElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            require(latitude in -90f..90f && longitude in -180f..<180f)
            Coordinate(latitude, longitude)
        }
}