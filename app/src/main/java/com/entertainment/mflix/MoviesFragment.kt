package com.entertainment.mflix

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.entertainment.mflix.adapter.Adapter
import com.entertainment.mflix.data.Info
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class MoviesFragment : Fragment(), MaxAdListener {
    private lateinit var database:DatabaseReference
    private lateinit var databaseListener:DatabaseReference
    private var url1:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private var url2:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private var url3:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private var url4:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private var url5:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private var url6:String = "https://m.media-amazon.com/images/M/MV5BNDE4OTk4MTk0M15BMl5BanBnXkFtZTgwODQ4MTg0MzI@._V1_.jpg"
    private lateinit var recyclerView: RecyclerView
    private lateinit var farray:ArrayList<Info>
    private lateinit var progressBar: ProgressBar
    //Applovin Ads
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null
    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    private lateinit var nativeAdContainer:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movies, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( context ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
            createNativeAd()
        })

        createInterstitialAd()
        nativeAdContainer = view.findViewById(R.id.native_ad_layout)
        progressBar = view.findViewById(R.id.progress)
        progressBar.getIndeterminateDrawable().setColorFilter( getResources().getColor(R.color.btnclr), android.graphics.PorterDuff.Mode.MULTIPLY)
        database = FirebaseDatabase.getInstance().getReference("Latest")
        database.child("img").get().addOnSuccessListener {
            if (it.exists()){
                url1 = it.child("one").value.toString()
                Log.d("CHECK","Data fetched is: ${it.child("one").value.toString()}")
                url2 = it.child("two").value.toString()
                url3 = it.child("three").value.toString()
                url4 = it.child("four").value.toString()
                url5 = it.child("five").value.toString()
                url6 = it.child("six").value.toString()
            }else{
                Toast.makeText(context,"Not Able to fetch data",Toast.LENGTH_SHORT).show()
            }
        }
        val imageSlider:ImageSlider = view.findViewById(R.id.imageSlider)
        val imageList = ArrayList<SlideModel>()
        val han = Handler()
        han.postDelayed(object :Runnable{
            override fun run() {
                imageList.add(SlideModel(url1))
                imageList.add(SlideModel(url2))
                imageList.add(SlideModel(url3))
                imageList.add(SlideModel(url4))
                imageList.add(SlideModel(url5))
                imageList.add(SlideModel(url6))
                imageSlider.setImageList(imageList,ScaleTypes.FIT)
            }
        },2000)
        val gridView = GridLayoutManager(context ,2)
        recyclerView.layoutManager = gridView
        recyclerView.setHasFixedSize(true)
        farray = ArrayList<Info>()
        val han2 = Handler()
        han2.postDelayed(object :Runnable{
            override fun run() {
                try{
                    getUserData()
                }catch (e:Exception){
                    Toast.makeText(context,"Plz wait data is loading..",Toast.LENGTH_SHORT).show()
                }
            }
        },3000)
        return view
    }

    private fun getUserData() {
        databaseListener = FirebaseDatabase.getInstance().getReference("Movies")
        databaseListener.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                farray.clear()
                if(snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val fdata = userSnapshot.getValue(Info::class.java)
                        Log.d("CHECK","Value received in fdata is: $fdata")
                        if(fdata!=null){
                            farray.add(fdata)
                        }

                    }
                    progressBar.visibility = View.GONE
                    val adapter = Adapter(requireContext(),farray)
                    recyclerView.adapter = Adapter(requireContext(),farray)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Not able to fetch data",Toast.LENGTH_SHORT).show()
            }
        })
    }
    //Applovin ads
    //Intrestial ads
    fun createInterstitialAd()
    {
        interstitialAd = MaxInterstitialAd( resources.getString(R.string.intrestial_ads),requireActivity() )
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

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?)
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
    fun createNativeAd()
    {
        nativeAdLoader = MaxNativeAdLoader( resources.getString(R.string.nativ_ads), requireContext() )
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