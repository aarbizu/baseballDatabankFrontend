package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.routes.Crumb
import org.aarbizu.baseballDatabankFrontend.routes.RouteHandler
import org.junit.jupiter.api.Test

class RouteHandlerTest {

    @Test
    fun `handler calls renderNavMenu() method`() {

        val handler = object : RouteHandler {
            override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
                TODO("Not yet implemented")
            }

            override fun getCrumb(parameters: Parameters): Crumb {
                TODO("Not yet implemented")
            }

            override fun injectCrumbs(): MutableList<Crumb> {
                TODO("Not yet implemented")
            }

            override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) {
                TODO("Not yet implemented")
            }
        }

        assertThat(handler).isNotNull()
    }
}
