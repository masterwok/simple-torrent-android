package com.masterwok.demosimpletorrentandroid.extensions

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator


/**
 * Set the support of change animations. By default this value is true. Setting
 * this value to false will prevent the, "dim" that occurs when an item is updated.
 */
fun RecyclerView.setSupportsChangeAnimations(doesSupport: Boolean) {
    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = doesSupport
}
