package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GameMode
import com.example.data.model.TournamentStatus
import com.example.data.repository.TournamentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FF Tournament", appName)
  }

  @Test
  fun `verify initial tournaments loaded`() {
    val repo = TournamentRepository.getInstance()
    val tournaments = repo.tournaments.value
    assertTrue("Should have initial mock tournaments", tournaments.isNotEmpty())
    val liveTourneys = tournaments.filter { it.status == TournamentStatus.LIVE }
    assertTrue("Should contain live tournaments", liveTourneys.isNotEmpty())
  }

  @Test
  fun `verify team registration logic`() {
    val repo = TournamentRepository.getInstance()
    val tourney = repo.tournaments.value.first { !it.isFull }
    val initialSlots = tourney.currentSlots
    val (success, message) = repo.registerTeam(
      tournamentId = tourney.id,
      teamName = "Alpha Squad",
      leaderIgn = "Alpha_Leader",
      leaderUid = "1029384756",
      members = listOf("Alpha_One", "Alpha_Two")
    )
    assertTrue("Registration should succeed: $message", success)
    val updated = repo.tournaments.value.first { it.id == tourney.id }
    assertEquals(initialSlots + 1, updated.currentSlots)
  }

  @Test
  fun `verify match proof submission`() {
    val repo = TournamentRepository.getInstance()
    val tourney = repo.tournaments.value.first()
    repo.submitMatchProof(
      tournamentId = tourney.id,
      kills = 8,
      placement = 1,
      note = "Booyah screenshot verified"
    )
    val user = repo.currentUser.value
    assertTrue("User should have matches played", user.matchesPlayed > 0)
  }

  @Test
  fun `verify 1 to 1 coin conversion for deposit and withdrawal`() {
    val repo = TournamentRepository.getInstance()
    val initialCoins = repo.currentUser.value.coins

    // Deposit 100 Pack (100 INR = 100 Coins)
    val depositTx = repo.depositCoins(100, 100, "UPI (Google Pay)")
    assertEquals(100, depositTx.amountCoins)
    assertEquals(initialCoins + 100, repo.currentUser.value.coins)

    // Withdraw 50 INR (requires exactly 50 Coins)
    val (withdrawn, withdrawTx) = repo.withdrawCoins(50, 50, "test@okaxis", "Pro Player")
    assertTrue("Withdrawal should succeed", withdrawn)
    assertNotNull(withdrawTx)
    assertEquals(50, withdrawTx!!.amountCoins)
    assertEquals(initialCoins + 100 - 50, repo.currentUser.value.coins)
  }

  @Test
  fun `verify admin create edit and delete tournament with prize split`() {
    val repo = TournamentRepository.getInstance()
    val initialCount = repo.tournaments.value.size

    val customTourney = com.example.data.model.Tournament(
      id = "test_custom_${System.currentTimeMillis()}",
      title = "Admin Custom Clash",
      mode = GameMode.SQUAD,
      map = com.example.data.model.GameMap.BERMUDA,
      status = TournamentStatus.UPCOMING,
      prizePoolCoins = 5000,
      entryFeeCoins = 50,
      perKillBonusCoins = 20,
      maxSlots = 12,
      scheduleTime = "Tonight, 10:00 PM IST",
      prizeBreakdown = listOf(
        com.example.data.model.PrizeBreakdown("1st Place", 2500, 100, "₹2500 Cash"),
        com.example.data.model.PrizeBreakdown("2nd Place", 1500, 70, "₹1500 Cash"),
        com.example.data.model.PrizeBreakdown("3rd Place", 1000, 50, "₹1000 Cash")
      )
    )

    // 1. Create
    repo.addTournament(customTourney)
    assertEquals(initialCount + 1, repo.tournaments.value.size)
    val found = repo.tournaments.value.first { it.id == customTourney.id }
    assertEquals("Admin Custom Clash", found.title)
    assertEquals(5000, found.prizePoolCoins)
    assertEquals(50, found.entryFeeCoins)
    assertEquals(3, found.prizeBreakdown.size)
    assertEquals(2500, found.prizeBreakdown[0].prizeCoins)

    // 2. Edit with Room Credentials
    val updatedTourney = found.copy(
      title = "Admin Custom Clash (Edited)",
      prizePoolCoins = 6000,
      roomId = "88997711",
      roomPassword = "pass123",
      prizeBreakdown = listOf(
        com.example.data.model.PrizeBreakdown("1st Place", 3600, 100, "₹3600 Cash"),
        com.example.data.model.PrizeBreakdown("2nd Place", 1500, 70, "₹1500 Cash"),
        com.example.data.model.PrizeBreakdown("3rd Place", 900, 50, "₹900 Cash")
      )
    )
    val (editSuccess, roomChanged) = repo.updateTournament(updatedTourney)
    assertTrue("Edit should succeed", editSuccess)
    assertTrue("Room changed should be true", roomChanged)
    val editedFound = repo.tournaments.value.first { it.id == customTourney.id }
    assertEquals("Admin Custom Clash (Edited)", editedFound.title)
    assertEquals(6000, editedFound.prizePoolCoins)
    assertEquals("88997711", editedFound.roomId)
    assertEquals("pass123", editedFound.roomPassword)

    // 3. Delete
    val deleteSuccess = repo.deleteTournament(customTourney.id)
    assertTrue("Delete should succeed", deleteSuccess)
    assertEquals(initialCount, repo.tournaments.value.size)
  }

  @Test
  fun `verify firestore leaderboard sorting by points and performance`() {
    val service = com.example.data.firestore.FirestoreLeaderboardService.getInstance()
    val testItems = listOf(
      com.example.data.model.LeaderboardItem(1, "Player A", "[A]", booyahs = 10, kills = 200, points = 1500, winRate = "50%", tier = "Heroic", matchesPlayed = 20, kdRatio = 10.0),
      com.example.data.model.LeaderboardItem(2, "Player B", "[B]", booyahs = 15, kills = 150, points = 1800, winRate = "60%", tier = "Grandmaster", matchesPlayed = 25, kdRatio = 6.0),
      com.example.data.model.LeaderboardItem(3, "Player C", "[C]", booyahs = 5, kills = 350, points = 1200, winRate = "25%", tier = "Master", matchesPlayed = 30, kdRatio = 11.6)
    )

    // 1. Sort by Points (Default)
    val byPoints = service.sortItems(testItems, com.example.data.firestore.LeaderboardSortBy.POINTS)
    assertEquals("Player B", byPoints[0].name)
    assertEquals(1800, byPoints[0].points)
    assertEquals(1, byPoints[0].rank)
    assertEquals("👑", byPoints[0].badge)
    assertEquals("Player A", byPoints[1].name)
    assertEquals("🥈", byPoints[1].badge)
    assertEquals("Player C", byPoints[2].name)
    assertEquals("🥉", byPoints[2].badge)

    // 2. Sort by Kills (Top Fraggers)
    val byKills = service.sortItems(testItems, com.example.data.firestore.LeaderboardSortBy.KILLS)
    assertEquals("Player C", byKills[0].name)
    assertEquals(350, byKills[0].kills)
    assertEquals(1, byKills[0].rank)
    assertEquals("Player A", byKills[1].name)
    assertEquals("Player B", byKills[2].name)

    // 3. Sort by Booyahs (Championship Wins)
    val byBooyahs = service.sortItems(testItems, com.example.data.firestore.LeaderboardSortBy.BOOYAHS)
    assertEquals("Player B", byBooyahs[0].name)
    assertEquals(15, byBooyahs[0].booyahs)
    assertEquals("Player A", byBooyahs[1].name)
    assertEquals(10, byBooyahs[1].booyahs)
    assertEquals("Player C", byBooyahs[2].name)
    assertEquals(5, byBooyahs[2].booyahs)

    // 4. Sort by Matches Played
    val byMatches = service.sortItems(testItems, com.example.data.firestore.LeaderboardSortBy.MATCHES_PLAYED)
    assertEquals("Player C", byMatches[0].name)
    assertEquals(30, byMatches[0].matchesPlayed)
  }
}

