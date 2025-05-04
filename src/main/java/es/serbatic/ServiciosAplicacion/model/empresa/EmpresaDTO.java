package es.serbatic.ServiciosAplicacion.model.empresa;

import lombok.Data;

import java.util.List;

@Data
public class EmpresaDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String telefono;
    private String email;
    private String pais;
    private String ciudad;
    private String direccion;
    private String sitioWeb;
    private String imagenPortadaUrl;
    private boolean activo;
    private String nombreServicio;
    private List<String> imagenesAdicionales;
    private boolean aceptaReservas;
    private Double latitud;
    private Double longitud;
    private String horaApertura;
    private String horaCierre;
    private String facebook;   
    private String instagram;  
    private String linkedin;
    private String x;   
}

