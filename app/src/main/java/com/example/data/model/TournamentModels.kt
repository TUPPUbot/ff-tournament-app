package com.example.data.model

enum class TournamentStatus(val label: String) {
    UPCOMING("Upcoming"),
    REGISTRATION_OPEN("Registration Open"),
    STARTING_SOON("Starting Soon"),
    LIVE("LIVE MATCH"),
    COMPLETED("Completed")
}

enum class GameMode(val label: String, val maxSlots: Int, val teamSize: Int) {
    SOLO("Solo", 48, 1),
    DUO("Duo", 24, 2),
    SQUAD("Squad", 12, 4),
    CLASH_SQUAD("Clash Squad", 2, 4)
}

enum class GameMap(val label: String) {
    BERMUDA("Bermuda"),
    PURGATORY("Purgatory"),
    KALAHARI("Kalahari"),
    ALPINE("Alpine"),
    NEXTERRA("NexTerra")
}

data class PrizeBreakdown(
    val rank: String,
    val prizeCoins: Int,
    val points: Int,
    val description: String = ""
)

data class TeamRegistration(
    val id: String,
    val teamName: String,
    val leaderIgn: String,
    val leaderUid: String,
    val members: List<String> = emptyList(),
    val slotNumber: Int,
    val registeredAt: String
)

data class Tournament(
    val id: String,
    val title: String,
    val mode: GameMode,
    val map: GameMap,
    val prizePoolCoins: Int,
    val entryFeeCoins: Int, // 0 = Free
    val perKillBonusCoins: Int = 50,
    val maxSlots: Int,
    val registeredTeams: List<TeamRegistration> = emptyList(),
    val status: TournamentStatus = TournamentStatus.REGISTRATION_OPEN,
    val scheduleTime: String,
    val date: String = "Today",
    val gunAttributes: Boolean = false,
    val roomId: String? = null,
    val roomPassword: String? = null,
    val roomActive: Boolean = false,
    val rules: List<String> = listOf(
        "Emulators strictly prohibited (Mobile only).",
        "Gun properties / attributes OFF.",
        "Character skill allowed.",
        "Hackers / script users will receive permanent platform ban.",
        "Room ID & password revealed 15 minutes prior to match.",
        "All players must join their assigned slot number."
    ),
    val prizeBreakdown: List<PrizeBreakdown> = emptyList(),
    val winnerTeam: String? = null,
    val mvpPlayer: String? = null,
    val streamUrl: String? = null
) {
    val currentSlots: Int
        get() = registeredTeams.size

    val isFull: Boolean
        get() = currentSlots >= maxSlots
}

data class MatchHistoryItem(
    val id: String,
    val tournamentTitle: String,
    val date: String,
    val rank: Int,
    val kills: Int,
    val coinsEarned: Int,
    val mode: String
)

enum class TransactionType(val label: String, val isCredit: Boolean) {
    DEPOSIT("Wallet Top-up", true),
    WITHDRAWAL("UPI Cashout", false),
    ENTRY_FEE("Entry Fee Payment", false),
    PRIZE_REWARD("Tournament Prize", true),
    KILL_BONUS("Kill Bounty Reward", true),
    REFUND("Entry Fee Refund", true)
}

data class TransactionRecord(
    val id: String,
    val title: String,
    val amountCoins: Int,
    val type: TransactionType,
    val date: String,
    val paymentMethod: String? = null,
    val status: String = "SUCCESS",
    val receiptId: String
)

data class PlayerProfile(
    val uid: String,
    val ign: String,
    val email: String,
    val rankTier: String = "Grandmaster",
    val level: Int = 68,
    val coins: Int = 250, // 1 Rupee = 1 Coin (₹250.00 Real Balance)
    val diamonds: Int = 380, // Platform tokens
    val upiId: String = "soul.mortal@okaxis", // User's linked UPI ID for payouts
    val matchesPlayed: Int = 142,
    val booyahs: Int = 48,
    val kills: Int = 612,
    val kdRatio: Double = 4.31,
    val winRate: Double = 33.8,
    val headshotRate: Double = 46.2,
    val isAdmin: Boolean = false,
    val registeredTournamentIds: Set<String> = setOf("tourney_1", "tourney_3"),
    val bio: String = "Pro Rusher & IGL | Guild: 🔥TEAM SOUL🔥 | Playing competitive since Season 4",
    val matchHistory: List<MatchHistoryItem> = listOf(
        MatchHistoryItem("h1", "Bermuda Squad Championship", "Yesterday", 1, 9, 350, "Squad"),
        MatchHistoryItem("h2", "Purgatory Duo Cup", "3 days ago", 3, 5, 120, "Duo"),
        MatchHistoryItem("h3", "Solo Rampage Series", "Sep 15", 7, 6, 50, "Solo"),
        MatchHistoryItem("h4", "Clash Squad Elite War", "Sep 12", 1, 14, 200, "Clash Squad")
    ),
    val walletTransactions: List<TransactionRecord> = listOf(
        TransactionRecord(
            id = "tx_1",
            title = "Wallet Deposit (₹100 Pack)",
            amountCoins = 100,
            type = TransactionType.DEPOSIT,
            date = "Today, 4:30 PM",
            paymentMethod = "UPI (Google Pay)",
            status = "SUCCESS",
            receiptId = "TXN_UPI_8829104"
        ),
        TransactionRecord(
            id = "tx_2",
            title = "Entry Fee: Bermuda Grand Squad Cup",
            amountCoins = 0,
            type = TransactionType.ENTRY_FEE,
            date = "Today, 6:15 PM",
            paymentMethod = "Wallet Balance",
            status = "SUCCESS",
            receiptId = "TXN_ENT_992104"
        ),
        TransactionRecord(
            id = "tx_3",
            title = "Prize Reward: Purgatory Duo Cup",
            amountCoins = 150,
            type = TransactionType.PRIZE_REWARD,
            date = "Yesterday",
            paymentMethod = "Tournament Pool",
            status = "SUCCESS",
            receiptId = "TXN_PRZ_771029"
        ),
        TransactionRecord(
            id = "tx_4",
            title = "UPI Cashout (₹50)",
            amountCoins = 50,
            type = TransactionType.WITHDRAWAL,
            date = "2 days ago",
            paymentMethod = "UPI (soul.mortal@okaxis)",
            status = "PROCESSED",
            receiptId = "PO_UPI_661902"
        )
    )
)

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val tag: String,
    val booyahs: Int,
    val kills: Int,
    val points: Int,
    val winRate: String,
    val tier: String,
    val isGuild: Boolean = false,
    val badge: String = "",
    val matchesPlayed: Int = 0,
    val kdRatio: Double = 0.0,
    val userId: String = ""
)

data class RoomSlot(
    val slotNumber: Int,
    val teamName: String?,
    val leaderIgn: String?,
    val members: List<String> = emptyList(),
    val isUserSlot: Boolean = false
)

data class MatchAnnouncement(
    val id: String,
    val time: String,
    val author: String,
    val message: String,
    val isImportant: Boolean = false
)

data class MatchResultSubmission(
    val id: String,
    val tournamentId: String,
    val playerIgn: String,
    val playerUid: String,
    val reportedKills: Int,
    val claimedPlacement: Int,
    val proofNote: String,
    val status: String = "Under Review"
)
