// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties

/** Configuration Properties for the Cosmo Tech Platform */
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
    val vendor: Vendor,

    /** Id Generator */
    val idGenerator: IdGenerator,

    /** Event Publisher */
    val eventPublisher: EventPublisher,

    /** Azure Platform */
    val azure: CsmPlatformAzure?,
    val containerRegistry: CsmPlatformContainerRegistries = CsmPlatformContainerRegistries(),

    /** Argo Service */
    val argo: Argo,

    /** Cosmo Tech core images */
    val images: CsmImages,

    /** Cosmo Tech available containers */
    val containers: List<CsmContainers>,

    /** Authorization Configuration */
    val authorization: Authorization = Authorization(),

    /**
     * Identity provider used for (azure : Azure Active Directory ,okta : Okta) if openapi default
     * configuration needs to be overwritten
     */
    val identityProvider: CsmIdentityProvider?,

    /** Okta configuration */
    val okta: CsmPlatformOkta?,

    /** Data Ingestion reporting behavior */
    val dataIngestion: DataIngestion = DataIngestion(),
    val twincache: CsmTwinCacheProperties,

    /** RBAC / ACL configuration */
    val rbac: CsmRbac = CsmRbac(),

    /** Upload files properties */
    val upload: Upload = Upload(),
    val namespace: String = "phoenix",

    /** Persistent metrics configuration */
    val metrics: Metrics = Metrics(),

    /** Loki Service */
    val loki: Loki = Loki(),
    val internalResultServices: CsmServiceResult?,
) {
  @ConditionalOnProperty(
      prefix = "csm.platform.internalResultServices.enabled",
      havingValue = "true",
      matchIfMissing = false)
  data class CsmServiceResult(
      /** Define if current API use internal result data service or cloud one */
      val enabled: Boolean = false,

      /** Storage properties */
      val storage: CsmStorage,

      /** Queue configuration */
      val eventbus: CsmEventBus
  ) {
    data class CsmStorage(
        val host: String,
        val port: Int = 5432,
        val reader: CsmStorageUser,
        val writer: CsmStorageUser,
        val admin: CsmStorageUser
    ) {
      data class CsmStorageUser(val username: String, val password: String)
    }
    data class CsmEventBus(
        val host: String,
        val port: Int = 5672,
        val defaultExchange: String = "csm-exchange",
        val defaultQueue: String = "csm",
        val defaultRoutingKey: String = "csm",
        val listener: CsmEventBusUser,
        val sender: CsmEventBusUser
    ) {
      data class CsmEventBusUser(val username: String, val password: String)
    }
  }

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

      /** The JWT Claim used to define application id in ACL */
      val applicationIdJwtClaim: String = "oid",

      /**
       * List of additional tenants allowed to register, besides the configured
       * `csm.platform.azure.credentials.tenantId`
       */
      val allowedTenants: List<String> = emptyList()
  )

  data class CsmImages(
      /** Container image to fetch Scenario Parameters */
      val scenarioFetchParameters: String,

      /** Container image to send data to DataWarehouse */
      val sendDataWarehouse: String,
      val scenarioDataUpload: String = "cosmo-tech/azure-storage-publish:latest",
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

  data class Loki(
      val baseUrl: String = "http://loki.default.svc.cluster.local:3100",
      val queryPath: String = "/loki/api/v1/query_range",
      val queryDaysAgo: Long = 1
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

  data class CsmPlatformContainerRegistries(
      /**
       * csmenginesdev.azurecr.io, for Azure ghcr.io, for github https://index.docker.io/v1/, for
       * local registry
       */
      val checkSolutionImage: Boolean = true,
      val registryUrl: String = "csmenginesdev.azurecr.io",
      val registryUserName: String? = null,
      val registryPassword: String? = null,
  )
  data class CsmPlatformAzure(
      /** Azure Credentials */
      val credentials: CsmPlatformAzureCredentials,
      val storage: CsmPlatformAzureStorage,
      val containerRegistries: CsmPlatformAzureContainerRegistries,
      val eventBus: CsmPlatformAzureEventBus,
      val dataWarehouseCluster: CsmPlatformAzureDataWarehouseCluster,
      val keyVault: String,
      val analytics: CsmPlatformAzureAnalytics,
      val appIdUri: String,
      val claimToAuthorityPrefix: Map<String, String> = mutableMapOf("roles" to "")
  ) {

    data class CsmPlatformAzureCredentials(
        /** The Azure Tenant ID (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.tenantId instead")
        val tenantId: String? = null,

        /** The Azure Client ID (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.clientId instead")
        val clientId: String? = null,

        /** The Azure Client Secret (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.clientSecret instead")
        val clientSecret: String? = null,

        /**
         * The Azure Active Directory Pod Id binding bound to an AKS pod identity linked to a
         * managed identity
         */
        @Deprecated(message = "use csm.platform.azure.credentials.core.aadPodIdBinding instead")
        val aadPodIdBinding: String? = null,

        /** The core App Registration credentials - provided by Cosmo Tech */
        val core: CsmPlatformAzureCredentialsCore,

        /**
         * Any customer-provided app registration. Useful for example when calling Azure Digital
         * Twins, because of security enforcement preventing from assigning permissions in the
         * context of a managed app, deployed via the Azure Marketplace
         */
        val customer: CsmPlatformAzureCredentialsCustomer? = null,
    ) {

      data class CsmPlatformAzureCredentialsCore(
          /** The Azure Tenant ID (core App) */
          val tenantId: String,

          /** The Azure Client ID (core App) */
          val clientId: String,

          /** The Azure Client Secret (core App) */
          val clientSecret: String,

          /**
           * The Azure Active Directory Pod Id binding bound to an AKS pod identity linked to a
           * managed identity
           */
          val aadPodIdBinding: String? = null,
      )

      data class CsmPlatformAzureCredentialsCustomer(
          /** The Azure Tenant ID (customer App Registration) */
          val tenantId: String?,

          /** The Azure Client ID (customer App Registration) */
          val clientId: String?,

          /** The Azure Client Secret (customer App Registration) */
          val clientSecret: String?,
      )
    }

    data class CsmPlatformAzureStorage(
        val connectionString: String,
        val baseUri: String,
        val resourceUri: String
    )
    @Deprecated(message = "use csm.platform.containerregistries instead")
    data class CsmPlatformAzureContainerRegistries(val core: String, val solutions: String)

    data class CsmPlatformAzureEventBus(
        val baseUri: String,
        val authentication: Authentication = Authentication()
    ) {
      data class Authentication(
          val strategy: Strategy = Strategy.TENANT_CLIENT_CREDENTIALS,
          val sharedAccessPolicy: SharedAccessPolicyDetails? = null,
          val tenantClientCredentials: TenantClientCredentials? = null
      ) {
        enum class Strategy {
          TENANT_CLIENT_CREDENTIALS,
          SHARED_ACCESS_POLICY
        }

        data class SharedAccessPolicyDetails(
            val namespace: SharedAccessPolicyCredentials? = null,
        )

        data class SharedAccessPolicyCredentials(val name: String, val key: String)
        data class TenantClientCredentials(
            val tenantId: String,
            val clientId: String,
            val clientSecret: String
        )
      }
    }

    data class CsmPlatformAzureDataWarehouseCluster(val baseUri: String, val options: Options) {
      data class Options(val ingestionUri: String)
    }

    data class CsmPlatformAzureAnalytics(
        val resourceUri: String,
        val instrumentationKey: String,
        val connectionString: String
    )
  }

  enum class Vendor {
    /** Microsoft Azure : https://azure.microsoft.com/en-us/ */
    AZURE,
    ON_PREMISE
  }

  data class DataIngestion(
      /**
       * Number of seconds to wait after a scenario run workflow end time, before starting to check
       * ADX for data ingestion state. See https://bit.ly/3FXshzE for the rationale
       */
      val waitingTimeBeforeIngestionSeconds: Long = 15,

      /**
       * number of minutes after a scenario run workflow end time during which an ingestion failure
       * detected is considered linked to the current scenario run
       */
      val ingestionObservationWindowToBeConsideredAFailureMinutes: Long = 5,

      /** Data ingestion state handling default behavior */
      val state: State = State()
  ) {
    data class State(
        /**
         * The timeout in second before considering no data in probes measures and control plane is
         * an issue
         */
        val noDataTimeOutSeconds: Long = 180,
    )
  }

  data class CsmIdentityProvider(
      /** okta|azure */
      val code: String,
      /**
       * entry sample :
       * - {"http://dev.api.cosmotech.com/platform" to "Platform scope"}
       * - {"default" to "Default scope"}
       */
      val defaultScopes: Map<String, String> = emptyMap(),
      /**
       * - "https://{yourOktaDomain}/oauth2/default/v1/authorize"
       * - "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
       */
      val authorizationUrl: String,
      /**
       * - "https://{yourOktaDomain}/oauth2/default/v1/token"
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

  data class CsmPlatformOkta(
      /** Okta Issuer */
      val issuer: String,

      /** Okta Application Id */
      val clientId: String,

      /** Okta Application Secret */
      val clientSecret: String,

      /** Okta Authorization Server Audience */
      val audience: String,
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

      /**
       * After specified timeout the query bulk will be deleted from Redis (in seconds) default
       * 86400 = 24h
       */
      val queryBulkTTL: Long = 86400,

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
