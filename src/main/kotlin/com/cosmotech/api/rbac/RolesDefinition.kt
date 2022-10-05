// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

import org.springframework.stereotype.Component

// openapi generator takes only last term if _ in name or bad parsing if -
const val ROLE_VIEWER = "viewer"
const val ROLE_EDITOR = "editor"
const val ROLE_ADMIN = "admin"
const val ROLE_VALIDATOR = "validator"
const val ROLE_USER = "user"
const val ROLE_NONE = "none"

// apply same format rules for permission for consistency
const val PERMISSION_READ_DATA = "read_data"
const val PERMISSION_READ_SECURITY = "read_security"
const val PERMISSION_CREATE_CHILDREN = "create_children"
const val PERMISSION_EDIT = "edit"
const val PERMISSION_EDIT_SECURITY = "edit_security"
const val PERMISSION_LAUNCH = "launch"
const val PERMISSION_VALIDATE = "validate"

val COMMON_ROLE_NONE_PERMISSIONS: List<String> = listOf()
val COMMON_ROLE_READER_PERMISSIONS = listOf(PERMISSION_READ_DATA, PERMISSION_READ_SECURITY)
val COMMON_ROLE_USER_PERMISSIONS =
    listOf(PERMISSION_READ_DATA, PERMISSION_READ_SECURITY, PERMISSION_CREATE_CHILDREN)
val COMMON_ROLE_EDITOR_PERMISSIONS =
    listOf(
        PERMISSION_READ_DATA, PERMISSION_READ_SECURITY, PERMISSION_CREATE_CHILDREN, PERMISSION_EDIT)
val COMMON_ROLE_ADMIN_PERMISSIONS =
    listOf(
        PERMISSION_READ_DATA,
        PERMISSION_READ_SECURITY,
        PERMISSION_CREATE_CHILDREN,
        PERMISSION_EDIT,
        PERMISSION_EDIT_SECURITY)

// Scenario roles & permissions
val SCENARIO_ROLE_VIEWER_PERMISSIONS = listOf(PERMISSION_READ_DATA, PERMISSION_READ_SECURITY)
val SCENARIO_ROLE_EDITOR_PERMISSIONS =
    listOf(PERMISSION_READ_DATA, PERMISSION_READ_SECURITY, PERMISSION_LAUNCH, PERMISSION_EDIT)
val SCENARIO_ROLE_VALIDATOR_PERMISSIONS =
    listOf(
        PERMISSION_READ_DATA,
        PERMISSION_READ_SECURITY,
        PERMISSION_LAUNCH,
        PERMISSION_EDIT,
        PERMISSION_VALIDATE)
val SCENARIO_ROLE_ADMIN_PERMISSIONS =
    listOf(
        PERMISSION_READ_DATA,
        PERMISSION_READ_SECURITY,
        PERMISSION_LAUNCH,
        PERMISSION_EDIT,
        PERMISSION_VALIDATE,
        PERMISSION_EDIT_SECURITY)

@Component
data class RolesDefinition(
    val permissions: MutableMap<String, List<String>> = mutableMapOf(),
    val adminRole: String = ROLE_ADMIN
)

fun getAllRolesDefinition(): Map<String, MutableMap<String, MutableList<String>>> {
  return mapOf(
      "organization" to
          getCommonRolesDefinition()
              .permissions
              .mapValues { it.value.toMutableList() }
              .toMutableMap(),
      "workspace" to
          getCommonRolesDefinition()
              .permissions
              .mapValues { it.value.toMutableList() }
              .toMutableMap(),
      "scenario" to
          getScenarioRolesDefinition()
              .permissions
              .mapValues { it.value.toMutableList() }
              .toMutableMap())
}

fun getPermissions(role: String, rolesDefinition: RolesDefinition): List<String> {
  return rolesDefinition.permissions[role] ?: mutableListOf()
}

fun getCommonRolesDefinition(): RolesDefinition {
  return RolesDefinition(
      permissions =
          mutableMapOf(
              ROLE_NONE to COMMON_ROLE_NONE_PERMISSIONS,
              ROLE_VIEWER to COMMON_ROLE_READER_PERMISSIONS,
              ROLE_USER to COMMON_ROLE_USER_PERMISSIONS,
              ROLE_EDITOR to COMMON_ROLE_EDITOR_PERMISSIONS,
              ROLE_ADMIN to COMMON_ROLE_ADMIN_PERMISSIONS,
          ),
      adminRole = ROLE_ADMIN)
}

fun getScenarioRolesDefinition(): RolesDefinition {
  return RolesDefinition(
      permissions =
          mutableMapOf(
              ROLE_NONE to COMMON_ROLE_NONE_PERMISSIONS,
              ROLE_VIEWER to SCENARIO_ROLE_VIEWER_PERMISSIONS,
              ROLE_EDITOR to SCENARIO_ROLE_EDITOR_PERMISSIONS,
              ROLE_VALIDATOR to SCENARIO_ROLE_VALIDATOR_PERMISSIONS,
              ROLE_ADMIN to SCENARIO_ROLE_ADMIN_PERMISSIONS,
          ),
      adminRole = ROLE_ADMIN)
}
