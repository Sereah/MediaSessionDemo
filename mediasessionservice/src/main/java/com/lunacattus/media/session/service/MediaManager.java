package com.lunacattus.media.session.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

public class MediaManager {

    private static final String TAG = "MediaManager";
    private static final String BUNDLE_EXTA_KEY_DISPLAY_ID_INT = "displayId";
    private static final String BUNDLE_EXTA_KEY_BLUETOOTH_SOUND_INT = "blSound";
    private static final String BUNDLE_EXTA_KEY_MEDIA_ID = "MediaId";
    private static final String BUNDLE_EXTA_KEY_BROWSER_SERVICE = "BrowserService";
    private static MediaManager mInstance = null;
    private final PlaybackState.Builder mPlaybackStateBuilder = new PlaybackState.Builder();
    private MediaSession mMediaSession;
    private final Bundle mMediaBundle = new Bundle();

    private final MediaSession.Callback mSessionCallback = new MediaSession.Callback() {
        @Override
        public boolean onMediaButtonEvent(@NonNull Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent keyEvent = intent.getParcelableExtra(
                        Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null) {
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            CarplaySessionProxy.getInstance().notifyFlashMedia(
                                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            CarplaySessionProxy.getInstance().notifyStartMedia(
                                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            CarplaySessionProxy.getInstance().notifyStopMedia(
                                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            CarplaySessionProxy.getInstance().notifyPreviousTrack(
                                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            CarplaySessionProxy.getInstance().notifyNextTrack(
                                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onSkipToNext() {
            CarplaySessionProxy.getInstance().notifyNextTrack(true);
            CarplaySessionProxy.getInstance().notifyNextTrack(false);
        }

        @Override
        public void onSkipToPrevious() {
            CarplaySessionProxy.getInstance().notifyPreviousTrack(true);
            CarplaySessionProxy.getInstance().notifyPreviousTrack(false);
        }

        @Override
        public void onPause() {
            CarplaySessionProxy.getInstance().notifyStopMedia(true);
            CarplaySessionProxy.getInstance().notifyStopMedia(false);
        }

        @Override
        public void onPlay() {
            CarplaySessionProxy.getInstance().notifyStartMedia(true);
            CarplaySessionProxy.getInstance().notifyStartMedia(false);
        }
    };

    public static MediaManager getInstance() {
        if (mInstance == null) {
            mInstance = new MediaManager();
        }
        return mInstance;
    }

    public MediaSession getMediaSession(Context context) {
        if (mMediaSession == null) {
            mMediaSession = new MediaSession(context, context.getPackageName());
            mMediaSession.setCallback(mSessionCallback, new Handler(Looper.getMainLooper()));
        }
        return mMediaSession;
    }

    public void init(Context context) {
        if (mMediaSession == null) {
            mMediaSession = new MediaSession(context, context.getPackageName());
            mMediaSession.setCallback(mSessionCallback, new Handler(Looper.getMainLooper()));
        }
        mPlaybackStateBuilder.setActions(PlaybackState.ACTION_PLAY_PAUSE
                | PlaybackState.ACTION_SKIP_TO_NEXT
                | PlaybackState.ACTION_SKIP_TO_PREVIOUS);
    }

    public void setSessionState(boolean active) {
        if (mMediaSession != null) {
            mMediaSession.setActive(active);
            if (active) {
                mMediaBundle.putString(BUNDLE_EXTA_KEY_MEDIA_ID, "media_id");
                mMediaBundle.putBoolean(BUNDLE_EXTA_KEY_BLUETOOTH_SOUND_INT, false);
                mMediaBundle.putInt(BUNDLE_EXTA_KEY_DISPLAY_ID_INT, CarplaySessionProxy.getInstance().getDisplayId());
                mMediaBundle.putString(BUNDLE_EXTA_KEY_BROWSER_SERVICE, "com.lunacattus.media.session.service.ZoneMediaService");
            }
        }
    }

    public void mediaItemInformation(
            int durationMs, String title, String album, String artist, String artwork) {
        mMediaBundle.putLong(MediaMetadata.METADATA_KEY_DURATION, durationMs);
        if (!TextUtils.isEmpty(title)) {
            mMediaBundle.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        }
        if (!TextUtils.isEmpty(album)) {
            mMediaBundle.putString(MediaMetadata.METADATA_KEY_ALBUM, album);
        }
        if (!TextUtils.isEmpty(artist)) {
            mMediaBundle.putString(MediaMetadata.METADATA_KEY_ARTIST, artist);
        }
        if (!TextUtils.isEmpty(artwork)) {
            mMediaBundle.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, "file://" + artwork);
        }
        mPlaybackStateBuilder.setExtras(mMediaBundle);
    }

    public void playbackInformation(byte status, int milliSeconds) {
        int playState = PlaybackState.STATE_STOPPED;
        switch (status) {
            case 1:
                playState = PlaybackState.STATE_PLAYING;
                break;
            case 2:
                playState = PlaybackState.STATE_PAUSED;
                break;
            case 0:
            default:
                break;
        }
        mPlaybackStateBuilder.setState(playState, milliSeconds, 1);
        if (mMediaSession != null) {
            mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        }
    }
}
