// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmClientException
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec

class ContainerRegistryServiceTest {

  private var csmPlatformProperties: CsmPlatformProperties = mockk(relaxed = true)
  private var restClient: RestClient = mockk(relaxed = true)

  @SpyK lateinit var containerRegistryService: ContainerRegistryService

  @BeforeTest
  fun beforeTest() {
    containerRegistryService = ContainerRegistryService(csmPlatformProperties)
    ReflectionTestUtils.setField(containerRegistryService, "restClient", restClient)
    every { csmPlatformProperties.containerRegistry.scheme } answers { "http" }
    every { csmPlatformProperties.containerRegistry.host } answers { "localhost:5000" }
  }

  @Test
  fun `check solution image with restClient throwing HttpServerErrorException`() {
    assertThrows<CsmClientException> {
      every {
        restClient
            .get()
            .uri("/v2/any/tags/list")
            .header(HttpHeaders.AUTHORIZATION, any())
            .retrieve()
            .body(String::class.java)
      } throws HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)

      containerRegistryService.checkSolutionImage("any", "any")
    }
  }

  @Test
  fun `check solution image with restClient throwing HttpClientErrorException`() {
    assertThrows<CsmClientException> {
      every {
        restClient
            .get()
            .uri("/v2/any/tags/list")
            .header(HttpHeaders.AUTHORIZATION, any())
            .retrieve()
            .body(String::class.java)
      } throws HttpClientErrorException(HttpStatus.BAD_REQUEST)

      containerRegistryService.checkSolutionImage("any", "any")
    }
  }

  @Test
  fun `check solution image with existing repository and unknown tag`() {
    assertThrows<CsmClientException> {
      val ja = JSONArray()
      ja.put("latest")

      val jo = JSONObject()
      jo.put("name", "my-repository")
      jo.put("tags", ja)

      every {
        restClient
            .get()
            .uri("/v2/any/tags/list")
            .header(HttpHeaders.AUTHORIZATION, any())
            .retrieve()
            .body(String::class.java)
      } returns jo.toString()

      containerRegistryService.checkSolutionImage("any", "wrong_tag")
    }
  }

  @Test
  fun `check solution image with existing repository and tag`() {
    val ja = JSONArray()
    ja.put("latest")

    val jo = JSONObject()
    jo.put("name", "my-repository")
    jo.put("tags", ja)
    val reponseMockk = mockk<ResponseSpec>()

    every {
      restClient
          .get()
          .uri("/v2/my-repository/tags/list")
          .header(HttpHeaders.AUTHORIZATION, any())
          .retrieve()
    } returns reponseMockk

    every { reponseMockk.onStatus(any(), any()) } returns reponseMockk
    every { reponseMockk.body(String::class.java) } returns jo.toString()

    containerRegistryService.checkSolutionImage("my-repository", "latest")
  }
}
