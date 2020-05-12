package uk.trantr.battletech

import java.io.File
import kotlin.math.roundToInt

// Global to all BattleTech scripts, probably should be moved
fun getAssets(assetName: String): Array<File> = File("/users/Gavin/Documents/battleTech/$assetName")
    .listFiles { name -> name.extension == "json"  } ?: emptyArray()

// Global to all BattleTech scripts, probably should be moved
fun extractDetails(regex: Regex, file: File): List<String> = regex.findAll(file.readText())
    .mapNotNull { it.groups }
    .mapNotNull { it.last() }
    .map { it.value.trim('"', ',', ' ') }
    .toList()

private enum class MechClass {
    Unknown,
    UltraLight,
    Light,
    Medium,
    Heavy,
    Assault,
    SuperHeavy;

    companion object Factory {
        fun from(tonnage: Int) = when (tonnage) {
                in 1 until 20 -> UltraLight
                in 20 until 40 -> Light
                in 40 until 60 -> Medium
                in 60 until 80 -> Heavy
                in 80 until 101 -> Assault
                in 101 until 1000 -> SuperHeavy
                else -> Unknown
            }
    }
}

private data class Mech(val model: String, val name: String, val mechClass: MechClass,
                        val tonnage: Int, val cost: Int) : Comparable<Mech> {
    val resale: Int = ((10.24 / 100) * cost).roundToInt()

    override fun compareTo(other: Mech): Int {
        val result = tonnage.compareTo(other.tonnage)
        return if (result == 0) model.compareTo(other.model) else result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mech
        return model == other.model
    }

    override fun hashCode(): Int {
        return model.hashCode()
    }

    override fun toString(): String {
        return "$mechClass, $model, $name, $tonnage, $resale"
    }

    companion object {
        private val regex = "\\s\"[CostNameTngVri]{4,11}\"\\s?:\\s?\"?([^0][- \\w]{2,})".toRegex()

        operator fun invoke(file: File): Mech {
            val (cost, name, tonnage, variantName) = extractDetails(regex, file)

            return Mech(variantName, name, MechClass.from(tonnage.toInt()), tonnage.toInt(), cost.toInt())
        }
    }
}

fun main(args: Array<String>) {
    val mechAssets = getAssets("Mechs").filterNot { f -> f.name.contains("TARGETDUMMY") }
    mechAssets.map { Mech(it) }
        .sorted()
        .forEach(::println)
}
