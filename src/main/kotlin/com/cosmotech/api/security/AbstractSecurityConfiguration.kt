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
const val ROLE_SOLUTION_READER = "Solution.Reader"
const val ROLE_SOLUTION_WRITER = "Solution.Writer"
const val ROLE_WORKSPACE_READER = "Workspace.Reader"
const val ROLE_WORKSPACE_WRITER = "Workspace.Writer"
const val ROLE_RUN_READER = "Run.Reader"
const val ROLE_RUN_WRITER = "Run.Writer"
const val ROLE_RUNNER_READER = "Runner.Reader"
const val ROLE_RUNNER_WRITER = "Runner.Writer"

// Allowed read scopes
const val SCOPE_CONNECTOR_READ = "SCOPE_csm.connector.read"
const val SCOPE_ORGANIZATION_READ = "SCOPE_csm.organization.read"
const val SCOPE_DATASET_READ = "SCOPE_csm.dataset.read"
const val SCOPE_SOLUTION_READ = "SCOPE_csm.solution.read"
const val SCOPE_WORKSPACE_READ = "SCOPE_csm.workspace.read"
const val SCOPE_RUN_READ = "SCOPE_csm.run.read"
const val SCOPE_RUNNER_READ = "SCOPE_csm.runner.read"

// Allowed write scopes
const val SCOPE_CONNECTOR_WRITE = "SCOPE_csm.connector.write"
const val SCOPE_ORGANIZATION_WRITE = "SCOPE_csm.organization.write"
const val SCOPE_DATASET_WRITE = "SCOPE_csm.dataset.write"
const val SCOPE_SOLUTION_WRITE = "SCOPE_csm.solution.write"
const val SCOPE_WORKSPACE_WRITE = "SCOPE_csm.workspace.write"
const val SCOPE_RUN_WRITE = "SCOPE_csm.run.write"
const val SCOPE_RUNNER_WRITE = "SCOPE_csm.runner.write"

// Endpoints paths
const val PATH_CONNECTORS = "/connectors"
const val PATH_DATASETS = "/organizations/*/datasets"
const val PATH_ORGANIZATIONS = "/organizations"
const val PATH_ORGANIZATIONS_USERS = "/organizations/*/users"
const val PATH_ORGANIZATIONS_SERVICES = "/organizations/*/services"
val PATHS_ORGANIZATIONS =
    listOf(PATH_ORGANIZATIONS, PATH_ORGANIZATIONS_USERS, PATH_ORGANIZATIONS_SERVICES)

// Path Solutions
const val PATH_SOLUTIONS = "/organizations/*/solutions"
const val PATH_SOLUTIONS_PARAMETERS = "/organizations/*/solutions/*/parameters"
const val PATH_SOLUTIONS_PARAMETERGROUPS = "/organizations/*/solutions/*/parameterGroups"
const val PATH_SOLUTIONS_RUNTEMPLATES = "/organizations/*/solutions/*/runTemplates"
val PATHS_SOLUTIONS =
    listOf(
        PATH_SOLUTIONS,
        PATH_SOLUTIONS_PARAMETERS,
        PATH_SOLUTIONS_PARAMETERGROUPS,
        PATH_SOLUTIONS_RUNTEMPLATES)

// Path Workspaces
const val PATH_WORKSPACES = "/organizations/*/workspaces"
const val PATH_WORKSPACES_USERS = "/organizations/*/workspaces/*/users"
val PATHS_WORKSPACES = listOf(PATH_WORKSPACES, PATH_WORKSPACES_USERS)
const val PATH_WORKSPACES_FILES = "/organizations/*/workspaces/*/files"

const val PATH_RUNS = "/organizations/*/workspaces/*/runners/*/runs"
const val PATH_RUNS_DATA_QUERY = "/organizations/*/workspaces/*/runners/*/runs/*/data/query"
const val PATH_RUNS_SEND_QUERY = "/organizations/*/workspaces/*/runners/*/runs/*/data/send"
const val PATH_RUNS_LOGS = "/organizations/*/workspaces/*/runners/*/runs/*/logs"
const val PATH_RUNS_STATUS = "/organizations/*/workspaces/*/runners/*/runs/*/status"
val PATHS_RUNS =
    listOf(PATH_RUNS, PATH_RUNS_DATA_QUERY, PATH_RUNS_SEND_QUERY, PATH_RUNS_LOGS, PATH_RUNS_STATUS)

const val PATH_RUNNERS = "/organizations/*/workspaces/*/runners"
const val PATH_RUNNERS_PERMISSIONS = "/organizations/*/workspaces/*/runners/*/permissions"
const val PATH_RUNNERS_SECURITY = "/organizations/*/workspaces/*/runners/*/security"
const val PATH_RUNNERS_SECURITY_DEFAULT = "/organizations/*/workspaces/*/runners/*/security/default"
const val PATH_RUNNERS_SECURITY_USERS = "/organizations/*/workspaces/*/runners/*/security/users"
const val PATH_RUNNERS_SECURITY_ACCESS = "/organizations/*/workspaces/*/runners/*/security/access"
const val PATH_RUNNERS_START = "/organizations/*/workspaces/*/runners/*/start"
const val PATH_RUNNERS_STOP = "/organizations/*/workspaces/*/runners/*/stop"

val PATHS_RUNNERS =
    listOf(
        PATH_RUNNERS,
        PATH_RUNNERS_PERMISSIONS,
        PATH_RUNNERS_SECURITY,
        PATH_RUNNERS_SECURITY_DEFAULT,
        PATH_RUNNERS_SECURITY_USERS,
        PATH_RUNNERS_SECURITY_ACCESS,
        PATH_RUNNERS_START,
        PATH_RUNNERS_STOP)

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
            paths = PATHS_RUNS,
            roles =
                arrayOf(
                    ROLE_RUN_READER,
                    ROLE_RUN_WRITER,
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
                    ROLE_RUNNER_READER,
                    ROLE_RUNNER_WRITER,
                    ROLE_RUN_READER,
                    ROLE_RUN_WRITER,
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
                    ROLE_RUN_WRITER,
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
                    ROLE_RUNNER_WRITER,
                    ROLE_WORKSPACE_WRITER,
                    ROLE_ORGANIZATION_ADMIN,
                    ROLE_ORGANIZATION_USER,
                    ROLE_ORGANIZATION_COLLABORATOR,
                    SCOPE_RUNNER_WRITE),
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
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "$path/**"))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "$path/**"))
          .hasAnyAuthority(*authoritiesList.toTypedArray())
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "$path/**"))
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
          .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "$path/**"))
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
