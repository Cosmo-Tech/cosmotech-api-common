// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.apache.http.client.ClientProtocolException
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.assertThrows

class ContainerRegistryServiceTest {
  var csmPlatformProperties: CsmPlatformProperties = mockk(relaxed = true)
  lateinit var containerRegistryService: ContainerRegistryService

  @BeforeTest
  fun beforeTest() {
    containerRegistryService = spyk(ContainerRegistryService(csmPlatformProperties))
    every { csmPlatformProperties.containerRegistry.registryUrl } answers { "localhost:5000" }
  }

  @Test
  fun `check solution image with wrong repository and tag throws exception`() {
    assertThrows<ClientProtocolException> {
      containerRegistryService.checkSolutionImage("any", "any")
    }
  }

  @Test
  fun `check solution image with existing repository and tag`() {
    val ja = JSONArray()
    ja.put("latest")

    val jo = JSONObject()
    jo.put("name", "my-repository")
    jo.put("tags", ja)

    every { containerRegistryService.getRepositoryTagList("my-repository") } returns jo.toString()

    containerRegistryService.checkSolutionImage("my-repository", "latest")
  }

  @Test
  fun `check endpoint url`() {
    assertEquals(containerRegistryService.getEndpoint(), "localhost:5000")
  }
}
