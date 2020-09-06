package org.aarbizu.baseballDatabankFrontend.db

abstract class Bind<T>(private val field: String, open val value: T) {
    override fun toString(): String {
        return "col=$field, value=$value"
    }
}

data class IntBind(private val field: String, override val value: Int) : Bind<Int>(field, value)
data class StrBind(private val field: String, override val value: String) : Bind<String>(field, value)
