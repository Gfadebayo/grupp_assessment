package com.grupp.assessment.productexplorer.data.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullConverter: Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {

        val nextConverter: Converter<ResponseBody, *> = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)

        return Converter<ResponseBody, Any> {
            val source = it.source().peek()

            source.request(Long.MAX_VALUE)

            if(source.buffer.size == 0L) Unit
            else nextConverter.convert(it)
        }
    }
}
