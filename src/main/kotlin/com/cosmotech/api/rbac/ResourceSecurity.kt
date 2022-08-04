// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import com.cosmotech.api.utils.getCurrentAuthenticatedMail

data class ResourceSecurity(
    var default: List<String> = listOf(),
    val accessControlList: UsersAccess = UsersAccess(),
)

fun createResourceSecurity(admin: String, rolesDefinition: RolesDefinition): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(
      accessControlList = UsersAccess(roles = mutableMapOf(admin to adminRoles)))
}

fun createResourceSecurity(
    admins: List<String>,
    rolesDefinition: RolesDefinition
): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(
      accessControlList = UsersAccess(roles = admins.associate { it to adminRoles }.toMutableMap()))
}

fun createResourceSecurityCurrentAdmin(
    rolesDefinition: RolesDefinition
): ResourceSecurity {
  val admins = listOf(getCurrentAuthenticatedMail())
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(
      accessControlList = UsersAccess(roles = admins.associate { it to adminRoles }.toMutableMap()))
}

fun createResourceSecurity(
    default: List<String>,
    roles: MutableMap<String, List<String>>
): ResourceSecurity {
  return ResourceSecurity(default = default, accessControlList = UsersAccess(roles = roles))
}
