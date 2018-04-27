package com.example.wen.videoplayer_wen;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SurfaceView mSurfaceView;
    private ImageButton mVolumeMute;
    private ImageButton mFastRewind;
    private ImageButton mPlayerArrow;
    private ImageButton mFastForward;
    private ImageButton mFullscreen;
    private ProgressBar mProgressBar;
    private SeekBar mSeekBar;

    private TextView mCurrentTime;
    private TextView mTotalTime;

    private final static String LOG_TAG = "wen_MainActivity";
    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;

    private boolean isMute = false;
    private boolean isStart;
    private boolean isFirst = true;
    private int mProgress;

    //屏幕宽度
    private int mScreenWidth;
    //屏幕高度
    private int mScreenHeight;
    //记录现在的播放位置
    private int mCurrentPos;
    private boolean isLand;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniView();

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void iniView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);

        mCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        mTotalTime = (TextView) findViewById(R.id.tv_total_time);

        mVolumeMute = (ImageButton) findViewById(R.id.volume_mute);
        mFastRewind = (ImageButton) findViewById(R.id.fast_rewind);
        mPlayerArrow = (ImageButton) findViewById(R.id.player_arrow);
        mFastForward = (ImageButton) findViewById(R.id.fast_forward);
        mFullscreen = (ImageButton) findViewById(R.id.fullscreen);

        mVolumeMute.setOnClickListener(this);
        mFastRewind.setOnClickListener(this);
        mPlayerArrow.setOnClickListener(this);
        mFastForward.setOnClickListener(this);
        mFullscreen.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);

        try {
            mMediaPlayer.reset();
            //mediaPlayer.setDisplay(...)必需在callback的surfaceCreated(...) method裡叫用，否則會有聲音無影像
            //也就是說mediaPlayer.setDisplay(...)必需在整個畫面render完成後才能叫用，否則會出現異常
            mMediaPlayer.setDataSource(mVideoUrl);
            mMediaPlayer.prepare();
            mTotalTime.setText(timeParse(mMediaPlayer.getDuration()));
            mSeekBar.setMax(mMediaPlayer.getDuration());
//                    mMediaPlayer.setOnPreparedListener(MainActivity.this);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(LOG_TAG, "surfaceChanged");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(LOG_TAG, "surfaceDestroyed");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.volume_mute:
                AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

                if (isMute) {
                    audioManager.adjustStreamVolume(audioManager.STREAM_MUSIC, audioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND);
                    mVolumeMute.setImageResource(R.drawable.ic_volume_mute_black_24dp);
                    isMute = false;
                    Log.d(LOG_TAG, "取消靜音");
                } else {
                    audioManager.adjustStreamVolume(audioManager.STREAM_MUSIC, audioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND);
                    mVolumeMute.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    isMute = true;
                    Toast.makeText(this, "靜音", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "靜音");
                }
                break;

            case R.id.fast_rewind:
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()-15000);


                break;
            case R.id.player_arrow:
                Log.d(LOG_TAG, "click player_arrow");

                if (!mMediaPlayer.isPlaying()) {
                    if (isFirst) {  //第一次啟用產生new Timer();
                        mTimer = new Timer();
                        mTimer.schedule(mTimerTask, 0, 100);
                        isFirst = false;
                    }
                    isStart = true;
                    mMediaPlayer.start();
                    mPlayerArrow.setImageResource(R.drawable.ic_pause_black_24dp);
                    Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show();

                } else if (mMediaPlayer.isPlaying()) {
                    isStart = false;
                    mMediaPlayer.pause();
                    mPlayerArrow.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    Toast.makeText(this, "暫停播放", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.fast_forward:


                break;
            case R.id.fullscreen:
                ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.FILL_PARENT;
                mSurfaceView.setLayoutParams(layoutParams);

                break;

            default:
                Log.d(LOG_TAG, "click player_arrow");
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(LOG_TAG, "onProgressChanged " + seekBar.getProgress());
        mCurrentTime.setText(timeParse(seekBar.getProgress()));

        if (fromUser) {
            mProgress = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(LOG_TAG, "onStartTrackingTouch");
        isStart = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(LOG_TAG, "onStopTrackingTouch");
        isStart = true;
        mMediaPlayer.seekTo(mProgress);
    }


    private static String timeParse(int duration) {
        String time = "";
        int minute = duration / 60000;
        int seconds = duration % 60000;
        int second = seconds / 1000;

        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }


    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (isStart) {   //如果isStart = true,開始運行
                mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            }  //TimerTask會一直更新，使bar會隨著歌曲播放而改變bar位置

        }
    };


//    private void resetVideoSize() {
//
//        float areaWH = 0.0f;
//        int height;
//
//        if (!isLand) {
//            // 竖屏16:9
//            height = (int) (mScreenWidth / SHOW_SCALE);
//            areaWH = SHOW_SCALE;
//        } else {
//            //横屏按照手机屏幕宽高计算比例
//            height = mScreenHeight;
//            areaWH = mScreenWidth / mScreenHeight;
//        }
//
//        RelativeLayout.LayoutParams layoutParams =
//                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//        mSurfaceLayout.setLayoutParams(layoutParams);
//
//        int mediaWidth = mMediaPlayer.getVideoWidth();
//        int mediaHeight = mMediaPlayer.getVideoHeight();
//
//
//        float mediaWH = mediaWidth * 1.0f / mediaHeight;
//
//        RelativeLayout.LayoutParams layoutParamsSV = null;
//
//
//        if (areaWH > mediaWH) {
//            //直接放会矮胖
//            int svWidth = (int) (height * mediaWH);
//            layoutParamsSV = new RelativeLayout.LayoutParams(svWidth, height);
//            layoutParamsSV.addRule(RelativeLayout.CENTER_IN_PARENT);
//            mSurfaceView.setLayoutParams(layoutParamsSV);
//        }
//
//        if (areaWH < mediaWH) {
//            //直接放会瘦高。
//            int svHeight = (int) (mScreenWidth / mediaWH);
//            layoutParamsSV = new RelativeLayout.LayoutParams(mScreenWidth, svHeight);
//            layoutParamsSV.addRule(RelativeLayout.CENTER_IN_PARENT);
//            mSurfaceView.setLayoutParams(layoutParamsSV);
//        }
//
//    }


}