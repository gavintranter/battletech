package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

private enum class Faction {
    MagistracyOfCanopus,
    Locals,
    AuriganDirectorate,
    AuriganMercenaries,
    AuriganPirates,
    AuriganRestoration,
    Marik,
    Liao,
    TaurianConcordat,
    Davion,
    ComStar,
    MercenaryReviewBoard,
    NoFaction;
}

private data class Location(val x: Double, val y: Double, val z: Double) {
    override fun toString(): String {
        return "$x,$y,$z"
    }
}

private data class PlanetarySystem(val name: String, val location: Location, val jumpDistance: Int,
                                   val allegiance: Faction, val difficulty: Int, val employers: Set<String>,
                                   val starLeague: Boolean = false) {
    override fun toString(): String {
        return "$name $allegiance $location $jumpDistance $difficulty"
    }

    companion object {
        private const val key = "key"
        private val regex = ".*(\"Name\"|\"Owner\"|\"x\"|\"y\"|\"z\"|\"JumpDistance\"|\"DefaultDifficulty\"|\"ContractEmployers\"): \"?([[\\\\]-.\\w ']+|.*)\"?,?"
            .toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2")
            p.load(StringReader("$key=$escaped"))

            return p.getProperty(key)
        }

        operator fun invoke(lines: List<String>): PlanetarySystem {
            val fields = lines.filter { it.matches(regex) }
                .map { extractValue(it) }

            val name = fields[0]
            val location = Location(fields[1].toDouble(), fields[2].toDouble(), fields[3].toDouble())
            val jumpDistance = fields[4].toInt()
            val faction = Faction.valueOf(fields[5])
            val difficulty = fields[6].toInt()
            val employers = fields[7].split(" ").map { it.trim('[', ']', '"', ',') }.toSet()
            val starLeague = lines.any { it.contains("planet_other_starleague") }

            return PlanetarySystem(name, location, jumpDistance, faction, difficulty, employers, starLeague)
        }
    }
}

fun main(args: Array<String>) {
    // Replace path with location of planetary system json files
    val systemsFiles = File("/users/Gavin/Documents/battleTech/Systems").listFiles().filter { it.extension.equals("json", true) }
    val systemsByAllegiance = systemsFiles.map { PlanetarySystem(it.readLines()) }
        .distinct()
        .groupBy { it.allegiance }

    systemsByAllegiance.keys.forEach {
            systemsByAllegiance[it]?.forEach {
                    system -> println(system)
            }
    }
}
