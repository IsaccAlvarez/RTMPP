package com.example.josenavarro.restauranteapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.net.URI;


public class AcompanamientoActivity extends ActionBarActivity {
clsGlobal glo = new clsGlobal();
    String IdCategoria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acompanamiento);
        Bundle datos = this.getIntent().getExtras();
        IdCategoria=datos.getString("Categoria");
        CargarAcompanamientoCategoria(IdCategoria);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acompanamiento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     public  void CargarAcompanamientoCategoria (String _IdCategoria){
        final GridView gridAco =(GridView)findViewById(R.id.gridAcompa);
        int c = 0;
        for (int i = 0; i < glo.lAcom.length; i++) {
            if (glo.lAcom[i].codigo.equals(_IdCategoria)) {
                c = c + 1;
            }
        }
        String tempListaAcompa[] = new String[c];
        if(tempListaAcompa != null) {

            c = 0;
            for (int i = 0; i < glo.lAcom.length; i++) {
                if (glo.lAcom[i].codigo.equals(_IdCategoria) ) {
                    tempListaAcompa[c] = glo.lAcom[i].descripcion.toString();
                    c = c + 1;
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(gridAco.getContext(), android.R.layout.simple_list_item_1,tempListaAcompa);
            gridAco.setAdapter(adapter);

            GridView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String cad =(String)gridAco.getAdapter().getItem(position);
                    Intent mod = new Intent();
                    mod.setData(Uri.parse(cad));
                    setResult(RESULT_OK,mod);
                    finish();
                }
            };
            gridAco.setOnItemClickListener(onItemClickListener);
        }
    }
}
