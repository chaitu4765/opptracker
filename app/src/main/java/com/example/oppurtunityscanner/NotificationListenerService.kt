package com.example.oppurtunityscanner

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var slmAnalyzer: LocalSLMAnalyzer

    override fun onCreate() {
        super.onCreate()
        slmAnalyzer = LocalSLMAnalyzer(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName ?: return
        val extras = sbn.notification?.extras ?: return
        
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        // Filter for specific apps
        val targetPackages = listOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.google.android.gm",
            "com.linkedin.android"
        )

        if (packageName !in targetPackages) return

        // Combine title and text into one message
        val fullMessage = "$title $text"

        // Step 1: Check if it's an opportunity using keywords
        if (OpportunityDetector.isOpportunity(fullMessage)) {
            Log.d("NotificationListener", "Opportunity detected from $packageName. Analyzing...")
            
            // Step 2: Use AI to analyze the notification and get JSON output
            // The AI/Parser logic now includes Risk Analysis
            val aiJsonResponse = slmAnalyzer.analyzeNotification(fullMessage)
            
            // Step 3: Convert JSON string to Opportunity object (which includes Risk)
            val opportunity = Opportunity.fromJson(aiJsonResponse, packageName, fullMessage)
            
            Log.d("NotificationListener", "Processed Opportunity: $opportunity")
            
            // Step 4: Store in the database
            storeOpportunity(opportunity)
        }
    }

    private fun storeOpportunity(opportunity: Opportunity) {
        val entity = OpportunityEntity(
            company = opportunity.company,
            role = opportunity.role,
            deadline = opportunity.deadline,
            link = opportunity.link,
            sourceApp = opportunity.sourceApp,
            fullMessage = opportunity.fullMessage,
            riskLevel = opportunity.riskLevel.name,
            riskReasons = opportunity.riskReasons.joinToString("|")
        )

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.opportunityDao()

        serviceScope.launch {
            try {
                dao.insertOpportunity(entity)
                Log.d("NotificationListener", "Stored ${opportunity.company} with Risk: ${opportunity.riskLevel}")
            } catch (e: Exception) {
                Log.e("NotificationListener", "Failed to store: ${e.message}")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
