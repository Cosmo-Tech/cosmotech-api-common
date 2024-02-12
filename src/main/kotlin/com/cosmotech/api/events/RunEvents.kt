// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.events

@Suppress("UnusedPrivateMember")
class RunStart(publisher: Any, runner: Any) : CsmRequestResponseEvent<String>(publisher)

@Suppress("UnusedPrivateMember") class RunStop(publisher: Any, run: Any) : CsmEvent(publisher)
