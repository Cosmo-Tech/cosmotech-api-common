// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.security.okta

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.security.AbstractSecurityConfiguration
import com.cosmotech.api.security.ROLE_ORGANIZATION_USER
import com.cosmotech.api.security.ROLE_ORGANIZATION_VIEWER
import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity(debug = true)
@ConditionalOnProperty(
    name = ["csm.platform.identityProvider.code"], havingValue = "okta", matchIfMissing = false)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
internal open class OktaSecurityConfiguration(
    csmPlatformProperties: CsmPlatformProperties,
) : AbstractSecurityConfiguration() {

  private val logger = LoggerFactory.getLogger(OktaSecurityConfiguration::class.java)
  private val organizationAdminGroup =
      csmPlatformProperties.identityProvider?.adminGroup ?: ROLE_PLATFORM_ADMIN
  private val organizationUserGroup =
      csmPlatformProperties.identityProvider?.userGroup ?: ROLE_ORGANIZATION_USER
  private val organizationViewerGroup =
      csmPlatformProperties.identityProvider?.viewerGroup ?: ROLE_ORGANIZATION_VIEWER

  @Bean
  open fun filterChain(http: HttpSecurity): SecurityFilterChain? {
    logger.info("Okta http security configuration")
    super.getOAuth2ResourceServer(
            http, organizationAdminGroup, organizationUserGroup, organizationViewerGroup)
        .oauth2ResourceServer { oauth2 -> oauth2.jwt {} }

    return http.build()
  }
}
