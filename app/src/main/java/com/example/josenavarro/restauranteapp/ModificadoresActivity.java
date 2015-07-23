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
import android.widget.ListAdapter;
import android.widget.ListView;


public class ModificadoresActivity extends ActionBarActivity {
    String IdCategoria;
    clsGlobal glo = new clsGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificadores);
        Bundle datos = this.getIntent().getExtras();
        IdCategoria = datos.getString("Categoria");
        CargarModificadoresCategoria(IdCategoria);
    }

    public void CargarModificadoresCategoria(String _IdCategoria) {
        final ListView gridModificadores = (ListView) findViewById(R.id.gridModificadores);
        int c = 0;
        for (int i = 0; i < glo.lMod.length; i++) {
            if (glo.lMenu[i].equals(_IdCategoria)) {
                c = c + 1;
            }
        }

        final String tempListaModificadoresId[] = new String[c];
        String tempListaModificadores[] = new String[c];
        if (tempListaModificadores != null) {
            c = 0;
            for (int i = 0; i < glo.lMod.length; i++) {
                if (glo.lMod[i].categoria.equals(_IdCategoria)) {
                    tempListaModificadoresId[c] = glo.lMod[i].codigo.toString();
                    tempListaModificadores[c] = glo.lMod[i].descripcion.toString();
                    c = c + 1;
                }
            }

            if (tempListaModificadores.length == 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(gridModificadores.getContext(), android.R.layout.simple_list_item_1, glo.ListaTodosModificadores);
                gridModificadores.setAdapter(adapter);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(gridModificadores.getContext(), android.R.layout.simple_list_item_1, tempListaModificadores);
                gridModificadores.setAdapter(adapter);
            }

            GridView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String cad = (String) gridModificadores.getAdapter().getItem(position);

                    Intent mod = new Intent();
                    mod.setData(Uri.parse(cad));
                    setResult(RESULT_OK, mod);
                    finish();
                }
            };
            gridModificadores.setOnItemClickListener(onItemClickListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modificadores, menu);
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
}
