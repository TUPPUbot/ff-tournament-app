package com.example.data.repository

import com.example.data.model.GameMap
import com.example.data.model.GameMode
import com.example.data.model.LeaderboardItem
import com.example.data.model.MatchAnnouncement
import com.example.data.model.MatchResultSubmission
import com.example.data.model.PlayerProfile
import com.example.data.model.PrizeBreakdown
import com.example.data.model.RoomSlot
import com.example.data.model.TeamRegistration
import com.example.data.model.Tournament
import com.example.data.model.TournamentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TournamentRepository private constructor() {

    companion object {
        @Volatile
        private var instance: TournamentRepository? = null

        fun getInstance(): TournamentRepository {
            return instance ?: synchronized(this) {
                instance ?: TournamentRepository().also { instance = it }
            }
        }
    }

    private val _currentUser = MutableStateFlow(
        PlayerProfile(
            uid = "182939401",
            ign = "Vampire★FF",
            email = "vampire.esports@gmail.com",
            rankTier = "Grandmaster",
            level = 72,
            coins = 350, // 1 Rupee = 1 Coin (₹350.00 Real Balance)
            diamonds = 450,
            matchesPlayed = 158,
            booyahs = 54,
            kills = 724,
            kdRatio = 4.58,
            winRate = 34.2,
            headshotRate = 48.6,
            isAdmin = false,
            registeredTournamentIds = setOf("tourney_1", "tourney_2"),
            bio = "Official Rusher for Team Ignis | Free Fire World Series Qualifier Semifinalist"
        )
    )
    val currentUser: StateFlow<PlayerProfile> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val sampleTournaments = listOf(
        Tournament(
            id = "tourney_1",
            title = "Bermuda Grand Squad Cup",
            mode = GameMode.SQUAD,
            map = GameMap.BERMUDA,
            prizePoolCoins = 5000,
            entryFeeCoins = 0, // Free entry
            perKillBonusCoins = 10,
            maxSlots = 12,
            status = TournamentStatus.LIVE,
            scheduleTime = "Tonight, 9:00 PM IST",
            date = "Tonight",
            gunAttributes = false,
            roomId = "8492041",
            roomPassword = "FF99",
            roomActive = true,
            rules = listOf(
                "Mobile players only. PC / Emulators strictly banned.",
                "Gun skin attributes OFF for fair esports competition.",
                "Character skill loadout permitted.",
                "Lobby joins strictly on allotted slot number.",
                "Room password revealed to registered squads."
            ),
            prizeBreakdown = listOf(
                PrizeBreakdown("1st Place (BOOYAH)", 2500, 12, "Gold Trophy & ₹2,500 Cash"),
                PrizeBreakdown("2nd Place", 1200, 9, "Silver Medal & ₹1,200 Cash"),
                PrizeBreakdown("3rd Place", 800, 8, "Bronze Medal & ₹800 Cash"),
                PrizeBreakdown("Top Fragger MVP", 500, 5, "MVP Esports Badge & ₹500 Cash")
            ),
            registeredTeams = listOf(
                TeamRegistration("r1", "Team Ignis", "Vampire★FF", "182939401", listOf("Ignis_Ace", "Ignis_Sniper", "Ignis_Rush"), 1, "18:20"),
                TeamRegistration("r2", "Soul Esports", "Mortal_God", "782390123", listOf("Soul_Regaltos", "Soul_Viper", "Soul_Aman"), 2, "18:25"),
                TeamRegistration("r3", "Total Gaming", "Ajjubhai94", "451298401", listOf("TG_FozyAjay", "TG_Mafia", "TG_Bala"), 3, "18:31"),
                TeamRegistration("r4", "Nigma Galaxy", "NG_VastoLorde", "329041234", listOf("NG_Golden", "NG_Soar", "NG_Shadow"), 4, "18:40"),
                TeamRegistration("r5", "GodLike FF", "JONATHAN_FF", "891230491", listOf("Goda_Neyoo", "Goda_Zgod", "Goda_Clutch"), 5, "18:45"),
                TeamRegistration("r6", "Chemin Esports", "Radhe_FF", "910248231", listOf("Swastik", "Wizzy", "Anand"), 6, "18:50"),
                TeamRegistration("r7", "Orangutan Elite", "Pahadi_Gaming", "239014829", listOf("MrJay", "Jash", "Killer"), 7, "18:52"),
                TeamRegistration("r8", "Revenant FF", "Rvnt_Paradox", "562910482", listOf("Rvnt_Kyro", "Rvnt_Blaze", "Rvnt_Zero"), 8, "19:00"),
                TeamRegistration("r9", "TSM Entity", "TSM_Shadow", "771029481", listOf("TSM_Ninja", "TSM_Frenzy", "TSM_Rex"), 9, "19:05"),
                TeamRegistration("r10", "Enigma Gaming", "EG_Toxic", "662910482", listOf("EG_Hunter", "EG_Ghost", "EG_Venom"), 10, "19:12")
            )
        ),
        Tournament(
            id = "tourney_2",
            title = "Purgatory Solo Blitz Arena",
            mode = GameMode.SOLO,
            map = GameMap.PURGATORY,
            prizePoolCoins = 1500,
            entryFeeCoins = 50,
            perKillBonusCoins = 5,
            maxSlots = 48,
            status = TournamentStatus.REGISTRATION_OPEN,
            scheduleTime = "Tomorrow, 6:00 PM IST",
            date = "Tomorrow",
            gunAttributes = false,
            roomId = null,
            roomPassword = null,
            roomActive = false,
            rules = listOf(
                "Solo mode: Teaming up will result in immediate disqualification.",
                "Gun attributes OFF.",
                "Points awarded for placement + 1 point per kill.",
                "Custom Room ID & Password released 15 mins before match."
            ),
            prizeBreakdown = listOf(
                PrizeBreakdown("1st Place (BOOYAH)", 750, 12, "₹750 Real Cash"),
                PrizeBreakdown("2nd Place", 400, 9, "₹400 Real Cash"),
                PrizeBreakdown("3rd Place", 200, 8, "₹200 Real Cash"),
                PrizeBreakdown("Most Kills", 150, 6, "₹150 Real Cash")
            ),
            registeredTeams = listOf(
                TeamRegistration("r11", "Solo Rusher", "Vampire★FF", "182939401", emptyList(), 1, "20:01"),
                TeamRegistration("r12", "Solo King", "Killer_77", "981240123", emptyList(), 2, "20:05"),
                TeamRegistration("r13", "Headshot God", "Raistar_Official", "882194012", emptyList(), 3, "20:10"),
                TeamRegistration("r14", "Dark Knight", "Badge99_YT", "441209381", emptyList(), 4, "20:15"),
                TeamRegistration("r15", "Sniper Pro", "Pahadi_Sniper", "331029481", emptyList(), 5, "20:22"),
                TeamRegistration("r16", "Lone Wolf", "GyanSujan", "662910481", emptyList(), 6, "20:30")
            )
        ),
        Tournament(
            id = "tourney_3",
            title = "Clash Squad 4v4 Showdown",
            mode = GameMode.CLASH_SQUAD,
            map = GameMap.BERMUDA,
            prizePoolCoins = 1000,
            entryFeeCoins = 0,
            perKillBonusCoins = 0,
            maxSlots = 2,
            status = TournamentStatus.STARTING_SOON,
            scheduleTime = "Today, 10:30 PM IST",
            date = "Today",
            gunAttributes = false,
            roomId = "5910294",
            roomPassword = "CS44",
            roomActive = true,
            rules = listOf(
                "Best of 7 Rounds Clash Squad format.",
                "Default Store / Limited Ammo: YES.",
                "Character skill active, Gun attributes OFF.",
                "Grenade spamming restricted to max 1 per round."
            ),
            prizeBreakdown = listOf(
                PrizeBreakdown("Winner Team", 700, 10, "₹700 Real Cash"),
                PrizeBreakdown("Runner Up", 300, 5, "₹300 Real Cash")
            ),
            registeredTeams = listOf(
                TeamRegistration("r21", "Red Fire Squad", "Alpha_King", "991204821", listOf("Flame_Boy", "Blaze_Rush", "Ghost_CS"), 1, "17:00"),
                TeamRegistration("r22", "Blue Titans", "Titan_Lead", "881029471", listOf("Titan_One", "Titan_Two", "Titan_Three"), 2, "17:15")
            )
        ),
        Tournament(
            id = "tourney_4",
            title = "Kalahari Duo Rush League",
            mode = GameMode.DUO,
            map = GameMap.KALAHARI,
            prizePoolCoins = 2000,
            entryFeeCoins = 100,
            perKillBonusCoins = 10,
            maxSlots = 24,
            status = TournamentStatus.REGISTRATION_OPEN,
            scheduleTime = "Sunday, 4:00 PM IST",
            date = "Sunday",
            gunAttributes = false,
            roomId = null,
            roomPassword = null,
            roomActive = false,
            rules = listOf(
                "Duo pairings only.",
                "Kalahari terrain rules: High elevation glider tactical play.",
                "No team-up with outside duos.",
                "Screenshots mandatory if disconnect occurs before landing."
            ),
            prizeBreakdown = listOf(
                PrizeBreakdown("1st Place", 1000, 12, "₹1,000 Real Cash"),
                PrizeBreakdown("2nd Place", 500, 9, "₹500 Real Cash"),
                PrizeBreakdown("3rd Place", 300, 8, "₹300 Real Cash"),
                PrizeBreakdown("Kill Leaders", 200, 6, "₹200 Real Cash")
            ),
            registeredTeams = listOf(
                TeamRegistration("r31", "Desert Storm", "Duo_Storm1", "771029482", listOf("Duo_Storm2"), 1, "12:00"),
                TeamRegistration("r32", "Mirage Duo", "Mirage_Ace", "551029381", listOf("Mirage_Pro"), 2, "12:15"),
                TeamRegistration("r33", "Cactus Snipers", "Kalahari_God", "441029481", listOf("Cactus_Boy"), 3, "12:30")
            )
        ),
        Tournament(
            id = "tourney_5",
            title = "Alpine Masters Cup (Invitational)",
            mode = GameMode.SQUAD,
            map = GameMap.ALPINE,
            prizePoolCoins = 10000,
            entryFeeCoins = 0,
            perKillBonusCoins = 20,
            maxSlots = 12,
            status = TournamentStatus.COMPLETED,
            scheduleTime = "Sep 18, 8:00 PM IST",
            date = "Sep 18",
            gunAttributes = false,
            roomId = "3910249",
            roomPassword = "ALP9",
            roomActive = false,
            winnerTeam = "Team Ignis",
            mvpPlayer = "Vampire★FF (14 Kills)",
            rules = listOf(
                "Official Alpine Esports Ruleset.",
                "Spectated live on YouTube Esports stream.",
                "Minimum player level 50."
            ),
            prizeBreakdown = listOf(
                PrizeBreakdown("1st Place - BOOYAH", 5000, 12, "Won by Team Ignis (₹5,000 Cash)"),
                PrizeBreakdown("2nd Place", 2500, 9, "Won by GodLike FF (₹2,500 Cash)"),
                PrizeBreakdown("3rd Place", 1500, 8, "Won by Total Gaming (₹1,500 Cash)"),
                PrizeBreakdown("MVP Player", 1000, 5, "Awarded to Vampire★FF (₹1,000 Cash)")
            ),
            registeredTeams = listOf(
                TeamRegistration("r41", "Team Ignis", "Vampire★FF", "182939401", listOf("Ignis_Ace", "Ignis_Sniper", "Ignis_Rush"), 1, "15:00"),
                TeamRegistration("r42", "GodLike FF", "JONATHAN_FF", "891230491", listOf("Goda_Neyoo", "Goda_Zgod", "Goda_Clutch"), 2, "15:05"),
                TeamRegistration("r43", "Total Gaming", "Ajjubhai94", "451298401", listOf("TG_FozyAjay", "TG_Mafia", "TG_Bala"), 3, "15:10")
            )
        )
    )

    private val _tournaments = MutableStateFlow<List<Tournament>>(sampleTournaments)
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val sampleLeaderboards = listOf(
        LeaderboardItem(1, "Vampire★FF", "[IGNIS]", 54, 724, 2850, "34.2%", "Grandmaster", false, "👑"),
        LeaderboardItem(2, "Raistar_Official", "[GODS]", 51, 698, 2720, "32.8%", "Grandmaster", false, "🥈"),
        LeaderboardItem(3, "Mortal_God", "[SOUL]", 47, 650, 2590, "31.5%", "Grandmaster", false, "🥉"),
        LeaderboardItem(4, "Ajjubhai94", "[TG]", 45, 630, 2480, "30.1%", "Heroic III", false),
        LeaderboardItem(5, "Pahadi_Sniper", "[OG]", 42, 595, 2350, "29.4%", "Heroic II", false),
        LeaderboardItem(6, "Badge99_YT", "[BADGE]", 39, 560, 2210, "28.0%", "Heroic I", false),
        LeaderboardItem(7, "JONATHAN_FF", "[GODL]", 38, 545, 2150, "27.5%", "Heroic I", false),
        LeaderboardItem(8, "Alpha_King", "[RED]", 35, 510, 2020, "26.2%", "Master", false)
    )

    private val sampleGuildLeaderboards = listOf(
        LeaderboardItem(1, "Team Ignis", "IGNIS", 184, 2450, 9800, "68.4%", "Tier 1 Esports", true, "👑"),
        LeaderboardItem(2, "Soul Esports", "SOUL", 172, 2310, 9250, "65.1%", "Tier 1 Esports", true, "🥈"),
        LeaderboardItem(3, "Total Gaming", "TG", 165, 2190, 8820, "62.8%", "Tier 1 Esports", true, "🥉"),
        LeaderboardItem(4, "GodLike Esports", "GODL", 154, 2040, 8340, "59.5%", "Tier 1 Esports", true),
        LeaderboardItem(5, "Orangutan Elite", "OG", 142, 1920, 7890, "57.0%", "Tier 2 Pro", true),
        LeaderboardItem(6, "Revenant FF", "RVNT", 130, 1780, 7400, "55.2%", "Tier 2 Pro", true)
    )

    val playerLeaderboard: List<LeaderboardItem> = sampleLeaderboards
    val guildLeaderboard: List<LeaderboardItem> = sampleGuildLeaderboards

    private val _announcements = MutableStateFlow(
        listOf(
            MatchAnnouncement("a1", "20:50", "Admin Referee", "📢 Match Room for Bermuda Grand Squad Cup is now OPEN! Join Slot 1 to 10.", true),
            MatchAnnouncement("a2", "20:45", "System", "⚠️ Reminder: Emulator players will be kicked automatically prior to start.", false),
            MatchAnnouncement("a3", "20:30", "Head Admin", "Gun skin attributes must be toggled off in settings before entering room.", false)
        )
    )
    val announcements: StateFlow<List<MatchAnnouncement>> = _announcements.asStateFlow()

    private val _submissions = MutableStateFlow<List<MatchResultSubmission>>(emptyList())
    val submissions: StateFlow<List<MatchResultSubmission>> = _submissions.asStateFlow()

    fun getTournamentById(id: String): Tournament? {
        return _tournaments.value.find { it.id == id }
    }

    fun registerTeam(
        tournamentId: String,
        teamName: String,
        leaderIgn: String,
        leaderUid: String,
        members: List<String>
    ): Pair<Boolean, String> {
        val list = _tournaments.value.toMutableList()
        val index = list.indexOfFirst { it.id == tournamentId }
        if (index == -1) return Pair(false, "Tournament not found")

        val tourney = list[index]
        if (tourney.isFull) return Pair(false, "Tournament slots are completely filled!")
        if (tourney.status == TournamentStatus.COMPLETED) return Pair(false, "Tournament has already concluded")

        val user = _currentUser.value
        if (tourney.entryFeeCoins > user.coins) {
            return Pair(false, "Insufficient balance (${user.coins} Coins available, ₹${tourney.entryFeeCoins} (🪙 ${tourney.entryFeeCoins}) needed. Please top-up to enter!)")
        }

        val nextSlot = tourney.registeredTeams.size + 1
        val newRegistration = TeamRegistration(
            id = UUID.randomUUID().toString(),
            teamName = teamName.ifBlank { "${leaderIgn}'s Team" },
            leaderIgn = leaderIgn,
            leaderUid = leaderUid,
            members = members.filter { it.isNotBlank() },
            slotNumber = nextSlot,
            registeredAt = "Just now"
        )

        val updatedTourney = tourney.copy(
            registeredTeams = tourney.registeredTeams + newRegistration
        )
        list[index] = updatedTourney
        _tournaments.value = list

        // Update user state
        val updatedRegistered = user.registeredTournamentIds + tournamentId
        val updatedCoins = user.coins - tourney.entryFeeCoins
        val newTxList = if (tourney.entryFeeCoins > 0) {
            val record = com.example.data.model.TransactionRecord(
                id = UUID.randomUUID().toString(),
                title = "Entry Fee: ${tourney.title}",
                amountCoins = tourney.entryFeeCoins,
                type = com.example.data.model.TransactionType.ENTRY_FEE,
                date = "Just now",
                paymentMethod = "Wallet Balance",
                status = "PAID",
                receiptId = "TXN_FEE_${System.currentTimeMillis() % 1000000}"
            )
            listOf(record) + user.walletTransactions
        } else {
            user.walletTransactions
        }

        _currentUser.value = user.copy(
            registeredTournamentIds = updatedRegistered,
            coins = updatedCoins,
            walletTransactions = newTxList
        )

        return Pair(true, "Successfully registered for ${tourney.title}! Assigned Slot #$nextSlot")
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, roomPassword: String, isLive: Boolean): Tournament? {
        val list = _tournaments.value.toMutableList()
        val index = list.indexOfFirst { it.id == tournamentId }
        if (index != -1) {
            val current = list[index]
            val updated = current.copy(
                roomId = roomId,
                roomPassword = roomPassword,
                roomActive = true,
                status = if (isLive) TournamentStatus.LIVE else TournamentStatus.STARTING_SOON
            )
            list[index] = updated
            _tournaments.value = list

            // Add announcement
            val newAnnouncement = MatchAnnouncement(
                id = UUID.randomUUID().toString(),
                time = "Just now",
                author = "Tournament Admin",
                message = "🔑 Custom Room ID & Password released for ${current.title}! ID: $roomId | Password: $roomPassword. All joined players enter immediately!",
                isImportant = true
            )
            _announcements.value = listOf(newAnnouncement) + _announcements.value
            return updated
        }
        return null
    }

    /**
     * Updates tournament details and checks if Room ID/Password changed to notify contenders.
     * Returns Pair(success, roomCredentialsChanged).
     */
    fun updateTournament(updated: Tournament): Pair<Boolean, Boolean> {
        val list = _tournaments.value.toMutableList()
        val index = list.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            val old = list[index]
            val roomChanged = (!updated.roomId.isNullOrBlank() && updated.roomId != old.roomId) ||
                    (!updated.roomPassword.isNullOrBlank() && updated.roomPassword != old.roomPassword)

            val finalTourney = if (roomChanged) {
                updated.copy(roomActive = true)
            } else {
                updated
            }
            list[index] = finalTourney
            _tournaments.value = list

            if (roomChanged) {
                val newAnnouncement = MatchAnnouncement(
                    id = UUID.randomUUID().toString(),
                    time = "Just now",
                    author = "Tournament Admin",
                    message = "🔑 Custom Room ID updated for ${finalTourney.title}! ID: ${finalTourney.roomId} | Password: ${finalTourney.roomPassword}. Enter your slot now!",
                    isImportant = true
                )
                _announcements.value = listOf(newAnnouncement) + _announcements.value
            }
            return Pair(true, roomChanged)
        }
        return Pair(false, false)
    }

    /**
     * Deletes a tournament by ID.
     */
    fun deleteTournament(tournamentId: String): Boolean {
        val list = _tournaments.value.toMutableList()
        val removed = list.removeAll { it.id == tournamentId }
        if (removed) {
            _tournaments.value = list
            return true
        }
        return false
    }

    fun updateTournamentStatus(tournamentId: String, status: TournamentStatus) {
        val list = _tournaments.value.toMutableList()
        val index = list.indexOfFirst { it.id == tournamentId }
        if (index != -1) {
            list[index] = list[index].copy(status = status)
            _tournaments.value = list
        }
    }

    fun declareWinners(tournamentId: String, winnerTeam: String, mvp: String) {
        val list = _tournaments.value.toMutableList()
        val index = list.indexOfFirst { it.id == tournamentId }
        if (index != -1) {
            list[index] = list[index].copy(
                winnerTeam = winnerTeam,
                mvpPlayer = mvp,
                status = TournamentStatus.COMPLETED
            )
            _tournaments.value = list

            val newAnnouncement = MatchAnnouncement(
                id = UUID.randomUUID().toString(),
                time = "Just now",
                author = "Tournament Referee",
                message = "🏆 BOOYAH! $winnerTeam won ${list[index].title}! MVP: $mvp.",
                isImportant = true
            )
            _announcements.value = listOf(newAnnouncement) + _announcements.value
        }
    }

    fun createTournament(tournament: Tournament) {
        val breakdown = if (tournament.prizeBreakdown.isEmpty()) {
            val total = tournament.prizePoolCoins
            listOf(
                PrizeBreakdown("1st Place (BOOYAH)", (total * 0.50).toInt(), 12, "Gold Trophy & Coins"),
                PrizeBreakdown("2nd Place", (total * 0.25).toInt(), 9, "Silver Medal & Coins"),
                PrizeBreakdown("3rd Place", (total * 0.15).toInt(), 8, "Bronze Medal & Coins"),
                PrizeBreakdown("Top Fragger MVP", (total * 0.10).toInt(), 5, "MVP Bounty")
            )
        } else {
            tournament.prizeBreakdown
        }
        val finalTourney = tournament.copy(prizeBreakdown = breakdown)
        _tournaments.value = listOf(finalTourney) + _tournaments.value

        val newAnnouncement = MatchAnnouncement(
            id = UUID.randomUUID().toString(),
            time = "Just now",
            author = "Tournament Admin",
            message = "🔥 NEW TOURNAMENT ANNOUNCED: ${tournament.title}! Prize Pool: 🪙 ${tournament.prizePoolCoins} Coins. Mode: ${tournament.mode.label} on ${tournament.map.label}. Register your team now!",
            isImportant = true
        )
        _announcements.value = listOf(newAnnouncement) + _announcements.value
    }

    fun depositCoins(amountCoins: Int, amountInr: Int, paymentMethod: String): com.example.data.model.TransactionRecord {
        val user = _currentUser.value
        val receipt = "TXN_DEP_${System.currentTimeMillis() % 1000000}"
        val record = com.example.data.model.TransactionRecord(
            id = UUID.randomUUID().toString(),
            title = "Wallet Deposit (₹$amountInr)",
            amountCoins = amountCoins,
            type = com.example.data.model.TransactionType.DEPOSIT,
            date = "Just now",
            paymentMethod = paymentMethod,
            status = "SUCCESS",
            receiptId = receipt
        )
        _currentUser.value = user.copy(
            coins = user.coins + amountCoins,
            walletTransactions = listOf(record) + user.walletTransactions
        )
        return record
    }

    fun withdrawCoins(amountCoins: Int, amountInr: Int, upiId: String, holderName: String): Pair<Boolean, com.example.data.model.TransactionRecord?> {
        val user = _currentUser.value
        if (amountCoins <= 0 || user.coins < amountCoins) {
            return Pair(false, null)
        }
        val receipt = "PO_UPI_${System.currentTimeMillis() % 1000000}"
        val record = com.example.data.model.TransactionRecord(
            id = UUID.randomUUID().toString(),
            title = "UPI Cashout (₹$amountInr)",
            amountCoins = amountCoins,
            type = com.example.data.model.TransactionType.WITHDRAWAL,
            date = "Just now",
            paymentMethod = "UPI ($upiId)",
            status = "PROCESSED",
            receiptId = receipt
        )
        _currentUser.value = user.copy(
            coins = user.coins - amountCoins,
            upiId = if (upiId.isNotBlank()) upiId else user.upiId,
            walletTransactions = listOf(record) + user.walletTransactions
        )
        return Pair(true, record)
    }

    fun updateUpiId(newUpiId: String) {
        if (newUpiId.isNotBlank()) {
            _currentUser.value = _currentUser.value.copy(upiId = newUpiId.trim())
        }
    }

    fun getRoomSlots(tournament: Tournament): List<RoomSlot> {
        val max = tournament.maxSlots
        val user = _currentUser.value
        val slots = mutableListOf<RoomSlot>()
        val registeredMap = tournament.registeredTeams.associateBy { it.slotNumber }

        for (i in 1..max) {
            val reg = registeredMap[i]
            if (reg != null) {
                val isUserSlot = reg.leaderUid == user.uid || reg.leaderIgn.equals(user.ign, ignoreCase = true)
                slots.add(
                    RoomSlot(
                        slotNumber = i,
                        teamName = reg.teamName,
                        leaderIgn = reg.leaderIgn,
                        members = reg.members,
                        isUserSlot = isUserSlot
                    )
                )
            } else {
                slots.add(
                    RoomSlot(
                        slotNumber = i,
                        teamName = null,
                        leaderIgn = null,
                        members = emptyList(),
                        isUserSlot = false
                    )
                )
            }
        }
        return slots
    }

    fun submitMatchResult(
        tournamentId: String,
        playerIgn: String,
        playerUid: String,
        kills: Int,
        placement: Int,
        note: String
    ) {
        val submission = MatchResultSubmission(
            id = UUID.randomUUID().toString(),
            tournamentId = tournamentId,
            playerIgn = playerIgn,
            playerUid = playerUid,
            reportedKills = kills,
            claimedPlacement = placement,
            proofNote = note
        )
        _submissions.value = listOf(submission) + _submissions.value
    }

    fun login(identifier: String, uid: String, asAdmin: Boolean = false): Boolean {
        _currentUser.value = _currentUser.value.copy(
            ign = if (identifier.contains("@")) identifier.substringBefore("@") else identifier,
            uid = uid.ifBlank { "182939401" },
            isAdmin = asAdmin
        )
        _isLoggedIn.value = true
        return true
    }

    fun signup(ign: String, uid: String, email: String): Boolean {
        val welcomeRecord = com.example.data.model.TransactionRecord(
            id = UUID.randomUUID().toString(),
            title = "Signup Welcome Bonus (₹50)",
            amountCoins = 50,
            type = com.example.data.model.TransactionType.DEPOSIT,
            date = "Just now",
            paymentMethod = "Esports Platform Gift",
            status = "SUCCESS",
            receiptId = "BONUS_${System.currentTimeMillis() % 1000000}"
        )
        _currentUser.value = PlayerProfile(
            uid = uid.ifBlank { "182939401" },
            ign = ign.ifBlank { "ProGamer" },
            email = email,
            rankTier = "Diamond IV",
            level = 45,
            coins = 50, // 1 Rupee = 1 Coin (₹50 Starter Real Balance)
            diamonds = 100,
            matchesPlayed = 0,
            booyahs = 0,
            kills = 0,
            kdRatio = 0.0,
            winRate = 0.0,
            headshotRate = 0.0,
            isAdmin = false,
            registeredTournamentIds = emptySet(),
            bio = "New challenger in FF Tournament!",
            matchHistory = emptyList(),
            walletTransactions = listOf(welcomeRecord)
        )
        _isLoggedIn.value = true
        return true
    }

    fun toggleAdminMode(enabled: Boolean) {
        _currentUser.value = _currentUser.value.copy(isAdmin = enabled)
    }

    fun updateProfile(newIgn: String, newUid: String, newBio: String, newUpiId: String = "") {
        _currentUser.value = _currentUser.value.copy(
            ign = newIgn,
            uid = newUid,
            bio = newBio,
            upiId = if (newUpiId.isNotBlank()) newUpiId.trim() else _currentUser.value.upiId
        )
    }

    fun addTournament(tournament: Tournament) {
        createTournament(tournament)
    }

    fun postAnnouncement(message: String, author: String = "Admin", isImportant: Boolean = false) {
        val newAnnouncement = MatchAnnouncement(
            id = UUID.randomUUID().toString(),
            time = "Just now",
            author = author,
            message = message,
            isImportant = isImportant
        )
        _announcements.value = listOf(newAnnouncement) + _announcements.value
    }

    fun submitMatchProof(tournamentId: String, kills: Int, placement: Int, note: String) {
        val user = _currentUser.value
        submitMatchResult(tournamentId, user.ign, user.uid, kills, placement, note)
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
