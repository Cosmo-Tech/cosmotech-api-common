// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ContainerConfig(private val csmPlatformProperties: CsmPlatformProperties) {
  @Bean
  open fun dockerClient(): DockerClient {
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
