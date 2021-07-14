package edu.pe.idat.app_qolca.common;

public class Constantes {
    private static String ipv="192.168.1.6";

    public static final String URL_API_USUARIO_CREAR="http://"+ipv+":8050/usuario/signup";
    public static final String URL_API_USUARIO_LOGIN="http://"+ipv+":8050/usuario/login";
    public static final String URL_API_USUARIO_ID="http://"+ipv+":8050/usuario/";
    public static final String URL_API_USUARIO_EDIT="http://"+ipv+":8050/usuario/modificar/";
    public static final String URL_API_USUARIO_EDIT_PASS="http://"+ipv+":8050/usuario/changepassword/";


    public static final String URL_API_CATEGORIA_LISTAR="http://"+ipv+":8050/categoria/listar";

    public static final String URL_API_PRODUCTO_LISTAR="http://"+ipv+":8050/producto/listar";
    public static final String URL_API_PRODUCTO_BUSCAR="http://"+ipv+":8050/producto/buscar?nombre=";
    public static final String URL_API_PRODUCTO_ID="http://"+ipv+":8050/producto/";

    public static final String URL_API_CARRITO_ID="http://"+ipv+":8050/carrito/";

    public static final String URL_API_CARRITOPRODUCTOS_LISTAR="http://"+ipv+":8050/carritoproducto/usuario/";
    public static final String URL_API_CARRITOPRODUCTOS_CREAR="http://"+ipv+":8050/carritoproducto/añadir";
    public static final String URL_API_CARRITOPRODUCTOS_INCREMENTAR="http://"+ipv+":8050/carritoproducto/increment/";
    public static final String URL_API_CARRITOPRODUCTOS_DECREMENTAR="http://"+ipv+":8050/carritoproducto/decrement/";
    public static final String URL_API_CARRITOPRODUCTOS_DELETE="http://"+ipv+":8050/carritoproducto/eliminar/";
    
    public static final String URL_API_CARRITOPRODUCTOS_DELETE_ALL="http://"+ipv+":8050/carritoproducto/eliminar/usuario/";

    public static final String PREF_ID="PREF_ID";
    public static final String PREF_NOMBRE="PREF_NOMBRE";
    public static final String PREF_APELLIDO="PREF_APELLIDO";
}

