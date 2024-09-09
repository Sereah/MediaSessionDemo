package com.lunacattus.media.session.ui.home

import android.content.ComponentName
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaMetadata.METADATA_KEY_ALBUM
import android.media.MediaMetadata.METADATA_KEY_ALBUM_ART_URI
import android.media.MediaMetadata.METADATA_KEY_ARTIST
import android.media.MediaMetadata.METADATA_KEY_DURATION
import android.media.MediaMetadata.METADATA_KEY_TITLE
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lunacattus.media.session.databinding.FragmentHomeBinding
import com.lunacattus.media.session.model.MediaInfo
import com.lunacattus.media.session.ui.base.BaseFragment
import com.lunacattus.media.session.util.AppLogger
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    lateinit var mediaController: MediaController
    private lateinit var mediaBrowser: MediaBrowser

    private val mediaBrowserConnectCallback = object : MediaBrowser.ConnectionCallback() {
        override fun onConnected() {
            var sessionToken = mediaBrowser.sessionToken
            mediaController = MediaController(requireContext(), sessionToken)

            mediaController.registerCallback(object : MediaController.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackState?) {
                    val playState = state?.state
                    val millSecond = state?.position
                    val extraBundle = state?.extras

                    //It can be passed from MediaData or through the PlayState bundle
                    val durationMs = extraBundle?.getLong(METADATA_KEY_DURATION)
                    val title = extraBundle?.getString(METADATA_KEY_TITLE)
                    val album = extraBundle?.getString(METADATA_KEY_ALBUM)
                    val artist = extraBundle?.getString(METADATA_KEY_ARTIST)
                    val pic = extraBundle?.getString(METADATA_KEY_ALBUM_ART_URI)

                    //Extra message passed by media session
                    val isBt = extraBundle?.getBoolean("blSound")
                    val displayId = extraBundle?.getInt("displayId")
                    val mediaId = extraBundle?.getString("MediaId")
                    val service = extraBundle?.getString("BrowserService")

                    AppLogger.d(
                        "onPlaybackStateChanged, playState:$playState, millSecond:$millSecond, " +
                                "duration:$durationMs, title:$title, album:$album, artist:$artist, pic:$pic, " +
                                "isBt:$isBt, displayId:$displayId, mediaId:$mediaId, service:$service"
                    )
                    if (pic != null) {
                        val filePath = pic.substring("file://".length)
                        val image = File(filePath)
                        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                        binding.pic.setImageBitmap(bitmap)
                    }
                    binding.mediaInfo?.let {
                        it.mediaTitle.set(title)
                        it.pic.set(pic)
                        it.artist.set(artist)
                        it.album.set(album)
                        it.durationMs.set(durationMs)
                        it.playState.set(playState)
                        it.millSecond.set(millSecond)
                    }
                }

                override fun onMetadataChanged(metadata: MediaMetadata?) {
                    val durationMs = metadata?.getLong(METADATA_KEY_DURATION)
                    val title = metadata?.getString(METADATA_KEY_TITLE)
                    val album = metadata?.getString(METADATA_KEY_ALBUM)
                    val artist = metadata?.getString(METADATA_KEY_ARTIST)
                    val pic = metadata?.getString(METADATA_KEY_ALBUM_ART_URI)
                    AppLogger.d("onMetadataChanged: url=$pic")
                    if (pic != null) {
                        val filePath = pic.substring("file://".length)
                        val image = File(filePath)
                        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                        binding.pic.setImageBitmap(bitmap)
                    }
                    binding.mediaInfo?.let {
                        it.mediaTitle.set(title)
                        it.pic.set(pic)
                        it.artist.set(artist)
                        it.album.set(album)
                        it.durationMs.set(durationMs)
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mediaInfo = MediaInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaBrowser = MediaBrowser(
            requireContext(),
            ComponentName(
                ""/*service app package name*/,
                ""/*service app media session service name*/
            ),
            mediaBrowserConnectCallback,
            null
        )
        mediaBrowser.connect()
        viewClickListener()
    }

    private fun viewClickListener() {
        binding.preButton.setOnClickListener {
            AppLogger.d("preButton")
            mediaController.transportControls.skipToPrevious()
        }

        binding.nextButton.setOnClickListener {
            AppLogger.d("nextButton")
            mediaController.transportControls.skipToNext()
        }

        binding.pauseButton.setOnClickListener {
            val state = mediaController.playbackState?.state
            if (state == PlaybackState.STATE_PLAYING) {
                mediaController.transportControls.pause()
            } else {
                mediaController.transportControls.play()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }
}