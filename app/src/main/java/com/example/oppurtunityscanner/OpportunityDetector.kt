package com.example.oppurtunityscanner

import java.util.regex.Pattern

object OpportunityDetector {

    private val hiringKeywords = listOf(
        "hiring",
        "apply now",
        "job opening",
        "career opportunity",
        "open position",
        "we are looking for",
        "join our team",
        "recruiting",
        "internship opportunity",
        "fellowship program",
        "hackathon",
        "application link",
        "apply here",
        "registration link"
    )

    private val personalAnnouncements = listOf(
        "i'm happy to share",
        "i am happy to share",
        "i'm excited to announce",
        "i am excited to announce",
        "delighted to share",
        "thrilled to share",
        "started a new position",
        "started my internship",
        "completed my internship",
        "received an offer",
        "joined",
        "got an internship"
    )

    private val urlPattern = Pattern.compile(
        "https?://[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]+",
        Pattern.CASE_INSENSITIVE
    )

    fun isOpportunity(text: String): Boolean {
        val lowerText = text.lowercase()

        // 1. Filter out personal achievements/announcements
        // If it looks like a personal post, it's likely not an opportunity for others.
        val isPersonalPost = personalAnnouncements.any { lowerText.contains(it) }
        
        // 2. Look for explicit hiring/opportunity signals
        val hasHiringSignal = hiringKeywords.any { lowerText.contains(it) }
        
        // 3. Check for URLs (Opportunities almost always have a link to apply)
        val hasLink = urlPattern.matcher(text).find()

        // 4. Role mentions (Intern, Developer, etc.)
        val containsInternship = lowerText.contains("internship") || lowerText.contains("intern")
        
        // Logic:
        // - If it's a personal post, reject it unless it explicitly says "hiring" (rare).
        // - It's an opportunity if:
        //    a) It has a link AND (hiring signal OR contains "internship")
        //    b) It has a very strong hiring signal (e.g., "we are hiring", "job opening")
        
        if (isPersonalPost && !lowerText.contains("hiring for")) {
            return false
        }

        return (hasLink && (hasHiringSignal || containsInternship)) || 
               (hasHiringSignal && (containsInternship || lowerText.contains("role")))
    }
}
