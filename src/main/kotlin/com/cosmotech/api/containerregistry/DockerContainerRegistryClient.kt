// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.github.dockerjava.api.DockerClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service("csmDockerContainerRegistry")
@ConditionalOnProperty(
    name = ["csm.platform.containerRegistry.provider"],
    havingValue = "local",
    matchIfMissing = true)
open class DockerContainerRegistryClient(
    private val csmPlatformProperties: CsmPlatformProperties,
    private val dockerClient: DockerClient
) : RegistryClient {

  override fun getEndpoint() = csmPlatformProperties.containerRegistry.registryUrl

  override fun checkSolutionImage(repository: String, tag: String) {
    val imageFullName =
        csmPlatformProperties.containerRegistry.registryUrl + "/" + repository + ":" + tag
    dockerClient.inspectImageCmd(imageFullName).exec()
  }
}
