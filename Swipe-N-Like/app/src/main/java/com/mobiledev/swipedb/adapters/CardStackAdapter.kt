package com.mobiledev.swipedb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobiledev.swipedb.R
import com.mobiledev.swipedb.database.models.ImageCard

class CardStackAdapter(
    private var cards: MutableList<ImageCard> = mutableListOf<ImageCard>()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.swipe_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        holder.name.text = "${card.id}. ${card.imageId}"
        Glide.with(holder.image)
            .load(card.url)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, card.imageId, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun setCards(cards: List<ImageCard>) {
        this.cards.addAll(cards)
        notifyItemRangeInserted(
            this.cards.size,
            cards.size - 1
        )
    }

    fun getCards(): List<ImageCard> {
        return cards
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemName)
        var image: ImageView = view.findViewById(R.id.itemImage)
    }

}
