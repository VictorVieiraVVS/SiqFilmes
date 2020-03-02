package com.victorvieira.torkflix.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.victorvieira.torkflix.R;

public class PlayerActivity extends AppCompatActivity {

    private VideoView videoView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videoView2 = findViewById(R.id.videoView2);

        //Esconder a statusBar
        View decorView = getWindow().getDecorView();

        //Ocultar componeentes
        int uiOpcoes = View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility( uiOpcoes );

        //Esconder a actionBar
        getSupportActionBar().hide();

        //Executar o video
        videoView2.setMediaController( new MediaController( this ));
        videoView2.setVideoPath( "android.resource://" + getPackageName() + "/" + R.raw.vingadores_guerra_infinita_trailer );
        videoView2.start();
    }
}
