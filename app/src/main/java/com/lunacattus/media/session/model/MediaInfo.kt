package com.lunacattus.media.session.model

import androidx.databinding.ObservableField

data class MediaInfo(
    var mediaTitle: ObservableField<String> = ObservableField(""),
    val album: ObservableField<String> = ObservableField(""),
    val artist: ObservableField<String> = ObservableField(""),
    val pic: ObservableField<String> = ObservableField(""),
    val durationMs: ObservableField<Long> = ObservableField(0),
    val millSecond: ObservableField<Long> = ObservableField(0),
    val playState: ObservableField<Int> = ObservableField(0),
)