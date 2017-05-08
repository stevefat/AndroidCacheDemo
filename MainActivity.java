package com.stevefat.cachdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button write_txt, read_txt;
    private TextView tv;

    CachManager cachManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cachManager = CachManager.getInstance(this);

        editText= (EditText) findViewById(R.id.edit_txt);
        tv = (TextView) findViewById(R.id.tv);


        findViewById(R.id.write_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                cachManager.writeCache("key_demo",str);
                tv.setText(cachManager.readCache("key_demo"));
            }
        });
        findViewById(R.id.read_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(cachManager.readCache("key_demo"));
            }
        });

        findViewById(R.id.del_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cachManager.remove("key_demo");
                tv.setText(cachManager.readCache("key_demo"));
            }
        });









    }
}
