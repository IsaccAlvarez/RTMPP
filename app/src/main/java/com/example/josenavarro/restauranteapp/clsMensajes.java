package com.example.josenavarro.restauranteapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by IALVAREZ on 04/07/2015.
 */
public class clsMensajes {
    public static boolean okResult=false;
    public static boolean fnOKCancel(Context ventana, String titulo, String pregunta)
    {
        okResult = true;
        new AlertDialog.Builder(ventana)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(titulo)
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clsMensajes.okResult=true;
                    }
                });
        return okResult;
    }

}
