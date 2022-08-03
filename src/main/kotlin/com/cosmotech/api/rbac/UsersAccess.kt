package com.cosmotech.api.rbac

data class UsersAccess (val roles: MutableMap<String, List<String>> = mutableMapOf())
