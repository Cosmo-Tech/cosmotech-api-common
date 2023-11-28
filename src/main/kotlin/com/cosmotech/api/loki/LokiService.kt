// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.loki

import com.cosmotech.api.config.CsmPlatformProperties
import java.time.OffsetDateTime
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

// Needed for authentication in multitenant mode
// https://grafana.com/docs/loki/latest/operations/authentication/
private const val CUSTOM_HEADER_TENANT_ID = "X-Scope-OrgID"

@Service("csmLoki")
class LokiService(private val csmPlatformProperties: CsmPlatformProperties) {
  private fun getLokiQueryURI(): String {
    return csmPlatformProperties.loki.baseUrl + csmPlatformProperties.loki.queryPath
  }

  private fun execRequest(namespace: String, podName: String): String {
    val httpResponse = getHttpClient().execute(buildHttpRequest(namespace, podName))
    return EntityUtils.toString(httpResponse.getEntity())
  }
  fun getPodLogs(namespace: String, podName: String) = execRequest(namespace, podName)

  fun getPodsLogs(namespace: String, podNames: List<String>): Map<String, String> {
    var podsLogs = mutableMapOf<String, String>()
    podNames.forEach { podsLogs[it] = getPodLogs(namespace, it)!! }
    return podsLogs
  }

  internal fun getQuery(namespace: String, podName: String) =
      "{namespace=\"$namespace\",pod=\"$podName\"}"

  private fun getHttpClient() = HttpClients.createDefault()

  private fun buildHttpRequest(namespace: String, podName: String): HttpUriRequest {
    var reqBuilder = RequestBuilder.get()
    reqBuilder = reqBuilder.setUri(getLokiQueryURI())
    reqBuilder = reqBuilder.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-ndjson")
    reqBuilder = reqBuilder.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
    reqBuilder = reqBuilder.addHeader(CUSTOM_HEADER_TENANT_ID, csmPlatformProperties.namespace)
    reqBuilder = reqBuilder.addParameter("query", getQuery(namespace, podName))
    val startTime =
        OffsetDateTime.now().minusDays(csmPlatformProperties.loki.queryDaysAgo).toString()
    val endTime = OffsetDateTime.now().toString()
    reqBuilder = reqBuilder.addParameter("start", startTime)
    reqBuilder = reqBuilder.addParameter("end", endTime)
    return reqBuilder.build()
  }
}
