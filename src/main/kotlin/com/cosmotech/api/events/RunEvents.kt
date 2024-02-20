// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.events

class RunStart(publisher: Any, val runnerData: Any) : CsmRequestResponseEvent<String>(publisher)

class RunStop(publisher: Any, val runnerData: Any) : CsmEvent(publisher)
