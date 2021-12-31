package com.example.guitarassistant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;

    private Button createButton, deleteButton, startButton, editButton;

    private LinkedList<Button> songButtons;

    private int currentId;

    private Songs songList;

    private ImageButton hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.layout);

        createButton = (Button)findViewById(R.id.create);
        deleteButton = (Button)findViewById(R.id.delete);
        startButton = (Button)findViewById(R.id.start);
        editButton = (Button)findViewById(R.id.edit);

        createButton.setOnClickListener(new Create());
        deleteButton.setOnClickListener(new Delete());
        startButton.setOnClickListener(new Start());
        editButton.setOnClickListener(new Edit());

        songButtons = new LinkedList<>();

        hintButton = (ImageButton)findViewById(R.id.mainHint);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("使用说明：")
                        .setMessage("1.首次下载软件自带三首示例歌曲（可删除）；\n" +
                                "2.点击“添加新歌”创建新的歌曲；\n" +
                                "3.点击歌曲按钮，选中歌曲；\n" +
                                "4.选中歌曲后，可以编辑、播放、删除歌曲。")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

        try {
            FileOutputStream fos = openFileOutput("songList.txt", MODE_APPEND);
            String str = "";
            fos.write(str.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String content="";
        try {
            FileInputStream inStream = openFileInput("songList.txt");
            byte[] buffer = new  byte[inStream.available()];
            inStream.read(buffer);
            if(buffer.length > 0){
                content = new String(buffer);
            }
            if(content.equals("")){
                songList = new Songs("4#" +
                        "1`《小星星》`300302`300302`300004`300004`300204`300204`300004`300000`300303`300303`300203`300203`300003`300003`300302`300000`300004`300004`300303`300303`300203`300203`300303`300004`300004`300303`300303`300203`300203`300303`300302`300302`300004`300004`300204`300204`300004`300000`300303`300303`300203`300203`300003`300003`300302`300000`#" +
                        "2`《小草》`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406705`403103`403104`403105`403106`403101`403104`403105`403106`406702`406704`406705`406706`403103`403104`403105`403106`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406705`403103`403104`403105`403106`403101`403104`403105`403106`403101`403104`403105`403106`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406706`403103`403104`403105`403106`406702`406704`406705`406706`406702`406704`406705`406706`406702`406704`406705`406706`403103`403104`403105`403106`401903`401904`401905`401906`401903`401904`401905`401906`403103`403104`403105`403106`403103`403104`403105`403106`401903`401904`401905`401906`401903`401904`401905`401906`403101`403104`403105`403106`206736`#" +
                        "3`《龙的传人》`306728`406733`406724`305328`405333`405324`300528`400533`400524`306728`406733`406724`306728`406733`406724`305328`405333`405324`300528`400533`400524`302928`402933`402924`306728`406733`406724`305328`405333`405324`300528`400533`400524`306728`406733`406724`305328`405333`405324`305328`405333`405324`306728`406733`406724`306728`406733`406724`300528`400533`400524`300528`400533`400524`305328`405333`405324`305328`405333`405324`306728`406733`406724`306728`406733`406724`305328`405333`405324`305328`405333`405324`300528`400533`400524`300528`400533`400524`305328`405333`405324`305328`405333`405324`300528`400533`400524`302928`402933`402924`306728`406733`406724`306728`406733`406724`#");
            }
            else{
                songList = new Songs(content);
            }
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        show();
    }

    public void creatButton(int _id, String _name){
        Button song = new Button(MainActivity.this);
        LinearLayout.LayoutParams btnCreateParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnCreateParam.setMargins(0,10,0,0);
        song.setLayoutParams(btnCreateParam);
        song.setBackgroundColor(Color.parseColor("#009688"));
        song.setText(_name);
        song.setId(_id);
        song.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentId = v.getId();
                for(int i = 0; i< songButtons.size(); i++)
                {
                    songButtons.get(i).setBackgroundColor(Color.parseColor("#009688"));
                }
                v.setBackgroundColor(Color.parseColor("#2196F3"));
            }
        });

        layout.addView(song,btnCreateParam);
        songButtons.add(song);
    }

    public void show(){
        for(Song x:songList.songs){
            creatButton(x.id, x.name);
        }
    }

    class Create implements View.OnClickListener{

        @Override
        public void onClick(View v){

            creatButton(songList.bId, "新建歌曲");

            songList.addSong();


            try {
                FileOutputStream fos = openFileOutput("songList.txt", MODE_PRIVATE);
                fos.write(songList.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Delete implements View.OnClickListener{

        @Override
        public void onClick(View v){
            if(currentId != 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("确认");
                builder.setMessage("确定删除该歌曲？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Button currentButton = (Button)findViewById(currentId);
                        layout.removeView(currentButton);
                        songButtons.remove(currentButton);

                        songList.deleteSong(currentId);

                        try {
                            FileOutputStream fos = openFileOutput("songList.txt", MODE_PRIVATE);
                            fos.write(songList.toString().getBytes());
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        currentId = 0;

                    }
                });
                builder.setNegativeButton("否", null);
                builder.show();
            }
            else{
                Toast.makeText(MainActivity.this,"请选择一首歌曲！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class Start implements View.OnClickListener{

        @Override
        public void onClick(View v){
            if(currentId != 0){
                Intent intent=new Intent(MainActivity.this,StartActivity.class);
                intent.putExtra("songId",currentId);
                intent.putExtra("songName",songList.getSongName(currentId));
                ArrayList<Integer> arrayList = new ArrayList<>(songList.getSongBeats(currentId));
                intent.putExtra("songBeats",arrayList);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this,"请选择一首歌曲！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class Edit implements View.OnClickListener{

        @Override
        public void onClick(View v){
            if(currentId != 0){
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("songId",currentId);
                intent.putExtra("songName",songList.getSongName(currentId));
                ArrayList<Integer> arrayList = new ArrayList<>(songList.getSongBeats(currentId));
                intent.putExtra("songBeats",arrayList);
                startActivityForResult(intent,0);
            }
            else{
                Toast.makeText(MainActivity.this,"请选择一首歌曲！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String currentName = data.getStringExtra("songName");
                Button currentButton = (Button)findViewById(currentId);
                currentButton.setText(currentName);
                ArrayList<Integer> arrayList = data.getIntegerArrayListExtra("songBeats");
                LinkedList<Integer> currentBeats = new LinkedList<>(arrayList);
                songList.setSong(currentId, currentName, currentBeats);
                try {
                    FileOutputStream fos = openFileOutput("songList.txt", MODE_PRIVATE);
                    fos.write(songList.toString().getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}