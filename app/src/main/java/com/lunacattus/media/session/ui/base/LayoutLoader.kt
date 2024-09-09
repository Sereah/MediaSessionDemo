package com.lunacattus.media.session.ui.base

import androidx.fragment.app.FragmentActivity

interface LayoutLoader<T: FragmentActivity> {

    fun setContentView(activity: T)
}