package mx.uv.feaa.vista;

import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.model.entidades.Apostador;
import mx.uv.feaa.model.entidades.Criador;

import java.util.Scanner;

public class LoginVista {
    private final AuthService authService = new AuthService();
    private final Scanner scanner = new Scanner(System.in);

    public Usuario mostrarLogin() {
        System.out.println("\n=== SISTEMA DE GESTIÓN HÍPICA ===");
        System.out.print("Usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        Usuario usuario = authService.login(username, password);

        if (usuario != null) {
            System.out.println("\n¡Bienvenido, " + usuario.getNombreUsuario() + "!");
            System.out.println("Tipo de usuario: " + usuario.getTipoUsuario());

            // Ejemplo de polimorfismo
            if (usuario instanceof Apostador) {
                Apostador apostador = (Apostador) usuario;
                System.out.println("Saldo disponible: " + apostador.getSaldo());
            } else if (usuario instanceof Criador) {
                Criador criador = (Criador) usuario;
                System.out.println("Haras: " + criador.getNombreHaras());
            }
        } else {
            System.out.println("\n❌ Credenciales inválidas o cuenta inactiva");
        }
        return usuario;
    }
}