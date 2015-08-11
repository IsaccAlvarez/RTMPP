package com.example.josenavarro.restauranteapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by IALVAREZ on 23/07/2015.
 */
public class VerificaConexion {
    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < 2; i++) {

            if (!redes[i].isRoaming()) {
                if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                    bConectado = true;
                }
            }

        }
        return bConectado;
    }

    static boolean verificando;
    static boolean positiva = false;

    public static boolean verificaAccesoBD() {

        verificando = true;
        positiva = false;
        Thread hilo = new Thread() {
            String res;

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
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);

                try {

                    transporte.call(SOAP_ACTION, envelope);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    if (resul.getPropertyCount() > 0) {
                        Log.d("Login:","Comprobo que esta bien");
                        positiva = true;
                    }
                    Log.d("Login","Paso sin problemas que esta bien");


                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();

                }
                verificando = false;

            }

        };
        hilo.start();
        while (verificando){}//Espera que termine
        return positiva;
    }
}
