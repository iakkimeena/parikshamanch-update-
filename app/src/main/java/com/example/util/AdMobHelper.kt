package com.example.util

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helper object for initializing and managing Google Mobile Ads (AdMob) SDK.
 */
object AdMobHelper {
    private const val TAG = "AdMobHelper"

    // Official Google AdMob sample test banner ad unit ID
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    private val isInitialized = AtomicBoolean(false)

    /**
     * Initializes the Mobile Ads SDK in background. Safe to call multiple times.
     */
    fun initialize(context: Context, onComplete: () -> Unit = {}) {
        if (isInitialized.getAndSet(true)) {
            onComplete()
            return
        }

        try {
            // Configure test device configuration for development
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob SDK Initialized successfully: ${status.adapterStatusMap.keys}")
                onComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob SDK", e)
            isInitialized.set(false)
        }
    }

    fun isSdkInitialized(): Boolean = isInitialized.get()
}

/**
 * Jetpack Compose wrapper for displaying Google AdMob Banner Ads.
 * Handles lifecycle events (destroy on dispose) and provides a clean container with disclosure.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobHelper.TEST_BANNER_AD_UNIT_ID,
    adSize: AdSize = AdSize.BANNER
) {
    val isInspectionMode = LocalInspectionMode.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var adLoadError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    if (isInspectionMode) {
        // Preview placeholder
        AdPlaceholder(modifier = modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Subtle sponsored label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "AD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "प्रायोजित (Sponsored)",
                fontSize = 9.sp,
                color = Color(0xFF64748B)
            )
        }

        val adView = remember {
            AdView(context).apply {
                setAdSize(adSize)
                this.adUnitId = adUnitId
                this.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        adLoadError = null
                        Log.d("AdMobBanner", "Ad loaded successfully: $adUnitId")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdLoaded = false
                        adLoadError = error.message
                        Log.w("AdMobBanner", "Ad failed to load: ${error.code} - ${error.message}")
                    }

                    override fun onAdClicked() {
                        Log.d("AdMobBanner", "Ad clicked")
                    }

                    override fun onAdOpened() {
                        Log.d("AdMobBanner", "Ad opened")
                    }
                }
            }
        }

        // Clean up AdView when leaving composition to prevent memory leaks
        DisposableEffect(adView) {
            try {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            } catch (e: Exception) {
                Log.e("AdMobBanner", "Exception loading ad", e)
            }

            onDispose {
                try {
                    adView.destroy()
                } catch (e: Exception) {
                    Log.e("AdMobBanner", "Exception destroying adView", e)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { adView },
                modifier = Modifier.testTag("admob_adview")
            )
        }
    }
}

@Composable
private fun AdPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = "Ad",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AdMob Banner Ad (Test Mode)",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
