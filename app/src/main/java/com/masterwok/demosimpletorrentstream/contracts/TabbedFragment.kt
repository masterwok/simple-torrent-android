package com.masterwok.demosimpletorrentstream.contracts


interface TabbedFragment<M : Any> {

    fun getTabTitle(): String

    fun configure(model: M)

}