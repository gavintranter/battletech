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
        return "[Skulls: $value]"
    }

    companion object Factory {
        fun from(difficulty: String): Skulls {
            val skulls = (difficulty.toInt() * 0.5) + 4
            return Skulls(skulls.coerceAtMost(5.0))
        }
    }
}

private data class PlanetarySystem(val name: String, val allegiance: Faction, val skulls: Skulls,
                                   val employers: Set<Faction>, val targets: Set<Faction>,
                                   val starLeague: Boolean = false) : Comparable<PlanetarySystem> {

    override fun compareTo(other: PlanetarySystem): Int {
        val result = allegiance.compareTo(other.allegiance)
        return if (result == 0) name.compareTo(other.name) else result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlanetarySystem
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "$name $allegiance $skulls $starLeague"
    }

    companion object {
        private const val KEY = "KEY"
        private val systemRegex = "\\s\"[NODC][aweo][mnf][eraultDifcyEmposTg]{1,17}\"\\s?:\\s?[\"\\[]?([\\w[-\"',\\s\\\\]*]+)]?,".toRegex()
        private val starLeagueRegex = "planet_other_starleague".toRegex()
        private val p = Properties()

        private fun extractValue(value: String): String {
            p.load(StringReader("$KEY=$value"))

            return p.getProperty(KEY)
        }

        operator fun invoke(file: File): PlanetarySystem {
            val starLeague = starLeagueRegex.containsMatchIn(file.readText())

            val (name, faction, difficulty, employers, targets) = extractDetails(systemRegex, file)

            return PlanetarySystem(extractValue(name),
                Faction.valueOf(faction),
                Skulls.from(difficulty),
                extractFactions(employers),
                extractFactions(targets),
                starLeague)
        }

        private fun extractFactions(employers: String) = employers.split(", ")
            .map { it.trim('"') }
            .map { Faction.valueOf(it) }
            .toSet()
    }
}

fun main(args: Array<String>) {
    val systemsFiles: Array<File> = getAssets("Systems")
    val systemsByAllegiance = systemsFiles.map { PlanetarySystem(it) }
        .distinct()
        .sorted()

    systemsByAllegiance.forEach { println(it) }
}