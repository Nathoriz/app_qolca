package edu.pe.idat.app_qolca.common;

public class Constantes {
    private static String ipv="192.168.1.6";

    public static final String URL_API_USUARIO_CREAR="http://"+ipv+":8050/usuario/signup";
    public static final String URL_API_USUARIO_LOGIN="http://"+ipv+":8050/usuario/login";


    public static final String URL_API_CATEGORIA_LISTAR=" http://"+ipv+":8050/categoria/listar";
    public static final String URL_API_PRODUCTO_LISTAR=" http://"+ipv+":8050/producto/listar";
    public static final String URL_API_PRODUCTO_BUSCAR=" http://"+ipv+":8050/producto/buscar?nombre=";


    public static final String PREF_ID="PREF_ID";
    public static final String PREF_NOMBRE="PREF_NOMBRE";
    public static final String PREF_APELLIDO="PREF_APELLIDO";
}

