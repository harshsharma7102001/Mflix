package com.entertainment.mflix.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.entertainment.mflix.DetailActivity
import com.entertainment.mflix.R
import com.entertainment.mflix.data.Info
import com.squareup.picasso.Picasso
import kotlin.coroutines.coroutineContext

//private lateinit var mListner:Adapter.onClickListner
class Adapter(val context: Context,private val InfoList:ArrayList<Info>):RecyclerView.Adapter<Adapter.myViewHolder>() {
    class myViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val img :ImageView = itemView.findViewById(R.id.imageView)
        val action: TextView = itemView.findViewById(R.id.type)
        val rating:TextView = itemView.findViewById(R.id.rate)
//        init {
//            itemView.setOnClickListener { listner.onItemClick(adapterPosition) }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movies_bluepring,parent,false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = InfoList[position]
        Picasso.get().load(currentItem.image.toString()).into(holder.img)
        holder.action.text = currentItem.type.toString()
        holder.rating.text = currentItem.star.toString()
        holder.img.setOnClickListener {
            val intent  =  Intent(context,DetailActivity::class.java)
            intent.putExtra("name",currentItem.name)
            intent.putExtra("rate",currentItem.star)
            intent.putExtra("desc",currentItem.description)
            intent.putExtra("img",currentItem.image)
            intent.putExtra("type",currentItem.type)
            intent.putExtra("adrino",currentItem.adrino)
            intent.putExtra("gdrive",currentItem.gdrive)
            intent.putExtra("mx",currentItem.mxplayer)
            intent.putExtra("terra",currentItem.terralink)
            context.startActivity(intent)
        }
    }
//    fun setOnItemClickListner(listner: onClickListner){
//        mListner = listner
//    }


    override fun getItemCount(): Int {
        return InfoList.size
    }
    interface onClickListner{
        fun onItemClick(position: Int)
    }

}