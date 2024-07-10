// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import com.cosmotech.api.config.CsmPlatformProperties
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import java.io.FileReader
import java.io.IOException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KubernetesApiConfig {
  @Bean
  open fun coreV1Api(csmPlatformProperties: CsmPlatformProperties): CoreV1Api {
    val kubernetesContext = System.getProperty("localKubernetesContext")
    if (kubernetesContext != null) {
      // Locate kube config file
      val kubeConfigPath =
          System.getenv("KUBECONFIG") ?: System.getenv("HOME")?.plus("/.kube/config")
      kubeConfigPath ?: throw IOException("Unable to locate a kube config file")

      // Load config and force the context
      var kubeConfig = KubeConfig.loadKubeConfig(FileReader(kubeConfigPath))
      kubeConfig.setContext(kubernetesContext)

      return CoreV1Api(ClientBuilder.kubeconfig(kubeConfig).build())
    } else {
      return CoreV1Api(ClientBuilder.defaultClient())
    }
  }
}
