package io.kito.kore_tests.common.data

import io.kito.kore.common.data.Save
import io.kito.kore.common.data.codec.KCodecSerializer

data class NiceData(@Save val name     : String,
                    @Save val age      : Int,
                    @Save val pronoums : List<String>)
{
    override fun toString() = """
        Name: $name
        Age: $age
        Pronoums: ${pronoums.joinToString { "$it/" }}
    """.trimIndent()

    companion object : KCodecSerializer<NiceData>(NiceData::class)
}