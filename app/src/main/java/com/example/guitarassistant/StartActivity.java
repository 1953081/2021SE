package com.example.guitarassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class StartActivity extends AppCompatActivity {

    private Button endButton, backButton;
    private TextView songName;
    private Spinner speedChoice;
    private ImageButton start, reverse, advance;

    public int songId;
    public ArrayList<Integer> songBeats;

    private int isRun, currentBeat, countDown;

    private LinkedList<ImageView> images;

    private Handler handler;

    private Runnable updateThread;

    private ImageButton hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        songId = getIntent().getIntExtra("songId", 0);
        songName = (TextView)findViewById(R.id.textView2);
        songName.setText(getIntent().getStringExtra("songName"));
        songBeats = getIntent().getIntegerArrayListExtra("songBeats");

        endButton = (Button)findViewById(R.id.s_end);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end();
            }
        });
        backButton = (Button)findViewById(R.id.s_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        speedChoice = (Spinner)findViewById(R.id.spinner);
        String[] speeds = {"二倍速", "正常速", "0.5倍速", "0.25倍速", "0.125倍速"};
        ArrayAdapter<String> gradeAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, speeds);
        speedChoice.setAdapter(gradeAdapter1);
        speedChoice.setSelection(1);

        start = (ImageButton)findViewById(R.id.s_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });
        reverse = (ImageButton)findViewById(R.id.s_reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reverse();
            }
        });
        advance = (ImageButton)findViewById(R.id.s_advance);
        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advance();
            }
        });

        hintButton = (ImageButton)findViewById(R.id.startHint);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StartActivity.this)
                        .setTitle("使用说明：")
                        .setMessage("1.中央为“开始”与“暂停”按钮；\n" +
                                "2.左侧按钮退后一个节拍，右侧按钮前进一个节拍；\n" +
                                "3.停止按钮终止歌曲并回到歌曲开头；\n" +
                                "4.可随时切换歌曲播放倍速。\n" +
                                "5.下方六弦图数字颜色变化时代表需要拨弦一次；\n" +
                                "6.本应用歌曲播放只有动作提示，没有声音。")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

        isRun = 0;
        currentBeat = -1;
        countDown = 0;
        start.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));

        handler = new Handler();

        updateThread = new Runnable() {
            @Override
            public void run() {
                if(currentBeat < songBeats.size()){
                    show();
                    int delayTime = 125;
                    switch (speedChoice.getSelectedItemPosition()){
                        case 0:
                            delayTime = 63;
                            break;
                        case 1:
                            delayTime = 125;
                            break;
                        case 2:
                            delayTime = 250;
                            break;
                        case 4:
                            delayTime = 500;
                            break;
                        case 5:
                            delayTime = 1000;
                            break;
                        default:
                            break;
                    }
                    handler.postDelayed(updateThread,delayTime);
                }
                else{
                    end();
                }
            }
        };

        images = new LinkedList<>();
    }

    public void end(){
        isRun = 0;
        start.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));
        clear();
        currentBeat = -1;
        countDown = 0;
        findViewById(R.id.imageView44).setVisibility(View.INVISIBLE);
        handler.removeCallbacks(updateThread);
    }

    public void back(){
        handler.removeCallbacks(updateThread);
        finish();
    }

    public void Start(){
        if(songBeats.size() == 0){
            Toast.makeText(StartActivity.this,"该歌曲无节拍！",Toast.LENGTH_SHORT).show();
        }
        else if(isRun == 0){
            isRun = 1;
            start.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            handler.post(updateThread);
        }
        else{
            isRun = 0;
            start.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));
            handler.removeCallbacks(updateThread);
        }
    }

    public void Reverse(){
        if(songBeats.size() == 0){
            Toast.makeText(StartActivity.this,"该歌曲无节拍！",Toast.LENGTH_SHORT).show();
        }
        else if(currentBeat <= 0){
            Toast.makeText(StartActivity.this,"已是第一拍！",Toast.LENGTH_SHORT).show();
        }
        else{
            currentBeat--;
            clear();
            change();
            for(ImageView image:images){
                image.setVisibility(View.VISIBLE);
            }
            countDown--;
            Toast.makeText(StartActivity.this,"已后退一拍！",Toast.LENGTH_SHORT).show();
        }
    }

    public void Advance(){
        if(songBeats.size() == 0){
            Toast.makeText(StartActivity.this,"该歌曲无节拍！",Toast.LENGTH_SHORT).show();
        }
        else if(currentBeat >= songBeats.size() - 1){
            Toast.makeText(StartActivity.this,"已是最后一拍！",Toast.LENGTH_SHORT).show();
        }
        else{
            currentBeat++;
            clear();
            change();
            for(ImageView image:images){
                image.setVisibility(View.VISIBLE);
            }
            countDown--;
            Toast.makeText(StartActivity.this,"已前进一拍！",Toast.LENGTH_SHORT).show();
        }
    }

    public void show(){
        if(countDown > 0){
            countDown--;
        }
        else{
            currentBeat++;
            if(currentBeat >= songBeats.size()){
                return;
            }
            clear();
            change();
            for(ImageView image:images){
                image.setVisibility(View.VISIBLE);
            }
            countDown--;
        }
    }

    public void clear(){
        for(ImageView image:images){
            image.setVisibility(View.INVISIBLE);
        }
        images.clear();
    }

    public void change(){
        int beat = songBeats.get(currentBeat);

        if(findViewById(R.id.imageView44).getVisibility() == View.VISIBLE)
            findViewById(R.id.imageView44).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.imageView44).setVisibility(View.VISIBLE);

        switch(beat/100000){
            case 0:
                countDown = 32;
                break;
            case 1:
                countDown = 24;
                break;
            case 2:
                countDown = 16;
                break;
            case 3:
                countDown = 8;
                break;
            case 4:
                countDown = 4;
                break;
            case 5:
                countDown = 2;
                break;
            case 6:
                countDown = 1;
                break;
            default:
                Toast.makeText(StartActivity.this,"歌曲中出现未知节拍！",Toast.LENGTH_SHORT).show();
                break;
        }

        switch ((beat / 100) % 1000){
            case 0:
                break;
            case 1:
                switch (beat % 100){
                    case 0:
                        break;
                    case 1:
                        images.add(findViewById(R.id.imageView20));
                        break;
                    case 2:
                        images.add(findViewById(R.id.imageView19));
                        break;
                    case 3:
                        images.add(findViewById(R.id.imageView18));
                        break;
                    case 4:
                        images.add(findViewById(R.id.imageView11));
                        break;
                    case 5:
                        images.add(findViewById(R.id.imageView10));
                        break;
                    case 6:
                        images.add(findViewById(R.id.imageView3));
                        break;
                    default:
                        images.add(findViewById(R.id.imageView20));
                        images.add(findViewById(R.id.imageView19));
                        images.add(findViewById(R.id.imageView18));
                        images.add(findViewById(R.id.imageView11));
                        images.add(findViewById(R.id.imageView10));
                        images.add(findViewById(R.id.imageView3));
                        break;
                }
                break;
            case 2:
                switch (beat % 100){
                    case 0:
                        break;
                    case 1:
                        images.add(findViewById(R.id.imageView21));
                        break;
                    case 2:
                        images.add(findViewById(R.id.imageView26));
                        break;
                    case 3:
                        images.add(findViewById(R.id.imageView17));
                        break;
                    case 4:
                        images.add(findViewById(R.id.imageView12));
                        break;
                    case 5:
                        images.add(findViewById(R.id.imageView9));
                        break;
                    case 6:
                        images.add(findViewById(R.id.imageView4));
                        break;
                    default:
                        images.add(findViewById(R.id.imageView21));
                        images.add(findViewById(R.id.imageView26));
                        images.add(findViewById(R.id.imageView17));
                        images.add(findViewById(R.id.imageView12));
                        images.add(findViewById(R.id.imageView9));
                        images.add(findViewById(R.id.imageView4));
                        break;
                }
                break;
            case 3:
                switch (beat % 100){
                    case 0:
                        break;
                    case 1:
                        images.add(findViewById(R.id.imageView22));
                        break;
                    case 2:
                        images.add(findViewById(R.id.imageView25));
                        break;
                    case 3:
                        images.add(findViewById(R.id.imageView16));
                        break;
                    case 4:
                        images.add(findViewById(R.id.imageView13));
                        break;
                    case 5:
                        images.add(findViewById(R.id.imageView8));
                        break;
                    case 6:
                        images.add(findViewById(R.id.imageView5));
                        break;
                    default:
                        images.add(findViewById(R.id.imageView22));
                        images.add(findViewById(R.id.imageView25));
                        images.add(findViewById(R.id.imageView16));
                        images.add(findViewById(R.id.imageView13));
                        images.add(findViewById(R.id.imageView8));
                        images.add(findViewById(R.id.imageView5));
                        break;
                }
                break;
            case 4:
                switch (beat % 100){
                    case 0:
                        break;
                    case 1:
                        images.add(findViewById(R.id.imageView23));
                        break;
                    case 2:
                        images.add(findViewById(R.id.imageView24));
                        break;
                    case 3:
                        images.add(findViewById(R.id.imageView15));
                        break;
                    case 4:
                        images.add(findViewById(R.id.imageView14));
                        break;
                    case 5:
                        images.add(findViewById(R.id.imageView7));
                        break;
                    case 6:
                        images.add(findViewById(R.id.imageView6));
                        break;
                    default:
                        images.add(findViewById(R.id.imageView23));
                        images.add(findViewById(R.id.imageView24));
                        images.add(findViewById(R.id.imageView15));
                        images.add(findViewById(R.id.imageView14));
                        images.add(findViewById(R.id.imageView7));
                        images.add(findViewById(R.id.imageView6));
                        break;
                }
                break;
            case 5:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView25));
                break;
            case 6:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView25));
                images.add(findViewById(R.id.imageView13));
                break;
            case 7:
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView16));
                break;
            case 8:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 9:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView25));
                break;
            case 10:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView25));
                break;
            case 11:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView5));
                break;
            case 12:
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView25));
                break;
            case 13:
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView25));
                break;
            case 14:
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView25));
                images.add(findViewById(R.id.imageView14));
                break;
            case 15:
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView25));
                break;
            case 16:
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView25));
                break;
            case 17://D
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView8));
                break;
            case 18://D7
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView12));
                break;
            case 19://Dm
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView8));
                break;
            case 20://Dsus4
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView8));
                break;
            case 21://D7sus4
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView12));
                break;
            case 22://D6
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView12));
            case 23://Dm6
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView12));
            case 24://Dm7
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                break;
            case 25://Dmaj7
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                break;
            case 26://Dmaj9
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                break;
            case 27://D9
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView21));
                images.add(findViewById(R.id.imageView12));
                break;
            case 28://Dm9
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                break;
            case 29://E
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 30://E7
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView26));
                break;
            case 31://Em
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 32://Esus4
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 33://E7sus4
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView26));
                break;
            case 34://E6
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 35://Em6
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 36://Em7
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 37://Emaj7
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView26));
                break;
            case 38://Emaj9
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView26));
                break;
            case 39://E9
            case 40://Em9
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView26));
                break;
            case 41://F
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView25));
                break;
            case 42://F7
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView7));
                images.add(findViewById(R.id.imageView25));
                break;
            case 43://Fm
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView25));
                break;
            case 44://Fsus4
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView13));
                break;
            case 45://F7sus4
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView25));
                images.add(findViewById(R.id.imageView13));
                break;
            case 46://F6
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView8));
                break;
            case 47://Fm6
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 48://Fm7
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView7));
                break;
            case 49://Fmaj7
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView16));
                break;
            case 50://Fmaj9
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView16));
                break;
            case 51://F9
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView20));
                break;
            case 52://Fm9
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView25));
                break;
            case 53://G
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView22));
                break;
            case 54://G7
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView22));
                break;
            case 55://Gm
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView25));
                break;
            case 56://Gsus4
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView22));
                break;
            case 57://G7sus4
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView22));
                break;
            case 58://G
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView22));
                break;
            case 59://Gm6
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView25));
                break;
            case 60://Gm7
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView16));
                images.add(findViewById(R.id.imageView7));
                break;
            case 61://Gmaj7
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView15));
                break;
            case 62://Gmaj9
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView22));
                images.add(findViewById(R.id.imageView15));
                break;
            case 63://G9
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView16));
                break;
            case 64://Gm9
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView22));
                images.add(findViewById(R.id.imageView16));
                break;
            case 65://A
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 66://A7
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView17));
                break;
            case 67://Am
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 68://Asus4
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 69://A7sus4
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView17));
                break;
            case 70://A6
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 71://Am6
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 72://Am7
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView17));
                break;
            case 73://Amaj7
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView17));
                break;
            case 74://Amaj9
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView17));
                break;
            case 75://A9
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView14));
                break;
            case 76://Am9
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView5));
                images.add(findViewById(R.id.imageView14));
                break;
            case 77://B
                images.add(findViewById(R.id.imageView21));
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView7));
                images.add(findViewById(R.id.imageView14));
                images.add(findViewById(R.id.imageView15));
                break;
            case 78://B7
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView18));
                break;
            case 79://Bm
                images.add(findViewById(R.id.imageView21));
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView8));
                images.add(findViewById(R.id.imageView14));
                images.add(findViewById(R.id.imageView15));
                break;
            case 80://Bsus4
                images.add(findViewById(R.id.imageView20));
                images.add(findViewById(R.id.imageView19));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView3));
                images.add(findViewById(R.id.imageView7));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView16));
                break;
            case 81://B7sus4
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView26));
                break;
            case 82://B6
                images.add(findViewById(R.id.imageView11));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView26));
                break;
            case 83://Bm6
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView10));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView17));
                break;
            case 84://Bm7
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView26));
                break;
            case 85://Bmaj7
                images.add(findViewById(R.id.imageView21));
                images.add(findViewById(R.id.imageView26));
                images.add(findViewById(R.id.imageView17));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView4));
                images.add(findViewById(R.id.imageView7));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView15));
                break;
            case 86://Bmaj9
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView13));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView26));
                break;
            case 87://B9
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView18));
                images.add(findViewById(R.id.imageView26));
                break;
            case 88://Bm9
                images.add(findViewById(R.id.imageView9));
                images.add(findViewById(R.id.imageView12));
                images.add(findViewById(R.id.imageView26));
                break;
            default:
                Toast.makeText(StartActivity.this,"歌曲中出现未知和弦！",Toast.LENGTH_SHORT).show();
                break;
        }

        switch (beat % 100){
            case 0:
                break;
            case 1:
                images.add(findViewById(R.id.imageView32));
                break;
            case 2:
                images.add(findViewById(R.id.imageView31));
                break;
            case 3:
                images.add(findViewById(R.id.imageView30));
                break;
            case 4:
                images.add(findViewById(R.id.imageView29));
                break;
            case 5:
                images.add(findViewById(R.id.imageView28));
                break;
            case 6:
                images.add(findViewById(R.id.imageView27));
                break;
            case 7:
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView28));
                break;
            case 8:
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView27));
                break;
            case 9:
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 10:
                images.add(findViewById(R.id.imageView32));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView28));
                break;
            case 11:
                images.add(findViewById(R.id.imageView31));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView28));
                break;
            case 12:
                images.add(findViewById(R.id.imageView30));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView28));
                break;
            case 13:
                images.add(findViewById(R.id.imageView32));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView27));
                break;
            case 14:
                images.add(findViewById(R.id.imageView31));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView27));
                break;
            case 15:
                images.add(findViewById(R.id.imageView30));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView27));
                break;
            case 16:
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView27));
                break;
            case 17:
                images.add(findViewById(R.id.imageView32));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 18:
                images.add(findViewById(R.id.imageView31));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 19:
                images.add(findViewById(R.id.imageView30));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 20:
                images.add(findViewById(R.id.imageView32));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 21:
                images.add(findViewById(R.id.imageView31));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 22:
                images.add(findViewById(R.id.imageView30));
                images.add(findViewById(R.id.imageView28));
                images.add(findViewById(R.id.imageView29));
                images.add(findViewById(R.id.imageView27));
                break;
            case 23:
                images.add(findViewById(R.id.imageView36));
                break;
            case 24:
                images.add(findViewById(R.id.imageView37));
                break;
            case 25:
                images.add(findViewById(R.id.imageView38));
                break;
            case 26:
                images.add(findViewById(R.id.imageView39));
                break;
            case 27:
                images.add(findViewById(R.id.imageView40));
                break;
            case 28:
                images.add(findViewById(R.id.imageView41));
                break;
            case 29:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView36));
                break;
            case 30:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView37));
                break;
            case 31:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView38));
                break;
            case 32:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView39));
                break;
            case 33:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView40));
                break;
            case 34:
                images.add(findViewById(R.id.imageView33));
                images.add(findViewById(R.id.imageView41));
                break;
            case 35:
                images.add(findViewById(R.id.imageView42));
                break;
            case 36:
                images.add(findViewById(R.id.imageView43));
                break;
            default:
                Toast.makeText(StartActivity.this,"歌曲中出现未知演奏手法！",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}