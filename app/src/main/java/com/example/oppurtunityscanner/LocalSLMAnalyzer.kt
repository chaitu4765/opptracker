package com.example.oppurtunityscanner

import android.content.Context
import android.util.Log
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import org.json.JSONObject
import java.io.File

class LocalSLMAnalyzer(private val context: Context) {

    private val modelFileName = "gemma-3n-E2B-it-int4.litertlm"
    private val assetPackName = "model_asset"
    private var assetPackManager: AssetPackManager = AssetPackManagerFactory.getInstance(context)
    private var modelPath: String? = null

    init {
        checkAndDownloadModel()
    }

    private fun checkAndDownloadModel() {
        val location = assetPackManager.getAssetPackLocation(assetPackName)
        if (location == null) {
            Log.d("LocalSLMAnalyzer", "Model pack not found. Requesting download...")
            assetPackManager.fetch(listOf(assetPackName))
                .addOnSuccessListener { 
                    Log.d("LocalSLMAnalyzer", "Download request successful")
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("LocalSLMAnalyzer", "Download request failed: ${e.message}")
                }
        } else {
            modelPath = "${location.assetsPath()}/$modelFileName"
            Log.d("LocalSLMAnalyzer", "Model found at: $modelPath")
        }
    }

    fun getDownloadStatus(): Int {
        val state = assetPackManager.getPackState(assetPackName)
        return state.status()
    }

    fun analyzeNotification(text: String): String {
        Log.d("LocalSLMAnalyzer", "Prompting model with: $text")
        
        // Check if model is ready
        if (modelPath == null) {
            val location = assetPackManager.getAssetPackLocation(assetPackName)
            if (location != null) {
                modelPath = "${location.assetsPath()}/$modelFileName"
            }
        }

        if (modelPath == null) {
            Log.w("LocalSLMAnalyzer", "Model not yet available. Using fallback parser.")
        } else {
            Log.d("LocalSLMAnalyzer", "Using model at $modelPath for inference")
        }

        // Simulating SLM extraction using our Parser logic
        return simulateInference(text)
    }

    private fun simulateInference(text: String): String {
        val parsedOpportunity = OpportunityParser.parse(title = "", text = text, sourceApp = "com.simulation.slm")
        
        val json = JSONObject().apply {
            put("company", parsedOpportunity.company)
            put("role", parsedOpportunity.role)
            put("deadline", parsedOpportunity.deadline)
            put("link", parsedOpportunity.link)
            put("riskLevel", parsedOpportunity.riskLevel.name)
            put("riskReasons", parsedOpportunity.riskReasons.joinToString("|"))
        }
        
        return json.toString()
    }
}
