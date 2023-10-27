// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.time.OffsetDateTime
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.net.URIBuilder
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

// Needed for authentication in multitenant mode
// https://grafana.com/docs/loki/latest/operations/authentication/
private const val CUSTOM_HEADER_TENANT_ID = "X-Scope-OrgID"

@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {

  fun getPodLogs(namespace: String, podName: String) = execRequest(namespace, podName)

  fun getPodsLogs(namespace: String, podNames: List<String>): Map<String, String> {
    var podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it) }
    return podsLogs
  }

  private fun getLokiQueryURI(): String {
    return csmPlatformProperties.loki.baseUrl + csmPlatformProperties.loki.queryPath
  }

  private fun execRequest(namespace: String, podName: String): String {
    // TODO Change it regarding sjoubert remarks
    val startTime =
        OffsetDateTime.now().minusDays(csmPlatformProperties.loki.queryDaysAgo).toString()
    val endTime = OffsetDateTime.now().toString()

    val parameters = buildParameters(namespace, podName, startTime, endTime)

    val uri = URIBuilder(getLokiQueryURI()).addParameters(parameters).build()

    val response =
        Request.get(uri)
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-ndjson")
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .execute()

    return response.toString()
  }

  private fun buildParameters(
      namespace: String,
      podName: String,
      startTime: String,
      endTime: String
  ) =
      mutableListOf<NameValuePair>(
          BasicNameValuePair("query", getQuery(namespace, podName)),
          BasicNameValuePair("start", startTime),
          BasicNameValuePair("end", endTime))

  private fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"
}
