package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object TournamentList : Screen("tournaments", "Tournaments")
    data class TournamentDetails(val tournamentId: String) : Screen("tournament_details", "Details")
    data class MatchRoom(val tournamentId: String? = null) : Screen("match_room", "Match Room")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
    object Profile : Screen("profile", "Profile")
    object AdminDashboard : Screen("admin", "Admin Hub")
    object Auth : Screen("auth", "Sign In")
}

enum class BottomNavTab(val route: String, val label: String) {
    HOME("home", "Home"),
    TOURNAMENTS("tournaments", "Tournaments"),
    MY_MATCHES("my_matches", "My Matches"),
    PROFILE("profile", "Profile")
}
