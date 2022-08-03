// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import com.cosmotech.api.utils.getCurrentAuthenticatedRoles
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CsmAdmin {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun verifyRolesAdmin(roles: List<String>): Boolean {
    return roles.contains(ROLE_PLATFORM_ADMIN)
  }

  fun verifyCurrentRolesAdmin(): Boolean {
    return this.verifyRolesAdmin(getCurrentAuthenticatedRoles())
  }
}
