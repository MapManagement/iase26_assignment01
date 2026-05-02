package de.seuhd.worldcup

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


fun main() {
    val data = loadWorldCupData()
    println("Loaded '${data.tournament}' with ${data.groups.size} groups and ${data.knockouts.size} knockout matches")

    //TODO: Implement interactive menu
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
    //TODO
}

/* -------------------------------------------------------------
   2) Show Matches
   ------------------------------------------------------------- */
private fun showMatches(allGroups: List<Group>) {
    //TODO
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
