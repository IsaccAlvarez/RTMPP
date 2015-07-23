package com.example.josenavarro.restauranteapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Jose Navarro on 13/05/2015.
 */
public class clsGlobal {
    public static boolean currentSaving= false;
    public static String impresoraArticulo = "";
    public String URL ="http://192.168.1.4/WSRest.asmx";
    public String NAMESPACE="http://WSRest.APP.org/";
    public String SOAP_ACTION ="http://WSRest.APP.org";
    public static int Time_out = 20000;
    public static String cedulaUsuario = "";
    public static String currentComanda= "0";
    public static boolean buscaCodigoComanda = false;
    public static Mesas currentMesa;
    public static int posCategoria= 0;
    public static int posSalon = 0;
    public static Categoria lCategoria[];
    public static Menu lMenu[];
    public static Menu lMenuSeleccionado[];
    public static Modificadores lMod[];
    public static Acompañamiento lAcom[];
    public static Comandas ListaComanda;
    public static String ListaTodosModificadores[];
    public static String formatString = "###,##0.00";
    public static String formatStringBD = "######.##";
    public static String currentImpresora = "";
    public static boolean llamadaEnCurso= false;
    public static String fnFormat(double num)
    {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat(formatString, otherSymbols);
        return df.format(num);
    }
    public static String fnFormatBD(double num)
    {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat(formatStringBD, otherSymbols);
        return df.format(num);
    }

}


