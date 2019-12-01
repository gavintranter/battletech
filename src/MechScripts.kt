package uk.trantr.battletech

import java.io.File

fun getAssets(assetName: String): Array<File> = File("/users/Gavin/Documents/battleTech/$assetName")
    .listFiles { name -> name.extension == "json"  } ?: emptyArray()

fun extractDetails(regex: Regex, file: File): List<String> = regex.findAll(file.readText())
    .mapNotNull { it.groups }
    .mapNotNull { it.last() }
    .map { it.value.trim('"', ',', ' ') }
    .toList()

private enum class MechClass {
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

private data class Mech(val model: String, val name: String, val mechClass: MechClass, val tonnage: Int) {
    override fun toString(): String {
        return "$model, $name, $mechClass, $tonnage"
    }

    companion object {
        private val regex = "\\s\"[NameTongVrit]{4,11}\"\\s?:\\s?\"?([^0][- \\w]{2,})".toRegex()

        operator fun invoke(file: File): Mech {
            val (name, tonnage, variantName) = extractDetails(regex, file)

            return Mech(variantName, name, MechClass.from(tonnage.toInt()), tonnage.toInt())
        }
    }
}

fun main(args: Array<String>) {
    val mechs = getAssets("Mechs").filterNot { f -> f.name.contains("TARGETDUMMY") }
    mechs.map { Mech(it) }
        .sortedWith(compareBy(Mech::tonnage, Mech::model))
        .forEach(::println)
}
