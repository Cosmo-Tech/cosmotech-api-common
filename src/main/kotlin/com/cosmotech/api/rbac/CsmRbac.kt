// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmAccessForbiddenException
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.utils.getCurrentAuthenticatedMail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Suppress("TooManyFunctions")
@Component
open class CsmRbac(
    protected val csmPlatformProperties: CsmPlatformProperties,
    protected val rolesDefinition: RolesDefinition,
    protected val csmAdmin: CsmAdmin
) {

  protected var resourceId: String = "Not defined"
  protected var resourceSecurity: ResourceSecurity = ResourceSecurity()

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  // This is the default method to call to check RBAC
  fun verify(permission: String) {
    if (!this.csmPlatformProperties.rbac.enabled) {
      logger.debug("RBAC $resourceId - RBAC not enabled")
      return
    }
    this.verify(permission, getCurrentAuthenticatedMail(this.csmPlatformProperties))
  }

  fun verify(permission: String, user: String) {
    if (!check(permission, user))
        throw CsmAccessForbiddenException(
            "RBAC $resourceId - User $user does not have permission $permission")
  }

  fun check(permission: String, user: String): Boolean {
    logger.info("RBAC $resourceId - Verifying permission $permission for user $user")
    if (!this.csmPlatformProperties.rbac.enabled) {
      logger.debug("RBAC $resourceId - RBAC not enabled")
      return true
    }
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

  fun setUserRoles(user: String, roles: List<String>, authorizedUsers: List<String>) {
    logger.info("RBAC $resourceId - Setting user $user roles with validation list")
    if (!authorizedUsers.contains(user))
        throw CsmClientException(
            "RBAC $resourceId - user $user not in the list of authorized users to be added. Check parent RBAC.")
    this.setUserRoles(user, roles)
  }

  fun setUserRoles(user: String, roles: List<String>) {
    logger.info("RBAC $resourceId - Setting user $user roles")
    this.verifyRolesOrThrow(roles)
    val currentRoles = this.getRoles(user)
    val adminRole = this.getAdminRole()
    if (currentRoles.contains(adminRole) &&
        (!roles.contains(adminRole)) &&
        this.getAdminCount() == 1) {
      throw CsmAccessForbiddenException(
          "RBAC $resourceId - It is forbidden to unset the last administrator")
    }
    this.resourceSecurity.accessControlList.roles.put(user, roles)
  }

  fun removeUser(user: String) {
    logger.info("RBAC $resourceId - Removing user $user from security")
    val roles = this.getRoles(user)
    if (roles.contains(this.getAdminRole()) && this.getAdminCount() == 1) {
      throw CsmAccessForbiddenException(
          "RBAC $resourceId - It is forbidden to remove the last administrator")
    }

    this.resourceSecurity.accessControlList.roles.remove(user)
  }

  fun setResourceInfo(newResourceId: String, newSecurity: ResourceSecurity) {
    this.resourceId = newResourceId
    this.resourceSecurity = newSecurity
  }
  fun setResourceInfo(
      newResourceId: String,
      default: List<String>,
      roles: MutableMap<String, List<String>>
  ) {
    this.resourceId = newResourceId
    this.resourceSecurity = createResourceSecurity(default, roles)
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
    logger.debug("RBAC $resourceId - Verifying $user has permission in ACL: $permission")
    val isAuthorized = this.verifyPermissionFromRoles(permission, getRoles(user))
    logger.debug("RBAC $resourceId - $user has permission $permission in ACL: $isAuthorized")
    return isAuthorized
  }

  internal fun verifyDefault(permission: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying default roles for permission: $permission")
    val isAuthorized = this.verifyPermissionFromRoles(permission, this.resourceSecurity.default)
    logger.debug("RBAC $resourceId - default roles for permission $permission: $isAuthorized")
    return isAuthorized
  }

  internal fun verifyAdminRole(user: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying if $user has default admin rbac role")
    val isAdmin = this.getRoles(user).contains(this.getAdminRole())
    logger.debug("RBAC $resourceId - $user has default admin rbac role: $isAdmin")
    return isAdmin
  }

  internal fun isAdminToken(user: String): Boolean {
    logger.debug("RBAC $resourceId - Verifying if $user has platform admin role in token")
    val isAdmin = csmAdmin.verifyCurrentRolesAdmin()
    logger.debug("RBAC $resourceId - $user has platform admin role in token: $isAdmin")
    return isAdmin
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
