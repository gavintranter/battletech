package uk.trantr.battletech

import java.io.File

private enum class Class {
    Light,
    Medium,
    Heavy,
    Assault;

    companion object Factory {
        fun from(tonnage: Int) = when (tonnage) {
                in 1 until 40 -> Light
                in 40 until 60 -> Medium
                in 60 until 80 -> Heavy
                else -> Assault
            }
    }
}

private data class Mech(val model: String, val name: String, val tonnageClass: Class, val tonnage: Int) {
    override fun toString(): String {
        return "$model, $name, $tonnageClass, $tonnage"
    }

    companion object {
        private val regex = ".*(\"Name\"|\"Tonnage\"|\"VariantName\"): (\"?|[^0]\\d*[[\\\\]-.\\w ']+)\"?,?".toRegex()

        operator fun invoke(lines: List<String>): Mech {
            val (name, tonnage, variantName) = lines.map { regex.matchEntire(it) }
                .mapNotNull { it?.groups }
                .mapNotNull { it.last() }
                .map { it.value.trim('"', ',') }

            return Mech(variantName, name, Class.from(tonnage.toInt()), tonnage.toInt())
        }
    }
}

fun main(args: Array<String>) {
    val mechs = File("/users/Gavin/Documents/battleTech/Mechs").listFiles().filter { it.extension.equals("json", true) }
    mechs.map { Mech(it.readLines()) }
        .sortedWith(compareBy(Mech::tonnage, Mech::model))
        .forEach(::println)

}
