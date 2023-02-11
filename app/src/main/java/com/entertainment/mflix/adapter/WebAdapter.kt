package com.entertainment.mflix.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.entertainment.mflix.R
import com.entertainment.mflix.WebDetailActivity
import com.entertainment.mflix.data.WebInfo
import com.squareup.picasso.Picasso

class WebAdapter(val context: Context, private val List:ArrayList<WebInfo>): RecyclerView.Adapter<WebAdapter.viewHolder>()  {
    class viewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val img : ImageView = itemView.findViewById(R.id.imageView)
        val action: TextView = itemView.findViewById(R.id.type)
        val rating: TextView = itemView.findViewById(R.id.rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movies_bluepring,parent,false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = List[position]
        Picasso.get().load(currentItem.image.toString()).into(holder.img)
        holder.action.text = currentItem.type.toString()
        holder.rating.text = currentItem.star.toString()
        holder.img.setOnClickListener {
            val intent  =  Intent(context, WebDetailActivity::class.java)
            intent.putExtra("name",currentItem.name)
            intent.putExtra("rate",currentItem.star)
            intent.putExtra("desc",currentItem.description)
            intent.putExtra("img",currentItem.image)
            intent.putExtra("type",currentItem.type)
            intent.putExtra("link",currentItem.link)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return List.size
    }

}