// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.exceptions.CsmResourceNotFoundException
import java.util.Base64
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.HttpStatus.SC_OK
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service("csmContainerRegistry")
class ContainerRegistryService(private val csmPlatformProperties: CsmPlatformProperties) {
  fun getEndpoint() = csmPlatformProperties.containerRegistry.registryUrl

  private fun getCredentials(user: String, password: String): String =
      Base64.getEncoder().encodeToString("$user:$password".toByteArray())

  private fun getHeaderAuthorization() =
      "Basic " +
          getCredentials(
              csmPlatformProperties.containerRegistry.registryUserName!!,
              csmPlatformProperties.containerRegistry.registryPassword!!)

  private fun buildRequestClient(repository: String): Request {

    return Request.get(getImageRegistryUri(repository))
        .addHeader(HttpHeaders.AUTHORIZATION, getHeaderAuthorization())
  }

  fun checkSolutionImage(repository: String, tag: String) {
    val images = getRepositoryTagList(repository)
    if (!doesTheTagExist(images, tag)) {
      throw CsmResourceNotFoundException("$repository:$tag")
    }
  }

  private fun doesTheTagExist(images: String, tag: String): Boolean {
    val tags: JSONArray = JSONObject(images).get("tags") as JSONArray
    return tags.contains(tag)
  }

  private fun getImageRegistryUri(repository: String): String {
    return csmPlatformProperties.containerRegistry.registryUrl + "/v2/" + repository + "/tags/list"
  }

  private fun execRequest(repository: String): String {
    val response = buildRequestClient(repository).execute()
    val httpResponse = response.returnResponse()
    val content = response.returnContent()
    if (SC_OK != httpResponse.code) {
      throw throw CsmClientException("The repository $repository : " + httpResponse.reasonPhrase)
    }
    return content.toString()
  }

  fun getRepositoryTagList(repository: String) = execRequest(repository)
}
