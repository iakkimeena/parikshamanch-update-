package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String = "",
    val textHindi: String = "",
    val textEnglish: String = "",
    val optionsHindi: List<String> = emptyList(),
    val optionsEnglish: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val explanationHindi: String = "",
    val explanationEnglish: String = "",
    val subject: String = "Rajasthan GK",
    val marks: Double = 2.0,
    val negativeMarks: Double = 0.66
)

@Serializable
data class TestSeries(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "Rajasthan GK",
    val targetExam: String = "REET / RAS / CET",
    val durationMinutes: Int = 60,
    val totalMarks: Double = 100.0,
    val questions: List<Question> = emptyList(),
    val isPremium: Boolean = false,
    val price: Double = 0.0,
    val isPublished: Boolean = true,
    val attemptsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class StudentResult(
    val id: String = "",
    val testSeriesId: String = "",
    val testTitle: String = "",
    val studentEmail: String = "",
    val score: Double = 0.0,
    val maxScore: Double = 100.0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unattemptedCount: Int = 0,
    val accuracy: Double = 0.0,
    val timeSpentSeconds: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class UserProfile(
    val email: String = "",
    val displayName: String = "",
    val role: AuthRole = AuthRole.STUDENT,
    val isSubscribed: Boolean = false,
    val subscriptionExpiry: Long = 0L,
    val testsAttempted: Int = 0
)

enum class AuthRole(val displayName: String) {
    STUDENT("Student / परीक्षार्थी"),
    ADMIN("Administrator / व्यवस्थापक")
}

enum class AppMode(val displayName: String) {
    AUTH("Login & Sign In"),
    ADMIN_AUTH("Admin Control Center Login"),
    STUDENT("Student Panel"),
    ADMIN("Admin Console")
}

enum class AdminTab(val title: String) {
    DASHBOARD("Dashboard"),
    TEST_MANAGER("Tests & Exams"),
    QUESTION_BUILDER("Questions"),
    USER_MANAGER("Students"),
    ANALYTICS("Analytics"),
    SETTINGS("Settings")
}

enum class StudentTab(val title: String) {
    HOME("Home"),
    TEST_SERIES("Test Series"),
    MY_PERFORMANCE("Report"),
    STUDENT_TOOLS("Tools & AI"),
    PROFILE("Profile")
}
