// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
@file:JvmName("SecurityUtilsKt")

package com.cosmotech.api.utils

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.security.ApiKeyAuthentication
import com.nimbusds.jose.util.JSONObjectUtils
import com.nimbusds.jwt.JWTClaimNames
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import java.text.ParseException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun getCurrentAuthentication(): Authentication? = SecurityContextHolder.getContext().authentication

fun getCurrentAuthenticatedUserName(configuration: CsmPlatformProperties): String {
  return getValueFromAuthenticatedToken {
    try {
      val jwtClaimsSet = JWTParser.parse(it).jwtClaimsSet
      jwtClaimsSet.getStringClaim(configuration.authorization.principalJwtClaim)
          ?: jwtClaimsSet.getStringClaim(configuration.authorization.applicationIdJwtClaim)
              ?: throw IllegalStateException("User Authentication not found in Security Context")
    } catch (e: ParseException) {
      JSONObjectUtils.parse(it)[configuration.authorization.principalJwtClaim] as String
    }
  }
}

fun getCurrentAuthenticatedIssuer(): String {
  return getValueFromAuthenticatedToken {
    try {
      JWTParser.parse(it).jwtClaimsSet.issuer
    } catch (e: ParseException) {
      JSONObjectUtils.parse(it)[JWTClaimNames.ISSUER] as String
    }
  }
}

fun getCurrentAccountIdentifier(configuration: CsmPlatformProperties): String {
  return getValueFromAuthenticatedToken {
    try {
      val jwtClaimsSet = JWTParser.parse(it).jwtClaimsSet
      jwtClaimsSet.getStringClaim(configuration.authorization.mailJwtClaim)
          ?: jwtClaimsSet.getStringClaim(configuration.authorization.applicationIdJwtClaim)
    } catch (e: ParseException) {
      JSONObjectUtils.parse(it)[configuration.authorization.mailJwtClaim] as String
    }
  }
}

fun getCurrentAuthenticatedRoles(configuration: CsmPlatformProperties): List<String> {
  return (getValueFromAuthenticatedToken {
    try {
      val jwt = JWTParser.parse(it)
      jwt.jwtClaimsSet.getStringListClaim(configuration.authorization.rolesJwtClaim)
    } catch (e: ParseException) {
      JSONObjectUtils.parse(it)[configuration.authorization.rolesJwtClaim] as List<String>
    }
  }
      ?: emptyList())
}

fun <T> getValueFromAuthenticatedToken(actionLambda: (String) -> T): T {
  if (getCurrentAuthentication() == null) {
    throw IllegalStateException("User Authentication not found in Security Context")
  }
  val authentication = getCurrentAuthentication()
  if (authentication is JwtAuthenticationToken) {
    return authentication.token.tokenValue.let { actionLambda(it) }
  }
  if (authentication is ApiKeyAuthentication) {
    return actionLambda(
        JWTClaimsSet.Builder()
            .issuer(authentication.apiKey)
            .claim("oid", "toto@gcwdcw.com")
            .claim(
                "roles",
                authentication.authorities
                    .map { (it as SimpleGrantedAuthority).authority }
                    .toList())
            .build()
            .toString())
  }
  return (authentication as BearerTokenAuthentication).token.tokenValue.let { actionLambda(it) }
}
