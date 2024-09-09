package com.lunacattus.media.session.ui.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel(), DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {

    }

    override fun onStop(owner: LifecycleOwner) {

    }
}