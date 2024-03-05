package com.example.weatherjourney.core.model.serializer

import com.example.weatherjourney.core.model.location.Coordinate
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
            element<Double>("latitude")
            element<Double>("longitude")
        }

    override fun serialize(encoder: Encoder, value: Coordinate) =
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.latitude)
            encodeDoubleElement(descriptor, 1, value.longitude)
        }

    override fun deserialize(decoder: Decoder): Coordinate =
        decoder.decodeStructure(descriptor) {
            var latitude = -1.0
            var longitude = -1.0
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> latitude = decodeDoubleElement(descriptor, 0)
                    1 -> longitude = decodeDoubleElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            require(latitude in -90.0..90.0 && longitude in -180.0..<180.00)
            Coordinate(latitude, longitude)
        }
}