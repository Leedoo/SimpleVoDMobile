package com.clear.vodmobile.player;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 
 * TODO, FIXME 1. figure out the relationship about, onMeasure, layout, scaling
 * 2. add KeyEvent support
 *
 */

public class ClearVideoView extends SurfaceView
        implements ClearMediaController.MediaPlayerControl {
    private static final String TAG = "ClearVideoView";

    /* states */
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_SUSPEND = 6;
    private static final int STATE_RESUME = 7;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private MediaPlayer mMediaPlayer = null;
    private SurfaceHolder mSurfaceHolder = null;
    private int mSurfaceX = 0;
    private int mSurfaceY = 0;
    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private boolean isLooping = false;

    private Uri mUri;
    private long mDuration = -1;
    private int mVideoWidth;
    private int mVideoHeight;
    
    private String mMovieName = null;

    private int mSeekWhenPrepared = 0; // recording the seek position while
                                       // preparing
    private int mCurrentBufferPercentage;

    private Context mContext;

    /* event to notify caller */
    private OnCompletionListener mOnCompletionListener = null;
    private OnPreparedListener mOnPreparedListener = null;
    private OnErrorListener mOnErrorListener = null;
    private OnSeekCompleteListener mOnSeekCompleteListener = null;
    private OnInfoListener mOnInfoListener = null;
    private OnBufferingUpdateListener mOnBufferingUpdateListener = null;

    private ClearMediaController mMediaController = null;

    public ClearVideoView(Context context) {
        super(context);
        initView(context);
    }

    public ClearVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public ClearVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    @SuppressWarnings("deprecation")
    private void initView(Context context) {
        mContext = context;
        getHolder().addCallback(mSHCallback);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        if (context instanceof Activity)
            ((Activity) context)
                    .setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /* TODO FIXME make sure it won't make any trouble ... */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {
            Log.d(TAG, "video surface changed. w: " + w + " h: " + h);
            mSurfaceHolder = holder;
            // mSurfaceWidth = w;
            // mSurfaceHeight = h;
            // if (mMediaController != null) {
            // if (mMediaController.isShowing())
            // mMediaController.hide();
            // mMediaController.show();
            // }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "video surface created");
            mSurfaceHolder = holder;

            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
                    && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "video surface destroyed");
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            release(true);
        }
    };

    /**
     * Be careful to use this api. make sure you put video view in a
     * framelayout. Otherwise, do NOT call this api.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public void setDisplayArea(int x, int y, int width, int height) {
        // Log.i("SetDisplayArea: " + x + " " + y + " " + width + " " + height);
        mSurfaceX = x;
        mSurfaceY = y;
        mSurfaceWidth = width;
        mSurfaceHeight = height;

        // if (mSurfaceHolder == null) {
        // return ;
        // }

        post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();

                if (lp == null) {
                    lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                }

                lp.leftMargin = mSurfaceX;
                lp.topMargin = mSurfaceY;
                lp.width = mSurfaceWidth;
                lp.height = mSurfaceHeight;

                setLayoutParams(lp);
            }
        });

      
    }

    /* events get from mediaplayer */
    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared. video width: " + mp.getVideoWidth()
                    + " height: " + mp.getVideoHeight());
            mCurrentState = STATE_PREPARED;

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer);
            if (mMediaController != null)
                mMediaController.setEnabled(true);

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            if (mSeekWhenPrepared != 0)
                seekTo(mSeekWhenPrepared);

            /* this flag change by caller invoke start() */
            if (mTargetState == STATE_PLAYING) {
                start();
                if (mMediaController != null)
                    mMediaController.show();
            }
        }
    };

    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: " + width + "x" + height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            // TODO, FIXME, check if we need to adopt any position related
            // setting
        }
    };

    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err,
                int impl_err) {
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();

            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err))
                    return true;
            }
            /*
             * if (getWindowToken() != null) { int message = framework_err ==
             * MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ?
             * getResources() .getIdentifier(
             * "VideoView_error_text_invalid_progressive_playback", "string",
             * mContext.getPackageName()) : getResources().getIdentifier(
             * "VideoView_error_text_unknown", "string",
             * mContext.getPackageName());
             * 
             * new AlertDialog.Builder(mContext) .setTitle(
             * getResources().getIdentifier( "VideoView_error_title", "string",
             * mContext.getPackageName())) .setMessage(message)
             * .setPositiveButton( getResources().getIdentifier(
             * "VideoView_error_button", "string", mContext.getPackageName()),
             * new DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface dialog, int whichButton) { if
             * (mOnCompletionListener != null) mOnCompletionListener
             * .onCompletion(mMediaPlayer); } }).setCancelable(false).show(); }
             */
            return true;
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "onCompletion");
            /*
             * on some devices, e.g huawei P6, mediaplayer.setLooping() doesn't
             * work...
             */
            if (isLooping == true) {
                openVideo();
                return;
            }

            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // Log.d("buffering " + percent);
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };
    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d(TAG, "onSeekComplete");
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + "," + extra);
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            } else if (mMediaPlayer != null) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    // mMediaPlayer.pause();
                    // if (mMediaBufferingIndicator != null)
                    // mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    // mMediaPlayer.start();
                    // if (mMediaBufferingIndicator != null)
                    // mMediaBufferingIndicator.setVisibility(View.GONE);
                }
            }
            return true;
        }
    };

    /* main operations */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        // requestLayout();
        // invalidate();
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            Log.d(TAG,
                    "openVideo failed due to empty uri or surface not created. uri: "
                            + mUri);
            return;
        }

        // end the player if any existed
        release(false);

        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            // mMediaPlayer.setOnTimedTextListener(mTimedTextListener);

            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(mContext, mUri);

            // for ffmpeg player. not supported by native player
            // mMediaPlayer.setBufferSize(mBufSize);
            // mMediaPlayer
            // .setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ?
            // MediaPlayer.VIDEOCHROMA_RGB565
            // : MediaPlayer.VIDEOCHROMA_RGBA);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to open content: " + mUri + " exception:" + ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Unable to open content: " + mUri + " exception:" + ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;

            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
                mUri = null;
            }
        }
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING);
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    public boolean canPause() {
        return true;
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    public boolean canSeekBackward() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSeekForward() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    public long getCurrentPosition() {
        if (isInPlaybackState())
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }

    public long getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0)
                return mDuration;
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        } else if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public boolean isPause() {
        return mCurrentState == STATE_PAUSED ? true : false;
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mUri = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    /* TODO, FIXME, */
    public void setVideoScalingMode(int mode) {
        if (mMediaPlayer != null) {
            // mMediaPlayer.setVideoScalingMode(mode);
        }
    }
    
    public void setMovieName(String name) {
    	mMovieName = name;
    }
    

    /**
     * 
     * @param leftVolume
     *            0.0 ~ 1.0
     * @param rightVolume
     *            0.0 ~ 1.0
     */
    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View
                    ? (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());

            if(mMovieName != null){
            	mMediaController.setFileName(mMovieName);
            }
            else if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null"
                        : paths.get(paths.size() - 1);
                mMediaController.setFileName(name);
            }
        }
    }

    public void enableMediaController(boolean enable) {
        if (enable && mMediaController == null)
            mMediaController = new ClearMediaController(mContext);

        attachMediaController();
    }

    @Override
    public void seekTo(long pos) {
        int pos_int = (int) pos;
        seekTo(pos_int);
    }

    public void setLooping(boolean enable) {
        isLooping = enable;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLooping);
        }
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }


   
}