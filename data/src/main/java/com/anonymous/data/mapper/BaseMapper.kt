package com.anonymous.data.mapper

interface BaseMapper<in From, out To> {
    fun map(from: From): To
}

interface BiMapper<A, B> : BaseMapper<A, B> {
    fun reverseMap(from: B): A
}

fun <From, To> BaseMapper<From, To>.mapList(from: List<From>): List<To> =
    from.map { map(it) }
