package com.example.guitarassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class EditActivity extends AppCompatActivity {

    private Button backButton, developerButton, normalButton;
    private EditText newSongName;
    private ScrollView content;

    private int currentMode;//0代表一般模式，1代表开发者模式

    public int songId;
    public LinkedList<Integer> songBeats;

    private LinkedList<Spinner> spinners1;
    private LinkedList<Spinner> spinners2;
    private LinkedList<Spinner> spinners3;
    private LinkedList<ImageButton> addButtons;
    private LinkedList<ImageButton> deleteButtons;

    private int btnIDIndex;

    private ImageButton hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        newSongName = (EditText) findViewById(R.id.edit_newName);

        songId = getIntent().getIntExtra("songId", 0);
        newSongName.setText(getIntent().getStringExtra("songName"));
        ArrayList<Integer> arrayList = getIntent().getIntegerArrayListExtra("songBeats");
        songBeats = new LinkedList<>(arrayList);

        developerButton = (Button) findViewById(R.id.e_developer);
        developerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerMode();
            }
        });

        normalButton = (Button) findViewById(R.id.e_normal);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalMode();
            }
        });

        backButton = (Button) findViewById(R.id.e_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        hintButton = (ImageButton)findViewById(R.id.editHint);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditActivity.this)
                        .setTitle("使用说明：")
                        .setMessage("1.一般模式下，点击加号在其后添加节拍，点击“垃圾桶”删除歌曲；\n" +
                                "2.一般模式下，每个节拍由时长、左手动作（单弦或和弦）、右手动作构成；\n" +
                                "3.一般模式和开发者模式可自动相互转换；\n" +
                                "4.开发者模式推荐复制粘贴而非随意输入。\n" +
                                "5.！！！歌曲编辑完成后请点击“保存并返回”，否则此次编辑不会保存！！！")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

        content = (ScrollView) findViewById(R.id.edit_content);

        currentMode = 0;
        showNormalMode();
    }

    public void back() {
        String songName = newSongName.getText().toString();
        if (songName.equals("")) {
            Toast.makeText(EditActivity.this, "请输入歌曲名", Toast.LENGTH_SHORT).show();
        } else if (songName.contains("#")) {
            Toast.makeText(EditActivity.this, "歌曲名请不要出现#", Toast.LENGTH_SHORT).show();
        } else if (songName.contains("`")) {
            Toast.makeText(EditActivity.this, "歌曲名请不要出现`", Toast.LENGTH_SHORT).show();
        } else {
            if (currentMode == 0)
                endNormalMode();
            else if (currentMode == 1)
                endDevelopMode();
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            intent.putExtra("songName", songName);
            ArrayList<Integer> arrayList = new ArrayList<>(songBeats);
            intent.putExtra("songBeats", arrayList);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public void developerMode() {
        if (currentMode == 0) {
            currentMode = 1;
            endNormalMode();
            showDevelopMode();
        }
    }

    public void endNormalMode() {
        @SuppressLint("ResourceType") LinearLayout linearLayout = (LinearLayout) content.findViewById(1);
        content.removeView(linearLayout);

        songBeats.clear();
        for(int i = 0; i < spinners1.size(); i++){
            int a = spinners1.get(i).getSelectedItemPosition();
            int b = spinners2.get(i).getSelectedItemPosition();
            int c = spinners3.get(i).getSelectedItemPosition();
            songBeats.add(a * 100000 + b * 100 + c);
        }
    }

    public void showDevelopMode() {
        String beats = "";
        for (int x : songBeats) {
            beats = beats + Integer.toString(x) + "`";
        }

        EditText editText = new EditText(EditActivity.this);
        LinearLayout.LayoutParams editTextParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(editTextParam);
        editText.setBackgroundColor(Color.WHITE);
        editText.setText(beats);
        editText.setTextColor(Color.BLACK);
        editText.setId(0);
        content.addView(editText);
    }

    public void normalMode() {
        if (currentMode == 1) {
            currentMode = 0;
            endDevelopMode();
            showNormalMode();
        }
    }

    public void endDevelopMode() {
        EditText editText = (EditText) content.findViewById(0);
        String beats = editText.getText().toString();
        content.removeView(editText);
        songBeats.clear();
        String strArray[] = beats.split("`");
        for (String x : strArray) {
            if (!x.equals("")) {
                songBeats.add(Integer.parseInt(x));
            }
        }
    }

    @SuppressLint("ResourceType")
    public void showNormalMode() {

        spinners1 = new LinkedList<>();
        spinners2 = new LinkedList<>();
        spinners3 = new LinkedList<>();
        addButtons = new LinkedList<>();
        deleteButtons = new LinkedList<>();

        btnIDIndex = 10;

        LinearLayout linearLayout = new LinearLayout(EditActivity.this);
        linearLayout.setId(1);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearLayoutParam);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        content.addView(linearLayout);

        if (songBeats.size() == 0) {
            showBigAdd();
        } else {
            int iIndex = 0;
            for (int beat : songBeats) {
                addBeat(iIndex, beat);
                iIndex += 1;
            }
        }
    }

    @SuppressLint("ResourceType")
    public void showBigAdd(){
        ImageButton addFirstBeat = new ImageButton(EditActivity.this);
        LinearLayout.LayoutParams btnAddParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnAddParam.gravity = Gravity.CENTER;
        addFirstBeat.setLayoutParams(btnAddParam);
        addFirstBeat.setBackgroundResource(R.drawable.ic_addfirstbeat);
        addFirstBeat.setId(3);
        LinearLayout linearLayout = (LinearLayout) content.findViewById(1);
        addFirstBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.removeView(v);
                addBeat(0, 300000);
            }
        });
        linearLayout.addView(addFirstBeat);
    }

    public void addBeat(int Index, int beat) {

        LinearLayout total_layout = new LinearLayout(EditActivity.this);
        LinearLayout.LayoutParams total_layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        total_layoutParams.setMargins(0, 5, 0, 0);
        total_layout.setLayoutParams(total_layoutParams);
        total_layout.setBackgroundColor(Color.argb(255, 162, 205, 90));   // #FFA2CD5A
        total_layout.setPadding(5, 5, 5, 5);
        total_layout.setOrientation(LinearLayout.VERTICAL);

        Spinner spinner1 = new Spinner(EditActivity.this);
        LinearLayout.LayoutParams spinnerParams1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams1.gravity = Gravity.CENTER;
        spinnerParams1.setMargins(0, 5, 0, 0);
        spinner1.setLayoutParams(spinnerParams1);
        String[] array1 = getResources().getStringArray(R.array.array1);
        ArrayAdapter<String> gradeAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, array1);
        spinner1.setAdapter(gradeAdapter1);
        spinner1.setSelection(beat / 100000);
        total_layout.addView(spinner1);
        spinners1.add(Index, spinner1);

        Spinner spinner2 = new Spinner(EditActivity.this);
        LinearLayout.LayoutParams spinnerParams2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams2.gravity = Gravity.CENTER;
        spinnerParams2.setMargins(0, 5, 0, 0);
        spinner2.setLayoutParams(spinnerParams2);
        String[] array2 = getResources().getStringArray(R.array.array2);
        ArrayAdapter<String> gradeAdapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, array2);
        spinner2.setAdapter(gradeAdapter2);
        spinner2.setSelection((beat / 100) % 1000);
        total_layout.addView(spinner2);
        spinners2.add(Index, spinner2);

        Spinner spinner3 = new Spinner(EditActivity.this);
        LinearLayout.LayoutParams spinnerParams3 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams3.gravity = Gravity.CENTER;
        spinnerParams3.setMargins(0, 5, 0, 0);
        spinner3.setLayoutParams(spinnerParams3);
        String[] array3 = getResources().getStringArray(R.array.array3);
        ArrayAdapter<String> gradeAdapter3 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, array3);
        spinner3.setAdapter(gradeAdapter3);
        spinner3.setSelection(beat % 100);
        total_layout.addView(spinner3);
        spinners3.add(Index, spinner3);

        // 3.创建“+”和“-”按钮外围控件RelativeLayout
        RelativeLayout rlBtn = new RelativeLayout(EditActivity.this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParam.setMargins(0, 5, 0, 0);
        rlBtn.setPadding(0, 5, 0, 0);
        rlBtn.setLayoutParams(rlParam);

        // 4.创建“+”按钮
        ImageButton btnAdd = new ImageButton(EditActivity.this);
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 靠右放置
        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnAdd.setLayoutParams(btnAddParam);
        // 设置属性
        btnAdd.setBackgroundResource(R.drawable.ic_add);
        btnAdd.setId(btnIDIndex);
        // 设置点击操作
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == null) {
                    return;
                }
                // 判断第几个“+”按钮触发了事件
                int Index = -1;
                for (int i = 0; i < addButtons.size(); i++) {
                    if (addButtons.get(i) == v) {
                        Index = i;
                        break;
                    }
                }
                addBeat(Index + 1, 300000);
            }
        });
        // 将“+”按钮放到RelativeLayout里
        rlBtn.addView(btnAdd);
        addButtons.add(Index, btnAdd);

        // 5.创建“-”按钮
        ImageButton btnDelete = new ImageButton(EditActivity.this);
        btnDelete.setBackgroundResource(R.drawable.ic_delete);
        RelativeLayout.LayoutParams btnDeleteAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDeleteAddParam.setMargins(0, 0, 5, 0);
        // “-”按钮放在“+”按钮左侧
        btnDeleteAddParam.addRule(RelativeLayout.LEFT_OF, btnIDIndex);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == null) {
                    return;
                }

                // 判断第几个“-”按钮触发了事件
                int iIndex = -1;
                for (int i = 0; i < deleteButtons.size(); i++) {
                    if (deleteButtons.get(i) == v) {
                        iIndex = i;
                        break;
                    }
                }
                if (iIndex >= 0) {
                    spinners1.remove(iIndex);
                    spinners2.remove(iIndex);
                    spinners3.remove(iIndex);
                    addButtons.remove(iIndex);
                    deleteButtons.remove(iIndex);

                    // 从外围llContentView容器里删除第iIndex控件
                    @SuppressLint("ResourceType") LinearLayout linearLayout = (LinearLayout) content.findViewById(1);
                    linearLayout.removeViewAt(iIndex);

                    if(spinners1.size() == 0){
                        showBigAdd();
                    }
                }
            }
        });
        // 将“-”按钮放到RelativeLayout里
        rlBtn.addView(btnDelete, btnDeleteAddParam);
        deleteButtons.add(Index, btnDelete);

        // 6.将RelativeLayout放到LinearLayout里
        total_layout.addView(rlBtn);

        // 7.将layout同它内部的所有控件加到最外围的llContentView容器里

        @SuppressLint("ResourceType") LinearLayout linearLayout = (LinearLayout) content.findViewById(1);
        linearLayout.addView(total_layout, Index);

        btnIDIndex++;
    }
}