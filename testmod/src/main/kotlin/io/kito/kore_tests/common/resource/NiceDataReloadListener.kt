package io.kito.kore_tests.common.resource

import io.kito.kore.common.resource.KJsonResourceReloadListener
import io.kito.kore.common.resource.RegisterReloadListener
import io.kito.kore.common.resource.RegisterReloadListener.Companion.getListener
import io.kito.kore.util.minecraft.ResourceLocationExt.toLoc
import io.kito.kore_tests.ID
import io.kito.kore_tests.common.data.NiceData
import io.kito.kore_tests.common.resource.NiceDataReloadListener.Companion.NICE_DATA_LISTENER
import net.minecraft.resources.FileToIdConverter

@RegisterReloadListener(NICE_DATA_LISTENER)
class NiceDataReloadListener : KJsonResourceReloadListener<NiceData>(
    FileToIdConverter.json("nice_data"), { NiceData.codec }
) {

    companion object {
        const val NICE_DATA_LISTENER = "$ID:nice_data_listener"

        val niceDataReloadListener by lazy { getListener<NiceDataReloadListener>(NICE_DATA_LISTENER.toLoc()) }
    }
}