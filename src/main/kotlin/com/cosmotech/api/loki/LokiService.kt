// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.time.Duration
import java.time.Instant
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

private const val MILLI_TO_NANO = 1_000_000
// Use the default value of loki for limits_config.max_query_length
// This should be made dynamic based on the actual loki configuration in v4.0
// Loki default is actually 30d1h
private const val LOKI_MAX_QUERY_LENGTH_DAYS = 30L
// Use the default value of loki for limits_config.max_entries_limit_per_query
// This should be made dynamic based on the actual loki configuration in v4.0
private const val LOKI_MAX_ENTRIES_LIMIT_PER_QUERY = "5000"

// Needed for authentication in multitenant mode
// https://grafana.com/docs/loki/latest/operations/authentication/
private const val CUSTOM_HEADER_TENANT_ID = "X-Scope-OrgID"

@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {

  private var restClient = RestClient.builder().baseUrl(csmPlatformProperties.loki.baseUrl).build()

  fun getPodLogs(namespace: String, podName: String, startTime: Instant) =
      execRequest(namespace, podName, startTime)

  fun getPodsLogs(
      namespace: String,
      podNames: List<String>,
      startTime: Instant
  ): Map<String, String> {
    val podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it, startTime) }
    return podsLogs
  }

  private fun execRequest(namespace: String, podName: String, startTime: Instant): String {
    val startTimeNano = startTime.toEpochMilli() * MILLI_TO_NANO
    val endTimeNano =
        startTime.plus(Duration.ofDays(LOKI_MAX_QUERY_LENGTH_DAYS)).toEpochMilli() * MILLI_TO_NANO

    val parameters =
        buildParameters(namespace, podName, startTimeNano.toString(), endTimeNano.toString())

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
    params.add("limit", LOKI_MAX_ENTRIES_LIMIT_PER_QUERY)
    params.add("start", startTime)
    params.add("end", endTime)
    return params
  }

  private fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"
}
