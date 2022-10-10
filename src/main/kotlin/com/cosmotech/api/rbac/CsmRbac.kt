// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.exceptions.CsmAccessForbiddenException
import com.cosmotech.api.exceptions.CsmClientException
import com.cosmotech.api.exceptions.CsmResourceNotFoundException
import com.cosmotech.api.rbac.model.RbacAccessControl
import com.cosmotech.api.rbac.model.RbacSecurity
import com.cosmotech.api.utils.getCurrentAuthenticatedMail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Suppress("TooManyFunctions")
@Component
open class CsmRbac(
    protected val csmPlatformProperties: CsmPlatformProperties,
    protected val csmAdmin: CsmAdmin
) {

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  fun verify(
      rbacSecurity: RbacSecurity,
      permission: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ) {
    this.verify(
        rbacSecurity,
        permission,
        getCurrentAuthenticatedMail(this.csmPlatformProperties),
        rolesDefinition)
  }

  fun verify(
      rbacSecurity: RbacSecurity,
      permission: String,
      user: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ) {
    if (!this.check(rbacSecurity, permission, user, rolesDefinition))
        throw CsmAccessForbiddenException(
            "RBAC ${rbacSecurity.id}}- User $user does not have permission $permission")
  }

  fun check(
      rbacSecurity: RbacSecurity,
      permission: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ): Boolean {
    return this.check(
        rbacSecurity,
        permission,
        getCurrentAuthenticatedMail(this.csmPlatformProperties),
        rolesDefinition)
  }

  fun check(
      rbacSecurity: RbacSecurity,
      permission: String,
      user: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ): Boolean {
    logger.info("RBAC ${rbacSecurity.id}} - Verifying permission $permission for user $user")
    if (!this.csmPlatformProperties.rbac.enabled) {
      logger.debug("RBAC ${rbacSecurity.id}} - RBAC check not enabled")
      return true
    }
    val userIsAdmin = this.isAdmin(rbacSecurity, user, rolesDefinition)
    return (userIsAdmin || this.verifyRbac(rbacSecurity, permission, rolesDefinition, user))
  }

  fun setDefault(
      rbacSecurity: RbacSecurity,
      defaultRole: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ): RbacSecurity {
    logger.info("RBAC ${rbacSecurity.id} - Setting default security")
    this.verifyRoleOrThrow(rbacSecurity, defaultRole, rolesDefinition)
    rbacSecurity.default = defaultRole
    return rbacSecurity
  }

  fun setUserRole(
      rbacSecurity: RbacSecurity,
      user: String,
      role: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ): RbacSecurity {
    logger.info("RBAC ${rbacSecurity.id}} - Setting user $user roles")
    this.verifyRoleOrThrow(rbacSecurity, role, rolesDefinition)
    val currentRole = this.getUserRole(rbacSecurity, user)
    val adminRole = this.getAdminRole(rolesDefinition)
    if (currentRole == adminRole &&
        role != adminRole &&
        this.getAdminCount(rbacSecurity, rolesDefinition) == 1) {
      throw CsmAccessForbiddenException(
          "RBAC ${rbacSecurity.id}} - It is forbidden to unset the last administrator")
    }
    val accessList = rbacSecurity.accessControlList
    val userAccess = accessList.find { it.id == user }
    if (userAccess == null) {
      accessList.add(RbacAccessControl(user, role))
    } else {
      userAccess.role = role
    }
    return rbacSecurity
  }

  fun getUsers(rbacSecurity: RbacSecurity): List<String> {
    return (rbacSecurity.accessControlList.map { it.id })
  }

  fun getAccessControl(rbacSecurity: RbacSecurity, identityId: String): RbacAccessControl {
    return rbacSecurity.accessControlList.find { it.id == identityId }
      ?: throw CsmResourceNotFoundException("User '$identityId' not found")
  }

  fun removeUser(
      rbacSecurity: RbacSecurity,
      user: String,
      rolesDefinition: RolesDefinition = getCommonRolesDefinition()
  ): RbacSecurity {
    logger.info("RBAC ${rbacSecurity.id}} - Removing user $user from security")
    val role = this.getUserRole(rbacSecurity, user)
    if (role == (this.getAdminRole(rolesDefinition)) &&
        this.getAdminCount(rbacSecurity, rolesDefinition) == 1) {
      throw CsmAccessForbiddenException(
          "RBAC ${rbacSecurity.id}} - It is forbidden to remove the last administrator")
    }
    rbacSecurity.accessControlList.removeIf { it.id == user }
    return rbacSecurity
  }

  internal fun isAdmin(
      rbacSecurity: RbacSecurity,
      user: String,
      rolesDefinition: RolesDefinition
  ): Boolean {
    return (this.isAdminToken(rbacSecurity, user) ||
        this.verifyAdminRole(rbacSecurity, user, rolesDefinition))
  }

  internal fun verifyAdminRole(
      rbacSecurity: RbacSecurity,
      user: String,
      rolesDefinition: RolesDefinition
  ): Boolean {
    logger.debug("RBAC ${rbacSecurity.id}} - Verifying if $user has default admin rbac role")
    val isAdmin = this.getUserRole(rbacSecurity, user) == this.getAdminRole(rolesDefinition)
    logger.debug("RBAC ${rbacSecurity.id}} - $user has default admin rbac role: $isAdmin")
    return isAdmin
  }

  internal fun verifyUser(
      rbacSecurity: RbacSecurity,
      permission: String,
      rolesDefinition: RolesDefinition,
      user: String
  ): Boolean {
    logger.debug("RBAC ${rbacSecurity.id}} - Verifying $user has permission in ACL: $permission")
    val isAuthorized =
        this.verifyPermissionFromRole(permission, getUserRole(rbacSecurity, user), rolesDefinition)
    logger.debug(
        "RBAC ${rbacSecurity.id}} - $user has permission $permission in ACL: $isAuthorized")
    return isAuthorized
  }

  internal fun verifyDefault(
      rbacSecurity: RbacSecurity,
      permission: String,
      rolesDefinition: RolesDefinition
  ): Boolean {
    logger.debug("RBAC ${rbacSecurity.id}} - Verifying default roles for permission: $permission")
    val isAuthorized =
        this.verifyPermissionFromRole(permission, rbacSecurity.default, rolesDefinition)
    logger.debug(
        "RBAC ${rbacSecurity.id}} - default roles for permission $permission: $isAuthorized")
    return isAuthorized
  }

  internal fun verifyRbac(
      rbacSecurity: RbacSecurity,
      permission: String,
      rolesDefinition: RolesDefinition,
      user: String
  ): Boolean {
    return (this.verifyDefault(rbacSecurity, permission, rolesDefinition) ||
        this.verifyUser(rbacSecurity, permission, rolesDefinition, user))
  }

  internal fun verifyPermissionFromRole(
      permission: String,
      role: String,
      rolesDefinition: RolesDefinition
  ): Boolean {
    return this.verifyPermission(
        permission, this.getRolePermissions(role, rolesDefinition.permissions))
  }

  internal fun getRolePermissions(
      role: String,
      rolesDefinition: MutableMap<String, List<String>>
  ): List<String> {
    return rolesDefinition[role] ?: listOf()
  }

  internal fun getUserRole(rbacSecurity: RbacSecurity, user: String): String {
    return rbacSecurity.accessControlList.firstOrNull { it.id == user }?.role ?: ROLE_NONE
  }

  internal fun getAdminCount(rbacSecurity: RbacSecurity, rolesDefinition: RolesDefinition): Int {
    return rbacSecurity
        .accessControlList
        .map { it.role }
        .filter { it == this.getAdminRole(rolesDefinition) }
        .count()
  }

  internal fun verifyRoleOrThrow(
      rbacSecurity: RbacSecurity,
      role: String,
      rolesDefinition: RolesDefinition
  ) {
    if (!rolesDefinition.permissions.keys.contains(role))
        throw CsmClientException("RBAC ${rbacSecurity.id} - Role $role does not exist")
  }

  internal fun verifyPermission(permission: String, userPermissions: List<String>): Boolean {
    return userPermissions.contains(permission)
  }

  internal fun verifyPermissionFromRoles(
      permission: String,
      roles: List<String>,
      rolesDefinition: RolesDefinition
  ): Boolean {
    return roles.any { role -> this.verifyPermissionFromRole(permission, role, rolesDefinition) }
  }

  internal fun isAdminToken(rbacSecurity: RbacSecurity, user: String): Boolean {
    logger.debug("RBAC ${rbacSecurity.id}} - Verifying if $user has platform admin role in token")
    val isAdmin = csmAdmin.verifyCurrentRolesAdmin()
    logger.debug("RBAC ${rbacSecurity.id}} - $user has platform admin role in token: $isAdmin")
    return isAdmin
  }

  internal fun getRolePermissions(role: String, rolesDefinition: RolesDefinition): List<String> {
    return rolesDefinition.permissions[role] ?: listOf()
  }

  internal fun getAdminRole(rolesDefinition: RolesDefinition): String {
    return rolesDefinition.adminRole
  }
}
