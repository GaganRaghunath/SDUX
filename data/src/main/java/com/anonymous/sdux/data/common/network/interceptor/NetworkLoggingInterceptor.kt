package com.anonymous.sdux.data.common.network.interceptor

import okhttp3.logging.HttpLoggingInterceptor

fun buildLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
