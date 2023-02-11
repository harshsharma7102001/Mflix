package com.entertainment.mflix

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
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.entertainment.mflix.adapter.Adapter
import com.entertainment.mflix.adapter.WebAdapter
import com.entertainment.mflix.data.Info
import com.entertainment.mflix.data.WebInfo
import com.google.firebase.database.*


class WebseriesFragment : Fragment() {
    private lateinit var databaseListener: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var warray:ArrayList<WebInfo>
    private lateinit var progressBar: ProgressBar
    //Applovin Ads
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null
    private lateinit var nativeAdContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_webseries, container, false)
        databaseListener = FirebaseDatabase.getInstance().getReference("WebSeries")
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( context ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
            createNativeAd()
        })

        nativeAdContainer = view.findViewById(R.id.native_ad_layout)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progress)
        progressBar.getIndeterminateDrawable().setColorFilter( getResources().getColor(R.color.btnclr), android.graphics.PorterDuff.Mode.MULTIPLY)
        val gridView = GridLayoutManager(context ,2)
        recyclerView.layoutManager = gridView
        recyclerView.setHasFixedSize(true)
        warray = ArrayList<WebInfo>()
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
        databaseListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                warray.clear()
                if(snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val fdata = userSnapshot.getValue(WebInfo::class.java)
                        Log.d("CHECK","Value received in fdata is: $fdata")
                        if(fdata!=null){
                            warray.add(fdata)
                        }

                    }
                    progressBar.visibility = View.GONE
                    recyclerView.adapter = WebAdapter(requireContext(),warray)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Not able to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
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