package com.websarva.wings.android.asyncsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText first;
    private EditText second;
    private EditText third;
    private EditText fourth;
    private EditText fifth;

    private String[] place = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        first = findViewById(R.id.editText1);
        second = findViewById(R.id.editText2);
        third = findViewById(R.id.editText3);
        fourth = findViewById(R.id.editText4);
        fifth = findViewById(R.id.editText5);
        button = findViewById(R.id.button4);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place[0] = first.getText().toString();
                place[1] = second.getText().toString();
                place[2] = third.getText().toString();
                place[3] = fourth.getText().toString();
                place[4] = fifth.getText().toString();


                Intent intent = new Intent(MainActivity.this , CheckPosition.class);
                intent.putExtra("place_info",place);

                startActivity(intent);

            }
        });
    }
}
