// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.Config
import java.util.Base64
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KubernetesClient : SecretManager {

  private val logger = LoggerFactory.getLogger(KubernetesClient::class.java)
  private lateinit var kubernetesApi: CoreV1Api

  @PostConstruct
  internal fun init() {
    kubernetesApi = CoreV1Api(Config.defaultClient())
  }

  override fun createSecret(
      tenantName: String,
      secretName: String,
      secretData: Map<String, String>
  ) = this.createSecretIntoKubernetes(tenantName, secretName, secretData)

  override fun readSecret(tenantName: String, secretName: String): Map<String, String> =
      this.getSecretFromKubernetes(tenantName, secretName)

  private fun getSecretFromKubernetes(
      namespace: String = "phoenix",
      secretName: String
  ): Map<String, String> {
    val result = kubernetesApi.readNamespacedSecret(secretName, namespace, "")

    logger.debug("Secret retrieved {}", result)
    return result.data?.mapValues { Base64.getDecoder().decode(it.value).toString(Charsets.UTF_8) }
        ?: mapOf()
  }

  private fun createSecretIntoKubernetes(
      namespace: String,
      secretName: String,
      secretData: Map<String, String>
  ) {
    val metadata = V1ObjectMeta()
    metadata.name = secretName.lowercase()
    metadata.namespace = namespace
    val body = V1Secret()
    body.metadata = metadata

    body.data = secretData.mapValues { Base64.getEncoder().encode(it.value.toByteArray()) }
    body.type = "Opaque"
    try {
      val result: V1Secret =
          kubernetesApi.createNamespacedSecret(namespace, body, null, null, null, null)
      logger.info("Secret created {}", result)
    } catch (e: ApiException) {
      logger.info("Exception when creating secret {}", e.message)
    }
  }
}
