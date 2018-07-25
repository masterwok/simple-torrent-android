package com.masterwok.demosimpletorrentstream.contracts


interface TabFragment<T> {

    fun getTitle(): String

    fun configure(model: T)

}

