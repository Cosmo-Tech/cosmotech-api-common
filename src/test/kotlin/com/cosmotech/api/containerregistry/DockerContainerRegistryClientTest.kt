// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.NotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows

class DockerContainerRegistryClientTest {
  var csmPlatformProperties: CsmPlatformProperties = mockk(relaxed = true)
  var dockerClient: DockerClient = mockk(relaxed = true)
  lateinit var dockerContainerRegistryClient: DockerContainerRegistryClient

  @BeforeTest
  fun beforeTest() {
    dockerContainerRegistryClient =
        spyk(DockerContainerRegistryClient(csmPlatformProperties, dockerClient))
    every { csmPlatformProperties.containerRegistry.registryUrl } answers { "localhost:5000" }
  }

  @Test
  fun `check solution image with wrong repository and tag throws exception`() {
    every { dockerClient.inspectImageCmd(any()).exec() } throws NotFoundException("")
    assertThrows<NotFoundException> {
      dockerContainerRegistryClient.checkSolutionImage("any", "any")
    }
  }

  @Test
  fun `check endpoint url`() {
    assertEquals(dockerContainerRegistryClient.getEndpoint(), "localhost:5000")
  }
}
