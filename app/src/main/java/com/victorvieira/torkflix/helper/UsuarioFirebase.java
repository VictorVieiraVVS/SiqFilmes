package com.victorvieira.torkflix.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.victorvieira.torkflix.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }

    //Recupera o ID do usuario
    public static String getIdentificadorUsuario(){

        return getUsuarioAtual().getUid();
    }

    //Método para atualizar o nome do usuário
    public static void atualizarNomeUsuario( String nome ) {

        //Atualizar os dados do usuário
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if ( !task.isSuccessful()){
                        Log.d( "Perfil", "Erro ao atualizar nome do perfil!");
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Método para atualizar a foto do usuário
    public static void atualizarFotoUsuario(StorageReference url ) {

        //Atualizar os dados do usuário
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri( Uri.parse(String.valueOf(url)) )  //Uri.parse(String.valueOf(url))... era pra ser URL apenas
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if ( !task.isSuccessful()){
                        Log.d( "Perfil", "Erro ao atualizar a foto de perfil!");
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Usuario getDadosUsuarioLogado(){

        //Pegar os dados
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName() );
        usuario.setId( firebaseUser.getUid() );

        if ( firebaseUser.getPhotoUrl() == null ){

            usuario.setCaminhoFoto("");

        }else{

            usuario.setCaminhoFoto( firebaseUser.getPhotoUrl().toString() );

        }

        return usuario;

    }

}
