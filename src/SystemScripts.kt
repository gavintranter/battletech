package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

private enum class Faction {
    MagistracyOfCanopus,
    Locals,
    Aurigan,
    Marik,
    Liao,
    TaurianConcordat,
    Davion,
    NoFaction;

    companion object Factory {
        fun from(value: String): Faction {
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

private data class PlanetarySystem(val name: String, val location: Location, val jumpDistance: Int, val allegiance: Faction) {
    override fun toString(): String {
        return "$name $allegiance $location $jumpDistance"
    }

    companion object {
        private const val key = "key"
        private val regex = ".*(\"Name\"|\"Owner\"|\"x\"|\"y\"|\"z\"|\"JumpDistance\"): \"?([-.\\w]+)\"?,?".toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2")
            p.load(StringReader("$key=$escaped"))

            return p.getProperty(key)
        }

        private enum class FieldType(val field: String) {
            NAME("\"Name\":"),
            OWNER("\"Owner\":"),
            X("\"x\":"),
            Y("\"y\":"),
            Z("\"z\":"),
            JUMPDISTANCE("\"JumpDistance\""),
            IGNORE("")
        }

        operator fun invoke(lines: List<String>): PlanetarySystem {
            val data = lines.map { it.trim() }
                .filter { it.startsWith(FieldType.NAME.field) ||
                        it.startsWith(FieldType.X.field) ||
                        it.startsWith(FieldType.Y.field) ||
                        it.startsWith(FieldType.Z.field) ||
                        it.startsWith(FieldType.JUMPDISTANCE.field) ||
                        it.startsWith(FieldType.OWNER.field) }

            val name = extractValue(data[0])
            val location = Location(extractValue(data[1]).toDouble(),
                extractValue(data[2]).toDouble(),
                extractValue(data[3]).toDouble())
            val jumpDistance = extractValue(data[4]).toInt()
            val faction = Faction.from(extractValue(data[5]))

            return PlanetarySystem(name, location, jumpDistance, faction)
        }
    }
}

fun main(args: Array<String>) {
    val systemsFiles = File("/users/Gavin/Documents/battleTech").listFiles().filter { it.extension.equals("json", true) }
    val systemsByAllegiance = systemsFiles.map { PlanetarySystem(it.readLines()) }
        .distinct()
        .groupBy { it.allegiance }

    systemsByAllegiance.keys.forEach {
            systemsByAllegiance[it]?.forEach {
                    system -> println(system)
            }
    }
}
