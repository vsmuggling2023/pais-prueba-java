/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Mouli
 */
public class Pais {
    String nombre;
    String continente;
    String ciudades;
    String region;
    String superficie;
    String anioindependencia;
    String poblacion;
    String vida_expectativa;
    String pib;
    String forma_gobierno;
    String jefe_estado;
    String ciudad_capital;
    String codigo;
    String idiomapais;

    public Pais() {
    }

    public Pais(String nombre, String continente, String poblacion, String codigo) {
        this.nombre = nombre;
        this.continente = continente;
        this.poblacion = poblacion;
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContinente() {
        return continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
}
