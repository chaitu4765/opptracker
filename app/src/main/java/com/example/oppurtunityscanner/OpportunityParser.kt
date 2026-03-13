package com.example.oppurtunityscanner

import java.util.regex.Pattern

object OpportunityParser {

    private val urlPattern = Pattern.compile(
        "(https?://[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]+)",
        Pattern.CASE_INSENSITIVE
    )

    private val roleKeywords = listOf(
        "Software Engineer Intern",
        "Data Scientist Intern",
        "ML Intern",
        "Hackathon Participant",
        "Backend Developer",
        "Frontend Developer",
        "AI Engineer",
        "Web Developer",
        "Software Engineer",
        "Data Scientist",
        "Product Manager",
        "Designer",
        "Analyst"
    )

    private val rolePatterns = listOf(
        Pattern.compile("hiring (?:for )?(.+?)(?: at| in| for|$)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(.+?) (?:role|position|opening)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Opportunity:? (.+?)(?: at|$)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("as (?:a |an )?(.+?)(?: at| in| for|$)", Pattern.CASE_INSENSITIVE)
    )

    private val companyPatterns = listOf(
        Pattern.compile("at ([A-Z][\\w\\s]+)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^([A-Z][\\w\\s]+) is hiring", Pattern.CASE_INSENSITIVE),
        Pattern.compile("join ([A-Z][\\w\\s]+)", Pattern.CASE_INSENSITIVE)
    )

    private val deadlinePatterns = listOf(
        Pattern.compile("(?:deadline|last date|apply by|ends on):?\\s*(.+?)(?:\\.|\\n|$)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("before\\s+(.+?)(?:\\.|\\n|$)", Pattern.CASE_INSENSITIVE)
    )

    private val knownCompanies = listOf(
        "Wipro", "Microsoft", "IBM", "Google", "Amazon", "Infosys", 
        "TCS", "Accenture", "Cisco", "Meta", "Apple", "Netflix", "Tesla"
    )

    fun parse(title: String, text: String, sourceApp: String): Opportunity {
        val fullContent = "$title $text".trim()

        val link = extractLink(fullContent)
        val deadline = extractDeadline(fullContent)
        
        var company = detectKnownCompany(fullContent)
        if (company.isEmpty()) company = extractCompany(title)
        if (company.isEmpty()) company = extractCompany(text)
        if (company.isEmpty()) company = "Unknown Company"

        var role = detectKnownRole(fullContent)
        if (role.isEmpty()) role = extractRole(title)
        if (role.isEmpty()) role = extractRole(text)
        if (role.isEmpty()) role = "Opportunity"

        val baseOpp = Opportunity(
            company = company.trim(),
            role = role.trim(),
            deadline = deadline ?: "N/A",
            link = link ?: "",
            sourceApp = sourceApp,
            fullMessage = text // Storing original text
        )

        return RiskAnalyzer.analyze(baseOpp)
    }

    private fun detectKnownCompany(text: String): String {
        for (company in knownCompanies) {
            if (text.contains(company, ignoreCase = true)) {
                return company
            }
        }
        return ""
    }

    private fun detectKnownRole(text: String): String {
        for (role in roleKeywords) {
            if (text.contains(role, ignoreCase = true)) {
                return role
            }
        }
        return ""
    }

    private fun extractLink(text: String): String? {
        val matcher = urlPattern.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun extractDeadline(text: String): String? {
        for (pattern in deadlinePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val found = matcher.group(1)?.trim()
                if (!found.isNullOrEmpty()) return found
            }
        }
        return null
    }

    private fun extractCompany(text: String): String {
        for (pattern in companyPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1)?.trim() ?: ""
        }
        return ""
    }

    private fun extractRole(text: String): String {
        for (pattern in rolePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) return matcher.group(1)?.trim() ?: ""
        }
        return ""
    }
}
