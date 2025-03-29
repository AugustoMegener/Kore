package io.kito.kore_tests.common.resource

import io.kito.kore.common.resource.KJsonResourceReloadListener
import io.kito.kore.common.resource.RegisterReloadListener
import io.kito.kore_tests.common.data.NiceData

@RegisterReloadListener
object NiceDataReloadListener : KJsonResourceReloadListener<NiceData>("nice_data", { NiceData })