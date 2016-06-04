package com.example.josenavarro.restauranteapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * Created by Jose Navarro on 27/04/2015.
 */
public class ComandaActivity extends ActionBarActivity {
    clsGlobal glo = new clsGlobal();

    protected void onCreate(Bundle savedInstanceState) {
        glo.ListaComanda = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.action_bar);
        MuestraSalones();
        ImageButton imageButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spActualizar();

            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);


    }

    protected void onRestart() {
        super.onRestart();
        spActualizar();

    }

    private void spActualizar() {
        Intent refresh = new Intent(ComandaActivity.this, ComandaActivity.class);
        startActivity(refresh);//Start the same Activity
        finish(); //finish Activity.

    }

    public void MuestraSalones() {
              glo.cargandoSalones = true;
              TabHost tabs = (TabHost) findViewById(R.id.tabhost);
              tabs.setup();
              if (glo.SalonesNombre  != null) {
                  int pivot = clsGlobal.posSalon;
                 for (int i = 0; i < glo.SalonesNombre.length; i++) {
                                TabHost.TabSpec tSpecSalon = tabs.newTabSpec(glo.SalonesID[i].toString());
                                tSpecSalon.setIndicator(glo.SalonesNombre[i].toString());
                                tSpecSalon.setContent(new TabContent(getBaseContext()));
                                tabs.addTab(tSpecSalon);
                 }
                  TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
                                @Override
                                public void onTabChanged(String tabId) {

                                        CargarMesas(tabId);
                                 }
                            };
                            tabs.setOnTabChangedListener(tabChangeListener);
                            tabs.setCurrentTab(0);
                            tabs.setCurrentTab(1);
                            tabs.setCurrentTab(pivot);
                            tabs.setCurrentTab(0);
                            tabs.setCurrentTab(1);
                            tabs.setCurrentTab(pivot);
                            glo.cargandoSalones = false;
                            CargarMesas(glo.SalonesID[0].toString());

                        } else {
                            Toast.makeText(ComandaActivity.this, "Problema de conexion", Toast.LENGTH_LONG).show();
                        }

    }
    public void CargarMesas(final String idGrupoMesa) {

        while (clsGlobal.llamadaEnCurso) {

        }
        if (glo.cargandoSalones == false){


        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        glo.posSalon = tabs.getCurrentTab();
        clsGlobal.llamadaEnCurso = true;
        Thread hilo = new Thread() {
            ListView listM = (ListView) findViewById(R.id.listMesa);
            public Mesas mesa[];
            public String ListaMesa[];

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarMesas";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarMesas";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_IdGrupoMesa", idGrupoMesa);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {

                    transporte.call(SOAP_ACTION, envelope);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);

                    SoapObject filas = (SoapObject) resul2.getProperty(0);

                    mesa = new Mesas[filas.getPropertyCount()];
                    ListaMesa = new String[filas.getPropertyCount()];

                    for (int i = 0; i < filas.getPropertyCount(); i++) {
                        SoapObject columnas = (SoapObject) filas.getProperty(i);
                        ListaMesa[i] = columnas.getProperty(1).toString();
                        mesa[i] = new Mesas(columnas.getProperty(0).toString(), columnas.getProperty(1).toString(), columnas.getProperty(3).toString());

                    }
                    clsGlobal.llamadaEnCurso=false;
                } catch (IOException e) {
                    e.printStackTrace();
                    clsGlobal.llamadaEnCurso=false;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    clsGlobal.llamadaEnCurso=false;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mesa != null) {

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(listM.getContext(), android.R.layout.simple_list_item_1, ListaMesa) {
                                @Override
                                public View getView(int position, View convertView,
                                                    ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                    Log.d("Login","Cargando Mesa..");
                                    if (clsGlobal.currentMesa != null) {

                                        if (mesa[position].Id.equals(clsGlobal.currentMesa.Id)) {

                                            mesa[position] = clsGlobal.currentMesa;
                                        }

                                    } else {

                                    }
                                    if (mesa[position].Estado.equals("0")) {
                                        textView.setTextColor(Color.DKGRAY);

                                    } else if (mesa[position].Estado.equals("1")) {
                                        textView.setTextColor(Color.parseColor("#088A29"));
                                    } else if (mesa[position].Estado.equals("2")) {
                                        textView.setTextColor(Color.parseColor("#0404B4"));
                                    } else if (mesa[position].Estado.equals("3")) {
                                        textView.setTextColor(Color.parseColor("#086A87"));
                                    } else if (mesa[position].Estado.equals("4")) {
                                        textView.setTextColor(Color.parseColor("#8A0808"));
                                    }



                                    return view;
                                }
                            };


                            listM.setAdapter(adapter);
                            ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (mesa[position].Estado.equals("0") || mesa[position].Estado.equals("1")) {
                                        ListAdapter adap = (ListAdapter) parent.getAdapter();
                                        parent.getContext();
                                        Intent menuForm = new Intent(ComandaActivity.this, MenuActivity.class);
                                        clsGlobal.currentMesa = mesa[position];
                                        menuForm.putExtra("Mesa", adap.getItem(position).toString());
                                        menuForm.putExtra("IdMesa", mesa[position].Id);
                                        menuForm.putExtra("EstadoMesa", mesa[position].Estado);
                                        startActivity(menuForm);
                                        mesa[position].spSetEstado("4");//Bloquea la mesa

                                    } else {
                                        Toast.makeText(ComandaActivity.this, "La mesa esta siendo usada por otro usuario", Toast.LENGTH_LONG).show();

                                    }
                                }
                            };
                            listM.setOnItemClickListener(onItemClickListener);
                        } else {
                            Toast.makeText(ComandaActivity.this, "Problema de conexion", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }

        };

        hilo.start();
    }
    }

}
