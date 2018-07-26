package com.masterwok.demosimpletorrentstream.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus

class TorrentPieceAdapter : RecyclerView.Adapter<TorrentPieceAdapter.PieceViewHolder>() {

    class PieceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.card_view_piece)

        fun bind(color: Int) {
            cardView.setBackgroundColor(color)
        }

    }

    private lateinit var torrentSessionStatus: TorrentSessionStatus
    private var isInitialized = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder =
            PieceViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(
                            R.layout.item_piece,
                            parent,
                            false
                    ))

    override fun getItemCount(): Int = if (isInitialized) torrentSessionStatus.totalPieces else 0

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.bind(getPieceColor(context, position))
    }

    private fun getPieceColor(
            context: Context
            , position: Int
    ): Int {

        if (position == torrentSessionStatus.firstMissingPieceIndex) {
            return ContextCompat.getColor(context, R.color.red)
        }

        if (position < torrentSessionStatus.firstMissingPieceIndex) {
            return ContextCompat.getColor(context, R.color.green)
        }

        var color = ContextCompat.getColor(context, R.color.purple)

        if (position > torrentSessionStatus.firstMissingPieceIndex
                && position <= torrentSessionStatus.lastPrioritizedPiece) {
            color = ContextCompat.getColor(context, R.color.yellow)
        }

        if (torrentSessionStatus.downloadedPieces.contains(position)) {
            color = ContextCompat.getColor(context, R.color.green)
        }

        return color
    }

    fun configure(torrentSessionStatus: TorrentSessionStatus) {
        this.torrentSessionStatus = torrentSessionStatus

        isInitialized = true

        notifyDataSetChanged()
    }

}