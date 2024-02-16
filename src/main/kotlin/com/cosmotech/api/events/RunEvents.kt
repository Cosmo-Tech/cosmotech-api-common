// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.events

@Suppress("UnusedPrivateMember")
class RunStart(publisher: Any, runner: Any) : CsmRequestResponseEvent<Any>(publisher)

@Suppress("UnusedPrivateMember") class RunStop(publisher: Any, run: String) : CsmEvent(publisher)
