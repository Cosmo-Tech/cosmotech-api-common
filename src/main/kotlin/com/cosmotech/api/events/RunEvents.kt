// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.events

@Suppress("UnusedPrivateMember")
class RunStart(publisher: Any, val runnerData: Any) : CsmRequestResponseEvent<String>(publisher)

@Suppress("UnusedPrivateMember") class RunStop(publisher: Any, val runnerData: Any) : CsmEvent(publisher)
