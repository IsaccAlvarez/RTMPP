package com.example.josenavarro.restauranteapp;

import android.util.Log;

/**
 * Created by IALVAREZ on 09/07/2015.
 */
public class Comandas {

    public Comanda lComanda[];
    private Comanda currentModificadores[];
    public int filas = 0;

    public Comandas(int cantidadInicial) {
        clsGlobal.currentSaving = false;
        filas = cantidadInicial;
        lComanda = new Comanda[filas];
        currentModificadores = null;
        filas = lComanda.length;
    }

    public Comandas() {
        clsGlobal.currentSaving = false;
        lComanda = new Comanda[filas];
        currentModificadores = null;
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
            Log.d("Comanda", "addComada: " + cmd.descripcion);
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

    public void spGuardar() {
        //TODO: Realizar actualizaci�n
        filas = lComanda.length - 1;
        for (int i = 0; i < lComanda.length; i++) {
            if (clsGlobal.currentSaving == false) {
                clsGlobal.currentSaving = true;
                if (clsGlobal.buscaCodigoComanda == false) {
                    if (lComanda[i].codigoComanda.equals("0")) {
                        lComanda[i].codigoComanda = clsGlobal.currentComanda;
                    }
                    Log.d("Comandas.spGuardar", "Guardando:" + lComanda[i].descripcion + ", Comanda: " + lComanda[i].codigoComanda);
                    lComanda[i].spGuardar();
                } else {
                    Log.d("Comandas.spGuardar", "Buscando #Comanda:" + lComanda[i].descripcion + ", Comanda: " + lComanda[i].codigoComanda);
                    lComanda[i].spComandaNumero(lComanda[i].mesa);
                    i = i - 1;
                }
            } else {
              i = i - 1;
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
