// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.security.keycloak

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.security.AbstractSecurityConfiguration
import com.cosmotech.api.security.ROLE_ORGANIZATION_USER
import com.cosmotech.api.security.ROLE_ORGANIZATION_VIEWER
import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import java.util.Collections
import java.util.stream.Collectors
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.jwt.JwtClaimValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

@Configuration
@EnableWebSecurity(debug = true)
@ConditionalOnProperty(
    name = ["csm.platform.identityProvider.code"], havingValue = "keycloak", matchIfMissing = false)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
internal open class KeycloakSecurityConfiguration(
    private val oAuth2ResourceServerProperties: OAuth2ResourceServerProperties,
    private val csmPlatformProperties: CsmPlatformProperties
) : AbstractSecurityConfiguration() {

  private val logger = LoggerFactory.getLogger(KeycloakSecurityConfiguration::class.java)
  private val organizationAdminGroup =
      csmPlatformProperties.identityProvider?.adminGroup ?: ROLE_PLATFORM_ADMIN
  private val organizationUserGroup =
      csmPlatformProperties.identityProvider?.userGroup ?: ROLE_ORGANIZATION_USER
  private val organizationViewerGroup =
      csmPlatformProperties.identityProvider?.viewerGroup ?: ROLE_ORGANIZATION_VIEWER

  override fun configure(http: HttpSecurity) {
    logger.info("Keycloak http security configuration")

    super.getOAuth2ResourceServer(
            http, organizationAdminGroup, organizationUserGroup, organizationViewerGroup)
        .jwt()
        .decoder(keycloakJwtDecoder(oAuth2ResourceServerProperties, csmPlatformProperties))
        .jwtAuthenticationConverter(KeycloakJwtAuthenticationConverter(csmPlatformProperties))
  }

  @Bean
  open fun keycloakJwtDecoder(
      oAuth2ResourceServerProperties: OAuth2ResourceServerProperties,
      csmPlatformProperties: CsmPlatformProperties
  ): JwtDecoder {
    val jwtProperties = oAuth2ResourceServerProperties.jwt
    val nimbusJwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(jwtProperties.jwkSetUri)
            .jwsAlgorithms { signatureAlgorithms: MutableSet<SignatureAlgorithm> ->
              for (algorithm in jwtProperties.jwsAlgorithms) {
                signatureAlgorithms.add(SignatureAlgorithm.from(algorithm))
              }
            }
            .build()

    // Timestamp and Issuer
    val issuerUri = jwtProperties.issuerUri
    val validators = mutableListOf(JwtValidators.createDefaultWithIssuer(issuerUri))
    // Audience
    val audienceValidator =
        JwtClaimValidator(JwtClaimNames.AUD) { aud: List<String> ->
          !Collections.disjoint(aud, jwtProperties.audiences)
        }

    validators.add(audienceValidator)

    // Tenant
    // With the assumption that 1 tenant = 1 realm (which cannot be the case once multi-tenancy will
    // be done)
    val allowedTenants = csmPlatformProperties.authorization.allowedTenants

    if ("*" in allowedTenants) {
      logger.info(
          "All tenants allowed to authenticate, since the following property contains a wildcard " +
              "element: csm.platform.authorization.allowed-tenants")
    } else {
      // Validate against the list of allowed tenants
      val tenantValidator =
          JwtClaimValidator(JwtClaimNames.ISS) { issuer: String ->
            val issuerSplit = issuer.split("realms/")
            if (issuerSplit.size > 1) {
              allowedTenants.contains(issuerSplit[1])
            } else {
              false
            }
          }
      validators.add(tenantValidator)
    }

    nimbusJwtDecoder.setJwtValidator(DelegatingOAuth2TokenValidator(validators))
    return nimbusJwtDecoder
  }
}

class KeycloakJwtAuthenticationConverter(private val csmPlatformProperties: CsmPlatformProperties) :
    JwtAuthenticationConverter() {

  private val logger = LoggerFactory.getLogger(KeycloakJwtAuthenticationConverter::class.java)

  @Deprecated("Deprecated in Java")
  override fun extractAuthorities(jwt: Jwt): MutableCollection<GrantedAuthority> {
    val extractAuthorities = super.extractAuthorities(jwt)
    extractAuthorities.addAll(
        convertRolesToAuthorities(jwt.claims, csmPlatformProperties.authorization.rolesJwtClaim))
    return extractAuthorities
  }

  private fun convertRolesToAuthorities(
      attributes: Map<String, Any>,
      claimKey: String
  ): MutableCollection<GrantedAuthority> {
    if (!CollectionUtils.isEmpty(attributes) && StringUtils.hasText(claimKey)) {
      val rawRoleClaim = attributes[claimKey]
      if (rawRoleClaim is Collection<*>) {
        return (rawRoleClaim as Collection<String>)
            .stream()
            .map { role: String -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())
      } else if (rawRoleClaim != null) {
        logger.debug(
            "Could not extract authorities from claim '{}', value was not a collection", claimKey)
      }
    }
    return mutableSetOf()
  }
}
