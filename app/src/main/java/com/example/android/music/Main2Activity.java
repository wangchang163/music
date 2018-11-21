package com.example.android.music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.music.core.IConstants;
import com.example.android.music.service.MediaServiceManager;


public class Main2Activity extends AppCompatActivity {

    private MediaServiceManager manager;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        text=findViewById(R.id.text);
        manager=MediaServiceManager.getInstance(getApplicationContext());
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.getPlayState()== IConstants.MPS_PLAYING){
                    manager.pause();
                    text.setText("播放");

                }else if (manager.getPlayState()==IConstants.MPS_PAUSE){
                    manager.rePlay();
                    text.setText("暂停");
                }
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
