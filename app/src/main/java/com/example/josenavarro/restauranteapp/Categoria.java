package com.example.josenavarro.restauranteapp;

/**
 * Created by Isacc Alvarez Salaza on 18/06/2015.
 */
public class Categoria {

    public  String descripcion = "";
    public  String codigo = "";
    public  String color = "";

    public void New(String _descripcion,String _codigo, String _color )
    {
        descripcion = _descripcion;
        codigo= _codigo;
        color= _color;
    }
}
