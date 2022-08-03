// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmAccessForbiddenException
import com.cosmotech.api.exceptions.CsmClientException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@Suppress("TooManyFunctions")
class CsmRbac(
    val resourceId: String,
    val rolesDefinition: RolesDefinition,
    val resourceSecurity: ResourceSecurity = ResourceSecurity(),
) {
  @Autowired lateinit var csmPlatformProperties: CsmPlatformProperties
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val csmAdmin = CsmAdmin()

  // This is the default method to call to check RBAC
  fun verify(permission: String, user: String) {
    if (!check(permission, user))
        throw CsmAccessForbiddenException("RBAC $resourceId - User $user does not have permission $permission")
  }

  fun check(permission: String, user: String): Boolean {
    logger.info("RBAC $resourceId - Verifying permission $permission for user $user")
    return (this.isAdmin(user) || this.verifyRbac(permission, user))
  }

  fun getUserInfo(user: String): UserInfo {
    logger.info("RBAC $resourceId - Getting user info: $user")
    val roles = this.getRoles(user)
    val permissions = roles.flatMap { this.getRolePermissions(it) }.toSet().toList()
    return UserInfo(id = user, roles = roles, permissions = permissions)
  }

  fun setDefault(roles: List<String>) {
    logger.info("RBAC $resourceId - Setting default security")
    this.verifyRolesOrThrow(roles)
    this.resourceSecurity.default = roles
  }

  fun setUserRoles(user: String, roles: List<String>) {
    logger.info("RBAC $resourceId - Setting user $user roles")
    this.verifyRolesOrThrow(roles)
    val currentRoles = this.getRoles(user)
    val adminRole = this.getAdminRole()
    if (currentRoles.contains(adminRole) &&
        (!roles.contains(adminRole)) &&
        this.getAdminCount() == 1) {
      throw CsmAccessForbiddenException("RBAC $resourceId - It is forbidden to unset the last administrator")
    }
    this.resourceSecurity.accessControlList.roles.put(user, roles)
  }

  fun removeUser(user: String) {
    logger.info("RBAC $resourceId - Removing user $user from security")
    val roles = this.getRoles(user)
    if (roles.contains(this.getAdminRole()) && this.getAdminCount() == 1) {
      throw CsmAccessForbiddenException("RBAC $resourceId - It is forbidden to remove the last administrator")
    }

    this.resourceSecurity.accessControlList.roles.remove(user)
  }

  internal fun verifyRolesOrThrow(roles: List<String>) {
    roles.forEach {
      if (!this.rolesDefinition.permissions.keys.contains(it))
          throw CsmClientException("RBAC $resourceId - Role $it does not exist")
    }
  }

  internal fun verifyPermission(permission: String, userPermissions: List<String>): Boolean {
    return userPermissions.contains(permission)
  }

  internal fun verifyPermissionFromRole(permission: String, role: String): Boolean {
    return this.verifyPermission(permission, this.getRolePermissions(role))
  }

  internal fun verifyPermissionFromRoles(permission: String, roles: List<String>): Boolean {
    return roles.any { role -> this.verifyPermissionFromRole(permission, role) }
  }

  internal fun getRolePermissions(role: String): List<String> {
    return this.rolesDefinition.permissions.get(role) ?: listOf()
  }

  internal fun getRoles(user: String): List<String> {
    return this.resourceSecurity.accessControlList.roles.get(user) ?: listOf()
  }

  internal fun verifyUser(permission: String, user: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying $user has permission: $permission")
    return this.verifyPermissionFromRoles(permission, getRoles(user))
  }

  internal fun verifyDefault(permission: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying default roles for permission: $permission")
    return this.verifyPermissionFromRoles(permission, this.resourceSecurity.default)
  }

  internal fun verifyAdminRole(user: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying if $user has default admin rbac role")
    return this.getRoles(user).contains(this.getAdminRole())
  }

  internal fun isAdminToken(user: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying if $user has platform admin role in token")
    return csmAdmin.verifyCurrentRolesAdmin()
  }

  internal fun verifyRbac(permission: String, user: String): Boolean {
    return (this.verifyDefault(permission) || this.verifyUser(permission, user))
  }

  internal fun isAdmin(user: String): Boolean {
    return (this.isAdminToken(user) || this.verifyAdminRole(user))
  }

  internal fun getAdminRole(): String {
    return this.rolesDefinition.adminRole
  }

  internal fun getAdminCount(): Int {
    return (this.resourceSecurity
        .accessControlList
        .roles
        .filterValues { it.contains(this.getAdminRole()) }
        .count())
  }
}
