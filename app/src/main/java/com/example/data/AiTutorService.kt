package com.example.data

object AiTutorService {
    fun getExplanation(topic: String): String {
        return "परीक्षा मंच AI ट्यूटर: $topic के महत्वपूर्ण बिंदु और परीक्षा उपयोगी तथ्य यहाँ उपलब्ध हैं।"
    }

    fun generateDoubtExplanation(question: String, subject: String = "सामान्य ज्ञान"): String {
        return "परीक्षा मंच AI ट्यूटर समाधान:\n\n" +
                "• विषय: $subject\n" +
                "• आपका प्रश्न: $question\n\n" +
                "• मुख्य बिंदु: प्रतियोगी परीक्षाओं में इस प्रकार के प्रश्नों में तथ्य और अवधारणा का स्पष्ट होना अनिवार्य है। संबंधित अध्याय के नोट्स और मॉक टेस्ट का अभ्यास नियमित रूप से करें।"
    }
}
