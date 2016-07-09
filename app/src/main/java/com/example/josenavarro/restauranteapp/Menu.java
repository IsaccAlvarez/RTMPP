package com.example.josenavarro.restauranteapp;

/**
 * Created by Isacc Alvarez Salaza on 18/06/2015.
 */
public class Menu implements Comparable<Menu> {
    public  String descripcion;
    public  String codigo;
    public  String colores;
    public  String precio;
    public  String codigoCategoria;
    public  String modificadores;
    public  String acompañamientos;
    public  String impuestoVenta;
    public  String impuestoServicio;
    public  String impresora;
    public Menu(){}

    public String getdescripcion() {
        return descripcion;
    }

    @Override
    public int compareTo(Menu o) {
       Menu p = (Menu) o;
            return descripcion.compareTo(p.getdescripcion());
    }
}
