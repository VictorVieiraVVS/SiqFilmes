package com.victorvieira.torkflix.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.victorvieira.torkflix.R;
import com.victorvieira.torkflix.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    //Referencia para autenticacao
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configurações de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Configurar o botao SAIR e PERFIL
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();

                //Ao clicar no botão sair, volta para tela de login
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            case R.id.menu_perfil:
                //Ao clicar no botão Pergil, abre a tela de perfil
                Intent acao2 = new Intent(MainActivity.this, EditarPerfilActivity.class);
                startActivity(acao2);

        }

        return super.onOptionsItemSelected(item);
    }

    //Metodo para deslogar
    private void deslogarUsuario(){
        try {
            autenticacao.signOut();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirVideo(View view) {

        startActivity( new Intent( this, PlayerActivity.class ));

    }

}
