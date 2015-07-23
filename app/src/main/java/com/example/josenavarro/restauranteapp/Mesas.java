package com.example.josenavarro.restauranteapp;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by IALVAREZ on 06/07/2015.
 */
public class Mesas {
    public String Id = "0";
    public String Nombre = "";
    public String Estado = "0";

    public Mesas(String _Id, String _Nombre, String _Estado) {
        Id = _Id;
        Nombre = _Nombre;
        Estado = _Estado;
    }

    public void spSetEstado(final String _estado) {

        Thread hilo = new Thread() {

            String res = "0";

            @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnActualizaMesa";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnActualizaMesa";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_id", Id);
                sp.addProperty("_activa", _estado);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);
                Log.d("Actualizar mesa", "Pase transporte " + Id);
                try {
                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    Log.d("Actualizar mesa", "Respuesta Envio: " + res);
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();
    }
}
