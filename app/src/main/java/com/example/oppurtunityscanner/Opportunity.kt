package com.example.oppurtunityscanner

import org.json.JSONObject

enum class RiskLevel {
    LOW, VERIFY, HIGH
}

data class Opportunity(
    val company: String,
    val role: String,
    val deadline: String,
    val link: String,
    val sourceApp: String,
    val fullMessage: String = "",
    val riskLevel: RiskLevel = RiskLevel.VERIFY,
    val riskReasons: List<String> = emptyList()
) {
    companion object {
        fun fromJson(jsonString: String, sourceApp: String, originalText: String = ""): Opportunity {
            return try {
                val json = JSONObject(jsonString)
                val baseOpp = Opportunity(
                    company = json.optString("company", "Unknown"),
                    role = json.optString("role", "Opportunity"),
                    deadline = json.optString("deadline", "N/A"),
                    link = json.optString("link", ""),
                    sourceApp = sourceApp,
                    fullMessage = if (originalText.isNotEmpty()) originalText else json.optString("fullMessage", "")
                )
                // Risk assessment is performed and returned
                RiskAnalyzer.analyze(baseOpp)
            } catch (e: Exception) {
                Opportunity("Error", "Could not parse JSON", "N/A", "", sourceApp, originalText, RiskLevel.HIGH, listOf("Parsing error"))
            }
        }
    }
}
