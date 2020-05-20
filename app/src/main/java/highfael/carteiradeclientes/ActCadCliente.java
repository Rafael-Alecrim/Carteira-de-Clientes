package highfael.carteiradeclientes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import highfael.carteiradeclientes.database.DadosOpenHelper;
import highfael.carteiradeclientes.entidades.Cliente;
import highfael.carteiradeclientes.repositorio.ClienteRepositorio;

public class ActCadCliente extends AppCompatActivity {
    FloatingActionButton criarPdf;

    private TextView nomeTxt;
    private TextView enderecoTxt;
    private TextView emailTxt;
    private TextView telefoneTxt;


    private EditText edtNome;
    private EditText edtEndereco;
    private EditText edtEmail;
    private EditText edtTelefone;
    private ConstraintLayout layoutContentActCadCliente;

    private ClienteRepositorio clienterepositorio;

    private SQLiteDatabase conexao;

    private DadosOpenHelper dadosOpenHelper;

    private Cliente cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {  //MÉTODO CHAMADO QUANDO O ACTIVITY É INICIADO
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_cliente);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //  REFERENCIA O CHAMADO APÓS O SETCONTENTVIE SEMPRE
        Toolbar toolbar = findViewById(R.id.toolbar);   // POR EXEMPLO AQUI É C REFERENCIAOD O COMPONENTE TOOLBAR
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        criarPdf = (FloatingActionButton) findViewById(R.id.action_salvaPdf);

       nomeTxt = (TextView)findViewById(R.id.txtNome);
       enderecoTxt = (TextView)findViewById(R.id.txtEndereco);
       emailTxt = (TextView)findViewById(R.id.txtEmail);
       telefoneTxt = (TextView)findViewById(R.id.txtTelefone);


        edtNome = (EditText) findViewById(R.id.edtNome); // AQUI FAZEMOS UM CACHE COM TIPO QUE VOU UTILIZAR UTILIZANDO MÉTODO F
        edtEndereco = (EditText)findViewById(R.id.edtEndereco) ;
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);

        layoutContentActCadCliente  = (ConstraintLayout) findViewById(R.id.layoutContentActCadCliente);



        criarConexao();
        verificaParametro();
        createPDF();



    }

    public void createPDF() {

        criarPdf.setOnClickListener(new View.OnClickListener(){ // DEFINE EVENTO AO CLICAR NO BOTÃO
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)   //API REQUERIDA  PARA TRABALHAR COM PDF
            @Override
            public void onClick(View view){

                PdfDocument myPdfDocument = new PdfDocument(); // INSTACIA UM OBJETO DA CLASSE PdfDocument
                Paint myPaint = new Paint(); //INSTANCIA UM OBJETO DA CLASSE PAINT

                PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(450, 300, 1).create();
                PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);

                Canvas canvas  = myPage1.getCanvas();


                canvas.drawText(nomeTxt.getText().toString()+":  "+edtNome.getText().toString(), 25,80, myPaint); // Pega o vlor dentro do objeto
                canvas.drawText(enderecoTxt.getText().toString()+":  "+edtEndereco.getText().toString(), 25,110, myPaint);
                canvas.drawText(emailTxt.getText().toString()+":  "+edtEmail.getText().toString(), 25,140, myPaint);
                canvas.drawText(telefoneTxt.getText().toString()+":  "+edtTelefone.getText().toString(), 25,170, myPaint);
                myPdfDocument.finishPage(myPage1);
                File file = new File(Environment.getExternalStorageDirectory(),"Info "+edtNome.getText().toString()+".pdf");

                try {
                    myPdfDocument.writeTo(new FileOutputStream(file));

                    Toast.makeText(getApplicationContext(), "PDF Criado Com Sucesso",Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao criar PDF",Toast.LENGTH_LONG).show();
                }

                myPdfDocument.close();
            }
        });

    }











    private void verificaParametro(){

        Bundle bundle = getIntent().getExtras();

        cliente = new Cliente();

        if  ( (bundle != null) && (bundle.containsKey("CLIENTE")) ){


            cliente = (Cliente) bundle.getSerializable("CLIENTE");

            edtNome.setText(cliente.nome);
            edtEndereco.setText(cliente.endereco);
            edtEmail.setText(cliente.email);
            edtTelefone.setText(cliente.telefone);



        }

    }

    private void criarConexao(){  //método que testsa conexao com banco dados

        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentActCadCliente, "Sucesso", Snackbar.LENGTH_SHORT)

                    .setAction("OK", null).show();

            clienterepositorio = new ClienteRepositorio(conexao);


        }catch (SQLException ex){

            androidx.appcompat.app.AlertDialog.Builder dlg = new androidx.appcompat.app.AlertDialog.Builder(this);
            dlg.setTitle("Erro");
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("ok",null);
            dlg.show();


        }


    }

    private void confirmar(){


        if(validaCampos() == false){

            try{

                if (cliente.codigo == 0) {
                    clienterepositorio.inserir(cliente);
                }else{

                    clienterepositorio.alterar (cliente);
                }

                finish();

            }catch (SQLException ex){

                androidx.appcompat.app.AlertDialog.Builder dlg = new androidx.appcompat.app.AlertDialog.Builder(this);
                dlg.setTitle("Erro");
                dlg.setMessage(ex.getMessage());
                dlg.setNeutralButton("ok",null);
                dlg.show();

            }

        }

    }

    private boolean validaCampos (){ // valida campos  convertendo pra uma string

        boolean res = false;

        String nome = edtNome.getText().toString();
        String endereco = edtEndereco.getText().toString();
        String email = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();

        cliente.nome     = nome;
        cliente.endereco = endereco;
        cliente.email    = email;
        cliente.telefone = telefone;


        if (res = isCampoVazio(nome)) {
            edtNome.requestFocus();
        } else if (res = isCampoVazio(endereco)) {
            edtEndereco.requestFocus();
        } else if (res = !isEmailValido(email)) {
            edtEmail.requestFocus();
        } else if (res = isCampoVazio(telefone)) {
            edtTelefone.requestFocus();
        }

        if (res) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_aviso);
            builder.setMessage(R.string.message_campo_invalido_ou_ou_embranco);
            builder.setNeutralButton(R.string.lbl_ok, null);
            builder.show();
        }

        return res;

    }

     private boolean isCampoVazio(String valor) {

        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
    }

    /*private boolean isCampoVazio(String valor){ /// FUNÇÃO VERIFICA SE O CAMPO ESTÁ VAZIO  OU CONTÉM ESPAÇOS

       boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty() );
       return resultado;
    }*/

    private boolean isEmailValido(String email) {
        return (!isCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /*private boolean isEmailValido(String email){ //  VALIDA SE O EMAIL É VALIDOU  E CAMPO VAZIO

        boolean resultado = (!isCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        return resultado;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();    /// AQUI INSTANCIA A CLASSE  MENUINFLATER PARA MOSTRAR O MENU EM CIMA
        inflater.inflate(R.menu.menu_ac_cad_cliente,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //AQUI  PARA RECEBER A OPÇÃO DESEJADA OK OU CANCELAR



        int id = item.getItemId();  //VARIAVEL QUE ARMAZENA  VALOR NA VARIAVEL ID, O VALOR QUE FOI CLICADO OK OU CANCELAR
                        // ESTA ERRADO, POIS DEVE SER item.getItemId
        switch (id){

            case android.R.id.home:

                finish();

                break;

            case R.id.action_ok:

                confirmar();

                break;

            case R.id.action_excluir:

                clienterepositorio.excluir(cliente.codigo);
                finish();
                Toast.makeText(this,"Excluido  com sucesso", Toast.LENGTH_SHORT).show();

                break;


        }

        return super.onOptionsItemSelected(item);
    }
}
