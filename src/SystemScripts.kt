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
        return "$name $allegiance $skulls $starLeague"
    }

    companion object {
        private const val KEY = "KEY"
        private val systemRegex = "\\s\"[NODC][aweo][mnf][eraultDifcyEmpos]{1,17}\"\\s?:\\s?[\"\\[]?([\\w[-\"',\\s\\\\]*]+)]?,".toRegex()
        private val starLeagueRegex = "planet_other_starleague".toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            p.load(StringReader("$KEY=$value"))

            return p.getProperty(KEY).trim('"')
        }

        operator fun invoke(file: File): PlanetarySystem {
            val starLeague = starLeagueRegex.containsMatchIn(file.readText())

            val (name, faction, difficulty, employers) = systemRegex.findAll(file.readText())
                .mapNotNull { it.groups }
                .mapNotNull { it.last() }
                .map { extractValue(it.value) }
                .toList()

            return PlanetarySystem(name,
                Faction.valueOf(faction),
                Skulls.from(difficulty),
                extractEmployers(employers),
                starLeague)
        }

        private fun extractEmployers(employers: String) = employers.split(", ")
            .map { it.trim('"') }
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