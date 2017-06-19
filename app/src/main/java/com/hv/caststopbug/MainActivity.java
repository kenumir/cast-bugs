package com.hv.caststopbug;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.WebImage;

public class MainActivity extends AppCompatActivity {
	
	private final String TAG = "cast-tests";
	
	private CastContext mCastContext;
	private CastSession mCastSession;
	private CastStateListener mCastStateListener;
	private SessionManagerListener<CastSession> mSessionManagerListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCastStateListener = new CastStateListener() {
			@Override
			public void onCastStateChanged(int newState) {
				Log.i(TAG, "New State " + newState);
			}
		};
		mSessionManagerListener = new SessionManagerListener<CastSession>() {
			@Override
			public void onSessionStarting(CastSession castSession) {
				Log.i(TAG, "onSessionStarting " + castSession);
			}
			
			@Override
			public void onSessionStarted(CastSession castSession, String s) {
				mCastSession = castSession;
				Log.i(TAG, "onSessionStarted " + castSession);
			}
			
			@Override
			public void onSessionStartFailed(CastSession castSession, int i) {
				Log.i(TAG, "onSessionStartFailed " + castSession);
			}
			
			@Override
			public void onSessionEnding(CastSession castSession) {
				Log.i(TAG, "onSessionEnding " + castSession);
			}
			
			@Override
			public void onSessionEnded(CastSession castSession, int i) {
				if (castSession == mCastSession) {
					mCastSession = null;
				}
				Log.i(TAG, "onSessionEnded " + castSession);
			}
			
			@Override
			public void onSessionResuming(CastSession castSession, String s) {
				Log.i(TAG, "onSessionResuming " + castSession);
			}
			
			@Override
			public void onSessionResumed(CastSession castSession, boolean b) {
				mCastSession = castSession;
				Log.i(TAG, "onSessionResumed " + castSession);
			}
			
			@Override
			public void onSessionResumeFailed(CastSession castSession, int i) {
				Log.i(TAG, "onSessionResumeFailed " + castSession);
			}
			
			@Override
			public void onSessionSuspended(CastSession castSession, int i) {
				Log.i(TAG, "onSessionSuspended " + castSession);
			}
		};
		mCastContext = CastContext.getSharedInstance(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.browse, menu);
		CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,R.id.media_route_menu_item);
		return true;
	}
	
	@Override
	public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
		return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
				|| super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onResume() {
		mCastContext.addCastStateListener(mCastStateListener);
		mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
		if (mCastSession == null) {
			mCastSession = CastContext.getSharedInstance(this).getSessionManager()
					.getCurrentCastSession();
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mCastContext.removeCastStateListener(mCastStateListener);
		mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
		super.onPause();
	}
	
	public void handlePlay(View view) {
		if (mCastSession == null) {
			Log.i(TAG, "no session");
			return;
		}
		RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
		if (remoteMediaClient == null) {
			Log.i(TAG, "no remoteMediaClient");
			return;
		}
		
		MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
		mediaMetadata.putString(MediaMetadata.KEY_TITLE, "Test title");
		mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, "Test subtitle");
		mediaMetadata.addImage(new WebImage(new Uri.Builder().encodedPath("https://lh3.googleusercontent.com/it1lIWxbxtfLdrfg1Y2fX-Qrw2087EUjGqEBiNwWGti5JlFiDZv1fmBBBpDVfrxwMCS0cwq8OSAkDFW9VGXFAZFlwpqWZao=s688").build()));
		
		
		MediaInfo media = new MediaInfo.Builder("http://ice1.somafm.com/sonicuniverse-192-mp3")
				//.setCustomData(playStream.toJSON())
				.setContentType("audio/mpeg")
				.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
				.setMetadata(mediaMetadata)
				.build();
		
		remoteMediaClient.load(media, true, 0).setResultCallback(new ResultCallbacks<RemoteMediaClient.MediaChannelResult>() {
			@Override
			public void onSuccess(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
				Log.i(TAG, "play onSuccess=" + mediaChannelResult);
			}
			@Override
			public void onFailure(@NonNull Status status) {
				Log.e(TAG, "play onFailure=" + status + ", isSuccess=" + status.isSuccess() + ", isInterrupted=" + status.isInterrupted() + ", isCanceled=" + status.isCanceled());
			}
		});
	}
	
	public void handleStop(View view) {
		if (mCastSession == null) {
			Log.i(TAG, "no session");
			return;
		}
		RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
		if (remoteMediaClient == null) {
			Log.i(TAG, "no remoteMediaClient");
			return;
		}
		remoteMediaClient.stop().setResultCallback(new ResultCallbacks<RemoteMediaClient.MediaChannelResult>() {
			@Override
			public void onSuccess(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
				Log.i(TAG, "stop onSuccess=" + mediaChannelResult);
			}
			
			@Override
			public void onFailure(@NonNull Status status) {
				Log.e(TAG, "stop onFailure=" + status);
			}
		});
	}
}
