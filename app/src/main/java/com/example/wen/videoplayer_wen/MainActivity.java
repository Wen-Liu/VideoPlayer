package com.example.wen.videoplayer_wen;

import android.content.Context;
import android.media.MediaPlayer;
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


                break;
            case R.id.fast_rewind:


                break;
            case R.id.player_arrow:
                Log.d(LOG_TAG, "click player_arrow");
                Toast.makeText(this, "player_arrow", Toast.LENGTH_SHORT).show();

                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mPlayerArrow.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayerArrow.setImageResource(R.drawable.ic_pause_black_24dp);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.d(LOG_TAG, "onProgressChanged " + seekBar.getProgress());

        mCurrentTime.setText(String.valueOf(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(LOG_TAG, "onStartTrackingTouch");
        Toast.makeText(this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(LOG_TAG, "onStopTrackingTouch");
        Toast.makeText(this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
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


}