// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.exceptions.CsmResourceNotFoundException
import java.util.Base64
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service("csmContainerRegistry")
class ContainerRegistryService(private val csmPlatformProperties: CsmPlatformProperties) {
  fun getEndpoint() = csmPlatformProperties.containerRegistry.registryUrl

  fun getCredentials(user: String, password: String) =
      Base64.getEncoder().encodeToString("$user:$password".toByteArray())

  fun getHeaderAuthorization() =
      "Basic " +
          getCredentials(
              csmPlatformProperties.containerRegistry.registryUserName!!,
              csmPlatformProperties.containerRegistry.registryPassword!!)

  private fun buildHttpRequest(repository: String): HttpUriRequest {
    var reqBuilder = RequestBuilder.get()
    reqBuilder = reqBuilder.setUri(getImageRegistryUri(repository))
    reqBuilder = reqBuilder.addHeader(HttpHeaders.AUTHORIZATION, getHeaderAuthorization())

    return reqBuilder.build()
  }

  fun checkSolutionImage(repository: String, tag: String) {
    val images = getRepositoryTagList(repository)
    if (!doesTheTagExist(images, tag)) {
      throw CsmResourceNotFoundException(repository + ":" + tag)
    }
  }

  fun doesTheTagExist(images: String, tag: String): Boolean {
    val tags: JSONArray = JSONObject(images).get("tags") as JSONArray
    return tags.contains(tag)
  }

  private fun getImageRegistryUri(repository: String): String {
    return csmPlatformProperties.containerRegistry.registryUrl + "/v2/" + repository + "/tags/list"
  }

  private fun execRequest(repository: String): String {
    val httpResponse = getHttpClient().execute(buildHttpRequest(repository))
    if (HttpStatus.SC_OK != httpResponse.statusLine.statusCode) {
      throw throw CsmClientException(
          "The repository ${repository} : " + "${httpResponse.statusLine.reasonPhrase}")
    }
    return EntityUtils.toString(httpResponse.getEntity())
  }
  fun getRepositoryTagList(repository: String) = execRequest(repository)

  private fun getHttpClient() = HttpClients.createDefault()
}
