package com.masterwok.demosimpletorrentandroid.adapters


import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentandroid.R
import com.masterwok.simpletorrentandroid.models.TorrentSessionBuffer


class TorrentPieceAdapter : RecyclerView.Adapter<TorrentPieceAdapter.PieceViewHolder>() {

    private var latestUpdatedBuffer: TorrentSessionBuffer = TorrentSessionBuffer()

    class PieceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.card_view_piece)

        fun bind(color: Int) {
            cardView.setBackgroundColor(color)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder =
            PieceViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(
                            R.layout.item_piece,
                            parent,
                            false
                    ))

    override fun getItemCount(): Int = latestUpdatedBuffer.pieceCount ?: 0

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.bind(getPieceColor(context, position))
    }

    private fun getPieceColor(
            context: Context
            , position: Int
    ): Int {
        val isDownloaded = latestUpdatedBuffer.isPieceDownloaded(position)
        val isHeadIndex = latestUpdatedBuffer.bufferHeadIndex == position

        if (isDownloaded) {
            if (isHeadIndex) {
                return ContextCompat.getColor(context, R.color.blue)
            }

            return ContextCompat.getColor(context, R.color.green)
        }

        if (latestUpdatedBuffer.bufferSize == 0) {
            return ContextCompat.getColor(context, R.color.purple)
        }

        if (isHeadIndex) {
            return ContextCompat.getColor(context, R.color.red)
        }

        if (position > latestUpdatedBuffer.bufferHeadIndex
                && position <= latestUpdatedBuffer.bufferTailIndex) {
            return ContextCompat.getColor(context, R.color.yellow)
        }

        return ContextCompat.getColor(context, R.color.purple)
    }

    fun configure(buffer: TorrentSessionBuffer) {

        if (latestUpdatedBuffer.pieceCount != buffer.pieceCount) {
            latestUpdatedBuffer = buffer
            notifyDataSetChanged()
            return
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean {
                return true
            }

            override fun getOldListSize(): Int = latestUpdatedBuffer.pieceCount

            override fun getNewListSize(): Int = buffer.pieceCount

            private fun isHeadIndex(buffer: TorrentSessionBuffer, index: Int): Boolean =
                    buffer.bufferHeadIndex == index

            private fun isTailIndex(buffer: TorrentSessionBuffer, index: Int): Boolean =
                    buffer.bufferTailIndex == index

            private fun isBufferBody(buffer: TorrentSessionBuffer, index: Int): Boolean =
                    buffer.bufferHeadIndex < index
                            && index < buffer.bufferTailIndex

            override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean {
                val oldIsDownloaded = latestUpdatedBuffer.isPieceDownloaded(oldIndex)
                val newIsDownloaded = buffer.isPieceDownloaded(newIndex)

                if (oldIsDownloaded != newIsDownloaded) {
                    return false
                }

                val oldIsHeadIndex = isHeadIndex(latestUpdatedBuffer, oldIndex)
                val oldIsTailIndex = isTailIndex(latestUpdatedBuffer, oldIndex)
                val oldIsBufferBody = isBufferBody(latestUpdatedBuffer, oldIndex)

                val newIsHeadIndex = isHeadIndex(buffer, newIndex)
                val newIsTailIndex = isTailIndex(buffer, newIndex)
                val newIsBufferBody = isBufferBody(buffer, newIndex)

                return oldIsHeadIndex == newIsHeadIndex
                        && oldIsTailIndex == newIsTailIndex
                        && oldIsBufferBody == newIsBufferBody
            }
        })

        latestUpdatedBuffer = buffer

        result.dispatchUpdatesTo(this)
    }

}