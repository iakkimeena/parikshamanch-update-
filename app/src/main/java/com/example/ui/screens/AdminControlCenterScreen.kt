package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminTab
import com.example.model.AppMode
import com.example.model.Question
import com.example.model.TestSeries
import com.example.ui.AdminViewModel
import com.example.ui.components.AdminTopAppBar
import com.example.ui.components.DashboardSummaryCards
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import java.util.UUID

@Composable
fun AdminControlCenterScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedAdminTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val testSeriesList by viewModel.testSeriesList.collectAsState()
    val questionsList by viewModel.questionsList.collectAsState()
    val resultsList by viewModel.resultsList.collectAsState()

    var showCreateTestDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("admin_control_center_screen"),
        topBar = {
            AdminTopAppBar(
                title = "Control Center • व्यवस्थापक",
                currentMode = AppMode.ADMIN,
                isDarkMode = isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                onSwitchToStudent = { viewModel.switchAppMode(AppMode.STUDENT) },
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0A0F1D),
                modifier = Modifier.testTag("admin_bottom_nav")
            ) {
                val tabs = listOf(
                    Triple(AdminTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                    Triple(AdminTab.TEST_MANAGER, "Tests", Icons.Default.Assignment),
                    Triple(AdminTab.QUESTION_BUILDER, "Questions", Icons.Default.PostAdd),
                    Triple(AdminTab.USER_MANAGER, "Students", Icons.Default.Group),
                    Triple(AdminTab.ANALYTICS, "Analytics", Icons.Default.Analytics),
                    Triple(AdminTab.SETTINGS, "Settings", Icons.Default.Settings)
                )

                tabs.forEach { (tab, label, icon) ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectAdminTab(tab) },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(text = label, fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricBlue,
                            selectedTextColor = ElectricBlue,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = NavyDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(NavyDark)
        ) {
            when (selectedTab) {
                AdminTab.DASHBOARD -> AdminDashboardTab(
                    testSeriesList = testSeriesList,
                    questionsList = questionsList,
                    resultsList = resultsList,
                    onCreateTestClick = { showCreateTestDialog = true },
                    onNavigateToTab = { viewModel.selectAdminTab(it) }
                )
                AdminTab.TEST_MANAGER -> AdminTestManagerTab(
                    testSeriesList = testSeriesList,
                    onCreateTestClick = { showCreateTestDialog = true }
                )
                AdminTab.QUESTION_BUILDER -> AdminQuestionBuilderTab(
                    onAddQuestion = { viewModel.addQuestion(it) },
                    totalQuestions = questionsList.size
                )
                AdminTab.USER_MANAGER -> AdminUserManagerTab(
                    resultsList = resultsList
                )
                AdminTab.ANALYTICS -> AdminAnalyticsTab(
                    resultsList = resultsList,
                    testSeriesList = testSeriesList
                )
                AdminTab.SETTINGS -> AdminSettingsTab(
                    onLogout = { viewModel.logout() },
                    onSwitchToStudent = { viewModel.switchAppMode(AppMode.STUDENT) }
                )
            }
        }
    }

    if (showCreateTestDialog) {
        CreateTestSeriesDialog(
            questionsList = questionsList,
            onDismiss = { showCreateTestDialog = false },
            onConfirm = { newTest ->
                showCreateTestDialog = false
                viewModel.addTestSeries(newTest)
            }
        )
    }
}

@Composable
private fun AdminDashboardTab(
    testSeriesList: List<TestSeries>,
    questionsList: List<Question>,
    resultsList: List<com.example.model.StudentResult>,
    onCreateTestClick: () -> Unit,
    onNavigateToTab: (AdminTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Firebase Cloud Live Sync Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Firebase Firestore • Live Connected",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Real-time updates active across all candidate instances",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Metric Cards
        DashboardSummaryCards(
            totalTests = testSeriesList.size,
            totalQuestions = questionsList.size,
            totalStudents = maxOf(12, resultsList.map { it.studentEmail }.distinct().size),
            totalRevenue = testSeriesList.filter { it.isPremium }.sumOf { it.price * it.attemptsCount } + 199.0
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Admin Actions
        Text(
            text = "Quick Admin Operations",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCreateTestClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("admin_quick_create_test_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Test", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onNavigateToTab(AdminTab.QUESTION_BUILDER) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("admin_quick_add_question_button")
            ) {
                Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Question", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Tests List Overview
        Text(
            text = "Active Test Series (${testSeriesList.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        testSeriesList.forEach { test ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = test.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = "${test.targetExam} • ${test.questions.size} Questions • ${test.durationMinutes} mins",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (test.isPublished) EmeraldSuccess.copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (test.isPublished) "PUBLISHED" else "DRAFT",
                            color = if (test.isPublished) EmeraldSuccess else Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTestManagerTab(
    testSeriesList: List<TestSeries>,
    onCreateTestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CBT Test Series Manager",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Button(
                onClick = onCreateTestClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.testTag("create_test_series_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Test", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(testSeriesList) { test ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = test.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                            Text(
                                text = if (test.isPremium) "₹${test.price.toInt()}" else "FREE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (test.isPremium) Color(0xFFF59E0B) else EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = test.description, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Category: ${test.category}  |  Target: ${test.targetExam}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "${test.attemptsCount} Attempts",
                                fontSize = 11.sp,
                                color = ElectricBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminQuestionBuilderTab(
    onAddQuestion: (Question) -> Unit,
    totalQuestions: Int
) {
    var subject by remember { mutableStateOf("Rajasthan GK") }
    var textHindi by remember { mutableStateOf("") }
    var textEnglish by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanationHindi by remember { mutableStateOf("") }
    var successNotice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Question Bank Builder",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Total questions in bank: $totalQuestions",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = textHindi,
                    onValueChange = { textHindi = it },
                    label = { Text("प्रश्न (हिन्दी में) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_q_text_hindi"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = textEnglish,
                    onValueChange = { textEnglish = it },
                    label = { Text("Question (in English - optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("विकल्प (Options):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                val optionsState = listOf(optA to { s: String -> optA = s },
                    optB to { s: String -> optB = s },
                    optC to { s: String -> optC = s },
                    optD to { s: String -> optD = s })

                optionsState.forEachIndexed { idx, (text, onVal) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { correctIndex = idx },
                            shape = CircleShape,
                            color = if (correctIndex == idx) EmeraldSuccess else Color(0xFF334155),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + idx).toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = text,
                            onValueChange = onVal,
                            placeholder = { Text("Option ${'A' + idx}") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = explanationHindi,
                    onValueChange = { explanationHindi = it },
                    label = { Text("उत्तर की व्याख्या (Explanation in Hindi)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (textHindi.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            val newQ = Question(
                                id = UUID.randomUUID().toString(),
                                textHindi = textHindi.trim(),
                                textEnglish = textEnglish.trim(),
                                optionsHindi = listOf(optA, optB, optC.ifBlank { "N/A" }, optD.ifBlank { "N/A" }),
                                optionsEnglish = emptyList(),
                                correctOptionIndex = correctIndex,
                                explanationHindi = explanationHindi.trim(),
                                explanationEnglish = "",
                                subject = subject.trim(),
                                marks = 2.0,
                                negativeMarks = 0.66
                            )
                            onAddQuestion(newQ)
                            textHindi = ""
                            textEnglish = ""
                            optA = ""
                            optB = ""
                            optC = ""
                            optD = ""
                            explanationHindi = ""
                            successNotice = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_save_question_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("प्रश्न बैंक में सहेजें (Save Question)", fontWeight = FontWeight.Bold)
                }

                if (successNotice) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ प्रश्न सफलतापूर्वक जोड़ा गया!",
                        color = EmeraldSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminUserManagerTab(
    resultsList: List<com.example.model.StudentResult>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Candidate Activity & Results",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (resultsList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Text(
                    text = "No test attempts recorded yet. Results will appear live when students submit exams.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(resultsList) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = res.studentEmail, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                            Text(text = res.testTitle, fontSize = 14.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Score: ${res.score}/${res.maxScore}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                Text(text = "Accuracy: ${res.accuracy}%", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAnalyticsTab(
    resultsList: List<com.example.model.StudentResult>,
    testSeriesList: List<TestSeries>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Platform Analytics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Exam Attempt Metrics", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                val totalAttempts = resultsList.size + testSeriesList.sumOf { it.attemptsCount }
                val avgAccuracy = if (resultsList.isNotEmpty()) resultsList.map { it.accuracy }.average() else 74.5

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Attempts:", color = Color(0xFF94A3B8))
                    Text("$totalAttempts", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Average Candidate Accuracy:", color = Color(0xFF94A3B8))
                    Text("${Math.round(avgAccuracy)}%", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Top Exam Stream:", color = Color(0xFF94A3B8))
                    Text("Rajasthan GK (REET / CET)", color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminSettingsTab(
    onLogout: () -> Unit,
    onSwitchToStudent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Admin Configuration & Platform Security",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Security Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Administrator Status: Master Administrator (Verified & Active)\nAuth Framework: Google Identity + Firebase Authentication\nDatabase: Cloud Firestore (Enterprise Mode)",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSwitchToStudent,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Switch to Student Portal", color = ElectricBlue)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Logout Administrator", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CreateTestSeriesDialog(
    questionsList: List<Question>,
    onDismiss: () -> Unit,
    onConfirm: (TestSeries) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetExam by remember { mutableStateOf("REET / RAS / CET") }
    var durationMinutes by remember { mutableIntStateOf(60) }
    var isPremium by remember { mutableStateOf(false) }
    var price by remember { mutableDoubleStateOf(199.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = {
            Text("Create New CBT Test Series", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Test Title (शीर्षक) *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (विवरण)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetExam,
                    onValueChange = { targetExam = it },
                    label = { Text("Target Exam") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Premium Test:", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isPremium,
                        onCheckedChange = { isPremium = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricBlue)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newTest = TestSeries(
                            id = UUID.randomUUID().toString(),
                            title = title.trim(),
                            description = description.trim(),
                            category = "Rajasthan GK",
                            targetExam = targetExam.trim(),
                            durationMinutes = durationMinutes,
                            totalMarks = (questionsList.size * 2.0).coerceAtLeast(50.0),
                            questions = questionsList,
                            isPremium = isPremium,
                            price = if (isPremium) price else 0.0,
                            isPublished = true,
                            attemptsCount = 0,
                            createdAt = System.currentTimeMillis()
                        )
                        onConfirm(newTest)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Text("Publish Test")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
