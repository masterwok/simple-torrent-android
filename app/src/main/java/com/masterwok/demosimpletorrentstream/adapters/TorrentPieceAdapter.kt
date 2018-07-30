package com.masterwok.demosimpletorrentstream.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.simpletorrentstream.models.TorrentSessionBufferState
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus

class TorrentPieceAdapter : RecyclerView.Adapter<TorrentPieceAdapter.PieceViewHolder>() {

    class PieceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.card_view_piece)

        fun bind(color: Int) {
            cardView.setBackgroundColor(color)
        }

    }

    private lateinit var bufferState: TorrentSessionBufferState

    private var isInitialized = false

    private var lastCompletedPieceCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder =
            PieceViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(
                            R.layout.item_piece,
                            parent,
                            false
                    ))

    override fun getItemCount(): Int = if (isInitialized) bufferState.pieceCount else 0

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.bind(getPieceColor(context, position))
    }

    private fun getPieceColor(
            context: Context
            , position: Int
    ): Int {
        val isDownloaded = bufferState.isPieceDownloaded(position)

        if (bufferState.bufferHeadIndex == position) {
            if (isDownloaded) {
                return ContextCompat.getColor(context, R.color.blue)
            }

            return ContextCompat.getColor(context, R.color.red)
        }

        if (bufferState.isFinished() || isDownloaded) {
            return ContextCompat.getColor(context, R.color.green)
        }

        if (position > bufferState.bufferHeadIndex
                && position <= bufferState.bufferTailIndex) {
            return ContextCompat.getColor(context, R.color.yellow)
        }

        return ContextCompat.getColor(context, R.color.purple)
    }

    fun configure(torrentSessionStatus: TorrentSessionStatus) {
        val downloadedPieceCount = torrentSessionStatus
                .torrentSessionBufferState
                .downloadedPieceCount

        if (downloadedPieceCount != lastCompletedPieceCount) {
            lastCompletedPieceCount = downloadedPieceCount
            bufferState = torrentSessionStatus.torrentSessionBufferState
            isInitialized = true

            notifyDataSetChanged()
        }
    }

}