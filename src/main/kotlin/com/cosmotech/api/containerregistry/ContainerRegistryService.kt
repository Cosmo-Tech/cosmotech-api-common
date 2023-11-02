// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmResourceNotFoundException
import java.util.Base64
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service("csmContainerRegistry")
class ContainerRegistryService(private val csmPlatformProperties: CsmPlatformProperties) {
  private val logger = LoggerFactory.getLogger(ContainerRegistryService::class.java)

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
    val tags = getRepositoryTagList(repository)
    logger.info("TOOOOOOOOOOOOo 24 octobre tags => {}", tags)
    if (tags.contains("errors")) {
      throw CsmResourceNotFoundException(tags)
    }
    if (!doesTheTagExist(tags, tag)) {
      throw CsmResourceNotFoundException(tags)
    }
    logger.info("TOOOOOOOOOOOOo 24 octobre image found => {}", true)
  }

  fun doesTheTagExist(tags: String, tag: String) = tags.contains(tag)

  private fun getImageRegistryUri(repository: String): String {
    return csmPlatformProperties.containerRegistry.registryUrl + "/v2/" + repository + "/tags/list"
  }

  private fun execRequest(repository: String): String {
    val httpResponse = getHttpClient().execute(buildHttpRequest(repository))
    return EntityUtils.toString(httpResponse.getEntity())
  }
  fun getRepositoryTagList(repository: String) = execRequest(repository)

  private fun getHttpClient() = HttpClients.createDefault()
}
