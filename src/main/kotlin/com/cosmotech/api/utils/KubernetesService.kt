// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret
import java.util.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private const val SECRET_LABEL = "cosmotech.com/context"

@Service
class KubernetesService(private val kubernetesApi: CoreV1Api?) : SecretManager {

  private val logger = LoggerFactory.getLogger(KubernetesService::class.java)

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
    val api = checkKubernetesContext()
    val secretNameLower = secretName.lowercase()
    val labelSelector = buildLabelSector(secretNameLower)

    try {
      val secrets =
          api.listNamespacedSecret(
              namespace, null, null, null, null, labelSelector, null, null, null, null, null)
      if (secrets.items.isEmpty()) {
        logger.debug("Secret does not exists in namespace $namespace: cannot delete it")
      } else {
        logger.info("Secret exists in namespace $namespace: deleting it")
        api.deleteNamespacedSecret(secretNameLower, namespace, null, null, null, null, null, null)
      }
    } catch (e: ApiException) {
      logger.error("Kubernetes API Exception when listing or deleting secret ${e.message}")
      throw e
    }
  }

  private fun getSecretFromKubernetes(namespace: String, secretName: String): Map<String, String> {
    val api = checkKubernetesContext()
    val secretNameLower = secretName.lowercase()
    val result = api.readNamespacedSecret(secretNameLower, namespace, "")

    logger.debug("Secret retrieved for namespace $namespace")
    return result.data?.mapValues { Base64.getDecoder().decode(it.value).toString(Charsets.UTF_8) }
        ?: mapOf()
  }

  private fun createOrReplaceSecretIntoKubernetes(
      namespace: String,
      secretName: String,
      secretData: Map<String, String>
  ) {
    val api = checkKubernetesContext()
    logger.debug("Creating secret $secretName in namespace $namespace")

    val secretNameLower = secretName.lowercase()
    val labelSelector = buildLabelSector(secretNameLower)
    var replace = false

    try {
      val secrets =
          api.listNamespacedSecret(
              namespace, null, null, null, null, labelSelector, null, null, null, null, null)
      if (secrets.items.isEmpty()) {
        logger.debug("Secret does not exists in namespace $namespace: creating it")
      } else {
        logger.debug("Secret already exists in namespace $namespace: replacing it")
        replace = true
      }
    } catch (e: ApiException) {
      logger.error("Kubernetes API Exception when listing secret ${e.message}")
      throw e
    }

    val metadata = V1ObjectMeta()
    metadata.name = secretNameLower
    metadata.namespace = namespace
    metadata.labels = mapOf(SECRET_LABEL to secretNameLower)
    val body = V1Secret()
    body.metadata = metadata

    body.data = secretData.mapValues { Base64.getEncoder().encode(it.value.toByteArray()) }
    body.type = "Opaque"

    try {
      if (replace) {
        api.replaceNamespacedSecret(secretNameLower, namespace, body, null, null, null, null)
      } else {
        api.createNamespacedSecret(namespace, body, null, null, null, null)
      }
      logger.info("Secret created/replaced")
    } catch (e: ApiException) {
      logger.error("Kubernetes API Exception when creating/replacing secret ${e.message}")
      throw e
    }
  }

  private fun checkKubernetesContext(): CoreV1Api {
    return this.kubernetesApi ?: throw IllegalStateException("Kubernetes API is not available")
  }

  private fun buildLabelSector(secretName: String) = "$SECRET_LABEL=$secretName"
}
