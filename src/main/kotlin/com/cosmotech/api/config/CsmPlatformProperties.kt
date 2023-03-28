// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/** Configuration Properties for the Cosmo Tech Platform */
@ConstructorBinding
@ConfigurationProperties(prefix = "csm.platform")
data class CsmPlatformProperties(

    /** the Platform summary */
    val summary: String?,

    /** the Platform description */
    val description: String?,

    /** the Platform version (MAJOR.MINOR.PATCH). */
    val version: String?,

    /** the Platform exact commit ID. */
    val commitId: String? = null,

    /** the Platform exact Version-Control System reference. */
    val vcsRef: String? = null,

    /** API Configuration */
    val api: Api,

    /** Platform vendor */
    val vendor: Vendor = Vendor.COSMOTECH,

    /** Id Generator */
    val idGenerator: IdGenerator,

    /** Event Publisher */
    val eventPublisher: EventPublisher,

    /** Argo Service */
    val argo: Argo,

    /** Cosmo Tech core images */
    val images: CsmImages,

    /** Cosmo Tech available containers */
    val containers: List<CsmContainers>,

    /** Authorization Configuration */
    val authorization: Authorization = Authorization(),

    /** Identity provider used configuration needs to be overwritten */
    val identityProvider: CsmIdentityProvider,

    /** Data Ingestion reporting behavior */
    val twincache: CsmTwinCacheProperties,

    /** RBAC / ACL configuration */
    val rbac: CsmRbac = CsmRbac(),

    /** Upload files properties */
    val upload: Upload = Upload(),
    val namespace: String = "phoenix",

    /** Persistent metrics configuration */
    val metrics: Metrics = Metrics(),
    val registries: CsmRegistries = CsmRegistries(),
) {

  data class CsmRegistries(
      val core: String = "coreregistry.svc.cluster.local:5000",
      val solutions: String = "solutionregistry.svc.cluster.local:5000",
  )

  data class Metrics(
      val enabled: Boolean = true,
      val retentionDays: Int = 7,
      val downSamplingDefaultEnabled: Boolean = false,
      val downSamplingRetentionDays: Int = 400,
      val downSamplingBucketDurationMs: Int = 3600000,
  )

  data class Authorization(

      /** The JWT Claim to use to extract a unique identifier for the user account */
      val principalJwtClaim: String = "sub",

      /** The JWT Claim where the tenant id information is stored */
      val tenantIdJwtClaim: String = "iss",

      /** The JWT Claim where the mail information is stored */
      val mailJwtClaim: String = "preferred_username",

      /** The JWT Claim where the roles information is stored */
      val rolesJwtClaim: String = "roles",

      /** List of additional tenants allowed to register */
      val allowedTenants: List<String> = emptyList()
  )

  data class CsmImages(
      /** Container image to fetch Scenario Parameters */
      val scenarioFetchParameters: String,

      /** Container image to send data to DataWarehouse */
      val sendDataWarehouse: String,
      val scenarioDataUpload: String,
  )

  data class CsmContainers(
      /** Container name */
      val name: String,

      /** Image registry (default: "") */
      val imageRegistry: String = "",

      /** Image name */
      val imageName: String,

      /** Image version (default: latest) */
      val imageVersion: String = "latest",
  )

  data class Argo(
      /** Argo service base Uri */
      val baseUri: String,

      /** Image Pull Secrets */
      val imagePullSecrets: List<String>? = null,

      /** Workflow Management */
      val workflows: Workflows,

      /** Default Main Container Image Pull Policy */
      val imagePullPolicy: String = "IfNotPresent",
  ) {
    data class Workflows(
        /** The Kubernetes namespace in which Argo Workflows should be submitted */
        val namespace: String,

        /** The node label to look for Workflows placement requests */
        val nodePoolLabel: String,

        /** The Kubernetes service account name */
        val serviceAccountName: String,

        /**
         * The Kubernetes storage-class to use for volume claims. Set to null or an empty string to
         * use the default storage class available in the cluster
         */
        val storageClass: String? = null,

        /** List of AccessModes for the Kubernetes Persistent Volume Claims to use for Workflows */
        val accessModes: List<String> = emptyList(),

        /**
         * Minimum resources the volumes requested by the Persistent Volume Claims should have.
         * Example: storage: 1Gi
         */
        val requests: Map<String, String> = emptyMap(),
    )
  }

  data class Api(
      /** API Version, e.g.: latest, or v1 */
      val version: String,

      /** API Base URL */
      val baseUrl: String,

      /**
       * Base path under which the API is exposed at root, e.g.: /cosmotech-api/. Typically when
       * served behind a reverse-proxy under a dedicated path, this would be such path.
       */
      val basePath: String,
  )

  data class IdGenerator(val type: Type) {
    enum class Type {
      /** short unique UIDs */
      HASHID,

      /** UUIDs */
      UUID
    }
  }

  data class EventPublisher(val type: Type) {
    enum class Type {
      /** In-process, via Spring Application Events */
      IN_PROCESS
    }
  }

  enum class Vendor {
    COSMOTECH
  }

  data class CsmIdentityProvider(
      /** keycloak */
      val code: String,
      /**
       * entry sample :
       * - {"http://dev.api.cosmotech.com/platform" to "Platform scope"}
       * - {"default" to "Default scope"}
       */
      val defaultScopes: Map<String, String> = emptyMap(),
      /**
       * - "https://{yourDomain}/oauth2/default/v1/authorize"
       * - "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
       */
      val authorizationUrl: String,
      /**
       * - "https://{yourDomain}/oauth2/default/v1/token"
       * - "https://login.microsoftonline.com/common/oauth2/v2.0/token"
       */
      val tokenUrl: String,
      /**
       * entry sample :
       * - {"csm.read.scenario" to "Read access to scenarios"}
       */
      val containerScopes: Map<String, String> = emptyMap(),
      /** Custom group name used acted as Organization.Admin default: Platform.Admin */
      val adminGroup: String? = null,
      /** Custom group name used acted as Organization.User default: Organization.User */
      val userGroup: String? = null,
      /** Custom group name used acted as Organization.Viewer default: Organization.Viewer */
      val viewerGroup: String? = null,
  )

  data class CsmTwinCacheProperties(
      /** Twin cache host */
      val host: String,

      /** Twin cache port */
      val port: String = "6379",

      /** Twin cache user */
      val username: String = "default",

      /** Twin cache password */
      val password: String,

      /** Twin cache query timeout. Kill a query after specified timeout (in millis) default 5000 */
      val queryTimeout: Long = 5000,

      /** Twin cache query page information for organization */
      val organization: PageSizing = PageSizing(),

      /** Twin cache query page information for workspace */
      val workspace: PageSizing = PageSizing(),

      /** Twin cache query page information for scenario */
      val scenario: PageSizing = PageSizing(),

      /** Twin cache query page information for connector */
      val connector: PageSizing = PageSizing(),

      /** Twin cache query page information for dataset */
      val dataset: PageSizing = PageSizing(),

      /** Twin cache query page information for scenariorun */
      val scenariorun: PageSizing = PageSizing(),

      /** Twin cache query page information for solution */
      val solution: PageSizing = PageSizing(),
  ) {

    data class PageSizing(
        /** Max result for a single page */
        val defaultPageSize: Int = 50
    )
  }

  data class CsmRbac(
      /** Enable Rbac */
      val enabled: Boolean = false
  )

  data class Upload(
      /** The list of files MIME types when uploading a file to the Platform */
      val authorizedMimeTypes: AuthorizedMimeTypes = AuthorizedMimeTypes(),
  ) {
    data class AuthorizedMimeTypes(
        /** List of authorized mime types for workspace file upload */
        val workspaces: List<String> = emptyList(),
        /** List of authorized mime types for step handler file upload */
        val handlers: List<String> = emptyList(),
    )
  }
}
