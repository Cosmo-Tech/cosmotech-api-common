// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.utils.getCurrentAuthenticatedUserName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CsmNoRbac {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val csmAdmin = CsmAdmin()

  fun verifyOwner(ownerId: String, userId: String): Boolean {
    logger.debug("Verifying $userId is Owner")
    return ownerId.equals(userId)
  }

  fun verifyCurrentOwner(ownerId: String): Boolean {
    return this.verifyOwner(ownerId, getCurrentAuthenticatedUserName())
  }

  fun isAdmin(ownerId: String): Boolean {
    return (csmAdmin.verifyCurrentRolesAdmin() || this.verifyCurrentOwner(ownerId))
  }
}
