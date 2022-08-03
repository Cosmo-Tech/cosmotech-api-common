// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

data class ResourceSecurity(
    var default: List<String> = listOf(),
    val accessControlList: UsersAccess = UsersAccess(),
)
