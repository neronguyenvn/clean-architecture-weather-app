package com.example.weatherjourney.util

val successfulReverseGeocodingResponse = """
    {
      "results": [ 
        {
          "components": {
            "city": "Thu Duc City"
          }
        }
      ]
    }
""".trimIndent()

val successfulForwardGeocodingResponse = """
    {
      "results": [
        {
          "geometry": {
            "lat": 10.873,
            "lng": 106.742
          }
        }
      ]
    }
""".trimIndent()

const val errorResponse = "I am not a json :o"

val emptyResultForwardGeocodingResponse = """
    {
        "results": []
    }
""".trimIndent()
