package com.websarva.wings.android.asyncsample;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Gmail extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gmail);

        }

        public void onClick(View view) {
            EditText edit1=(EditText)findViewById(R.id.editTextTextEmailAddress2);
            String msg=edit1.getText().toString();
            EditText edit2=(EditText)findViewById(R.id.editTextTextEmailAddress3);
            String msg2=edit2.getText().toString();
            EditText edit3=(EditText)findViewById(R.id.editTextTextEmailAddress4);
            String msg3=edit3.getText().toString();
            EditText edit4=(EditText)findViewById(R.id.editTextTextEmailAddress5);
            String msg4=edit4.getText().toString();
            EditText edit5=(EditText)findViewById(R.id.editTextTextEmailAddress6);
            String msg5=edit5.getText().toString();
            Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{msg,msg2,msg3,msg4,msg5});
            intent.putExtra(Intent.EXTRA_SUBJECT,"(用件)");
            intent.putExtra(Intent.EXTRA_TEXT,"本文：\n");
            startActivity(intent);
        }
}
