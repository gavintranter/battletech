package uk.trantr.battletech

import java.io.File
import java.io.StringReader
import java.util.*

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
            return Skulls(skulls.coerceAtMost(5.0))
        }
    }
}

private data class PlanetarySystem(val name: String, val allegiance: Faction, val skulls: Skulls,
                                   val employers: Set<Faction>, val starLeague: Boolean = false) {
    override fun toString(): String {
        return "$name $allegiance $skulls"
    }

    companion object {
        private const val KEY = "KEY"
        private val regex = ".*(\"Name\"|\"Owner\"|\"DefaultDifficulty\"|\"ContractEmployers\"): \"?([[\\\\]-.\\w ']+|.*)\"?,?"
            .toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            val escaped = value.replace(regex, "$2")
            p.load(StringReader("$KEY=$escaped"))

            return p.getProperty(KEY)
        }

        operator fun invoke(file: File): PlanetarySystem {
            val starLeague = file.readLines().any { it.contains("planet_other_starleague") }

            val (name, faction, difficulty, employers) = file.readLines()
                .filter { it.matches(regex) }
                .map(::extractValue)

            return PlanetarySystem(name,
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
    val systemsFiles = File("/users/Gavin/Documents/battleTech/Systems").listFiles().filter { it.extension.equals("json", true) }
    val systemsByAllegiance = systemsFiles.map { PlanetarySystem(it) }
        .distinct()
        .sortedBy(PlanetarySystem::allegiance)

    systemsByAllegiance.forEach { println(it) }
}