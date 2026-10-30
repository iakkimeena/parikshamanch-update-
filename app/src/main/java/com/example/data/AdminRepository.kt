package com.example.data

import com.example.model.Question
import com.example.model.TestSeries

object AdminRepository {
    fun getInitialQuestions(): List<Question> {
        return listOf(
            Question(
                id = "q1",
                textHindi = "राजस्थान का राज्य पशु कौन सा है?",
                textEnglish = "What is the state animal of Rajasthan?",
                optionsHindi = listOf("चिंकारा और ऊंट", "बाघ", "हाथी", "काला हिरण"),
                optionsEnglish = listOf("Chinkara & Camel", "Tiger", "Elephant", "Blackbuck"),
                correctOptionIndex = 0,
                explanationHindi = "राजस्थान का वन्यजीव श्रेणी में राज्य पशु चिंकारा है तथा पशुधन श्रेणी में ऊंट है।",
                explanationEnglish = "Chinkara is the state animal in wildlife category, and Camel is the domestic state animal.",
                subject = "Rajasthan GK",
                marks = 2.0,
                negativeMarks = 0.66
            ),
            Question(
                id = "q2",
                textHindi = "हवा महल का निर्माण किसने करवाया था?",
                textEnglish = "Who built the Hawa Mahal in Jaipur?",
                optionsHindi = listOf("सवाई प्रताप सिंह", "सवाई जय सिंह", "मान सिंह", "माधो सिंह"),
                optionsEnglish = listOf("Sawai Pratap Singh", "Sawai Jai Singh", "Man Singh", "Madho Singh"),
                correctOptionIndex = 0,
                explanationHindi = "हवा महल का निर्माण 1799 में सवाई प्रताप सिंह ने करवाया था, इसके वास्तुकार लालचंद उस्ता थे।",
                explanationEnglish = "Hawa Mahal was built in 1799 by Maharaja Sawai Pratap Singh and designed by Lal Chand Ustad.",
                subject = "Rajasthan History",
                marks = 2.0,
                negativeMarks = 0.66
            ),
            Question(
                id = "q3",
                textHindi = "राजस्थान दिवस कब मनाया जाता है?",
                textEnglish = "When is Rajasthan Day celebrated?",
                optionsHindi = listOf("30 मार्च", "1 नवंबर", "15 अगस्त", "26 जनवरी"),
                optionsEnglish = listOf("30 March", "1 November", "15 August", "26 January"),
                correctOptionIndex = 0,
                explanationHindi = "30 मार्च 1949 को वृहत् राजस्थान संघ का निर्माण हुआ था, इसलिए प्रतिवर्ष 30 मार्च को राजस्थान दिवस मनाया जाता है।",
                explanationEnglish = "On 30 March 1949, Greater Rajasthan was formed, hence 30 March is celebrated as Rajasthan Day.",
                subject = "Rajasthan GK",
                marks = 2.0,
                negativeMarks = 0.66
            )
        )
    }

    fun getInitialTestSeries(): List<TestSeries> {
        val qList = getInitialQuestions()
        return listOf(
            TestSeries(
                id = "ts1",
                title = "राजस्थान सामान्य ज्ञान - महा मॉक टेस्ट 01",
                description = "REET, RAS, CET व पुलिस कॉन्स्टेबल के लिए विशेष उपयोगी",
                category = "Rajasthan GK",
                targetExam = "REET / RAS / CET",
                durationMinutes = 60,
                totalMarks = 100.0,
                questions = qList,
                isPremium = false,
                price = 0.0,
                isPublished = true,
                attemptsCount = 124
            ),
            TestSeries(
                id = "ts2",
                title = "राजस्थान भूगोल एवं कला-संस्कृति स्पेशल",
                description = "विगत 10 वर्षों के महत्वपूर्ण प्रश्नों का संग्रह",
                category = "Art & Culture",
                targetExam = "CET 2026",
                durationMinutes = 45,
                totalMarks = 80.0,
                questions = qList,
                isPremium = true,
                price = 199.0,
                isPublished = true,
                attemptsCount = 89
            )
        )
    }
}
