package mx.uv.feaa;

import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.vista.LoginVista;

public class Main {
    public static void main(String[] args) {
        LoginVista login = new LoginVista();
        Usuario usuario = login.mostrarLogin();

        if (usuario != null) {

            System.out.println("\nConexión exitosa. Implementar menú principal...");
        }
    }
}