// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.rbac

data class UsersAccess(val roles: MutableMap<String, List<String>> = mutableMapOf())
