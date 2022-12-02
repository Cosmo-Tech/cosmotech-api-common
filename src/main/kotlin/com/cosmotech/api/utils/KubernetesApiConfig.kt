// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import com.cosmotech.api.config.CsmPlatformProperties
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.ClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KubernetesApiConfig {
  @Bean
  @ConditionalOnProperty(
      name = ["csm.platform.runOutsideKubernetes"], havingValue = "false", matchIfMissing = true)
  open fun coreV1Api(csmPlatformProperties: CsmPlatformProperties): CoreV1Api? {
    val client = ClientBuilder.defaultClient()
    return CoreV1Api(client)
  }
}
