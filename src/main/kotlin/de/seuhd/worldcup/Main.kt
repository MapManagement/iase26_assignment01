package de.seuhd.worldcup

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


fun main() {
    val data = loadWorldCupData()
    println("Loaded '${data.tournament}' with ${data.groups.size} groups and ${data.knockouts.size} knockout matches")

    //TODO: Implement interactive menu
    val menuText = """
===== FIFA World Cup 2026 ? Betting Console =====
1) Show Standings
2) Show Matches
3) Place Bets
4) Show Betting Score
5) Exit
=================================================
Choose an option (1 to 5) :
"""

    while (true) {
        println(menuText)
        val userInput = readln().toIntOrNull()

        when (userInput) {
            1 -> showStandings(data.groups)
            2 -> showMatches(data.groups)
            3 -> println("3")
            4 -> println("4")
            5 -> println("5")
            null -> println("Wrong input")
            else -> println("There are only 5 actions to choose from")
        }

        break
    }
}

private fun loadWorldCupData(): WorldCupData {
    val jsonText = object {}.javaClass.getResource("/world_cup_2026_full_data.json")
        ?.readText()
        ?: error("Resource /world_cup_2026_full_data.json not found (expected under src/main/resources)")

    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    return json.decodeFromString(jsonText)
}

/* -------------------------------------------------------------
   1) Show Standings
   ------------------------------------------------------------- */
private fun showStandings(allGroups: List<Group>) {
    println(
        """
===== FIFA World Cup 2026 - Standings =====
Single Group: Enter the group name, e.g. Group A
All Groups: Enter all
==========================================
""".trimIndent()
    )

    print("> ")
    val userInput = readln().trim()

    if (userInput.equals("all", ignoreCase = true) || userInput.equals("all groups", ignoreCase = true)) {
        for (group in allGroups) {
            println()
            printGroupStandings(group)
        }
        return
    }

    val chosenGroup = allGroups.firstOrNull { it.name.equals(userInput, ignoreCase = true) }
    if (chosenGroup == null) {
        println("Group '$userInput' not found. Available groups: ${allGroups.joinToString { it.name }}")
        return
    }

    println()
    printGroupStandings(chosenGroup)
}

private data class StandingRow(
    val team: Team,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}

private class MutableStandingRow(val team: Team) {
    var played: Int = 0
    var wins: Int = 0
    var draws: Int = 0
    var losses: Int = 0
    var goalsFor: Int = 0
    var goalsAgainst: Int = 0
    var points: Int = 0

    fun freeze(): StandingRow = StandingRow(
        team = team,
        played = played,
        wins = wins,
        draws = draws,
        losses = losses,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        points = points,
    )
}

private fun printGroupStandings(group: Group) {
    // Seed with all teams so teams with no played matches still show up.
    val table = group.teams.associate { it.id to MutableStandingRow(it) }.toMutableMap()

    for (match in group.matches) {
        val hs = match.homeScore
        val as_ = match.awayScore
        if (hs == null || as_ == null) continue

        val home = table.getOrPut(match.homeTeam) { MutableStandingRow(Team(match.homeTeam, match.homeTeam)) }
        val away = table.getOrPut(match.awayTeam) { MutableStandingRow(Team(match.awayTeam, match.awayTeam)) }

        home.played++
        away.played++

        home.goalsFor += hs
        home.goalsAgainst += as_
        away.goalsFor += as_
        away.goalsAgainst += hs

        when {
            hs > as_ -> {
                home.wins++
                away.losses++
                home.points += 3
            }

            hs < as_ -> {
                away.wins++
                home.losses++
                away.points += 3
            }

            else -> {
                home.draws++
                away.draws++
                home.points += 1
                away.points += 1
            }
        }
    }

    val rows = table.values
        .map { it.freeze() }
        .sortedWith(
            compareByDescending<StandingRow> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor }
                .thenBy { it.team.name }
        )

    val nameWidth = maxOf(10, rows.maxOf { it.team.name.length })
    println(group.name)
    println("-".repeat(group.name.length))
    println("#  " + "Team".padEnd(nameWidth) + "  Pts  GD")
    rows.forEachIndexed { idx, r ->
        println(
            buildString {
                append((idx + 1).toString().padStart(2))
                append("  ")
                append(r.team.name.padEnd(nameWidth))
                append("  ")
                append(r.points.toString().padStart(3))
                append("  ")
                append(r.goalDifference.toString().padStart(3))
            }
        )
    }
}

/* -------------------------------------------------------------
   2) Show Matches
   ------------------------------------------------------------- */
private fun showMatches(allGroups: List<Group>) {
    println("Which group's matches do you want to see?")
    val userInput = readln()

    val chosenGroup = allGroups.firstOrNull { it.name.equals(userInput, ignoreCase = true) }
    if (chosenGroup == null) {
        println("Group '$userInput' not found. Available groups: ${allGroups.joinToString { it.name }}")
        return
    }

    for (match in chosenGroup.matches) {
        println(match.round)
        println("#${match.matchId} ${match.homeTeam} vs. ${match.awayTeam}")
        println("- ${match.date}")
        if (match.homeScore == null || match.awayScore == null) {
            println("- To be started")
        } else {
            println("- ${match.homeScore}:${match.awayScore}")
        }
        println("- ${match.ground}")
        println("=====")
    }


}

/* -------------------------------------------------------------
   3) Place Bets
   ------------------------------------------------------------- */
private fun placeBets(allGroups: List<Group>) {
    //TODO
}

/* -------------------------------------------------------------
   4) Show Betting Score
   ------------------------------------------------------------- */
private fun showBettingScore(allGroups: List<Group>) {
    //TODO
}
