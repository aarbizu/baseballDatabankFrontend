package org.aarbizu.baseballlDatabankFrontend

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.PlayerCareerStatRecord
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
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
    fun testInteropWithJsonArray() {
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
        val parsed2 = JSON.stringify(Foo("2", "3", "4"))
        val obj2 = JSON.parse<Foo>(parsed2)
        val obj3 = JSON.parse<Foo>(s)
        println("$s, ${parsed.a}, $parsed2, ${obj2.b}, ${obj3.b}")
    }

    @Test
    fun jsonSerialization() {
        val record: BaseballRecord = SimplePlayerRecord(
            "alan",
            "arbizu",
            "alan",
            "alan arbizu",
            "10-26-1975",
            "10-26-1975",
            "12-31-1999",
            "aarb001",
            "bbref",
            "false",
        )

        val module = SerializersModule {
            polymorphic(BaseballRecord::class) {
                subclass(SimplePlayerRecord::class)
                subclass(PlayerCareerStatRecord::class)
            }
        }

        val json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = module
        }

        val jsonRepresentation = json.encodeToString(record)
        println(jsonRepresentation)

        val decodeFromString = json.decodeFromString<SimplePlayerRecord>(jsonRepresentation)
        println(decodeFromString.name)
    }
}
