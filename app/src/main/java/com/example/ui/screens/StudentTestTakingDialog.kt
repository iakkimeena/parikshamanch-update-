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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.StudentResult
import com.example.model.TestSeries
import com.example.ui.components.TimerDisplay
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoseDanger
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun StudentTestTakingDialog(
    testSeries: TestSeries,
    studentEmail: String,
    onClose: () -> Unit,
    onSubmit: (StudentResult) -> Unit
) {
    val questions = testSeries.questions
    if (questions.isEmpty()) {
        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = {
                Button(onClick = onClose) { Text("ठीक है (OK)") }
            },
            title = { Text("कोई प्रश्न उपलब्ध नहीं") },
            text = { Text("इस टेस्ट सीरीज में अभी प्रश्न जोड़े जा रहे हैं। कृपया बाद में प्रयास करें।") }
        )
        return
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var useEnglishText by remember { mutableStateOf(false) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> optionIndex

    var remainingSeconds by remember { mutableLongStateOf(testSeries.durationMinutes * 60L) }
    var isSubmitted by remember { mutableStateOf(false) }
    var finalResult by remember { mutableStateOf<StudentResult?>(null) }
    var showConfirmSubmit by remember { mutableStateOf(false) }

    // Countdown Timer Effect
    LaunchedEffect(isSubmitted) {
        while (!isSubmitted && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (!isSubmitted && remainingSeconds <= 0) {
            // Auto submit when time runs out
            isSubmitted = true
        }
    }

    fun calculateAndFinish() {
        var correctCount = 0
        var wrongCount = 0
        var unattemptedCount = 0

        questions.forEachIndexed { index, question ->
            val selectedOption = userAnswers[index]
            if (selectedOption == null) {
                unattemptedCount++
            } else if (selectedOption == question.correctOptionIndex) {
                correctCount++
            } else {
                wrongCount++
            }
        }

        val totalMarksEarned = (correctCount * 2.0) - (wrongCount * 0.66)
        val finalScore = maxOf(0.0, totalMarksEarned)
        val accuracy = if (correctCount + wrongCount > 0) {
            (correctCount.toDouble() / (correctCount + wrongCount).toDouble()) * 100.0
        } else 0.0

        val result = StudentResult(
            id = UUID.randomUUID().toString(),
            testSeriesId = testSeries.id,
            testTitle = testSeries.title,
            studentEmail = studentEmail,
            score = Math.round(finalScore * 100.0) / 100.0,
            maxScore = testSeries.totalMarks,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            accuracy = Math.round(accuracy * 10.0) / 10.0,
            timeSpentSeconds = (testSeries.durationMinutes * 60L) - remainingSeconds,
            timestamp = System.currentTimeMillis()
        )

        finalResult = result
        isSubmitted = true
        onSubmit(result)
    }

    Dialog(
        onDismissRequest = {
            if (!isSubmitted) showConfirmSubmit = true else onClose()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("test_taking_dialog"),
            color = NavyDark
        ) {
            if (finalResult != null) {
                // Test Result Breakdown Screen
                val res = finalResult!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "परीक्षा परिणाम (Test Result)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = testSeries.title,
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${res.score} / ${res.maxScore}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "कुल प्राप्तांक (Score)",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${res.correctCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                    Text(text = "सही (Correct)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${res.wrongCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RoseDanger)
                                    Text(text = "गलत (Wrong)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${res.unattemptedCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                    Text(text = "छोड़े (Skipped)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "सटीकता (Accuracy): ${res.accuracy}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("result_close_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("विद्यार्थी डैशबोर्ड पर लौटें", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Test CBT UI
                val currentQ = questions[currentQuestionIndex]
                val selectedOption = userAnswers[currentQuestionIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Top Bar with Timer and Language toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showConfirmSubmit = true }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Test",
                                tint = Color.White
                            )
                        }

                        TimerDisplay(remainingSeconds = remainingSeconds)

                        TextButton(
                            onClick = { useEnglishText = !useEnglishText },
                            modifier = Modifier.testTag("language_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (useEnglishText) "ENG" else "हिन्दी",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Question Palette row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(questions) { idx, _ ->
                            val isCurrent = idx == currentQuestionIndex
                            val isAnswered = userAnswers.containsKey(idx)

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> ElectricBlue
                                            isAnswered -> EmeraldSuccess
                                            else -> Color(0xFF334155)
                                        }
                                    )
                                    .clickable { currentQuestionIndex = idx }
                                    .testTag("question_palette_item_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question Card and Options (Scrollable)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "प्रश्न ${currentQuestionIndex + 1} / ${questions.size}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                    Text(
                                        text = "+2.0  /  -0.66",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val qText = if (useEnglishText && currentQ.textEnglish.isNotBlank()) {
                                    currentQ.textEnglish
                                } else {
                                    currentQ.textHindi
                                }

                                Text(
                                    text = qText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options List
                        val options = if (useEnglishText && currentQ.optionsEnglish.isNotEmpty()) {
                            currentQ.optionsEnglish
                        } else {
                            currentQ.optionsHindi
                        }

                        options.forEachIndexed { optIndex, optText ->
                            val isSelected = selectedOption == optIndex
                            val optionLabels = listOf("A", "B", "C", "D")
                            val label = optionLabels.getOrElse(optIndex) { "${optIndex + 1}" }

                            Surface(
                                onClick = { userAnswers[currentQuestionIndex] = optIndex },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ElectricBlue else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("option_${currentQuestionIndex}_$optIndex")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ElectricBlue else Color(0xFF334155)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = optText,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Navigation Buttons (Prev, Clear, Next/Submit)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (currentQuestionIndex > 0) currentQuestionIndex--
                            },
                            enabled = currentQuestionIndex > 0,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("पिछला")
                        }

                        if (selectedOption != null) {
                            TextButton(
                                onClick = { userAnswers.remove(currentQuestionIndex) },
                                modifier = Modifier.testTag("clear_response_button")
                            ) {
                                Text("उत्तर हटाएं", color = RoseDanger, fontSize = 12.sp)
                            }
                        }

                        if (currentQuestionIndex < questions.size - 1) {
                            Button(
                                onClick = { currentQuestionIndex++ },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Text("अगला")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                            }
                        } else {
                            Button(
                                onClick = { showConfirmSubmit = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                modifier = Modifier.testTag("test_submit_button")
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("सबमिट करें")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmSubmit) {
        AlertDialog(
            onDismissRequest = { showConfirmSubmit = false },
            title = { Text("टेस्ट सबमिट करें?") },
            text = {
                Text("आपने कुल ${questions.size} में से ${userAnswers.size} प्रश्नों के उत्तर दिए हैं। क्या आप टेस्ट पूरा करना चाहते हैं?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSubmit = false
                        calculateAndFinish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("हाँ, सबमिट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSubmit = false }) {
                    Text("जारी रखें")
                }
            }
        )
    }
}
