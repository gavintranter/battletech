package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]

private enum class Faction {
    // Independent powers
    ComStar,
    MercenaryReviewBoard,
    // Inner Sphere
    Davion,
    Kurita,
    Liao,
    Marik,
    Steiner,
    // Periphery
    AuriganDirectorate,
    AuriganMercenaries,
    AuriganPirates,
    AuriganRestoration,
    MagistracyOfCanopus,
    TaurianConcordat,
    // Others
    Locals,
    NoFaction
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

private data class Location(val x: Double, val y: Double, val z: Double, val jumpDistance: Int) {
    override fun toString(): String {
        return "[[$x,$y,$z],[$jumpDistance]]"
    }

    companion object Factory {
        fun from(x: String, y: String, z: String, jumpDistance: String): Location {
            return Location(x.toDouble(), y.toDouble(), z.toDouble(), jumpDistance.toInt())
        }
    }
}

private data class PlanetarySystem(val name: String, val location: Location, val allegiance: Faction,
                                   val skulls: Skulls, val employers: Set<Faction>, val starLeague: Boolean = false) {
    override fun toString(): String {
        return "$name $allegiance $location $skulls"
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
            return PlanetarySystem(name,
                Location.from(x, y, z, jumpDistance),
                Faction.valueOf(faction),
                Skulls.from(difficulty),
                extractEmployers(employers),
                starLeague)
        }

        private fun extractEmployers(employers: String) = employers.split(" ")
            .map { it.trim('[', ']', '"', ',') }
            .map { Faction.valueOf(it) }
            .toSet()
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
