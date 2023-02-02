package com.example.medicatrack.model;

import com.example.medicatrack.model.enums.Frecuencia;

public class Medicamento
{
    private final int id;
    private String nombre;
    private String color;
    private String forma;
    private float concentracion;
    private Frecuencia frecuencia;

    public Medicamento(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public float getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(float concentracion) {
        this.concentracion = concentracion;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Frecuencia frecuencia) {
        this.frecuencia = frecuencia;
    }
}


