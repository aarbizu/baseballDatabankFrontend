package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SupervocallicTest {

    @Test
    fun `validates isSupervocalic`() {
        assertThat("aeiou".isSuperVocalic()).isTrue()
        assertThat("AEIOU".isSuperVocalic()).isTrue()
        assertThat("UOIEA".isSuperVocalic()).isTrue()
        assertThat("iOuAe".isSuperVocalic()).isTrue()
        assertThat("axxexx ixxoxx xxuxx".isSuperVocalic()).isTrue()
        assertThat(
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
            """.trimIndent().isSuperVocalic(),
        ).isTrue()

        assertThat("aaeiou".isSuperVocalic()).isFalse()
        assertThat("zzzzz".isSuperVocalic()).isFalse()
        assertThat("aaeeiioouu".isSuperVocalic()).isFalse()
        assertThat("aeioz".isSuperVocalic()).isFalse()
        assertThat("AZiou".isSuperVocalic()).isFalse()
        assertThat("Barry Bonds".isSuperVocalic()).isFalse()
        assertThat("Juan Encarnacion".isSuperVocalic()).isFalse()
    }
}
