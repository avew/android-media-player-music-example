package com.github.avew.mpexample;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.seekBar)
    SeekBar mSeekBar;

    @BindView(R.id.btn_forward)
    Button mBtnForward;

    @BindView(R.id.btn_play)
    Button mBtnPlay;

    @BindView(R.id.btn_pause)
    Button mBtnPause;

    @BindView(R.id.btn_backward)
    Button mBtnBackward;

    @BindView(R.id.text_view_playing_time)
    TextView mTvPlayingTime;

    @BindView(R.id.text_view_end_time)
    TextView mTvEndTime;


    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();

    private double mPlayingTime = 0;
    private double mFinalTime = 0;

    private int forwardTime = 10000;
    private int backwardTime = 5000;

    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mSeekBar.setClickable(false);
        mBtnPause.setEnabled(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "Finish", Toast.LENGTH_SHORT).show();
                mediaPlayer.seekTo(0);
                mBtnPlay.setEnabled(true);
                mSeekBar.setProgress(0);
            }
        });
    }

    @Override
    protected void onPause() {
        mSeekBar.setProgress(0);
        super.onPause();
    }

    @SuppressLint("DefaultLocale")
    @OnClick(R.id.btn_play)
    public void toPlay() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
        mediaPlayer.start();

        mFinalTime = mediaPlayer.getDuration();
        mPlayingTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            mSeekBar.setMax((int) mFinalTime);
            oneTimeOnly = 1;
        }

        //set playing time
        mTvPlayingTime.setText(String.format("%d min, %d sec",
                MILLISECONDS.toMinutes((long) mPlayingTime),
                MILLISECONDS.toSeconds((long) mPlayingTime) -
                        TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes((long) mPlayingTime)))
        );

        //set final time
        mTvEndTime.setText(String.format("%d min, %d sec",
                MILLISECONDS.toMinutes((long) mFinalTime),
                MILLISECONDS.toSeconds((long) mFinalTime) -
                        TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes((long) mFinalTime)))
        );


        mSeekBar.setProgress((int) mPlayingTime);
        myHandler.postDelayed(UpdateSongTime, 100);

        mBtnPause.setEnabled(true);
        mBtnPlay.setEnabled(false);
    }

    @OnClick(R.id.btn_pause)
    public void toPause() {
        Toast.makeText(this, "Pausing", Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        mBtnPlay.setEnabled(true);
        mBtnPause.setEnabled(false);
    }

    @OnClick(R.id.btn_forward)
    public void toForward() {
        int temp = (int) mPlayingTime;

        if ((temp + forwardTime) <= mFinalTime) {
            mPlayingTime = mPlayingTime + forwardTime;
            mediaPlayer.seekTo((int) mPlayingTime);
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_backward)
    public void toBackward() {
        int temp = (int) mPlayingTime;

        if ((temp - backwardTime) > 0) {
            mPlayingTime = mPlayingTime - backwardTime;
            mediaPlayer.seekTo((int) mPlayingTime);
            Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            mPlayingTime = mediaPlayer.getCurrentPosition();
            mTvPlayingTime.setText(String.format("%d min, %d sec",

                    MILLISECONDS.toMinutes((long) mPlayingTime),
                    MILLISECONDS.toSeconds((long) mPlayingTime) -
                            TimeUnit.MINUTES.toSeconds(MILLISECONDS.
                                    toMinutes((long) mPlayingTime)))
            );
            mSeekBar.setProgress((int) mPlayingTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
