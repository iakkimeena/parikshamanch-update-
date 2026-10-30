package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdminRepository
import com.example.data.FirebaseAuthService
import com.example.data.FirestoreService
import com.example.model.AdminTab
import com.example.model.AppMode
import com.example.model.AuthRole
import com.example.model.Question
import com.example.model.StudentResult
import com.example.model.StudentTab
import com.example.model.TestSeries
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    companion object {
        const val PRIMARY_ADMIN_EMAIL = "youngindianews2@gmail.com"
    }

    private val firestoreService = FirestoreService()

    private val _appMode = MutableStateFlow(AppMode.AUTH)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserRole = MutableStateFlow(AuthRole.STUDENT)
    val currentUserRole: StateFlow<AuthRole> = _currentUserRole.asStateFlow()

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedAdminTab = MutableStateFlow(AdminTab.DASHBOARD)
    val selectedAdminTab: StateFlow<AdminTab> = _selectedAdminTab.asStateFlow()

    private val _selectedStudentTab = MutableStateFlow(StudentTab.HOME)
    val selectedStudentTab: StateFlow<StudentTab> = _selectedStudentTab.asStateFlow()

    private val _testSeriesList = MutableStateFlow(AdminRepository.getInitialTestSeries())
    val testSeriesList: StateFlow<List<TestSeries>> = _testSeriesList.asStateFlow()

    private val _questionsList = MutableStateFlow(AdminRepository.getInitialQuestions())
    val questionsList: StateFlow<List<Question>> = _questionsList.asStateFlow()

    private val _resultsList = MutableStateFlow<List<StudentResult>>(emptyList())
    val resultsList: StateFlow<List<StudentResult>> = _resultsList.asStateFlow()

    private val _activeTest = MutableStateFlow<TestSeries?>(null)
    val activeTest: StateFlow<TestSeries?> = _activeTest.asStateFlow()

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    private val _adminMasterPassword = MutableStateFlow("admin123")
    val adminMasterPassword: StateFlow<String> = _adminMasterPassword.asStateFlow()

    fun updateAdminPassword(newPassword: String) {
        _adminMasterPassword.value = newPassword
        _userFeedback.value = "एडमिन पासवर्ड सफलतापूर्वक अपडेट किया गया।"
    }

    fun openAdminLogin() {
        _appMode.value = AppMode.ADMIN_AUTH
    }

    fun returnToStudentPortal() {
        _appMode.value = if (_currentUserRole.value == AuthRole.STUDENT && _currentUserEmail.value.isNotBlank()) {
            AppMode.STUDENT
        } else {
            AppMode.AUTH
        }
    }

    fun switchAppMode(mode: AppMode) {
        if (mode == AppMode.ADMIN) {
            val clean = _currentUserEmail.value.trim().lowercase()
            if (clean != PRIMARY_ADMIN_EMAIL.lowercase()) {
                _userFeedback.value = "Admin authentication required. Opening Admin Login."
                _appMode.value = AppMode.ADMIN_AUTH
                return
            }
        }
        _appMode.value = mode
    }

    fun login(email: String, name: String) {
        _currentUserEmail.value = email
        _currentUserName.value = name
        val role = FirebaseAuthService.determineRole(email)
        _currentUserRole.value = role

        if (role == AuthRole.ADMIN) {
            _appMode.value = AppMode.ADMIN
        } else {
            _appMode.value = AppMode.STUDENT
        }
    }

    fun loginWithFirebaseUser(user: com.google.firebase.auth.FirebaseUser) {
        val email = user.email ?: "student@parikshamanch.in"
        val name = user.displayName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@")
        _currentUserEmail.value = email
        _currentUserName.value = name
        val role = FirebaseAuthService.determineRole(email)
        _currentUserRole.value = role

        if (role == AuthRole.ADMIN) {
            _appMode.value = AppMode.ADMIN
        } else {
            _appMode.value = AppMode.STUDENT
        }
    }

    fun loginAsGuest() {
        _currentUserEmail.value = "student.guest@parikshamanch.in"
        _currentUserName.value = "परीक्षार्थी (Guest)"
        _currentUserRole.value = AuthRole.STUDENT
        _appMode.value = AppMode.STUDENT
    }

    fun logout() {
        FirebaseAuthService.signOut()
        _currentUserEmail.value = ""
        _currentUserName.value = ""
        _currentUserRole.value = AuthRole.STUDENT
        _appMode.value = AppMode.AUTH
    }

    fun selectAdminTab(tab: AdminTab) {
        _selectedAdminTab.value = tab
    }

    fun selectStudentTab(tab: StudentTab) {
        _selectedStudentTab.value = tab
    }

    fun toggleDarkMode(enable: Boolean) {
        _isDarkMode.value = enable
    }

    fun buyFullSubscription() {
        _isSubscribed.value = true
        _userFeedback.value = "प्रीमियम टेस्ट पास सक्रिय हुआ!"
    }

    fun startTest(testSeries: TestSeries) {
        _activeTest.value = testSeries
    }

    fun closeActiveTest() {
        _activeTest.value = null
    }

    fun submitTest(result: StudentResult) {
        _resultsList.value = listOf(result) + _resultsList.value
        _activeTest.value = null
        _userFeedback.value = "टेस्ट सफलतापूर्वक सबमिट हुआ! आपका स्कोर: ${result.score}/${result.maxScore}"
        viewModelScope.launch {
            firestoreService.saveStudentResult(result)
        }
    }

    fun addQuestion(question: Question) {
        _questionsList.value = _questionsList.value + question
        _userFeedback.value = "प्रश्न सफलतापूर्वक जोड़ा गया।"
    }

    fun addTestSeries(testSeries: TestSeries) {
        _testSeriesList.value = _testSeriesList.value + testSeries
        _userFeedback.value = "टेस्ट सीरीज प्रकाशित हुई।"
        viewModelScope.launch {
            firestoreService.saveTestSeries(testSeries)
        }
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }
}
