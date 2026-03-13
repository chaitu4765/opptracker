package com.example.oppurtunityscanner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oppurtunityscanner.ui.theme.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunityDashboard(
    viewModel: OpportunityViewModel = viewModel(),
    onOpportunityClick: (Int) -> Unit
) {
    val opportunities by viewModel.opportunities.collectAsState()
    
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(BgStart, BgMid, BgEnd)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        StarBackground()

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                GlowButton(
                    onClick = { viewModel.testOpportunityDetection() },
                    text = "Discover"
                )
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                HeaderSection()
                Spacer(modifier = Modifier.height(32.dp))

                if (opportunities.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = opportunities,
                            key = { it.id }
                        ) { opportunity ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(600)) + 
                                        slideInVertically(initialOffsetY = { 60 })
                            ) {
                                OpportunityCard(
                                    opportunity = opportunity,
                                    onDetailClick = { onOpportunityClick(opportunity.id) }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun OpportunityCard(opportunity: OpportunityEntity, onDetailClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val riskColor = when (opportunity.riskLevel) {
        "HIGH" -> RiskHigh
        "LOW" -> RiskLow
        else -> RiskVerify
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { expanded = !expanded }
            )
            .border(
                BorderStroke(1.2.dp, if (opportunity.riskLevel == "HIGH") riskColor.copy(alpha = 0.5f) else GlassBorder),
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Main Card Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = opportunity.role,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RiskBadge(opportunity.riskLevel)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deadline: ${opportunity.deadline}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f)
                )
            }

            // Expanded Section
            if (expanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.05f)
                )

                Text(
                    text = "Company: ${opportunity.company}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                if (opportunity.riskLevel == "HIGH") {
                    Spacer(modifier = Modifier.height(12.dp))
                    RiskWarningSection(opportunity.riskReasons)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Full Message:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = opportunity.fullMessage.ifEmpty { "No additional content." },
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (opportunity.link.isNotEmpty()) {
                        Text(
                            text = "Source: ${opportunity.sourceApp}",
                            fontSize = 11.sp,
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                    TextButton(onClick = onDetailClick) {
                        Text("Open Full View", color = AccentBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RiskBadge(level: String) {
    val (color, label, icon) = when (level) {
        "HIGH" -> Triple(RiskHigh, "High Risk", Icons.Default.Warning)
        "LOW" -> Triple(RiskLow, "Verified", Icons.Default.CheckCircle)
        else -> Triple(RiskVerify, "Verify", Icons.Default.Info)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RiskWarningSection(reasons: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RiskHigh.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(1.dp, RiskHigh.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = RiskHigh, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Potential Scam Warning",
                color = RiskHigh,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "⚠️ This opportunity has scam-like characteristics and should be verified carefully.",
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Detected Patterns:",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        reasons.split("|").forEach { reason ->
            if (reason.isNotEmpty()) {
                Text(
                    text = "• $reason",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Suggestion: Check the official company career website directly before providing any info or payment.",
            color = AccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StarBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = Random(42)
        repeat(100) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val radius = random.nextFloat() * 2f
            drawCircle(
                color = Color.White.copy(alpha = random.nextFloat() * 0.4f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = AccentCyan.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(72.dp),
            border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.padding(18.dp).size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "OppTrack",
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = 2.sp
        )
        Text(
            text = "Automated Opportunity Discovery",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = AccentCyan.copy(alpha = 0.2f),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Scanning for opportunities...",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun GlowButton(
    onClick: () -> Unit,
    text: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 16.dp.value,
        targetValue = 32.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val buttonBrush = Brush.horizontalGradient(listOf(AccentCyan, AccentBlue))
    
    Box(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .shadow(
                elevation = glowPulse.dp,
                shape = CircleShape,
                ambientColor = AccentCyan,
                spotColor = AccentCyan
            )
            .background(buttonBrush, shape = CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 44.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}
