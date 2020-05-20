package highfael.carteiradeclientes;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import highfael.carteiradeclientes.database.DadosOpenHelper;
import highfael.carteiradeclientes.entidades.Cliente;
import highfael.carteiradeclientes.repositorio.ClienteRepositorio;

public class ActMain extends AppCompatActivity {

    private RecyclerView  lstDados;
    private FloatingActionButton fab;
    private ConstraintLayout layoutContentMain;

    private SQLiteDatabase conexao;

    private DadosOpenHelper dadosOpenHelper;

    private ClienteRepositorio clienteRepositorio;

    private ClienteAdapter clienteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);  // SETTA o layout do act main pelo nome na pasta layout
        Toolbar toolbar = findViewById(R.id.toolbar); // a barra no superior da página
        setSupportActionBar(toolbar);


        fab = findViewById(R.id.fab);
        lstDados = (RecyclerView)findViewById(R.id.lstDados);

        layoutContentMain = (ConstraintLayout)findViewById(R.id.layoutContentMain);

        criarConexao();


        lstDados.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lstDados.setLayoutManager(linearLayoutManager);

        clienteRepositorio = new ClienteRepositorio(conexao);

        List<Cliente> dados = clienteRepositorio.buscarTodos();

        clienteAdapter = new ClienteAdapter(dados);

        lstDados.setAdapter(clienteAdapter);

    }


    private void criarConexao(){  //método que testsa conexao com banco dados

        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentMain, "Sucesso", Snackbar.LENGTH_SHORT)

                .setAction("OK", null).show();


        }catch (SQLException ex){

            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Erro");
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("ok",null);
            dlg.show();


        }


    }


    public void cadastrar(View view){ // Método que chama a tela de cadastrp

        Intent it = new Intent(ActMain.this, ActCadCliente.class); //objeto it que tem como parametro  quem chama e quem  abre
        startActivityForResult(it,0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        List<Cliente> dados = clienteRepositorio.buscarTodos();

        clienteAdapter = new ClienteAdapter(dados);
        lstDados.setAdapter(clienteAdapter);


    }
}
