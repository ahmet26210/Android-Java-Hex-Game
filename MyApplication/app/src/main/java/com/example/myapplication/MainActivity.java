package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {
    RadioButton button1,button2,button3,button4,button5,button6,button7,button8;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=(RadioButton)findViewById(R.id.radioButton);
        button2=(RadioButton)findViewById(R.id.radioButton1);
        button3=(RadioButton)findViewById(R.id.radioButton2);
        button4=(RadioButton)findViewById(R.id.radioButton3);
        button5=(RadioButton)findViewById(R.id.radioButton4);
        button6=(RadioButton)findViewById(R.id.radioButton5);
        button7=(RadioButton)findViewById(R.id.radioButton6);
        button8=(RadioButton)findViewById(R.id.radioButton7);
        button3.setVisibility(INVISIBLE);
        button4.setVisibility(INVISIBLE);
        button5.setVisibility(INVISIBLE);
        button6.setVisibility(INVISIBLE);
        button2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button3.setVisibility(VISIBLE);
                button4.setVisibility(VISIBLE);
                button5.setVisibility(VISIBLE);
                button6.setVisibility(VISIBLE);
            }
        });
        button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button3.setVisibility(INVISIBLE);
                button4.setVisibility(INVISIBLE);
                button5.setVisibility(INVISIBLE);
                button6.setVisibility(INVISIBLE);
            }
        });
    }
    public void launchgame(View v){
        Intent i=new Intent(this,Gamestart.class);
        String input=((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString();
        size = Integer.valueOf(input);
        if(button1.isChecked()){
            gametype=false;
        }
        if(button2.isChecked()){
            gametype=true;
        }
        if (button7.isChecked()){
            whofirst=false;
        }
        if (button8.isChecked()){
            whofirst=true;
        }
        i.putExtra("game_size",size);
        i.putExtra("game_type",gametype);
        if (button3.isChecked()) level=1;
        if (button4.isChecked()) level=2;
        if (button5.isChecked()) level=3;
        if (button6.isChecked()) level=4;
        i.putExtra("level_game",level);
        i.putExtra("whofirst",whofirst);
        startActivity(i);
    }
    private int size;
    private boolean gametype,whofirst;
    private int level;
}