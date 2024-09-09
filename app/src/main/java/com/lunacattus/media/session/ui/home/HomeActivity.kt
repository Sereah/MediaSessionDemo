package com.lunacattus.media.session.ui.home

import android.os.Bundle
import com.lunacattus.media.session.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var loader: HomeLayoutLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader.setContentView(this)
    }
}