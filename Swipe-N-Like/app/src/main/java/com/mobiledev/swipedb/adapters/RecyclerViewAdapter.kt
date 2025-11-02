package com.mobiledev.swipedb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobiledev.swipedb.R
import com.mobiledev.swipedb.database.models.ImageCard

class RecyclerViewAdapter(private val items: MutableList<ImageCard> = mutableListOf()) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_card, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageCard = items[position]

        // sets the image to the imageview from our itemHolder class
        Glide.with(holder.imageView)
            .load(imageCard.url)
            .into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = imageCard.imageId

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    fun appendLikes(imageCards: List<ImageCard>) {
        this.items.addAll(imageCards)
        notifyItemRangeInserted(
            this.items.size,
            this.items.size - 1
        )
    }

    fun appendLike(imageCard: ImageCard) {
        this.items.add(0, imageCard)
        notifyItemRangeInserted(
            0,
            1
        )
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.imageId)
    }
}
