package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SupervocallicTest {

    @Test
    fun `validates isSupervocalic`() {
        assertThat(isSuperVocalic("aeiou")).isTrue()
        assertThat(isSuperVocalic("AEIOU")).isTrue()
        assertThat(isSuperVocalic("UOIEA")).isTrue()
        assertThat(isSuperVocalic("iOuAe")).isTrue()
        assertThat(isSuperVocalic("axxexx ixxoxx xxuxx")).isTrue()
        assertThat(
            isSuperVocalic(
                """
            a
            
            zzzzzzz
            
            e
            
            zzzzzzz
            
            i
            
            zzzzzzzz
            
            o
            
            zzzzzzzz
            
            u
                """.trimIndent()
            )
        )
            .isTrue()

        assertThat(isSuperVocalic("aaeiou")).isFalse()
        assertThat(isSuperVocalic("zzzzz")).isFalse()
        assertThat(isSuperVocalic("aaeeiioouu")).isFalse()
        assertThat(isSuperVocalic("aeioz")).isFalse()
        assertThat(isSuperVocalic("AZiou")).isFalse()
        assertThat(isSuperVocalic("Barry Bonds")).isFalse()
        assertThat(isSuperVocalic("Juan Encarnacion")).isFalse()

    }
}
