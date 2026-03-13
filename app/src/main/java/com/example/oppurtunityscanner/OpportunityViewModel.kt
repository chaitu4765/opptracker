package com.example.oppurtunityscanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OpportunityViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).opportunityDao()
    private val analyzer = LocalSLMAnalyzer(application)

    val opportunities: StateFlow<List<OpportunityEntity>> = dao.getAllOpportunities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var testCount = 0

    fun testOpportunityDetection() {
        viewModelScope.launch(Dispatchers.IO) {
            val testMessages = listOf(
                "Google Summer Internship applications open. Apply here: https://careers.google.com/jobs/results/12345 Deadline April 5.",
                "URGENT: Wipro is hiring for multiple roles. Reply YES to get code. Limited seats! Join Telegram: https://t.me/fake_wipro",
                "Amazon is hiring Software Engineer Interns for Summer 2026. Apply before March 20 at https://amazon.jobs/en/jobs/999",
                "Hurry up! Earn 50k stipend at IBM, Microsoft and Google. No interview required. Pay 500 registration fee. Apply: https://bit.ly/scam-link"
            )
            
            val testMessage = testMessages[testCount % testMessages.size]
            testCount++

            // 1. Analyze with SLM (which uses the improved Parser and RiskAnalyzer)
            val jsonResponse = analyzer.analyzeNotification(testMessage)
            
            // 2. Parse JSON to Opportunity (this triggers RiskAnalyzer)
            val opportunity = Opportunity.fromJson(jsonResponse, "com.example.test", testMessage)
            
            // 3. Convert to Entity and Insert
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
            
            dao.insertOpportunity(entity)
        }
    }

    suspend fun getOpportunityById(id: Int): OpportunityEntity? {
        return dao.getOpportunityById(id)
    }
}
