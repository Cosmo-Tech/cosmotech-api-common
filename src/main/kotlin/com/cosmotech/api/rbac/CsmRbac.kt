// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class CsmRbac(
    val rolesDefinition: RolesDefinition,
    val resourceSecurity: ResourceSecurity = ResourceSecurity(),
) {
  @Autowired lateinit var csmPlatformProperties: CsmPlatformProperties
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val csmAdmin = CsmAdmin()

  fun verifyPermission(permission: String, userPermissions: List<String>): Boolean {
    return userPermissions.contains(permission)
  }

  fun verifyPermissionFromRole(permission: String, role: String): Boolean {
    return this.verifyPermission(permission, this.getRolePermissions(role))
  }

  fun verifyPermissionFromRoles(permission: String, roles: List<String>): Boolean {
    return roles.any { role -> this.verifyPermissionFromRole(permission, role) }
  }

  fun getRolePermissions(role: String): List<String> {
    return this.rolesDefinition.permissions.get(role) ?: listOf()
  }

  fun getRoles(user: String): List<String> {
    return this.resourceSecurity.accessControlList.roles.get(user) ?: listOf()
  }

  fun verifyUser(permission: String, user: String): Boolean {
    logger.debug("Verifying $user has $permission permission")
    return this.verifyPermissionFromRoles(permission, getRoles(user))
  }

  fun verifyDefault(permission: String): Boolean {
    logger.debug("Verifying default roles for $permission permission")
    return this.verifyPermissionFromRoles(permission, this.resourceSecurity.default)
  }

  fun setUserRoles(user: String, roles: List<String>) {
    logger.debug("Adding user $user to security")
    this.resourceSecurity.accessControlList.roles.put(user, roles)
  }

  fun removeUser(user: String) {
    logger.debug("Removing user $user to security")
    this.resourceSecurity.accessControlList.roles.remove(user)
  }

  fun verifyRbac(permission: String, user: String): Boolean {
    return (this.verifyDefault(permission) || this.verifyUser(permission, user))
  }

  fun setDefault(roles: List<String>) {
    this.resourceSecurity.default = roles
  }

  // This is the default method to call to check RBAC
  fun verify(permission: String, user: String): Boolean {
    return (csmAdmin.verifyCurrentRolesAdmin() || this.verifyRbac(permission, user))
  }

  fun isAdmin(user: String): Boolean {
    return csmAdmin.verifyCurrentRolesAdmin()
  }

  fun getAdminRole(): String {
    return this.rolesDefinition.adminRole
  }
}
