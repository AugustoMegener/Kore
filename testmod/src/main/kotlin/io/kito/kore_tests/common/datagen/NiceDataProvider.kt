package io.kito.kore_tests.common.datagen

import io.kito.kore.common.datagen.KJsonProvider
import io.kito.kore.common.datagen.RegisterDataProvider
import io.kito.kore_tests.DataGenerator
import io.kito.kore_tests.ID
import io.kito.kore_tests.KoreTests.local
import io.kito.kore_tests.common.data.NiceData
import net.minecraft.data.PackOutput
import net.minecraft.data.PackOutput.Target.DATA_PACK
import net.neoforged.api.distmarker.Dist

@RegisterDataProvider(DataGenerator::class, Dist.CLIENT)
class NiceDataProvider(packOutput : PackOutput) :
    KJsonProvider<NiceData>(packOutput, DATA_PACK, ID, "nice_data", { NiceData })
{
    override fun addData() {
        local("marx")   by NiceData("Marx",   64, listOf("he", "him"))
        local("lenin")  by NiceData("Lenin",  53, listOf("he", "him"))
        local("stalin") by NiceData("Stalin", 74, listOf("he", "him"))
    }
}