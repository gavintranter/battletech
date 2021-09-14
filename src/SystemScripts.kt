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

private enum class Skulls(val representation: String) {
    Zero("0"), Half("0.5"),
    One("1"), One_Half("1.5"),
    Two("2"), Two_Half("2.5"),
    Three("3"), Three_Half("3.5"),
    Four("4"), Four_Half("4.5"),
    Five("5");

    override fun toString(): String {
        return "[Skulls $representation]"
    }

    companion object {
        operator fun invoke(difficulty: String): Skulls {
            return when (((difficulty.toInt() * 0.5) + 4).coerceAtMost(5.0)) {
                0.0 -> Zero
                0.5 -> Half
                1.0 -> One
                1.5 -> One_Half
                2.0 -> Two
                2.5 -> Two_Half
                3.0 -> Three
                3.5 -> Three_Half
                4.0 -> Four
                4.5 -> Four_Half
                5.0 -> Five
                else -> throw IllegalArgumentException("Expected value between 0.0 and 5.0 found $difficulty")
            }
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

        // Seem to need this to deal with accented chars
        private fun extractValue(value: String): String {
            p.load(StringReader("$KEY=$value"))

            return p.getProperty(KEY)
        }

        operator fun invoke(file: File): PlanetarySystem {
            val starLeague = starLeagueRegex.containsMatchIn(file.readText())

            val (name, faction, difficulty, employers, targets) = extractDetails(systemRegex, file)

            return PlanetarySystem(extractValue(name),
                Faction.valueOf(faction),
                Skulls(difficulty),
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
    systemsFiles.map { PlanetarySystem(it) }
        .sorted()
        .forEach { println(it) }
}