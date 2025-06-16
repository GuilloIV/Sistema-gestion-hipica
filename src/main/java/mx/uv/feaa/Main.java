package mx.uv.feaa;


import mx.uv.feaa.util.ConexionBD;
import mx.uv.feaa.view.LoginView;



public class Main {
    public static void main(String[] args) {
        // Verificar conexión a la base de datos
        if (!ConexionBD.verificarConexion()) {
            System.err.println("Error al conectar con la base de datos");
            return;
        }

        // Mostrar vista de login
        LoginView loginView = new LoginView();
        loginView.mostrar();
    }
}