package com.lunacattus.media.session.service;

import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.service.media.MediaBrowserService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MediaService extends MediaBrowserService {
    private static final String TAG = "MediaService";
    private static final String MY_MEDIA_ROOT_ID = "media_id";

    @Override
    public void onCreate() {
        super.onCreate();
        MediaSession session = MediaManager
                .getInstance().getMediaSession(this);
        setSessionToken(session.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowser.MediaItem>> result) {
    }

}
