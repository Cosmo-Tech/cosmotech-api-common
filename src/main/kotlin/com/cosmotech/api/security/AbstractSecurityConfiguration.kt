// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.security

import com.cosmotech.api.config.CsmPlatformProperties
import com.cosmotech.api.security.filters.ApiKeyAuthenticationFilter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.context.DelegatingSecurityContextRepository
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher
import org.springframework.web.cors.CorsConfiguration

// Business roles
const val ROLE_PLATFORM_ADMIN = "Platform.Admin"
const val ROLE_CONNECTOR_DEVELOPER = "Connector.Developer"
const val ROLE_ORGANIZATION_ADMIN = "Organization.Admin"
const val ROLE_ORGANIZATION_COLLABORATOR = "Organization.Collaborator"
const val ROLE_ORGANIZATION_MODELER = "Organization.Modeler"
const val ROLE_ORGANIZATION_USER = "Organization.User"
const val ROLE_ORGANIZATION_VIEWER = "Organization.Viewer"

// Endpoints roles
const val ROLE_CONNECTOR_READER = "Connector.Reader"
const val ROLE_CONNECTOR_WRITER = "Connector.Writer"
const val ROLE_DATASET_READER = "Dataset.Reader"
const val ROLE_DATASET_WRITER = "Dataset.Writer"
const val ROLE_ORGANIZATION_READER = "Organization.Reader"
const val ROLE_ORGANIZATION_WRITER = "Organization.Writer"
const val ROLE_SCENARIO_READER = "Scenario.Reader"
const val ROLE_SCENARIO_WRITER = "Scenario.Writer"
const val ROLE_SCENARIORUN_READER = "ScenarioRun.Reader"
const val ROLE_SCENARIORUN_WRITER = "ScenarioRun.Writer"
const val ROLE_SOLUTION_READER = "Solution.Reader"
const val ROLE_SOLUTION_WRITER = "Solution.Writer"
const val ROLE_WORKSPACE_READER = "Workspace.Reader"
const val ROLE_WORKSPACE_WRITER = "Workspace.Writer"
const val ROLE_TWIN_GRAPH_READER = "Twingraph.Reader"
const val ROLE_TWIN_GRAPH_WRITER = "Twingraph.Writer"

// Allowed read scopes
const val SCOPE_CONNECTOR_READ = "SCOPE_csm.connector.read"
const val SCOPE_ORGANIZATION_READ = "SCOPE_csm.organization.read"
const val SCOPE_DATASET_READ = "SCOPE_csm.dataset.read"
const val SCOPE_SOLUTION_READ = "SCOPE_csm.solution.read"
const val SCOPE_WORKSPACE_READ = "SCOPE_csm.workspace.read"
const val SCOPE_SCENARIO_READ = "SCOPE_csm.scenario.read"
const val SCOPE_SCENARIORUN_READ = "SCOPE_csm.scenariorun.read"
const val SCOPE_TWIN_GRAPH_READ = "SCOPE_csm.twingraph.read"
const val SCOPE_RUN_READ = "SCOPE_csm.run.read"
const val SCOPE_RUNNER_READ = "SCOPE_csm.runner.read"

// Allowed write scopes
const val SCOPE_CONNECTOR_WRITE = "SCOPE_csm.connector.write"
const val SCOPE_ORGANIZATION_WRITE = "SCOPE_csm.organization.write"
const val SCOPE_DATASET_WRITE = "SCOPE_csm.dataset.write"
const val SCOPE_SOLUTION_WRITE = "SCOPE_csm.solution.write"
const val SCOPE_WORKSPACE_WRITE = "SCOPE_csm.workspace.write"
const val SCOPE_SCENARIO_WRITE = "SCOPE_csm.scenario.write"
const val SCOPE_SCENARIORUN_WRITE = "SCOPE_csm.scenariorun.write"
const val SCOPE_TWIN_GRAPH_WRITE = "SCOPE_csm.twingraph.write"
const val SCOPE_RUN_WRITE = "SCOPE_csm.run.write"
const val SCOPE_RUNNER_WRITE = "SCOPE_csm.runner.write"

// Path Connectors
val PATHS_CONNECTORS =
    listOf(
        "/connectors",
        "/connectors/*",
        "/connectors/name/*",
    )

// Path Datasets
val PATHS_DATASETS =
    listOf(
        "/organizations/*/datasets",
        "/organizations/*/datasets/copy",
        "/organizations/*/datasets/search",
        "/organizations/*/datasets/twingraph/download/*",
        "/organizations/*/datasets/*",
        "/organizations/*/datasets/*/batch",
        "/organizations/*/datasets/*/batch-query",
        "/organizations/*/datasets/*/compatibility",
        "/organizations/*/datasets/*/link",
        "/organizations/*/datasets/*/refresh",
        "/organizations/*/datasets/*/refresh/rollback",
        "/organizations/*/datasets/*/security",
        "/organizations/*/datasets/*/security/access",
        "/organizations/*/datasets/*/security/access/*",
        "/organizations/*/datasets/*/security/default",
        "/organizations/*/datasets/*/security/users",
        "/organizations/*/datasets/*/status",
        "/organizations/*/datasets/*/subdataset",
        "/organizations/*/datasets/*/twingraph",
        "/organizations/*/datasets/*/twingraph/*",
        "/organizations/*/datasets/*/unlink")

// Path Organizations
val PATHS_ORGANIZATIONS =
    listOf(
        "/organizations",
        "/organizations/permissions",
        "/organizations/*",
        "/organizations/*/permissions/*",
        "/organizations/*/security",
        "/organizations/*/security/access",
        "/organizations/*/security/access/*",
        "/organizations/*/security/default",
        "/organizations/*/security/users",
        "/organizations/*/services/solutionsContainerRegistry",
        "/organizations/*/services/storage",
        "/organizations/*/services/tenantCredentials",
    )

// Path Runs
val PATHS_RUNS =
    listOf(
        "/organizations/*/workspaces/*/runners/*/runs",
        "/organizations/*/workspaces/*/runners/*/runs/*",
        "/organizations/*/workspaces/*/runners/*/runs/*/data/query",
        "/organizations/*/workspaces/*/runners/*/runs/*/data/send",
        "/organizations/*/workspaces/*/runners/*/runs/*/logs",
        "/organizations/*/workspaces/*/runners/*/runs/*/status")

// Path Runners
val PATHS_RUNNERS =
    listOf(
        "/organizations/*/workspaces/*/runners",
        "/organizations/*/workspaces/*/runners/*",
        "/organizations/*/workspaces/*/runners/*/permissions/*",
        "/organizations/*/workspaces/*/runners/*/security",
        "/organizations/*/workspaces/*/runners/*/security/access",
        "/organizations/*/workspaces/*/runners/*/security/access/*",
        "/organizations/*/workspaces/*/runners/*/security/default",
        "/organizations/*/workspaces/*/runners/*/security/users",
        "/organizations/*/workspaces/*/runners/*/start",
        "/organizations/*/workspaces/*/runners/*/stop")

// Path Scenarios
val PATHS_SCENARIOS =
    listOf(
        "/organizations/*/workspaces/*/scenarios",
        "/organizations/*/workspaces/*/scenarios/tree",
        "/organizations/*/workspaces/*/scenarios/*",
        "/organizations/*/workspaces/*/scenarios/*/ValidationStatus",
        "/organizations/*/workspaces/*/scenarios/*/compare/*",
        "/organizations/*/workspaces/*/scenarios/*/downloads",
        "/organizations/*/workspaces/*/scenarios/*/downloads/*",
        "/organizations/*/workspaces/*/scenarios/*/parameterValues",
        "/organizations/*/workspaces/*/scenarios/*/permissions/*",
        "/organizations/*/workspaces/*/scenarios/*/security",
        "/organizations/*/workspaces/*/scenarios/*/security/access",
        "/organizations/*/workspaces/*/scenarios/*/security/access/*",
        "/organizations/*/workspaces/*/scenarios/*/security/default",
        "/organizations/*/workspaces/*/scenarios/*/security/users",
        "/organizations/*/workspaces/*/*",
    )

// Path ScenarioRuns
val PATHS_SCENARIORUNS =
    listOf(
        "/organizations/*/scenarioruns/historicaldata",
        "/organizations/*/scenarioruns/search",
        "/organizations/*/scenarioruns/startcontainers",
        "/organizations/*/scenarioruns/*",
        "/organizations/*/scenarioruns/*/cumulatedlogs",
        "/organizations/*/scenarioruns/*/logs",
        "/organizations/*/scenarioruns/*/status",
        "/organizations/*/scenarioruns/*/stop",
        "/organizations/*/workspaces/*/scenarioruns",
        "/organizations/*/workspaces/*/scenarioruns/historicaldata",
        "/organizations/*/workspaces/*/scenarios/*/run",
        "/organizations/*/workspaces/*/scenarios/*/scenarioruns",
        "/organizations/*/workspaces/*/scenarios/*/scenarioruns/historicaldata")

// Path Solutions
val PATHS_SOLUTIONS =
    listOf(
        "/organizations/*/solutions",
        "/organizations/*/solutions/*",
        "/organizations/*/solutions/*/parameterGroups",
        "/organizations/*/solutions/*/parameters",
        "/organizations/*/solutions/*/runTemplates",
        "/organizations/*/solutions/*/runTemplates/*",
        "/organizations/*/solutions/*/runtemplates/*/handlers/*/download",
        "/organizations/*/solutions/*/runtemplates/*/handlers/*/upload",
        "/organizations/*/solutions/*/security",
        "/organizations/*/solutions/*/security/access",
        "/organizations/*/solutions/*/security/access/*",
        "/organizations/*/solutions/*/security/default",
        "/organizations/*/solutions/*/security/users",
    )

// Path Twingraph
val PATHS_TWIN_GRAPH =
    listOf(
        "/organizations/*/job/*/status",
        "/organizations/*/twingraph/download/*",
        "/organizations/*/twingraph/*",
        "/organizations/*/twingraph/*/batch",
        "/organizations/*/twingraph/*/batch-query",
        "/organizations/*/twingraph/*/entity/*",
        "/organizations/*/twingraph/*/metadata",
        "/organizations/*/twingraph/*/query",
        "/organizations/*/twingraphs")

// Path Workspaces files
val PATHS_WORKSPACES_FILES =
    listOf(
        "/organizations/*/workspaces/*/files",
        "/organizations/*/workspaces/*/files/delete",
        "/organizations/*/workspaces/*/files/download")

// Path Workspaces
val PATHS_WORKSPACES =
    listOf(
        "/organizations/*/workspaces",
        "/organizations/*/workspaces/*",
        "/organizations/*/workspaces/*/link",
        "/organizations/*/workspaces/*/permissions/*",
        "/organizations/*/workspaces/*/security",
        "/organizations/*/workspaces/*/security/access",
        "/organizations/*/workspaces/*/security/access/*",
        "/organizations/*/workspaces/*/security/default",
        "/organizations/*/workspaces/*/security/users",
        "/organizations/*/workspaces/*/security/unlink",
    )

// Endpoints roles
val endpointSecurityPublic =
    listOf(
        "/actuator/prometheus",
        "/actuator/health/**",
        "/actuator/info",
        "/",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/openapi.*",
        "/openapi/*",
        "/openapi",
        "/error",
    )

@Suppress("LongMethod")
internal fun endpointSecurityReaders(
    customOrganizationAdmin: String,
    customOrganizationUser: String,
    customOrganizationViewer: String
) =
    listOf(
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_CONNECTORS,
            roles =
                arrayOf(
                    ROLE_CONNECTOR_READER,
                    ROLE_CONNECTOR_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_CONNECTOR_READ,
                    SCOPE_CONNECTOR_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_DATASETS,
            roles =
                arrayOf(
                    ROLE_DATASET_READER,
                    ROLE_DATASET_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_DATASET_READ,
                    SCOPE_DATASET_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_ORGANIZATIONS,
            roles =
                arrayOf(
                    ROLE_ORGANIZATION_READER,
                    ROLE_ORGANIZATION_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_ORGANIZATION_READ,
                    SCOPE_ORGANIZATION_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_SCENARIOS,
            roles =
                arrayOf(
                    ROLE_SCENARIO_READER,
                    ROLE_SCENARIO_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_SCENARIO_READ,
                    SCOPE_SCENARIO_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_SCENARIORUNS,
            roles =
                arrayOf(
                    ROLE_SCENARIORUN_READER,
                    ROLE_SCENARIORUN_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    SCOPE_SCENARIORUN_READ,
                    SCOPE_SCENARIORUN_WRITE,
                    customOrganizationUser),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_SOLUTIONS,
            roles =
                arrayOf(
                    ROLE_SOLUTION_READER,
                    ROLE_SOLUTION_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_SOLUTION_READ,
                    SCOPE_SOLUTION_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_WORKSPACES,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_READER,
                    ROLE_WORKSPACE_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_WORKSPACE_READ,
                    SCOPE_WORKSPACE_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_TWIN_GRAPH,
            roles =
                arrayOf(
                    ROLE_TWIN_GRAPH_READER,
                    ROLE_TWIN_GRAPH_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_TWIN_GRAPH_READ,
                    SCOPE_TWIN_GRAPH_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_RUNS,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_READER,
                    ROLE_WORKSPACE_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_RUN_READ,
                    SCOPE_RUN_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesReader(
            paths = PATHS_RUNNERS,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_READER,
                    ROLE_WORKSPACE_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_VIEWER,
                    SCOPE_RUNNER_READ,
                    SCOPE_RUNNER_WRITE,
                    customOrganizationUser,
                    customOrganizationViewer),
            customAdmin = customOrganizationAdmin))

@Suppress("LongMethod")
internal fun endpointSecurityWriters(
    customOrganizationAdmin: String,
    customOrganizationUser: String
) =
    listOf(
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_CONNECTORS,
            roles = arrayOf(ROLE_CONNECTOR_WRITER, ROLE_CONNECTOR_DEVELOPER, SCOPE_CONNECTOR_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_DATASETS,
            roles =
                arrayOf(
                    ROLE_DATASET_WRITER,
                    ROLE_CONNECTOR_DEVELOPER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    SCOPE_DATASET_WRITE,
                    customOrganizationUser),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_ORGANIZATIONS,
            roles =
                arrayOf(
                    ROLE_ORGANIZATION_WRITER, ROLE_ORGANIZATION_ADMIN, SCOPE_ORGANIZATION_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_SCENARIOS,
            roles =
                arrayOf(
                    ROLE_SCENARIO_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    SCOPE_SCENARIO_WRITE,
                    customOrganizationUser),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_SCENARIORUNS,
            roles =
                arrayOf(
                    ROLE_SCENARIORUN_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    SCOPE_SCENARIORUN_WRITE,
                    customOrganizationUser),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_SOLUTIONS,
            roles =
                arrayOf(
                    ROLE_SOLUTION_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    SCOPE_SOLUTION_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_WORKSPACES,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    SCOPE_WORKSPACE_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_RUNS,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    SCOPE_RUN_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_RUNNERS,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    SCOPE_RUNNER_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_WORKSPACES_FILES,
            roles =
                arrayOf(
                    ROLE_WORKSPACE_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    ROLE_ORGANIZATION_USER,
                    SCOPE_WORKSPACE_WRITE,
                    customOrganizationUser),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = PATHS_TWIN_GRAPH,
            roles =
                arrayOf(
                    ROLE_TWIN_GRAPH_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    ROLE_ORGANIZATION_MODELER,
                    SCOPE_TWIN_GRAPH_WRITE),
            customAdmin = customOrganizationAdmin),
    )

abstract class AbstractSecurityConfiguration {

  fun getOAuth2ResourceServer(
      http: HttpSecurity,
      organizationAdminGroup: String,
      organizationUserGroup: String,
      organizationViewerGroup: String,
      csmPlatformProperties: CsmPlatformProperties
  ): HttpSecurity {

    val corsHttpMethodsAllowed =
        HttpMethod.values().filterNot { it == HttpMethod.TRACE }.map(HttpMethod::name)

    return http
        .cors { cors ->
          cors.configurationSource {
            val corsConfig = CorsConfiguration().applyPermitDefaultValues()
            corsConfig.apply { allowedMethods = corsHttpMethodsAllowed }
            corsConfig
          }
        }
        .csrf { csrfConfigurer ->
          csmPlatformProperties.authorization.allowedApiKeyConsumers.forEach { apiKeyConsumer ->
            csrfConfigurer.ignoringRequestMatchers(
                RequestHeaderRequestMatcher(apiKeyConsumer.apiKeyHeaderName))
          }
        }
        .securityContext {
          it.securityContextRepository(
              DelegatingSecurityContextRepository(
                  RequestAttributeSecurityContextRepository(),
                  HttpSessionSecurityContextRepository()))
        }
        .addFilterBefore(
            ApiKeyAuthenticationFilter(csmPlatformProperties), AuthorizationFilter::class.java)
        .authorizeHttpRequests { requests ->
          requests
              .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS, "/**"))
              .permitAll()
          // Public paths
          endpointSecurityPublic.forEach { path ->
            requests
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, path))
                .permitAll()
          }
          // Endpoint security for reader roles
          endpointSecurityReaders(
                  organizationAdminGroup, organizationUserGroup, organizationViewerGroup)
              .forEach { endpointsRoles -> endpointsRoles.applyRoles(requests) }

          // Endpoint security for writer roles
          endpointSecurityWriters(organizationAdminGroup, organizationUserGroup).forEach {
              endpointsRoles ->
            endpointsRoles.applyRoles(requests)
          }

          requests.anyRequest().authenticated()
        }
  }
}

internal class CsmSecurityEndpointsRolesWriter(
    val customAdmin: String,
    val paths: List<String>,
    val roles: Array<String>,
) {

  @Suppress("SpreadOperator")
  fun applyRoles(
      requests:
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
  ) {
    val authoritiesList = addAdminRolesIfNotAlreadyDefined(this.roles)
    this.paths.forEach { path ->
      requests
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, path))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, path))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, path))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
    }
  }

  private fun addAdminRolesIfNotAlreadyDefined(roles: Array<String>): MutableList<String> {
    val authoritiesList = roles.toSet().toMutableList()
    if (ROLE_PLATFORM_ADMIN !in authoritiesList) {
      authoritiesList.add(ROLE_PLATFORM_ADMIN)
    }
    if (customAdmin !in authoritiesList) {
      authoritiesList.add(customAdmin)
    }
    return authoritiesList
  }
}

internal class CsmSecurityEndpointsRolesReader(
    val customAdmin: String,
    val paths: List<String>,
    val roles: Array<String>,
) {

  @Suppress("SpreadOperator")
  fun applyRoles(
      requests:
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
  ) {
    val authoritiesList = addAdminRolesIfNotAlreadyDefined(this.roles)
    this.paths.forEach { path ->
      requests
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, path))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
    }
  }

  private fun addAdminRolesIfNotAlreadyDefined(roles: Array<String>): MutableList<String> {
    val authoritiesList = roles.toSet().toMutableList()
    if (ROLE_PLATFORM_ADMIN !in authoritiesList) {
      authoritiesList.add(ROLE_PLATFORM_ADMIN)
    }
    if (customAdmin !in authoritiesList) {
      authoritiesList.add(customAdmin)
    }
    return authoritiesList
  }
}
