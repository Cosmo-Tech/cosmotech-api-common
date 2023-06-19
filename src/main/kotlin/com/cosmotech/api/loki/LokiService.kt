// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.net.URLEncoder
import java.time.OffsetDateTime
import java.time.ZoneOffset
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

private const val LOKI_QUERY_PATH = "/loki/api/v1/query_range"
private const val LOKI_QUERY_DAYS_AGO: Long = 1
@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {
  private fun getLokiQueryURI(): String {
    return csmPlatformProperties.loki?.baseUrl + LOKI_QUERY_PATH
  }

  private fun execRequest(namespace: String, podName: String) =
      getWebClient()
          .get()
          .uri(getUriBuilder(namespace, podName).build(true).toUri())
          .retrieve()
          .bodyToMono<String>(String::class.java)
          .block()

  fun getPodLogs(namespace: String, podName: String) = execRequest(namespace, podName)

  fun getPodsLogs(namespace: String, podNames: List<String>): Map<String, String> {
    var podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it)!! }
    return podsLogs
  }

  private fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"

  private fun getUriBuilder(namespace: String, podName: String): UriComponentsBuilder {
    val startTime =
        OffsetDateTime.now(ZoneOffset.UTC)
            .minusDays(csmPlatformProperties.loki?.queryDaysAgo ?: LOKI_QUERY_DAYS_AGO)
            .toString()
    val endTime = OffsetDateTime.now().toString()
    return UriComponentsBuilder.fromUriString(getLokiQueryURI())
        .queryParam("query", URLEncoder.encode(getQuery(namespace, podName)))
        .queryParam("start", URLEncoder.encode(startTime))
        .queryParam("end", URLEncoder.encode(endTime))
  }

  private fun getWebClient() =
      WebClient.builder()
          .baseUrl(getLokiQueryURI())
          .defaultHeader(
              HttpHeaders.CONTENT_TYPE,
              MediaType.APPLICATION_FORM_URLENCODED_VALUE,
              MediaType.APPLICATION_NDJSON_VALUE)
          .build()
}
