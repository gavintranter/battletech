package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

private enum class FieldType(val field: String) {
    NAME("\"Name\":"),
    OWNER("\"Owner\":"),
    X("\"x\":"),
    Y("\"y\":"),
    Z("\"z\":"),
    JUMPDISTANCE("\"JumpDistance\""),
    IGNORE("")
}

private enum class Faction {
    MagistracyOfCanopus,
    Locals,
    Aurigan,
    Marik,
    Liao,
    TaurianConcordat,
    Davion,
    NoFaction;

    companion object {
        operator fun invoke(value: String): Faction {
            return when {
                value.contains("Aurigan") -> Aurigan
                else -> Faction.valueOf(value)
            }
        }
    }
}

private class Location(val x: Double, val y: Double, val z: Double) {
    override fun toString(): String {
        return "$x,$y,$z"
    }
}

private class PlanetarySystem(val name: String, val location: Location, val jumpDistance: Int, val allegiance: Faction) {
    override fun toString(): String {
        return "$name $allegiance $location $jumpDistance"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlanetarySystem

        if (name != other.name) return false
        if (allegiance != other.allegiance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + allegiance.hashCode()
        return result
    }


    companion object {
        private const val key = "key"
        private val regex = ".*(\"Name\"|\"Owner\"|\"x\"|\"y\"|\"z\"|\"JumpDistance\"): \"?([-.\\w]+)\"?,?".toRegex()
        private val p = Properties()

        operator fun invoke(value: List<String>): PlanetarySystem {
            val name = extractValue(value[0])
            val location = Location(extractValue(value[1]).toDouble(),
                extractValue(value[2]).toDouble(),
                extractValue(value[3]).toDouble())
            val jumpDistance = extractValue(value[4]).toInt()
            val faction = uk.trantr.battletech.Faction(extractValue(value[5]))

            return PlanetarySystem(name, location, jumpDistance, faction)
        }

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2")
            p.load(StringReader("$key=$escaped"))

            return p.getProperty(key)
        }
    }
}

fun main(args: Array<String>) {
    val systemsFiles = File("/users/Gavin/Documents/battleTech").listFiles().filter { it.extension.equals("json", true) }
    val systemsByAllegiance = systemsFiles.map { toSystem(it.readLines()) }
        .distinct()
        .groupBy { it.allegiance }

    systemsByAllegiance.keys.forEach {
            systemsByAllegiance[it]?.forEach {
                    system -> println(system)
            }
    }
}

private fun toSystem(lines: List<String>): PlanetarySystem {
    val data = lines.map { it.trim() }
        .filter { it.startsWith(FieldType.NAME.field) ||
                it.startsWith(FieldType.X.field) ||
                it.startsWith(FieldType.Y.field) ||
                it.startsWith(FieldType.Z.field) ||
                it.startsWith(FieldType.JUMPDISTANCE.field) ||
                it.startsWith(FieldType.OWNER.field) }

    return PlanetarySystem(data)
}
