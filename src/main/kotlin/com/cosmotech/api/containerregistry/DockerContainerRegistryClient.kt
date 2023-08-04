// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service("csmDockerContainerRegistry")
@ConditionalOnProperty(
    name = ["csm.platform.containerRegistry.provider"],
    havingValue = "local",
    matchIfMissing = true)
class DockerContainerRegistryClient(private val csmPlatformProperties: CsmPlatformProperties) :
    RegistryClient {

  override fun getEndpoint() = csmPlatformProperties.containerRegistry.registryUrl

  override fun checkSolutionImage(repository: String, tag: String) {
    getDockerClient().inspectImageCmd(buildImageFullImageName(repository, tag)).exec()
  }

  private fun buildImageFullImageName(repository: String, tag: String) =
      csmPlatformProperties.containerRegistry.registryUrl + "/" + repository + ":" + tag

  private fun getDockerClient(): DockerClient {
    val config =
        DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withRegistryUrl(csmPlatformProperties.containerRegistry.registryUrl)
            .withRegistryPassword(csmPlatformProperties.containerRegistry.registryPassword)
            .withRegistryUsername(csmPlatformProperties.containerRegistry.registryUserName)
            .build()

    val httpClient: DockerHttpClient =
        ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost()).build()

    return DockerClientImpl.getInstance(config, httpClient)
  }
}
