package com.victorvieira.torkflix.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.victorvieira.torkflix.R;
import com.victorvieira.torkflix.helper.ConfiguracaoFirebase;
import com.victorvieira.torkflix.helper.UsuarioFirebase;
import com.victorvieira.torkflix.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView ivFotoPerfil;
    private TextView tvAlterarFoto;
    private TextInputEditText etEditarNomePerfil, etEditarEmailPerfil;
    private Button btnSalvarAlteracao;
    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    private static final int SELECAO_GALERIA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //Volta para tela Main
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_preto_24dp);

        //inicializar componentes
        inicializarComponentes();

        //Recuperar os dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        etEditarNomePerfil.setText( usuarioPerfil.getDisplayName() );
        etEditarEmailPerfil.setText( usuarioPerfil.getEmail() );

        Uri url = usuarioPerfil.getPhotoUrl();
        if ( url != null ){

            Glide.with( EditarPerfilActivity.this )
                    .load( url )
                    .into(ivFotoPerfil);
        }else{
            ivFotoPerfil.setImageResource(R.drawable.avatar);
        }

        //Salvar alterações do nome
        btnSalvarAlteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recupera nome atualizado
                String nomeAtualizado = etEditarNomePerfil.getText().toString();

                //Atualizar nome do perfil
                UsuarioFirebase.atualizarNomeUsuario( nomeAtualizado );

                //Atualizar nome no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this,
                        "Dados alterados com sucesso",
                        Toast.LENGTH_SHORT).show();

            }
        });

        //Alterar foto do usuário
        tvAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if ( i.resolveActivity( getPackageManager() ) != null ){

                    startActivityForResult( i, SELECAO_GALERIA );

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {
            Bitmap imagem = null;


            try {
                //Selecionar apenas da galeria
                switch (requestCode) {

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap( getContentResolver(), localImagemSelecionada );

                        break;
                }

                //Caso tenha escolhido uma imagem
                if ( imagem != null ){
                    //Configurar a imagem na tela do usuário
                    ivFotoPerfil.setImageBitmap( imagem );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress( Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef.child( "imagens" )
                                                           .child( "perfil" )
                                                           .child( identificadorUsuario + ".jpeg" );
                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recuperar local da foto
                            StorageReference url = taskSnapshot.getStorage();
                            atualizarFotoUsuario ( url );

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(StorageReference url ){
        //Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario( url );

        //Atualizar a foto no firebase
        usuarioLogado.setCaminhoFoto( url.toString() );
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atualizada!",
                Toast.LENGTH_SHORT).show();

    }

    public void inicializarComponentes(){

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        tvAlterarFoto = findViewById(R.id.tvAlterarFoto);
        etEditarNomePerfil = findViewById(R.id.etEditarNomePerfil);
        etEditarEmailPerfil = findViewById(R.id.etEditarEmailPerfil);
        btnSalvarAlteracao = findViewById(R.id.btnSalvarAlteracao);

        etEditarEmailPerfil.setFocusable( false );


    }

    //Fecha activity Editar Perfil e volta para Perfil
    @Override
    public boolean onSupportNavigateUp() {

        Intent acao = new Intent(EditarPerfilActivity.this, MainActivity.class);
        startActivity(acao);
        EditarPerfilActivity.this.finish();
        return false;
    }
}
