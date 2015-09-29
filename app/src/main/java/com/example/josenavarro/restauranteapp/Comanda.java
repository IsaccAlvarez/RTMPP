
package com.example.josenavarro.restauranteapp;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by Isacc Alvarez Salaza on 18/06/2015.
 */
public class Comanda {

    public String tipo = "Principal";//Acompañamiento y Modificador .
    public String descripcion = "";
    public static String codigoComanda = "0";
    public String producto = "0";
    public String cantidad;
    public String precio;
    public String mesa;
    public String iVenta;
    public String iServicio;
    public String impresora;
    public String id = "0";
    public String estado = "Activo";
    public static String solicitud = "0";


    public Comanda(String _producto, String _codigoComanda,
                   String _cantidad, String _descripcion,
                   String _precio, String _mesa, String _iVenta,
                   String _iServicio, String _impresora, String _id, String _tipo) {
        producto = _producto;
        codigoComanda = _codigoComanda;
        cantidad = _cantidad;
        descripcion = _descripcion;
        precio = _precio;
        mesa = _mesa;
        iVenta = _iVenta;
        iServicio = _iServicio;
        impresora = _impresora;
        id = _id;
        tipo = _tipo;

    }


    public void spQuitar() {

        Thread hilo = new Thread() {

            String res = "0";

            @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnQuitarLineaComanda";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnQuitarLineaComanda";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_id", id);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);

                try {

                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    clsGlobal.currentSaving = false;
                    Log.d("Quitar Comanda", "Respuesta Envio: " + res);
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();
        while (clsGlobal.currentSaving) {
        }
    }

    public void spGuardar() {
        String cedula = clsGlobal.IdUsuarioLog;
        mesa = clsGlobal.currentMesa.Id;
        clsGlobal.currentSaving = true;
        if (id == "0" & estado.equals("Activo")) {
            spProcesarLinea(producto, descripcion, descripcion, precio, clsGlobal.currentComanda, clsGlobal.currentMesa.Id, "0", impresora, tipo, cedula, cantidad);
        } else {
            if (estado == "Inactivo") {
                spQuitar();
            } else {
                clsGlobal.currentSaving = false;
            }
        }

    }

    void spProcesarLinea(final String _cProducto, final String _cDescripcion, final String _Descripcion,
                         final String _cPrecio, final String _cCodigo, final String _cMesa, final String _cImpreso, final String _Impresora,
                         final String _tipo, final String _Cedula, final String _cCantidad) {

        Thread hilo = new Thread() {

            String res = "0";

            @Override
            public void run() {
                NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);

                double precio = 0;
                try {
                    precio = fmt.parse(_cPrecio).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                    precio = 0;
                }
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnTerminarComanda";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnTerminarComanda";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_cProducto", _cProducto);
                sp.addProperty("_cDescripcion", _cDescripcion);
                sp.addProperty("_Descripcion", _Descripcion);
                sp.addProperty("_cPrecio", clsGlobal.fnFormatBD(precio));
                sp.addProperty("_cCodigo", _cCodigo);
                sp.addProperty("_cMesa", _cMesa);
                sp.addProperty("_cImpreso", _cImpreso);
                sp.addProperty("_Impresora", _Impresora);
                sp.addProperty("_tipo", _tipo);
                sp.addProperty("_Cedula", _Cedula);
                sp.addProperty("_cCantidad", _cCantidad);
                sp.addProperty("_solicitud", clsGlobal.currentSolicitud);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);
                Log.d("Guardar Comanda", "Pase transporte");
                try {

                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();
                    Log.d("Guardar Comanda", "Respuesta Envio: " + res);


                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                try {
                    int entero = Integer.getInteger(res);
                    id = res;
                } catch (Exception ex) {
                    id = "0";
                }
                clsGlobal.currentSaving = false;

            }

        };
        hilo.start();
        if (clsGlobal.currentSaving) {

        }

    }


}