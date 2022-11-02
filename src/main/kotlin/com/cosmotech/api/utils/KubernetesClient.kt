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
class KubernetesClient {

  private val logger = LoggerFactory.getLogger(KubernetesClient::class.java)
  private lateinit var kubernetesApi: CoreV1Api

  @PostConstruct
  internal fun init() {
    kubernetesApi = CoreV1Api(Config.defaultClient())
  }

  fun getSecretFromKubernetes(
      secretName: String,
      namespace: String = "phoenix"
  ): Pair<String, String> {
    var name = String()
    var key = String()
    try {
      val result = kubernetesApi.readNamespacedSecret(secretName, namespace, "")

      result.data?.forEach { entry ->
        run {
          name = entry.key
          key = String(Base64.getDecoder().decode(entry.value))
        }
      }

      logger.info("Secret retrieved {}", result)
    } catch (e: ApiException) {
      logger.info("Exception when getting secret {}", e.message)
    }

    return Pair(name, key)
  }

  fun createSecretIntoKubernetes(
      secretName: String,
      namespace: String,
      secret: Pair<String, String>
  ) {
    val metadata = V1ObjectMeta()
    metadata.name = secretName.lowercase()
    metadata.namespace = namespace
    val body = V1Secret()
    body.metadata = metadata

    val keyEncoded = Base64.getEncoder().encode(secret.second.toByteArray())
    body.data = mutableMapOf(secret.first to keyEncoded)
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
