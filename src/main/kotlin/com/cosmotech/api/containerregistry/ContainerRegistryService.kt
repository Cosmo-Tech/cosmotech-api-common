// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.exceptions.CsmResourceNotFoundException
import java.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Service("csmContainerRegistry")
class ContainerRegistryService(private val csmPlatformProperties: CsmPlatformProperties) {

  private val restClient =
      RestClient.builder().baseUrl(csmPlatformProperties.containerRegistry.registryUrl).build()

  private fun getHeaderAuthorization(): String {
    val user = csmPlatformProperties.containerRegistry.registryUserName!!
    val password = csmPlatformProperties.containerRegistry.registryPassword!!
    val basicToken = Base64.getEncoder().encodeToString("$user:$password".toByteArray())
    return "Basic $basicToken"
  }

  fun checkSolutionImage(repository: String, tag: String) {
    try {
      val images =
          restClient
              .get()
              .uri("/v2/$repository/tags/list")
              .header(HttpHeaders.AUTHORIZATION, getHeaderAuthorization())
              .retrieve()
              .body(String::class.java)!!

      val tags: JSONArray = JSONObject(images).get("tags") as JSONArray
      if (!tags.contains(tag)) {
        throw CsmResourceNotFoundException("$repository:$tag")
      }
    } catch (e: RestClientException) {
      throw CsmClientException(
          "The repository $repository:$tag check has thrown error : " + e.message, e)
    }
  }
}
