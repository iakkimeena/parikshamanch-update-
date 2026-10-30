package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AiTutorService
import com.example.model.StudentTab
import com.example.model.TestSeries
import com.example.ui.AdminViewModel
import com.example.ui.components.PaymentCheckoutDialog
import com.example.ui.components.StudentMetricsDashboard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoseDanger
import com.example.util.AdMobBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPanelScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedStudentTab.collectAsState()
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val testSeriesList by viewModel.testSeriesList.collectAsState()
    val resultsList by viewModel.resultsList.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val activeTest by viewModel.activeTest.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userFeedback by viewModel.userFeedback.collectAsState()

    var showPaymentDialogFor by remember { mutableStateOf<TestSeries?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var aiQuestionInput by remember { mutableStateOf("") }
    var aiAnswerOutput by remember { mutableStateOf<String?>(null) }
    var isAiLoading by remember { mutableStateOf(false) }

    BackHandler {
        if (selectedTab != StudentTab.HOME) {
            viewModel.selectStudentTab(StudentTab.HOME)
        } else {
            viewModel.logout()
        }
    }

    // Active CBT test screen
    activeTest?.let { currentTest ->
        StudentTestTakingDialog(
            testSeries = currentTest,
            studentEmail = currentUserEmail.ifBlank { "student@parikshamanch.in" },
            onClose = { viewModel.closeActiveTest() },
            onSubmit = { result ->
                viewModel.submitTest(result)
            }
        )
    }

    // Payment Checkout Dialog for Premium Tests
    showPaymentDialogFor?.let { test ->
        PaymentCheckoutDialog(
            onDismiss = { showPaymentDialogFor = null },
            onConfirm = {
                viewModel.buyFullSubscription()
                showPaymentDialogFor = null
            }
        )
    }

    Scaffold(
        modifier = modifier.testTag("student_panel_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "परीक्षा मंच",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ElectricBlue.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                            ) {
                                Text(
                                    text = "CBT 2026",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (currentUserName.isNotBlank()) "नमस्ते, $currentUserName" else "अभ्यर्थी पोर्टल",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                actions = {
                    // Quick Switch to Admin Portal
                    IconButton(
                        onClick = { viewModel.openAdminLogin() },
                        modifier = Modifier.testTag("switch_to_admin_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFF3B82F6)) {
                                    Text("PRO", fontSize = 8.sp, color = Color.White)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Portal",
                                tint = Color(0xFF60A5FA)
                            )
                        }
                    }

                    // Dark mode toggle
                    IconButton(onClick = { viewModel.toggleDarkMode(!isDarkMode) }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = Color(0xFFE2E8F0)
                        )
                    }

                    // Logout
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("student_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
            ) {
                // Banner ad in student interface
                AdMobBanner(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )

                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    modifier = Modifier.testTag("student_bottom_nav")
                ) {
                val navItems = listOf(
                    Triple(StudentTab.HOME, "होम", Icons.Default.Home),
                    Triple(StudentTab.TEST_SERIES, "टेस्ट सीरीज", Icons.Default.Quiz),
                    Triple(StudentTab.MY_PERFORMANCE, "रिपोर्ट", Icons.Default.TrendingUp),
                    Triple(StudentTab.STUDENT_TOOLS, "AI टूल्स", Icons.Default.AutoAwesome),
                    Triple(StudentTab.PROFILE, "प्रोफ़ाइल", Icons.Default.Person)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectStudentTab(tab) },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(text = label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricBlue,
                            selectedTextColor = ElectricBlue,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("student_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(if (isDarkMode) Color(0xFF090D16) else NavyDark)
        ) {
            when (selectedTab) {
                StudentTab.HOME -> {
                    StudentHomeTab(
                        currentUserName = currentUserName,
                        currentUserEmail = currentUserEmail,
                        testSeriesList = testSeriesList,
                        resultsList = resultsList,
                        isSubscribed = isSubscribed,
                        onStartTest = { test ->
                            if (test.isPremium && !isSubscribed) {
                                showPaymentDialogFor = test
                            } else {
                                viewModel.startTest(test)
                            }
                        },
                        onNavigateToTests = { viewModel.selectStudentTab(StudentTab.TEST_SERIES) },
                        onNavigateToAdmin = { viewModel.openAdminLogin() }
                    )
                }
                StudentTab.TEST_SERIES -> {
                    StudentTestsTab(
                        testSeriesList = testSeriesList,
                        isSubscribed = isSubscribed,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onStartTest = { test ->
                            if (test.isPremium && !isSubscribed) {
                                showPaymentDialogFor = test
                            } else {
                                viewModel.startTest(test)
                            }
                        }
                    )
                }
                StudentTab.MY_PERFORMANCE -> {
                    StudentPerformanceTab(
                        resultsList = resultsList,
                        onRetakeTest = { viewModel.selectStudentTab(StudentTab.TEST_SERIES) }
                    )
                }
                StudentTab.STUDENT_TOOLS -> {
                    StudentToolsTab(
                        questionInput = aiQuestionInput,
                        onQuestionChange = { aiQuestionInput = it },
                        aiAnswer = aiAnswerOutput,
                        isLoading = isAiLoading,
                        onAskAi = {
                            if (aiQuestionInput.isNotBlank()) {
                                isAiLoading = true
                                val q = aiQuestionInput
                                kotlinx.coroutines.GlobalScope.let {
                                    val answer = AiTutorService.generateDoubtExplanation(q, "Rajasthan GK")
                                    aiAnswerOutput = answer
                                    isAiLoading = false
                                }
                            }
                        }
                    )
                }
                StudentTab.PROFILE -> {
                    StudentProfileTab(
                        name = currentUserName,
                        email = currentUserEmail,
                        isSubscribed = isSubscribed,
                        resultsCount = resultsList.size,
                        onUpgradeClick = { viewModel.buyFullSubscription() },
                        onSwitchToAdmin = { viewModel.openAdminLogin() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentHomeTab(
    currentUserName: String,
    currentUserEmail: String,
    testSeriesList: List<TestSeries>,
    resultsList: List<com.example.model.StudentResult>,
    isSubscribed: Boolean,
    onStartTest: (TestSeries) -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Rajasthan CBT Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(ElectricBlue, Color(0xFF3B82F6))),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = "🎯 REET / RAS / CET 2026",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (isSubscribed) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                            ) {
                                Text(
                                    text = "★ PREMIUM PASS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "राजस्थान राज्य स्तरीय ऑनलाइन CBT मॉक टेस्ट",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "नवीनतम परीक्षा पैटर्न, द्विभाषी प्रश्न (हिंदी/English), नेगेटिव मार्किंग और विस्तृत व्याख्या सहित।",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onNavigateToTests,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier.testTag("home_explore_tests_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("टेस्ट सीरीज देखें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToAdmin,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF60A5FA)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("home_admin_portal_button")
                        ) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("व्यवस्थापक लॉगिन", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Performance Overview Summary Cards
        val totalAttempted = resultsList.size
        val avgScore = if (resultsList.isNotEmpty()) resultsList.map { it.score }.average() else 0.0
        val totalCorrect = resultsList.sumOf { it.correctCount }
        val totalWrong = resultsList.sumOf { it.wrongCount }

        StudentMetricsDashboard(
            totalAttempted = totalAttempted,
            averageScore = avgScore,
            totalCorrect = totalCorrect,
            totalWrong = totalWrong
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Popular Test Series Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "उपलब्ध टेस्ट सीरीज (Featured Tests)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = onNavigateToTests) {
                Text("सभी देखें (${testSeriesList.size})", fontSize = 12.sp, color = ElectricBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (testSeriesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "वर्तमान में कोई टेस्ट उपलब्ध नहीं है। व्यवस्थापक जल्द ही जोड़ेंगे।",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            testSeriesList.take(3).forEach { test ->
                TestSeriesCard(
                    test = test,
                    isSubscribed = isSubscribed,
                    onStart = { onStartTest(test) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StudentTestsTab(
    testSeriesList: List<TestSeries>,
    isSubscribed: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onStartTest: (TestSeries) -> Unit
) {
    val filteredList = testSeriesList.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.targetExam.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("टेस्ट या परीक्षा खोजें (Search Tests)...", color = Color(0xFF64748B), fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tests_search_field"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "कुल टेस्ट: ${filteredList.size}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF94A3B8)
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF1E293B)
            ) {
                Text(
                    text = "Bilingual (हिन्दी/Eng)",
                    fontSize = 10.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { test ->
                TestSeriesCard(
                    test = test,
                    isSubscribed = isSubscribed,
                    onStart = { onStartTest(test) }
                )
            }
        }
    }
}

@Composable
private fun TestSeriesCard(
    test: TestSeries,
    isSubscribed: Boolean,
    onStart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .testTag("test_card_${test.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = test.targetExam,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = test.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (test.isPremium && !isSubscribed) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF78350F).copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("₹${test.price.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A))
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text(
                            text = "निःशुल्क (Free)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA7F3D0),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = test.description.ifBlank { "राजस्थान प्रतियोगी परीक्षाओं के लिए विशेष प्रश्नोत्तरी।" },
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${test.questions.size} प्रश्न", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${test.durationMinutes} मिनट", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${test.totalMarks.toInt()} अंक", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }
                }

                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (test.isPremium && !isSubscribed) Color(0xFFD97706) else ElectricBlue
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (test.isPremium && !isSubscribed) "Unlock" else "Start CBT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentPerformanceTab(
    resultsList: List<com.example.model.StudentResult>,
    onRetakeTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "आपकी परीक्षा प्रगति व रिपोर्ट कार्ड",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "विगत टेस्टों में अर्जित अंक और एक्यूरेसी",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (resultsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "अभी तक कोई टेस्ट नहीं दिया गया है।",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "अपनी तैयारी परखने के लिए पहला टेस्ट शुरू करें!",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetakeTest, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                        Text("टेस्ट सीरीज खोलें")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(resultsList) { res ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = res.testTitle.ifBlank { "Rajasthan CBT Mock Test" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Score: ${res.score}/${res.maxScore.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.score >= res.maxScore * 0.5) EmeraldSuccess else RoseDanger
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("सही: ${res.correctCount}", fontSize = 11.sp, color = EmeraldSuccess)
                                Text("गलत: ${res.wrongCount}", fontSize = 11.sp, color = RoseDanger)
                                Text("छोड़े गए: ${res.unattemptedCount}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text("Accuracy: ${res.accuracy.toInt()}%", fontSize = 11.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentToolsTab(
    questionInput: String,
    onQuestionChange: (String) -> Unit,
    aiAnswer: String?,
    isLoading: Boolean,
    onAskAi: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI संशय निवारक (AI Doubt Solver)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "राजस्थान GK, इतिहास, भूगोल या परीक्षा से जुड़ा कोई भी प्रश्न पूछें:",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = questionInput,
                    onValueChange = onQuestionChange,
                    placeholder = { Text("उदा. राजस्थान के एकीकरण के कितने चरण थे?", color = Color(0xFF64748B), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF090D16),
                        unfocusedContainerColor = Color(0xFF090D16),
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAskAi,
                    enabled = !isLoading && questionInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("उत्तर तैयार हो रहा है...")
                    } else {
                        Text("समाधान प्राप्त करें (Get Solution)")
                    }
                }

                aiAnswer?.let { ans ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💡 स्पष्टीकरण (Explanation):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = ans, fontSize = 12.sp, color = Color.White, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Rajasthan GK Key Revision Cards
        Text("त्वरित रिवीज़न नोट्स (Quick GK Capsule)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        val gkPoints = listOf(
            "राजस्थान का राज्य दिवस 30 मार्च को मनाया जाता है।",
            "कालीबंगा सभ्यता हनुमानगढ़ जिले में घग्घर नदी के किनारे स्थित है।",
            "महाराणा प्रताप का राज्याभिषेक गोगुन्दा में 1572 ई. में हुआ था।",
            "थार का मरुस्थल भारत के कुल क्षेत्रफल का लगभग 61.11% भूभाग घेरता है।"
        )

        gkPoints.forEach { pt ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = pt, fontSize = 11.sp, color = Color(0xFFE2E8F0))
                }
            }
        }
    }
}

@Composable
private fun StudentProfileTab(
    name: String,
    email: String,
    isSubscribed: Boolean,
    resultsCount: Int,
    onUpgradeClick: () -> Unit,
    onSwitchToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF38BDF8)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name.ifBlank { "परीक्षार्थी" },
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = email.ifBlank { "student@parikshamanch.in" },
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("प्रीमियम टेस्ट पास", fontSize = 13.sp, color = Color(0xFF94A3B8))
                    Text(
                        text = if (isSubscribed) "सक्रिय (Active)" else "निष्क्रिय (Inactive)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSubscribed) EmeraldSuccess else Color(0xFFFBBF24)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("कुल दिए गए टेस्ट", fontSize = 13.sp, color = Color(0xFF94A3B8))
                    Text("$resultsCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (!isSubscribed) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("₹199 में सम्पूर्ण टेस्ट सीरीज अनलॉक करें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Switch to Admin Portal Button
        Surface(
            onClick = onSwitchToAdmin,
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_admin_login_button")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("व्यवस्थापक कंट्रोल सेंटर (Admin Control Center)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("सुरक्षित एडमिन लॉगिन हेतु यहां क्लिक करें", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseDanger),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoseDanger.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("लॉगआउट करें (Sign Out)")
        }
    }
}
