package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import io.mockk.junit5.MockKExtension
import java.net.URI
import org.aarbizu.baseballDatabankFrontend.db.DbConnectionParams
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BasicTest {

    @Test
    fun `db conn parses jdbc URIs properly`() {
        val urlEnvVar = "postgres://username:password@ec2-54-86-57-171.compute-1.amazonaws.com:5432/path"

        val uri = URI(urlEnvVar)
        val dbConnParams = DbConnectionParams(uri)

        assertThat(dbConnParams.user).isEqualTo("username")
        assertThat(dbConnParams.password).isEqualTo("password")
        assertThat(dbConnParams.userinfo).isEqualTo("username:password")
        assertThat(dbConnParams.getJdbcUrl()).isEqualTo("jdbc:postgresql://ec2-54-86-57-171.compute-1.amazonaws.com:5432/path")
    }
}
