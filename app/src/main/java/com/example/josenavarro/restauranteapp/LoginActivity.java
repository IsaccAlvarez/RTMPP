package com.example.josenavarro.restauranteapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class LoginActivity extends ActionBarActivity {
    clsGlobal glo = new clsGlobal();
    ProgressDialog progressDialog;
    int progreso = 0;
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progreso = progreso + (Integer) msg.obj;
            progressDialog.setProgress(progreso);
            if (progreso == 100) {
                progressDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        System.setProperty("http.keepAlive", "false");
        clsGlobal.ObtenerConfiguracion(this);
        if (!VerificaConexion.verificaConexion(this)) {
            Toast.makeText(getBaseContext(),
                    "Comprueba tu conexión de wifi. Saliendo ... ", Toast.LENGTH_SHORT)
                    .show();
            this.finish();
            return;
        }
        Log.d("Login","Verificando Acceso BD");
        if (!VerificaConexion.verificaAccesoBD()){
            Intent comandaForm = new Intent(LoginActivity.this, ConexionActivity.class);
            startActivity(comandaForm);
            this.finish();
            return;
        }
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Cargando...");
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();

        CargarUsuarioId();
        CargarCategorias();
        CargarMenu();
        CargarModificadores();
        CargarAcompanamientos();
        CargarTodosModificadores();
        CargarSalones();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Salir")
                    .setMessage("¿Desea salir de la aplicación?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.finish();
                        }
                    })
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void LoginOnClick(View v) {
        Thread hilo = new Thread() {
            String res;

            @Override
            public void run() {

                String contra = ((EditText) findViewById(R.id.txClave)).getText().toString().trim();
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnLogin";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnLogin";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_IdUsuario", clsGlobal.IdUsuarioLog);
                sp.addProperty("_Clave", contra);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);

                try {

                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    Log.d("Login", "Validacion");


                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res != null) {
                            if (res.equals("Bienvenido!")) {
                                clsGlobal.GuardarConfiguracion(LoginActivity.this);
                                Intent comandaForm = new Intent(LoginActivity.this, ComandaActivity.class);
                                startActivity(comandaForm);
                            }
                            Toast.makeText(LoginActivity.this, res, Toast.LENGTH_LONG).show();

                        } else {
                            res = "Problema de conexión.";
                            Toast.makeText(LoginActivity.this, res, Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }

        };
        hilo.start();
    }

    public void CargarUsuarioId() {
        Thread th = new Thread() {
            Spinner cbUser = (Spinner) findViewById(R.id.spinUsuario);
            String ListaUsuarios[];
            String ListaId[];

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarIdUsuarios";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarIdUsuarios";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);


                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {

                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 10;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);
                    if (resul2.getPropertyCount() > 0) {
                        SoapObject filas = (SoapObject) resul2.getProperty(0);
                        ListaUsuarios = new String[filas.getPropertyCount()];
                        ListaId = new String[filas.getPropertyCount()];
                        for (int i = 0; i < filas.getPropertyCount(); i++) {
                            SoapObject columnas = (SoapObject) filas.getProperty(i);
                            ListaUsuarios[i] = columnas.getProperty(1).toString();
                            ListaId[i] = columnas.getProperty(0).toString();
                        }
                        Log.d("Login", "Termine de cargarlos");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    Log.d("Login", "Problema de cargarlos");

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ListaUsuarios != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(cbUser.getContext(), android.R.layout.simple_spinner_item, ListaUsuarios);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            cbUser.setAdapter(adapter);
                            cbUser.setSelection(clsGlobal.posUltimoUsuarioLog);
                            cbUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    clsGlobal.posUltimoUsuarioLog = cbUser.getSelectedItemPosition();
                                    clsGlobal.IdUsuarioLog = ListaId[clsGlobal.posUltimoUsuarioLog].toString();
                                    clsGlobal.NombreUsuarioLog = ListaUsuarios[clsGlobal.posUltimoUsuarioLog].toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Login: Problema de conexion", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        th.start();
    }

    public void CargarCategorias() {
        Thread th = new Thread() {

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarCategoria";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarCategoria";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {

                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 20;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);

                    if (resul2.getPropertyCount() > 0) {
                        SoapObject filas = (SoapObject) resul2.getProperty(0);

                        glo.lCategoria = new Categoria[filas.getPropertyCount()];

                        for (int i = 0; i < filas.getPropertyCount(); i++) {
                            SoapObject columnas = (SoapObject) filas.getProperty(i);
                            glo.lCategoria[i] = new Categoria();
                            glo.lCategoria[i].codigo = columnas.getProperty(0).toString();
                            glo.lCategoria[i].descripcion = columnas.getProperty(1).toString();
                            glo.lCategoria[i].color = columnas.getProperty(2).toString();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.lCategoria != null) {

                        } else {
                            Toast.makeText(LoginActivity.this, "Categoria: Problema de conexion", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        th.start();
    }

    public void CargarMenu() {
        Thread hilo = new Thread() {

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarMenuTodos";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/" + METHOD_NAME;

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {

                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 20;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);
                    if (resul2.getPropertyCount() > 0) {

                        SoapObject filas = (SoapObject) resul2.getProperty(0);

                        glo.lMenu = new com.example.josenavarro.restauranteapp.Menu[filas.getPropertyCount()];

                        for (int i = 0; i < filas.getPropertyCount(); i++) {
                            SoapObject columnas = (SoapObject) filas.getProperty(i);
                            glo.lMenu[i] = new com.example.josenavarro.restauranteapp.Menu();
                            glo.lMenu[i].codigo = columnas.getProperty(0).toString();
                            glo.lMenu[i].descripcion = columnas.getProperty(1).toString();
                            glo.lMenu[i].codigoCategoria = columnas.getProperty(2).toString();
                            glo.lMenu[i].precio = columnas.getProperty(3).toString();
                            glo.lMenu[i].colores = columnas.getProperty(4).toString();
                            glo.lMenu[i].modificadores = columnas.getProperty(5).toString();
                            glo.lMenu[i].acompañamientos = columnas.getProperty(6).toString();
                            glo.lMenu[i].impuestoVenta = columnas.getProperty(7).toString();
                            glo.lMenu[i].impuestoServicio = columnas.getProperty(8).toString();
                            glo.lMenu[i].impresora = columnas.getProperty(9).toString();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.lMenu != null) {

                        } else {
                            Toast.makeText(LoginActivity.this, "Cargar Menú: Problema de conexion", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        hilo.start();
    }

    public void CargarModificadores() {
        Thread hilo = new Thread() {

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarModificadores";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarModificadores";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {

                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 20;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);
                    if (resul2.getPropertyCount() > 0) {
                        SoapObject filas = (SoapObject) resul2.getProperty(0);

                        glo.lMod = new Modificadores[filas.getPropertyCount()];


                        for (int i = 0; i < filas.getPropertyCount(); i++) {
                            SoapObject columnas = (SoapObject) filas.getProperty(i);
                            glo.lMod[i] = new Modificadores();
                            glo.lMod[i].descripcion = columnas.getProperty(1).toString();


                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.lMod != null) {

                        } else {
                            Toast.makeText(LoginActivity.this, "CargarModificadores: Problema de conexion", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        hilo.start();
    }

    public void CargarAcompanamientos() {

        Thread hilo = new Thread() {

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarAcompanamientos";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarAcompanamientos";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);

                try {

                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 10;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    if (resul.getPropertyCount() > 0) {
                        glo.lAcom = new Acompañamiento[1];
                        SoapObject resul2 = (SoapObject) resul.getProperty(1);
                        if (resul2.getPropertyCount() > 0) {
                            SoapObject filas = (SoapObject) resul2.getProperty(0);
                            glo.lAcom = new Acompañamiento[filas.getPropertyCount()];


                            for (int i = 0; i < filas.getPropertyCount(); i++) {
                                SoapObject columnas = (SoapObject) filas.getProperty(i);
                                glo.lAcom[i] = new Acompañamiento();
                                glo.lAcom[i].codigo = columnas.getProperty(0).toString();
                                glo.lAcom[i].descripcion = columnas.getProperty(1).toString();
                                glo.lAcom[i].categoria = columnas.getProperty(4).toString();

                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.lAcom != null) {

                        } else {

                            Toast.makeText(LoginActivity.this, "CargarAcompanamientos: Problema de conexion", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        };

        hilo.start();
    }

    public void CargarTodosModificadores() {
        Thread hilo = new Thread() {

            @Override
            public void run() {

                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarTodosModificadores";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarTodosModificadores";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {
                    transporte.call(SOAP_ACTION, envelope);
                    Message msg = new Message();
                    msg.obj = 20;
                    puente.sendMessage(msg);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    if (resul.getPropertyCount() > 0) {
                        SoapObject resul2 = (SoapObject) resul.getProperty(1);
                        if (resul2.getPropertyCount() > 0) {
                            SoapObject filas = (SoapObject) resul2.getProperty(0);

                            glo.ListaTodosModificadores = new String[filas.getPropertyCount()];

                            for (int i = 0; i < filas.getPropertyCount(); i++) {
                                SoapObject columnas = (SoapObject) filas.getProperty(i);

                                glo.ListaTodosModificadores[i] = columnas.getProperty(1).toString();

                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.ListaTodosModificadores != null) {

                        } else {
                            Toast.makeText(LoginActivity.this, "CargarTodosModificadores: Problema de conexion", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        hilo.start();
    }
    public void CargarSalones() {
        Thread th = new Thread() {
                         @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarSalones";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarSalones";

                final SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {
                    transporte.call(SOAP_ACTION, envelope);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);

                    SoapObject filas = (SoapObject) resul2.getProperty(0);

                    glo.SalonesID = new String[filas.getPropertyCount()];
                    glo.SalonesNombre = new String[filas.getPropertyCount()];
                    for (int i = 0; i < filas.getPropertyCount(); i++) {
                        SoapObject columnas = (SoapObject) filas.getProperty(i);

                        glo.SalonesNombre[i] = columnas.getProperty(1).toString();
                        glo.SalonesID[i] = columnas.getProperty(0).toString();
                    }
                    Log.d("Login","Cargue salones");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }


        };

        th.start();
    }
}
