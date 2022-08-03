package com.cosmotech.api.rbac

data class RoleDefinition (val permissions: Map<String, List<String>>)
