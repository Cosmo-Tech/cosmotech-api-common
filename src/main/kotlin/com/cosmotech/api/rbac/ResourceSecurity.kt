package com.cosmotech.api.rbac

data class ResourceSecurity(
  val default: List<String>?,
  val accessControlList: UsersAccess,
)
