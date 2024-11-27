package com.grupp.assessment.productexplorer.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * In a situation where only 1, 2 or 3 values are to be retrieved from a json.
 * Using this annotation and passing the keys of the required json values may be more beneficial to creating a class around the json.
 *
 * If only 1 value is passed, then the required response class is returned
 * If 2 values are passed, then a Pair is used to bundle them. As such, the response to the request should be a Pair
 * if 3 values are passed, a Triple is used
 */
annotation class DataValue(
    val value: String,
    val value2: String = "",
    val value3: String = ""
)

/**
 * All responses come in a pre-defined format which is a wrapper around the actual data. Most of the time this is not needed
 * but a wrapper class around the actual data will have to be implemented since that's how the data comes back.
 *
 * So this Converter is to help with that by automatically unwrapping and returning the actual data
 */
class UnwrapConverter: Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        val dataValue = annotations.firstOrNull { it is DataValue } as? DataValue

        val returnType = if(type is ParameterizedType) getRawType(type)
        else type as Class<*>

        val envType = type.createEnvelopeType()
        val envConverter = retrofit.nextResponseBodyConverter<Any>(this, envType, annotations)

        return Converter {
            val json = it.source().peek().readString(Charset.defaultCharset())

            if(dataValue == null || dataValue.value.isBlank()) (envConverter.convert(it) as Envelope<*>).copy(json = json)
            else {
                val jsonEnv = json.createEnvelope()

                //A converter for the original return type
                val notEnvConverter = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)

                val value1 = jsonEnv.data.getValueFor(dataValue.value, notEnvConverter)

                val value2 = jsonEnv.data.getValueFor(dataValue.value2, notEnvConverter)

                val value3 = jsonEnv.data.getValueFor(dataValue.value3, notEnvConverter)

                if(returnType != Pair::class.java && returnType != Triple::class.java && value1 == null) (envConverter.convert(it) as Envelope<*>).copy(json = json)
                else {
                    try { it.close() }
                    catch(e: Exception) { e.printStackTrace() }

                    jsonEnv.toEnvelope(value1, value2, value3, returnType).copy(json = json)
                }
            }
        }
    }

    private fun JSONObject?.getValueFor(name: String, normalConverter: Converter<ResponseBody, Any>): Any? {
        if(this == null || name.isEmpty()) return null

        return if(optJSONObject(name) != null) {
            val bodyResult = optJSONObject(name)!!

            normalConverter.convert(bodyResult.toString().toResponseBody("application/json".toMediaType()))
        }
        else if(optJSONArray(name) != null) {
            val bodyResult = optJSONArray(name)!!

            normalConverter.convert(bodyResult.toString().toResponseBody("application/json".toMediaType()))
        }
        else if(optString(name).isNotEmpty()) {
            optString(name)
        }
        else null
    }

    private fun String.createEnvelope(): JsonEnvelope {
        val json = JSONObject(this)

        return JsonEnvelope(
            data = json.optJSONObject("responseBody"),
            isSuccessful = json.getBoolean("requestSuccessful"),
            requestCode = json.getInt("requestCode"),
            responseMessage = json.getString("responseMessage"),
            requestStatus = json.getString("requestStatus")
        )
    }

    private fun Type.createEnvelopeType(): Type {
        return object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(this@createEnvelopeType)
            }

            override fun getRawType(): Type {
                return Envelope::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

        }
    }
}

data class Envelope<T>(
    @SerializedName("responseBody") val responseBody: T? = null,

    @SerializedName("requestSuccessful") val requestSuccessful: Boolean? = null,

    @SerializedName("requestCode") val requestCode: Int? = null,

    @SerializedName("responseMessage") val responseMessage: String? = null,

    @SerializedName("requestStatus") val requestStatus: String? = null,

    @Transient val json: String = ""
)

private data class JsonEnvelope(
    val data: JSONObject? = null,

    val isSuccessful: Boolean? = null,

    val requestCode: Int? = null,

    val responseMessage: String? = null,

    val requestStatus: String? = null
) {
    fun toEnvelope(data: Any?, data2: Any?, data3: Any?, returnType: Class<*>): Envelope<*> {
        val combineData = when (returnType) {
            Triple::class.java -> Triple(data, data2, data3)

            Pair::class.java -> Pair(data, data2)

            else -> data
        }

        return Envelope(
            responseBody = combineData,
            requestSuccessful = isSuccessful,
            requestCode = requestCode,
            responseMessage = responseMessage,
            requestStatus = requestStatus
        )
    }
}