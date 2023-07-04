// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.security

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimValidator
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

// Allowed write scopes
const val SCOPE_CONNECTOR_WRITE = "SCOPE_csm.connector.write"
const val SCOPE_ORGANIZATION_WRITE = "SCOPE_csm.organization.write"
const val SCOPE_DATASET_WRITE = "SCOPE_csm.dataset.write"
const val SCOPE_SOLUTION_WRITE = "SCOPE_csm.solution.write"
const val SCOPE_WORKSPACE_WRITE = "SCOPE_csm.workspace.write"
const val SCOPE_SCENARIO_WRITE = "SCOPE_csm.scenario.write"
const val SCOPE_SCENARIORUN_WRITE = "SCOPE_csm.scenariorun.write"
const val SCOPE_TWIN_GRAPH_WRITE = "SCOPE_csm.twingraph.write"

// Endpoints paths
const val PATH_CONNECTORS = "/connectors"
const val PATH_DATASETS = "/organizations/*/datasets"
const val PATH_ORGANIZATIONS = "/organizations"
const val PATH_ORGANIZATIONS_USERS = "/organizations/*/users"
const val PATH_ORGANIZATIONS_SERVICES = "/organizations/*/services"
val PATHS_ORGANIZATIONS =
    listOf(PATH_ORGANIZATIONS, PATH_ORGANIZATIONS_USERS, PATH_ORGANIZATIONS_SERVICES)
const val PATH_SCENARIOS = "/organizations/*/workspaces/*/scenarios"
const val PATH_SCENARIOS_COMPARE = "/organizations/*/workspaces/*/scenarios/*/compare"
const val PATH_SCENARIOS_USERS = "/organizations/*/workspaces/*/scenarios/*/users"
const val PATH_SCENARIOS_PARAMETERVALUES =
    "/organizations/*/workspaces/*/scenarios/*/parameterValues"
val PATHS_SCENARIOS =
    listOf(
        PATH_SCENARIOS,
        PATH_SCENARIOS_COMPARE,
        PATH_SCENARIOS_USERS,
        PATH_SCENARIOS_PARAMETERVALUES)
const val PATH_SCENARIORUNS = "/organizations/*/scenarioruns"
const val PATH_SCENARIORUNS_STATUS = "/organizations/*/scenarioruns/*/status"
const val PATH_SCENARIORUNS_LOGS = "/organizations/*/scenarioruns/*/logs"
const val PATH_SCENARIORUNS_CUMULATEDLOGS = "/organizations/*/scenarioruns/*/cumulatedlogs"
const val PATH_SCENARIORUNS_WORKSPACES = "/organizations/*/workspaces/scenarioruns"
const val PATH_SCENARIORUNS_SCENARIOS = "/organizations/*/workspaces/*/scenarios/*/scenarioruns"
const val PATH_SCENARIORUNS_SCENARIOS_RUN = "/organizations/*/workspaces/*/scenarios/*/run"
val PATHS_SCENARIORUNS =
    listOf(
        PATH_SCENARIORUNS,
        PATH_SCENARIORUNS_STATUS,
        PATH_SCENARIORUNS_LOGS,
        PATH_SCENARIORUNS_CUMULATEDLOGS,
        PATH_SCENARIORUNS_WORKSPACES,
        PATH_SCENARIORUNS_SCENARIOS)
const val PATH_SOLUTIONS = "/organizations/*/solutions"
const val PATH_SOLUTIONS_PARAMETERS = "/organizations/*/solutions/*/parameters"
const val PATH_SOLUTIONS_PARAMETERGROUPS = "/organizations/*/solutions/*/parameterGroups"
const val PATH_SOLUTIONS_RUNTEMPLATES = "/organizations/*/solutions/*/runTemplates"
const val PATH_SOLUTIONS_RUNTEMPLATES_HANDLERS_UPLOAD =
    "/organizations/*/solutions/*/runTemplates/*/handlers/*/upload"
val PATHS_SOLUTIONS =
    listOf(
        PATH_SOLUTIONS,
        PATH_SOLUTIONS_PARAMETERS,
        PATH_SOLUTIONS_PARAMETERGROUPS,
        PATH_SOLUTIONS_RUNTEMPLATES,
        PATH_SOLUTIONS_RUNTEMPLATES_HANDLERS_UPLOAD)
const val PATH_WORKSPACES = "/organizations/*/workspaces"
const val PATH_WORKSPACES_USERS = "/organizations/*/workspaces/*/users"
val PATHS_WORKSPACES = listOf(PATH_WORKSPACES, PATH_WORKSPACES_USERS)
const val PATH_WORKSPACES_FILES = "/organizations/*/workspaces/*/files"
// Job
const val PATH_JOB_STATUS = "/organizations/*/job/*/status"
val PATHS_JOB = listOf(PATH_JOB_STATUS)
// Twingraph
const val PATH_TWIN_GRAPH_IMPORT = "/organizations/*/twingraph/import"
const val PATH_TWIN_GRAPH = "/organizations/*/twingraph"
const val PATH_TWIN_GRAPHS = "/organizations/*/twingraphs"
const val PATH_TWIN_GRAPH_QUERY = "/organizations/*/twingraph/*/query"
const val PATH_TWIN_GRAPH_BATCH_QUERY = "/organizations/*/twingraph/*/batch-query"
const val PATH_TWIN_GRAPH_BULK_DOWNLOAD = "/organizations/*/twingraph/download"
const val PATH_TWIN_GRAPH_ENTITY = "/organizations/*/twingraph/*/entity"
const val PATH_TWIN_GRAPH_METADATA = "/organizations/*/twingraph/*/metadata"
const val PATH_TWIN_GRAPH_BATCH_ACTIONS = "/organizations/*/twingraph/*/batch"
val PATHS_TWIN_GRAPH =
    listOf(
        PATH_TWIN_GRAPH_IMPORT,
        PATH_TWIN_GRAPH,
        PATH_TWIN_GRAPHS,
        PATH_TWIN_GRAPH_QUERY,
        PATH_TWIN_GRAPH_BATCH_QUERY,
        PATH_TWIN_GRAPH_BULK_DOWNLOAD,
        PATH_TWIN_GRAPH_ENTITY,
        PATH_TWIN_GRAPH_METADATA,
        PATH_TWIN_GRAPH_BATCH_ACTIONS)

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
            paths = listOf(PATH_CONNECTORS),
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
            paths = listOf(PATH_DATASETS),
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
            paths = PATHS_JOB,
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
            customAdmin = customOrganizationAdmin))

@Suppress("LongMethod")
internal fun endpointSecurityWriters(
    customOrganizationAdmin: String,
    customOrganizationUser: String
) =
    listOf(
        CsmSecurityEndpointsRolesWriter(
            paths = listOf(PATH_CONNECTORS),
            roles = arrayOf(ROLE_CONNECTOR_WRITER, ROLE_CONNECTOR_DEVELOPER, SCOPE_CONNECTOR_WRITE),
            customAdmin = customOrganizationAdmin),
        CsmSecurityEndpointsRolesWriter(
            paths = listOf(PATH_DATASETS),
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
            paths = listOf(PATH_WORKSPACES_FILES),
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
@Suppress("unused")
open class AbstractSecurityConfiguration {

  fun getOAuth2JwtConfigurer(
      http: HttpSecurity,
      organizationAdminGroup: String,
      organizationUserGroup: String,
      organizationViewerGroup: String
  ): HttpSecurity {

    val corsHttpMethodsAllowed =
        HttpMethod.values().filterNot { it == HttpMethod.TRACE }.map(HttpMethod::name)

    return http
        .cors { cors ->
          cors.configurationSource {
            CorsConfiguration().applyPermitDefaultValues().apply {
              allowedMethods = corsHttpMethodsAllowed
            }
          }
        }
        .authorizeHttpRequests { requests ->
          requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
          // Public paths
          endpointSecurityPublic.forEach { path ->
            requests.requestMatchers(HttpMethod.GET, path).permitAll()
          }

//SPOK adding roles creates some double entries in the set of roles (not possible because of the set)
          // Endpoint security for reader roles
//          endpointSecurityReaders(
//                  organizationAdminGroup, organizationUserGroup, organizationViewerGroup)
//              .forEach { endpointsRoles -> endpointsRoles.applyRoles(requests) }

          // Endpoint security for writer roles
//          endpointSecurityWriters(organizationAdminGroup, organizationUserGroup).forEach {
//              endpointsRoles ->
//            endpointsRoles.applyRoles(requests)
//          }
          requests.anyRequest().authenticated()
        }
        .oauth2ResourceServer { oauth2 -> oauth2.jwt {} }
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
    this.paths.forEach { path ->
      requests
          .requestMatchers(HttpMethod.POST, path, "$path/*")
          .hasAnyAuthority(ROLE_PLATFORM_ADMIN, customAdmin, *this.roles)
          .requestMatchers(HttpMethod.PATCH, path, "$path/*")
          .hasAnyAuthority(ROLE_PLATFORM_ADMIN, customAdmin, *this.roles)
          .requestMatchers(HttpMethod.DELETE, path, "$path/*")
          .hasAnyAuthority(ROLE_PLATFORM_ADMIN, customAdmin, *this.roles)
    }
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
    this.paths.forEach { path ->
      requests
          .requestMatchers(HttpMethod.GET, path, "$path/*")
          .hasAnyAuthority(ROLE_PLATFORM_ADMIN, customAdmin, *this.roles)
    }
  }
}

class CsmJwtClaimValueInCollectionValidator(
    claimName: String,
    private val allowed: Collection<String>
) : OAuth2TokenValidator<Jwt> {

  private val jwtClaimValidator: JwtClaimValidator<String> =
      JwtClaimValidator(claimName, allowed::contains)

  override fun validate(token: Jwt?): OAuth2TokenValidatorResult =
      this.jwtClaimValidator.validate(
          token ?: throw IllegalArgumentException("JWT must not be null"))
}
