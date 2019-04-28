package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

private enum class Class {
    Light,
    Medium,
    Heavy,
    Assault;

    companion object Factory {
        fun from(value: String): Class {
            return Class.valueOf(value.toLowerCase().capitalize())
        }
    }
}

private data class Mech(val model: String, val name: String, val weightClass: Class, val weight: Int) {
    override fun toString(): String {
        return "$model, $name, $weightClass, $weight"
    }

    companion object {
        private const val key = "key"
        private val regex = ".*(\"Name\"|\"Tonnage\"|\"weightClass\"|\"VariantName\"): (\"?|[^0]\\d*[[\\\\]-.\\w ']+)\"?,?".toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2").trim('"', ',')
            p.load(StringReader("$key=$escaped"))

            return p.getProperty(key)
        }

        operator fun invoke(lines: List<String>): Mech {
            val fields = lines.filter { it.matches(regex) }
                .map { extractValue(it) }

            val name = fields[0]
            val tonnage = fields[1].toInt()
            val weightClass = Class.from(fields[2])
            val variantName = fields[3]

            return Mech(variantName, name, weightClass, tonnage)
        }
    }
}

fun main(args: Array<String>) {
    val mechs = File("/users/Gavin/Documents/battleTech/Mechs").listFiles().filter { it.extension.equals("json", true) }
    mechs.map { Mech(it.readLines()) }
        .sortedBy { it.weight }
        .forEach{ println(it) }

}
