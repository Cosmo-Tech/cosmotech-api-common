// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.ClientBuilder
import java.util.Base64
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@org.springframework.context.annotation.Configuration
open class KubernetesApi {
  @Bean
  open fun coreV1Api(): CoreV1Api {
    val client = ClientBuilder.defaultClient()
    return CoreV1Api(client)
  }
}

@Service
class KubernetesClient(private val kubernetesApi: CoreV1Api) : SecretManager {

  private val logger = LoggerFactory.getLogger(KubernetesClient::class.java)

  override fun createOrReplaceSecret(
      tenantName: String,
      secretName: String,
      secretData: Map<String, String>
  ) = this.createOrReplaceSecretIntoKubernetes(tenantName, secretName, secretData)

  override fun readSecret(tenantName: String, secretName: String): Map<String, String> =
      this.getSecretFromKubernetes(tenantName, secretName)

  override fun deleteSecret(tenantName: String, secretName: String) {
    this.deleteSecretFromKubernetes(tenantName, secretName)
  }

  private fun deleteSecretFromKubernetes(namespace: String, secretName: String) {
    val secretNameLower = secretName.lowercase()
    @Suppress("SwallowedException")
    try {
      kubernetesApi.readNamespacedSecret(secretNameLower, namespace, null)
      logger.info("Secret $secretNameLower exists in namespace $namespace: deleting it")
      kubernetesApi.deleteNamespacedSecret(
          secretNameLower, namespace, null, null, null, null, null, null)
    } catch (e: ApiException) {
      logger.debug(
          "Secret $secretNameLower does not exists in namespace $namespace: cannot delete it")
    }
  }

  private fun getSecretFromKubernetes(namespace: String, secretName: String): Map<String, String> {
    val secretNameLower = secretName.lowercase()
    val result = kubernetesApi.readNamespacedSecret(secretNameLower, namespace, "")

    logger.debug("Secret retrieved")
    return result.data?.mapValues { Base64.getDecoder().decode(it.value).toString(Charsets.UTF_8) }
        ?: mapOf()
  }

  private fun createOrReplaceSecretIntoKubernetes(
      namespace: String,
      secretName: String,
      secretData: Map<String, String>
  ) {
    logger.debug("Creating secret $secretName in namespace $namespace")

    val secretNameLower = secretName.lowercase()
    val metadata = V1ObjectMeta()
    metadata.name = secretNameLower
    metadata.namespace = namespace
    val body = V1Secret()
    body.metadata = metadata

    body.data = secretData.mapValues { Base64.getEncoder().encode(it.value.toByteArray()) }
    body.type = "Opaque"

    var replace = false
    @Suppress("SwallowedException")
    try {
      kubernetesApi.readNamespacedSecret(secretNameLower, namespace, null)
      logger.debug("Secret $secretNameLower already exists in namespace $namespace: replacing it")
      replace = true
    } catch (e: ApiException) {
      logger.debug("Secret $secretNameLower does not exists in namespace $namespace: creating it")
    }
    try {
      if (replace) {
        kubernetesApi.replaceNamespacedSecret(
            secretNameLower, namespace, body, null, null, null, null)
      } else {
        kubernetesApi.createNamespacedSecret(namespace, body, null, null, null, null)
      }
      logger.info("Secret $secretNameLower created/replaced")
    } catch (e: ApiException) {
      logger.error("Kubernetes API Exception when creating/replacing secret ${e.message}")
      throw e
    }
  }
}
