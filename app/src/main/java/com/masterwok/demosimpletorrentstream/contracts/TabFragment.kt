package com.masterwok.demosimpletorrentstream.contracts


/**
 * This interface is used by the [@see TabFragmentManager] to get tab
 * titles and to configure the tab for some model of type [T].
 */
interface TabFragment<T> {

    /**
     * Get the title of the tab.
     */
    fun getTitle(): String

    /**
     * Configure all tabs with the provided [model].
     */
    fun configure(model: T)

}

