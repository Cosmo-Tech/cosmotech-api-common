// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.Instant
import kotlin.time.Duration
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

// Needed for authentication in multitenant mode
// https://grafana.com/docs/loki/latest/operations/authentication/
private const val CUSTOM_HEADER_TENANT_ID = "X-Scope-OrgID"
private const val MILLI_TO_NANO = 1_000_000

@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  data class LokiConfig(@JsonProperty("limits_config") val limitsConfig: LokiLimitsConfig) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class LokiLimitsConfig(
        @JsonProperty("max_query_length") val maxQueryLength: String,
        @JsonProperty("max_entries_limit_per_query") val maxEntriesLimitPerQuery: Long
    )
  }

  private var lokiConfig: LokiConfig? = null

  private var restClient =
      RestClient.builder()
          .baseUrl(csmPlatformProperties.loki.baseUrl)
          .defaultHeader(
              HttpHeaders.CONTENT_TYPE, "application/x-ndjson", "application/x-www-form-urlencoded")
          .defaultHeader(CUSTOM_HEADER_TENANT_ID, csmPlatformProperties.namespace)
          .build()

  private fun getLokiConfig(): LokiConfig {
    if (lokiConfig == null) {
      lokiConfig =
          ObjectMapper(YAMLFactory())
              .readValue<LokiConfig>(
                  restClient.get().uri("/config").retrieve().body(String::class.java)!!)
    }
    return lokiConfig!!
  }

  fun getPodLogs(namespace: String, podName: String, startTime: Instant): List<String> {
    val lokiConfig = getLokiConfig()
    val maxQueryLengthNano =
        Duration.parse(lokiConfig.limitsConfig.maxQueryLength).inWholeNanoseconds

    val startTimeNano = startTime.toEpochMilli() * MILLI_TO_NANO
    val endTimeNano = startTimeNano + maxQueryLengthNano

    val params = LinkedMultiValueMap<String, String>()
    params.add("query", "{namespace=\"$namespace\",pod=\"$podName\",container=\"main\"}")
    params.add("direction", "forward")
    params.add("limit", lokiConfig.limitsConfig.maxEntriesLimitPerQuery.toString())
    params.add("start", startTimeNano.toString())
    params.add("end", endTimeNano.toString())

    var logs = listOf<String>()
    do {
      val response =
          restClient
              .get()
              .uri {
                UriComponentsBuilder.fromUri(it.path("/loki/api/v1/query_range").build())
                    .queryParams(params)
                    .build()
                    .toUri()
              }
              .retrieve()
              .body(String::class.java)!!
      val logEntries =
          JSONObject(response)
              .getJSONObject("data")
              .getJSONArray("result")
              .flatMap { (it as JSONObject).getJSONArray("values") }
              .map { it as JSONArray }
              .sortedBy { it.getString(0) }

      logs += logEntries.map { it.getString(1) }

      // Update new range start to the next nanosecond after the last log entry and adjust end
      logEntries.lastOrNull()?.let {
        val newStartTime = it.getLong(0) + 1
        params.set("start", newStartTime.toString())
        params.set("end", (newStartTime + maxQueryLengthNano).toString())
      }
    } while (logEntries.size >= lokiConfig.limitsConfig.maxEntriesLimitPerQuery)

    return logs
  }
}
