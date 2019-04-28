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
        fun from(tonnage: Int): Class {
            return when (tonnage) {
                in 1 until 40 -> Light
                in 40 until 60 -> Medium
                in 60 until 80 -> Heavy
                else -> Assault
            }
        }
    }
}

private data class Mech(val model: String, val name: String, val tonnageClass: Class, val tonnage: Int) {
    override fun toString(): String {
        return "$model, $name, $tonnageClass, $tonnage"
    }

    companion object {
        private const val key = "key"
        private val regex = ".*(\"Name\"|\"Tonnage\"|\"VariantName\"): (\"?|[^0]\\d*[[\\\\]-.\\w ']+)\"?,?".toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2").trim('"', ',')
            p.load(StringReader("$key=$escaped"))

            return p.getProperty(key)
        }

        operator fun invoke(lines: List<String>): Mech {
            val (name, tonnage, variantName) = lines.filter { it.matches(regex) }
                .map { extractValue(it) }

            return Mech(variantName, name, Class.from(tonnage.toInt()), tonnage.toInt())
        }
    }
}

fun main(args: Array<String>) {
    val mechs = File("/users/Gavin/Documents/battleTech/Mechs").listFiles().filter { it.extension.equals("json", true) }
    mechs.map { Mech(it.readLines()) }
        .sortedWith(compareBy(Mech::tonnage, Mech::name))
        .forEach { println(it) }

}
