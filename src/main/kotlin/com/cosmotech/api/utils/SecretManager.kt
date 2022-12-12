// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.utils

interface SecretManager {
  /**
   * Create a secret from a key/value map
   *
   * @param tenantName The tenant name to create the secret for
   * @param secretName The secret name
   * @param secretData The secret data in key/value form
   */
  fun createOrReplaceSecret(tenantName: String, secretName: String, secretData: Map<String, String>)

  /**
   * Read a secret and return it as a key/value map
   *
   * @param tenantName The tenant name to create the secret for
   * @param secretName The secret name
   * @return The secret data in key/value form
   */
  fun readSecret(tenantName: String, secretName: String): Map<String, String>

  /**
   * Delete a secret
   *
   * @param tenantName The tenant name to delete the secret for
   * @param secretName The secret name
   */
  fun deleteSecret(tenantName: String, secretName: String)
}
