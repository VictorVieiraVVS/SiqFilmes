package com.victorvieira.torkflix.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.victorvieira.torkflix.R;
import com.victorvieira.torkflix.helper.ConfiguracaoFirebase;
import com.victorvieira.torkflix.helper.UsuarioFirebase;
import com.victorvieira.torkflix.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar, botaoCadastrarVoltar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();


        //Botao Voltar para o menu
        progressBar.setVisibility(View.GONE);
        botaoCadastrarVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            }
        });

        //Cadastrar usuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Recuperando os campos
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //Verificando se os campos estão preenchidos
                //SENAO estiver preenchidos, retornara um aviso para o usuario preencher
                if ( !textoNome.isEmpty() ){
                    if ( !textoEmail.isEmpty() ){
                        if ( !textoSenha.isEmpty() ){

                            //Instanciando o usuario
                            usuario = new Usuario();

                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            cadastrar( usuario );


                        }else{
                            Toast.makeText(CadastroActivity.this,"Preencha a senha", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(CadastroActivity.this,"Preencha o Email", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(CadastroActivity.this,"Preencha o nome", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    //Método responsavel por cadastrar usuarios com e-mail e senha e fazer validações ao fazer o cadastro
    public void cadastrar(final Usuario usuario ){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Testando se foi salvo com sucesso
                        if ( task.isSuccessful() ){

                            try {

                                progressBar.setVisibility(View.GONE);

                                //Salvar os dados no Firebase
                                String idUsuario = task.getResult().
                                                        getUser().getUid();
                                usuario.setId( idUsuario );
                                usuario.salvar();

                                //Salvar dados no profile Firebase
                                UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                                //Mensagem de cadastro com sucesso
                                Toast.makeText(CadastroActivity.this, "Cadastro com sucesso",
                                        Toast.LENGTH_SHORT).show();

                                //Levando o usuário para dentro do app
                                startActivity( new Intent(getApplicationContext(), MainActivity.class ));
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }



                        }else{

                            progressBar.setVisibility(View.GONE);

                            //Tratando os erros que pode ocorrer
                            String erroExcecao = "";
                            try {

                                throw task.getException();

                            }catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e ){
                                erroExcecao = "Por favor, digite um e-mail válido!";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Essa conta já foi cadastrada!";
                            }catch (Exception e){
                                erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this, "Erro: "
                                    + erroExcecao , Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    public void inicializarComponentes(){

        campoNome = findViewById(R.id.etCadastroNome);
        campoSenha = findViewById(R.id.etCadastroSenha);
        campoEmail= findViewById(R.id.etCadastroEmail);
        botaoCadastrar = findViewById(R.id.btnCadastrar);
        progressBar = findViewById(R.id.progressCadastro);
        botaoCadastrarVoltar = findViewById(R.id.btnCadastrarVoltar);

        campoNome.requestFocus();


    }

}
