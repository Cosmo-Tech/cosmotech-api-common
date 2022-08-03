package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import org.springframework.beans.factory.annotation.Autowired
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.cosmotech.api.security.ROLE_PLATFORM_ADMIN
import com.cosmotech.api.utils.getCurrentAuthenticatedUserName
import com.cosmotech.api.utils.getCurrentAuthenticatedRoles

class CsmRbac(val roleDefinition: RoleDefinition) {
  @Autowired lateinit var csmPlatformProperties: CsmPlatformProperties
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun verifyOwner(ownerId: String, userId: String): Boolean {
    logger.debug("Verifying $userId is Owner")
    return ownerId.equals(userId)
  }

  fun verifyCurrentOwner(ownerId: String): Boolean {
    return this.verifyOwner(ownerId, getCurrentAuthenticatedUserName())
  }

  fun verifyRolesAdmin(roles: List<String>): Boolean {
    return roles.contains(ROLE_PLATFORM_ADMIN)
  }

  fun verifyCurrentRolesAdmin(): Boolean {
    return verifyRolesAdmin(getCurrentAuthenticatedRoles())
  }

  fun verifyPermission(permission: String, userPermissions: List<String>): Boolean {
    return userPermissions.contains(permission)
  }

  fun verifyPermissionFromRole(permission: String, role: String): Boolean {
    return true
  }

  fun getRolePermissions(role: String): List<String> {
    return this.roleDefinition.permissions.get(role) ?: listOf()
  }
}
