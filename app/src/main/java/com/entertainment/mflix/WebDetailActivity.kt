package com.entertainment.mflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import android.widget.Toast
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.entertainment.mflix.databinding.ActivityWebDetailBinding
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class WebDetailActivity : AppCompatActivity(), MaxAdListener {
    private lateinit var binding:ActivityWebDetailBinding
    //Applovin Ads
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null
    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    private lateinit var nativeAdContainer: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent?.extras?.get("name").toString()
        val rate = intent?.extras?.getString("rate").toString()
        val description = intent?.extras?.getString("desc").toString()
        val img = intent?.extras?.getString("img").toString()
        val type = intent?.extras?.getString("type").toString()
        val link = intent?.extras?.getString("link").toString()
        binding.action.text = type
        binding.star.text = rate
        binding.description.text = description
        binding.movieName.text = name
        Picasso.get().load(img).into(binding.movieImg)
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( this ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
            createNativeAd()
        })
        createInterstitialAd()
        nativeAdContainer = binding.nativeAdLayout
        binding.download.setOnClickListener {
            performtask()
            val i = Intent(this,WebOptionActivity::class.java)
            i.putExtra("link",link)
            startActivity(i)
        }

    }
    //Applovin ads
    private fun performtask() {
        if  ( interstitialAd.isReady )
        {
            interstitialAd.showAd()
        }
    }
    //Intrestial ads
    fun createInterstitialAd()
    {
        interstitialAd = MaxInterstitialAd( resources.getString(R.string.intrestial_ads),this)
        interstitialAd.setListener( this )

        // Load the first ad
        interstitialAd.loadAd()
    }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd)
    {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0.0
    }

    override fun onAdLoadFailed(adUnitId: kotlin.String?, error: MaxError?)
    {
        // Interstitial ad failed to load
        // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis( Math.pow( 2.0, Math.min( 6.0, retryAttempt ) ).toLong() )

        Handler().postDelayed( { interstitialAd.loadAd() }, delayMillis )
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?)
    {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}

    override fun onAdClicked(maxAd: MaxAd) {}

    override fun onAdHidden(maxAd: MaxAd)
    {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd()
    }
    //Native ad
    //Native ad
    fun createNativeAd()
    {
        val nativeAdContainer = findViewById<FrameLayout>(R.id.native_ad_layout)
        nativeAdLoader = MaxNativeAdLoader( resources.getString(R.string.nativ_ads), this@WebDetailActivity )
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd)
            {
                // Clean up any pre-existing native ad to prevent memory leaks.
                if ( nativeAd != null )
                {
                    nativeAdLoader.destroy( nativeAd )
                }

                // Save ad for cleanup.
                nativeAd = ad

                // Add ad view to view.
                nativeAdContainer.removeAllViews()
                nativeAdContainer.addView( nativeAdView )
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError)
            {
                // We recommend retrying with exponentially higher delays up to a maximum delay
            }

            override fun onNativeAdClicked(ad: MaxAd)
            {
                // Optional click callback
            }
        })
        nativeAdLoader.loadAd()
    }
}