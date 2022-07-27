// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

import com.cosmotech.api.config.CsmPlatformProperties
import com.nimbusds.jwt.JWTParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication

@Autowired lateinit var configuration: CsmPlatformProperties

fun getCurrentAuthentication(): Authentication? = SecurityContextHolder.getContext().authentication

fun getCurrentUserName(): String? = getCurrentAuthentication()?.name

fun getCurrentAuthenticatedUserName() =
    getCurrentUserName()
        ?: throw IllegalStateException("User Authentication not found in Security Context")

fun getCurrentAuthenticatedIssuer(): String {
  if (getCurrentAuthentication() == null) {
    throw IllegalStateException("User Authentication not found in Security Context")
  }

  val authentication = getCurrentAuthentication() as BearerTokenAuthentication
  return authentication.token.tokenValue.let { JWTParser.parse(it).jwtClaimsSet.issuer }
}

fun getCurrentAuthenticatedMail(): String {
  if (getCurrentAuthentication() == null) {
    throw IllegalStateException("User Authentication not found in Security Context")
  }

  val authentication = getCurrentAuthentication() as BearerTokenAuthentication
  return authentication.token.tokenValue.let {
    JWTParser.parse(it).jwtClaimsSet.getStringClaim(configuration.authorization.mailJwtClaim)
  }
}
