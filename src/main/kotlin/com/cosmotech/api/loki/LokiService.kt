// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.time.OffsetDateTime
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.time.Duration
import java.time.Instant
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

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
  private fun getLokiQueryURI(): String {
    return csmPlatformProperties.loki.baseUrl + csmPlatformProperties.loki.queryPath
  }

  private fun execRequest(namespace: String, podName: String, startTime: Instant): String {
    val httpResponse = getHttpClient().execute(buildHttpRequest(namespace, podName,startTime))
    return EntityUtils.toString(httpResponse.getEntity())
  }
  fun getPodLogs(namespace: String, podName: String, startTime: Instant) = execRequest(namespace, podName, startTime)

  fun getPodsLogs(namespace: String, podNames: List<String>, startTime: Instant): Map<String, String> {
    var podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it, startTime)!! }
    return podsLogs
  }

  internal fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"

  private fun getHttpClient() = HttpClients.createDefault()

  private fun buildHttpRequest(namespace: String, podName: String, startTime: Instant): HttpUriRequest {
    var reqBuilder = RequestBuilder.get()
    reqBuilder = reqBuilder.setUri(getLokiQueryURI())
    reqBuilder = reqBuilder.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-ndjson")
    reqBuilder = reqBuilder.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
    reqBuilder = reqBuilder.addHeader(CUSTOM_HEADER_TENANT_ID, csmPlatformProperties.namespace)
    reqBuilder = reqBuilder.addParameter("query", getQuery(namespace, podName))
    reqBuilder = reqBuilder.addParameter("limit", LOKI_MAX_ENTRIES_LIMIT_PER_QUERY)

      val startTimeNano = startTime.toEpochMilli() * MILLI_TO_NANO
      val endTimeNano =
          startTime.plus(Duration.ofDays(LOKI_MAX_QUERY_LENGTH_DAYS)).toEpochMilli() * MILLI_TO_NANO

    reqBuilder = reqBuilder.addParameter("start", startTimeNano)
    reqBuilder = reqBuilder.addParameter("end", endTimeNano)
    return reqBuilder.build()
  }
}
