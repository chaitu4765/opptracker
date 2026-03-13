package com.example.oppurtunityscanner

import java.util.Locale

object RiskAnalyzer {

    private val bigCompanies = listOf(
        "Wipro", "Microsoft", "IBM", "Google", "Amazon", "Infosys", 
        "TCS", "Accenture", "Cisco", "Meta", "Apple", "Netflix", "Tesla"
    )

    private val urgencyPhrases = listOf(
        "Hurry up", "Limited seats", "Apply today", "Immediate shortlist", 
        "Don't miss", "Last chance", "Grab it"
    )

    private val interactionTriggers = listOf(
        "Reply YES", "Send code", "Pay fee", "Activate manually", 
        "Comment", "DM for info"
    )

    private val unofficialLinkPatterns = listOf(
        "forms.gle", "docs.google.com/forms", "t.me", "wa.me", "bit.ly", "tinyurl.com"
    )

    fun analyze(opportunity: Opportunity): Opportunity {
        val message = opportunity.fullMessage
        val lowerMessage = message.lowercase(Locale.ROOT)
        val reasons = mutableListOf<String>()

        // 1. Check for big company impersonation
        val mentionsBigCompany = bigCompanies.any { 
            lowerMessage.contains(it.lowercase(Locale.ROOT)) 
        }
        
        // 2. Unofficial Links
        if (unofficialLinkPatterns.any { lowerMessage.contains(it) }) {
            reasons.add("Uses unofficial application links (Google Forms, Telegram, or shorteners) instead of official career pages.")
        }

        // 3. Interaction Triggers / Fees
        if (interactionTriggers.any { lowerMessage.contains(it.lowercase(Locale.ROOT)) }) {
            reasons.add("Asks for suspicious interactions like replying 'YES' or mentions fees.")
        }

        // 4. Urgency
        if (urgencyPhrases.any { lowerMessage.contains(it.lowercase(Locale.ROOT)) }) {
            reasons.add("Uses high-pressure urgency phrases (e.g., 'Hurry up', 'Limited seats').")
        }

        // 5. Multiple Big Companies
        val count = bigCompanies.count { lowerMessage.contains(it.lowercase(Locale.ROOT)) }
        if (count > 2) {
            reasons.add("Promises certificates or association with multiple unrelated big companies in one post.")
        }

        // 6. Stipend without process
        if (lowerMessage.contains("stipend") && !lowerMessage.contains("interview") && !lowerMessage.contains("test")) {
            reasons.add("Mentions stipend without clear interview or selection process details.")
        }

        // 7. Referral Codes
        if (lowerMessage.contains("referral code") || lowerMessage.contains("use my code")) {
            reasons.add("Uses referral codes without official context.")
        }

        // 8. Marketing-heavy / Promotional Style
        if (lowerMessage.contains("msme") && lowerMessage.contains("mnc") && lowerMessage.contains("global")) {
            reasons.add("Claims MSME, MNC, and Global certificates together in a promotional style.")
        }

        // 9. Lack of Professional Language
        if (lowerMessage.contains("!!!!") || lowerMessage.contains("FREE") || lowerMessage.contains("EARN")) {
            // Very basic check for marketing-heavy text
            if (!lowerMessage.contains("official")) {
                reasons.add("Uses marketing-heavy or unprofessional wording.")
            }
        }

        // 10. Check for Official Domain in Link
        val hasOfficialDomain = mentionsBigCompany && bigCompanies.any { company ->
            opportunity.link.contains("${company.lowercase(Locale.ROOT)}.com") || 
            opportunity.link.contains("jobs.${company.lowercase(Locale.ROOT)}")
        }

        val riskLevel = when {
            reasons.size >= 2 -> RiskLevel.HIGH
            reasons.size == 1 -> RiskLevel.VERIFY
            hasOfficialDomain -> RiskLevel.LOW
            else -> RiskLevel.VERIFY
        }

        return opportunity.copy(
            riskLevel = riskLevel,
            riskReasons = reasons
        )
    }
}
