package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]

private enum class Faction {
    ComStar,
    Davion,
    Liao,
    Marik,
    AuriganDirectorate,
    AuriganMercenaries,
    AuriganPirates,
    AuriganRestoration,
    MagistracyOfCanopus,
    TaurianConcordat,
    Locals,
    NoFaction,
    MercenaryReviewBoard;
}

private data class Skulls(val value: Double) {
    override fun toString(): String {
        return "[$value Skulls]"
    }

    companion object Factory {
        fun from(difficulty: String): Skulls {
            val skulls = (difficulty.toInt() * 0.5) + 4
            return Skulls(skulls)
        }
    }
}

private data class Location(val x: Double, val y: Double, val z: Double) {
    override fun toString(): String {
        return "[$x,$y,$z]"
    }

    companion object Factory {
        fun from(x: String, y: String, z: String): Location {
            return Location(x.toDouble(), y.toDouble(), z.toDouble())
        }
    }
}

private data class PlanetarySystem(val name: String, val location: Location, val jumpDistance: Int,
                                   val allegiance: Faction, val skulls: Skulls, val employers: Set<String>,
                                   val starLeague: Boolean = false) {
    override fun toString(): String {
        return "$name $allegiance $location $jumpDistance $skulls"
    }

    companion object {
        private const val KEY = "KEY"
        private val regex = ".*(\"Name\"|\"Owner\"|\"x\"|\"y\"|\"z\"|\"JumpDistance\"|\"DefaultDifficulty\"|\"ContractEmployers\"): \"?([[\\\\]-.\\w ']+|.*)\"?,?"
            .toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2")
            p.load(StringReader("$KEY=$escaped"))

            return p.getProperty(KEY)
        }

        operator fun invoke(lines: List<String>): PlanetarySystem {
            val starLeague = lines.any { it.contains("planet_other_starleague") }

            val (name, x, y, z, jumpDistance, faction, difficulty, employers) = lines.filter { it.matches(regex) }.map { extractValue(it) }
            return  PlanetarySystem(name,
                Location.from(x, y, z),
                jumpDistance.toInt(),
                Faction.valueOf(faction),
                Skulls.from(difficulty),
                extractEmployers(employers),
                starLeague)
        }

        private fun extractEmployers(employers: String) = employers.split(" ").map { it.trim('[', ']', '"', ',') }.toSet()
    }
}

fun main(args: Array<String>) {
    // Replace path with location of planetary system json files
    val systemsFiles = File("/users/Gavin/Documents/battleTech/Systems").listFiles().filter { it.extension.equals("json", true) }
    val systemsByAllegiance = systemsFiles.map { PlanetarySystem(it.readLines()) }
        .distinct()
        .sortedBy { it.allegiance }

    systemsByAllegiance.forEach { println(it) }
}
