package com.example.josenavarro.restauranteapp;

import android.app.ProgressDialog;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by IALVAREZ on 09/07/2015.
 */
public class Comandas {

    public Comanda lComanda[];
    public int filas = 0;
    private boolean comprobando = false;

    public Comandas(int cantidadInicial) {
        clsGlobal.currentSaving = false;
        filas = cantidadInicial;
        lComanda = new Comanda[filas];
        filas = lComanda.length;
    }

    public Comandas() {
        clsGlobal.currentSaving = false;
        lComanda = new Comanda[filas];
        filas = lComanda.length;
    }

    public void spClear() {
        lComanda = null;
        filas = 0;
    }

    public Comanda getLinea(int pos) {
        return lComanda[pos];
    }

    public int getFilas() {
        return filas;
    }

    public void spAddComanda(Comanda cmd) {

        try {
            if (cmd.impresora.equals("")) {
                cmd.impresora = clsGlobal.currentImpresora;
            } else {
                clsGlobal.currentImpresora = cmd.impresora;
            }
            int nL = 0;
            if (lComanda == null) {
                lComanda = new Comanda[1];
                lComanda[0] = cmd;
            } else {
                nL = lComanda.length + 1;
                Comanda pivot[] = lComanda;
                lComanda = new Comanda[nL];
                //Retorna datos del pivot al original
                for (int i = 0; i < pivot.length; i++) {
                    lComanda[i] = pivot[i];
                }
                lComanda[nL - 1] = cmd;
                pivot = null;//Limpia pivot
            }
            filas = lComanda.length;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void spGuardar(int lineas) {
        //Comprobar comanda y solicitud.
        clsGlobal.buscaCodigoComanda = false;
        spComandaNumero(clsGlobal.currentMesa.Id);
        spComprobarSolicitud();
        filas = lComanda.length;
        for (int i = 0; i < filas; i++) {
            lComanda[i].spGuardar();
            Log.d("Guardar", "procesando fila");
        }
        spActualizarMesa(clsGlobal.currentMesa.Id, lineas);
    }

    public void spComprobarSolicitud() {

        if (clsGlobal.currentMesa.Estado.equals("1")) {
            if (clsGlobal.currentComanda.equals("0")) {
                clsGlobal.currentSolicitud = "1";
            } else {
                spObtenerSolicitud();
            }

        } else {
            clsGlobal.currentSolicitud = "1";
        }
    }

    public void spObtenerSolicitud() {
        comprobando = true;
        Thread hilo = new Thread() {

            String res = "0";
            @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnNumeroSolicitud";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnNumeroSolicitud";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("cNumero", clsGlobal.currentComanda);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);
                Log.d("Guardar", "Comprueba solicitud de comanda: " + clsGlobal.currentComanda.toString());
                try {

                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    Log.d("Guardar", "Termina comprueba solicitud: " + res);
                } catch (IOException e) {
                    e.printStackTrace();
                    comprobando = false;

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    comprobando = false;
                }
                try {
                    Log.d("Guardar", "Termina comprueba solicitud x: " + res);
                    clsGlobal.currentSolicitud = res;
                    comprobando = false;


                } catch (Exception ex) {
                    clsGlobal.currentSolicitud = "0";
                    comprobando = false;
                }


            }
        };
        hilo.start();
    }

    public void spActualizarMesa(final String _id, final int _lineas) {

        Thread hilo = new Thread() {

            String res = "0";

            @Override
            public void run() {
                int lineas = _lineas;
                if (lineas == 0) {
                    clsGlobal.currentMesa.Estado = "0";
                } else {
                    clsGlobal.currentMesa.Estado = "1";
                }
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnActualizaMesa";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnActualizaMesa";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_id", _id);
                sp.addProperty("_activa", clsGlobal.currentMesa.Estado);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);

                try {
                    //TODO: Procesa linea revisar si se envia
                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    Log.d("Guardar", "Actualizar mesa");

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException ie) {

        }

    }

    void spComandaNumero(final String _cMesa) {

        if (clsGlobal.currentComanda.equals("0")) {
            clsGlobal.buscaCodigoComanda = true;
            Thread hilo = new Thread() {
                String res = "0";

                @Override
                public void run() {

                    String NAMESPACE = new clsGlobal().NAMESPACE;
                    String URL = new clsGlobal().URL;
                    String METHOD_NAME = "fnComandaMesa";
                    String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnComandaMesa";

                    SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                    sp.addProperty("_id", _cMesa);

                    SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    sobre.dotNet = true;
                    sobre.setOutputSoapObject(sp);
                    HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);
                    Log.d("Guardar", "Comprueba Comanda");
                    try {
                        transporte.call(SOAP_ACTION, sobre);
                        SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                        res = resulxml.toString();
                        Log.d("Guardar", "Comanda recuperada: " + res);

                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                    try {
                        clsGlobal.currentComanda = res;
                    } catch (Exception ex) {
                        clsGlobal.currentComanda = "0";
                    }
                    clsGlobal.buscaCodigoComanda = false;

                }
            };
            hilo.start();
            try {
                hilo.join();
            } catch (InterruptedException ie) {

            }
        }
    }

    private void spRemoverComanda(int pos) {
        try {
            int nL = 0;
            if (lComanda != null) {
                if (lComanda[pos].estado == "Activo") {
                    lComanda[pos].estado = "Inactivo";
                } else {
                    spRemoverComanda(pos + 1);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
