// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

data class ResourceSecurity(
    var default: List<String> = listOf(),
    val accessControlList: UsersAccess = UsersAccess(),
)

fun createResourceSecurity(admin: String, rolesDefinition: RolesDefinition): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(accessControlList = UsersAccess(roles = mutableMapOf(admin to adminRoles)))
}

fun createResourceSecurity(
    admins: List<String>,
    rolesDefinition: RolesDefinition
): ResourceSecurity {
  val adminRoles = listOf(rolesDefinition.adminRole)
  return ResourceSecurity(
      accessControlList = UsersAccess(roles = admins.associate { it to adminRoles }.toMutableMap()))
}
