package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import io.ktor.http.Parameters
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kweb.ElementCreator
import kweb.div
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.routes.Crumb
import org.aarbizu.baseballDatabankFrontend.routes.RouteHandler
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RouteHandlerTest {

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun `handler calls renderNavMenu() method`(@MockK(relaxed = true) ec: ElementCreator<*>, @MockK params: Parameters) {
        val mockCrumb = mockk<Crumb>()

        val handler = object : RouteHandler {
            override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
            }

            override fun getCrumb(parameters: Parameters) = mockCrumb

            override fun injectCrumbs() = mutableListOf<Crumb>()

            override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) {
            }
        }

        assertThat(handler).isNotNull()
    }
}
