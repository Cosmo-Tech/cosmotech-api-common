// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.time.OffsetDateTime
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

// Needed for authentication in multitenant mode
// https://grafana.com/docs/loki/latest/operations/authentication/
private const val CUSTOM_HEADER_TENANT_ID = "X-Scope-OrgID"

@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {

  private var restClient = RestClient.builder().baseUrl(csmPlatformProperties.loki.baseUrl).build()

  fun getPodLogs(namespace: String, podName: String) = execRequest(namespace, podName)

  fun getPodsLogs(namespace: String, podNames: List<String>): Map<String, String> {
    val podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it) }
    return podsLogs
  }

  private fun execRequest(namespace: String, podName: String): String {
    // TODO Change it regarding sjoubert remarks
    val startTime =
        OffsetDateTime.now()
            .minusDays(csmPlatformProperties.loki.queryDaysAgo)
            .toInstant()
            .toEpochMilli()
            .toString()
    val endTime = OffsetDateTime.now().toInstant().toEpochMilli().toString()

    val parameters = buildParameters(namespace, podName, startTime, endTime)

    return restClient
        .get()
        .uri { uriBuilder ->
          UriComponentsBuilder.fromUri(
                  uriBuilder.path(csmPlatformProperties.loki.queryPath).build())
              .queryParams(parameters)
              .build()
              .toUri()
        }
        .header(
            HttpHeaders.CONTENT_TYPE, "application/x-ndjson", "application/x-www-form-urlencoded")
        .header(CUSTOM_HEADER_TENANT_ID, csmPlatformProperties.namespace)
        .retrieve()
        .body<String>()!!
  }

  private fun buildParameters(
      namespace: String,
      podName: String,
      startTime: String,
      endTime: String
  ): LinkedMultiValueMap<String, String> {
    val params = LinkedMultiValueMap<String, String>()
    params.add("query", getQuery(namespace, podName))
    params.add("start", startTime)
    params.add("end", endTime)
    return params
  }

  private fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"
}
