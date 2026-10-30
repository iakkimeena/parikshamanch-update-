package com.example.data

import com.example.model.TestSeries

object DailyTestAutoUpdateManager {
    fun isTodayDailyTestAvailable(tests: List<TestSeries>): Boolean {
        return tests.any { it.isPublished }
    }
}
