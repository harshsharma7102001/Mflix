package com.entertainment.mflix

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button


class MyList : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_list, container, false)
        val btn: Button = view.findViewById(R.id.joinbtn)
        val link = "https://t.me/mflix_org"
        btn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
        btn.setOnClickListener {
            val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("$link"))
            telegram.setPackage("org.telegram.messenger")
            startActivity(telegram)
        }
        return view
    }

}