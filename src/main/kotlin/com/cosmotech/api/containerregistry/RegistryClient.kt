// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.containerregistry

interface RegistryClient {

  /**
   * Return the endpoint of the container registry
   *
   * @return The endpoint
   */
  fun getEndpoint(): String

  /**
   * Check the image of the solution
   *
   * @param repository The repository of the solution image
   * @param tag The tag of the solution image
   */
  fun checkSolutionImage(repository: String, tag: String)
}
