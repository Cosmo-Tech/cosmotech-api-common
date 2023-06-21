// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
@file:JvmName("SecurityUtilsKt")

package com.cosmotech.api.utils

import com.cosmotech.api.config.CsmPlatformProperties
import com.nimbusds.jwt.JWTParser
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun getCurrentAuthentication(): Authentication? = SecurityContextHolder.getContext().authentication

fun getCurrentAuthenticatedUserName(configuration: CsmPlatformProperties): String {
  return getValueFromAuthenticatedToken {
    val jwtClaimsSet = JWTParser.parse(it).jwtClaimsSet
    jwtClaimsSet.getStringClaim("name")
        ?: jwtClaimsSet.getStringClaim(configuration.authorization.applicationIdJwtClaim)
        ?: throw IllegalStateException("User Authentication not found in Security Context")
  }
}

fun getCurrentAuthenticatedIssuer(): String {
  return getValueFromAuthenticatedToken { JWTParser.parse(it).jwtClaimsSet.issuer }
}

fun getCurrentAccountIdentifier(configuration: CsmPlatformProperties): String {
  return getValueFromAuthenticatedToken {
    val jwtClaimsSet = JWTParser.parse(it).jwtClaimsSet
    jwtClaimsSet.getStringClaim(configuration.authorization.mailJwtClaim)
        ?: jwtClaimsSet.getStringClaim(configuration.authorization.applicationIdJwtClaim)
  }
}

fun getCurrentAuthenticatedRoles(configuration: CsmPlatformProperties): List<String> {
  return getValueFromAuthenticatedToken {
    JWTParser.parse(it).jwtClaimsSet.getStringListClaim(configuration.authorization.rolesJwtClaim)
  }
}

fun <T> getValueFromAuthenticatedToken(actionLambda: (String) -> T): T {
  if (getCurrentAuthentication() == null) {
    throw IllegalStateException("User Authentication not found in Security Context")
  }
  val authentication = getCurrentAuthentication()
  if (authentication is JwtAuthenticationToken) {
    return authentication.token.tokenValue.let { actionLambda(it) }
  }
  return (authentication as BearerTokenAuthentication).token.tokenValue.let { actionLambda(it) }
}
