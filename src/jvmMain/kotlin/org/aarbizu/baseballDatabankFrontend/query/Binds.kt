package org.aarbizu.baseballDatabankFrontend.query

abstract class Bind<T>(private val field: String, open val value: T) {
    override fun toString() = "col=$field, value=$value"
}

data class IntBind(private val field: String, override val value: Int) : Bind<Int>(field, value)

data class StrBind(private val field: String, override val value: String) :
    Bind<String>(field, value)
