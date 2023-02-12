package org.aarbizu.baseballlDatabankFrontend

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.putJsonArray
import kotlin.test.Test

external interface Names {
    val stats: List<String>
}

data class NamesObj(override val stats: List<String>) : Names

class SimpleJSTests {

    @Test
    fun testInteropWithJsonObject() {
        val response = buildJsonObject {
            putJsonArray("stats") {
                add("a")
                add("b")
                add("c")
            }
        }

        val names = response["stats"]
        val strings = names?.jsonArray?.map { it.toString() }?.toList()!!

        val obj = NamesObj(strings)

        println("testing $names, $response, $obj, $strings")
    }

    @Test
    fun testInnteropWithJsonArray() {
        val response = buildJsonArray {
            add("a")
            add("b")
            add("c")
        }

        val strings = response.map { it.toString() }.toList()
        val obj = NamesObj(strings)

        println("testing $response, $obj, $strings")
    }

    @Serializable
    data class Foo(var a: String, var b: String, var c: String)

    @Test
    fun jsonParsing() {
        val s = """{"a":"2", "b":"3", "c":"4"}"""
        val parsed = JSON.parse<String>(s).asDynamic()
        val parsed2 = Json.encodeToString(Foo("2", "3", "4"))
        val obj2 = Json.decodeFromString<Foo>(parsed2)
        val obj3 = Json.decodeFromString<Foo>(s)

        println("$s, ${parsed.a}, $parsed2, $obj2, $obj3")
    }
}
