package mx.uv.feaa;

import mx.uv.feaa.model.*;
import mx.uv.feaa.servicios.*;
import mx.uv.feaa.excepciones.*;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsuarioService usuarioService = new UsuarioService();
    private static Usuario usuarioActual;

    public static void main(String[] args) {
        System.out.println("=== Sistema de Carreras de Caballos ===");

        // Cargar datos preestablecidos
        usuarioService.cargarDatos();

        // Menú principal simplificado
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            System.out.print("Seleccione: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    usuarioService.guardarDatos();
                    System.out.println("¡Hasta pronto!");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void iniciarSesion() {
        System.out.print("\nUsuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        try {
            usuarioActual = usuarioService.autenticar(usuario, password);
            System.out.println("Bienvenido " + usuarioActual.getNombreUsuario());

            if (usuarioActual instanceof Apostador) {
                menuApostador();
            } else if (usuarioActual instanceof Criador) {
                menuCriador();
            }
        } catch (AutenticacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void menuApostador() {
        Apostador apostador = (Apostador) usuarioActual;

        while (true) {
            System.out.println("\n=== MENÚ APOSTADOR ===");
            System.out.println("Usuario: " + apostador.getNombreUsuario());
            System.out.println("Saldo: $" + apostador.getSaldo());
            System.out.println("1. Ver perfil");
            System.out.println("2. Ver historial de apuestas");
            System.out.println("3. Realizar apuesta demo");
            System.out.println("4. Cerrar sesión");
            System.out.print("Seleccione: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    mostrarPerfil();
                    break;
                case 2:
                    System.out.println("\nHistorial de apuestas:");
                    System.out.println("- Total apostado: $" + apostador.getTotalApostado());
                    System.out.println("- Total ganado: $" + apostador.getTotalGanado());
                    System.out.println("- Apuestas realizadas: " + apostador.getApuestasRealizadas());
                    break;
                case 3:
                    System.out.println("\nSimulación de apuesta realizada");
                    apostador.setSaldo(apostador.getSaldo() + 100); // Demo
                    System.out.println("Nuevo saldo: $" + apostador.getSaldo());
                    break;
                case 4:
                    usuarioActual = null;
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void menuCriador() {
        Criador criador = (Criador) usuarioActual;

        while (true) {
            System.out.println("\n=== MENÚ CRIADOR ===");
            System.out.println("Haras: " + criador.getNombreHaras());
            System.out.println("1. Ver perfil");
            System.out.println("2. Ver caballos registrados");
            System.out.println("3. Registrar caballo demo");
            System.out.println("4. Cerrar sesión");
            System.out.print("Seleccione: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    mostrarPerfil();
                    break;
                case 2:
                    System.out.println("\nCaballos registrados:");
                    System.out.println("- Total: " + criador.getCaballosRegistrados());
                    System.out.println("- Activos: " + criador.getCaballosActivos());
                    break;
                case 3:
                    System.out.println("\nSimulación: Caballo registrado");
                    int nuevoTotal= criador.getCaballosActivos()+1;
                    criador.setCaballosRegistrados(nuevoTotal);
                    System.out.println("Total caballos: " + criador.getCaballosRegistrados());
                    break;
                case 4:
                    usuarioActual = null;
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void mostrarPerfil() {
        System.out.println("\n=== PERFIL ===");
        System.out.println("Usuario: " + usuarioActual.getNombreUsuario());
        System.out.println("Email: " + usuarioActual.getEmail());
        System.out.println("Tipo: " + usuarioActual.getTipoUsuario());

        if (usuarioActual instanceof Apostador) {
            Apostador a = (Apostador) usuarioActual;
            System.out.println("Nombre: " + a.getNombre());
            System.out.println("Teléfono: " + a.getTelefono());
        } else if (usuarioActual instanceof Criador) {
            Criador c = (Criador) usuarioActual;
            System.out.println("Licencia: " + c.getLicenciaCriador());
            System.out.println("Haras: " + c.getNombreHaras());
        }
    }
}