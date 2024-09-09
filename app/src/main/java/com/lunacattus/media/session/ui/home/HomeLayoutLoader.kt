package com.lunacattus.media.session.ui.home

import com.lunacattus.media.session.R
import com.lunacattus.media.session.ui.base.LayoutLoader
import javax.inject.Inject

class HomeLayoutLoader @Inject constructor() : LayoutLoader<HomeActivity> {

    override fun setContentView(activity: HomeActivity) {
        val layout: Int = R.layout.activity_home
        activity.setContentView(layout)
    }
}