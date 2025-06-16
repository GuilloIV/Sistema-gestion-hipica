package mx.uv.feaa.view;

import mx.uv.feaa.model.dao.UsuarioDAO;
import mx.uv.feaa.model.dao.CriadorDAO;
import mx.uv.feaa.model.dao.ApostadorDAO;
import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.model.entidades.Apostador;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class LoginView {
    private final Scanner scanner;
    private final UsuarioDAO usuarioDAO;
    private final CriadorDAO criadorDAO;
    private final ApostadorDAO apostadorDAO;

    public LoginView() {
        this.scanner = new Scanner(System.in);
        this.usuarioDAO = new UsuarioDAO();
        this.criadorDAO = new CriadorDAO();
        this.apostadorDAO = new ApostadorDAO();
    }

    public void mostrar() {
        System.out.println("=== SISTEMA DE GESTIÓN HÍPICA ===");
        System.out.println("=== INICIO DE SESIÓN ===");

        while (true) {
            System.out.print("Nombre de usuario: ");
            String nombreUsuario = scanner.nextLine().trim();

            if (nombreUsuario.isEmpty()) {
                System.out.println("\nError: El nombre de usuario no puede estar vacío.");
                continue;
            }

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            if (password.isEmpty()) {
                System.out.println("\nError: La contraseña no puede estar vacía.");
                continue;
            }

            try {
                // Validar credenciales usando el DAO
                if (usuarioDAO.validarCredenciales(nombreUsuario, password)) {
                    Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);

                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();

                        if (!usuario.isActivo()) {
                            System.out.println("\nError: La cuenta está desactivada. Contacte al administrador.");
                            continue;
                        }

                        // Actualizar último acceso
                        usuarioDAO.actualizarUltimoAcceso(usuario.getIdUsuario());

                        System.out.println("\n¡Bienvenido, " + usuario.getNombreUsuario() + "!");

                        // Redireccionar según el tipo de usuario
                        redirigirSegunTipoUsuario(usuario);
                        return;
                    }
                } else {
                    System.out.println("\nError: Credenciales incorrectas.");
                }
            } catch (SQLException e) {
                System.err.println("Error al acceder a la base de datos: " + e.getMessage());
                return;
            }

            System.out.println("\n¿Desea intentar nuevamente? (s/n)");
            String respuesta = scanner.nextLine().toLowerCase().trim();
            if (!respuesta.equals("s") && !respuesta.equals("sí") && !respuesta.equals("si")) {
                System.out.println("Saliendo del sistema...");
                return;
            }
            System.out.println();
        }
    }

    private void redirigirSegunTipoUsuario(Usuario usuario) {
        try {
            String tipoUsuario = usuario.getTipoUsuario();

            switch (tipoUsuario.toUpperCase()) {
                case "CRIADOR":
                    Optional<Criador> criadorOpt = criadorDAO.getById(usuario.getIdUsuario());
                    if (criadorOpt.isPresent()) {
                        Criador criador = criadorOpt.get();
                        System.out.println("Tipo de usuario: Criador");
                        if (criador.getNombreHaras() != null) {
                            System.out.println("Haras: " + criador.getNombreHaras());
                        }
                        System.out.println("Licencia vigente: " + (criador.validarLicencia() ? "Sí" : "No"));
                        new MenuCriadorView(criador).mostrar();
                        System.out.println("Redirigiendo al menú de criador...");
                    }
                    break;

                case "APOSTADOR":
                    Optional<Apostador> apostadorOpt = apostadorDAO.getById(usuario.getIdUsuario());
                    if (apostadorOpt.isPresent()) {
                        Apostador apostador = apostadorOpt.get();
                        System.out.println("Tipo de usuario: Apostador");
                        System.out.printf("Saldo disponible: $%.2f%n", apostador.getSaldo());
                        // Aquí llamarías al menú del apostador
                        new MenuApostadorView(apostador).mostrar();
                        System.out.println("Redirigiendo al menú de apostador...");
                    }
                    break;

                case "ADMINISTRADOR":
                    System.out.println("Tipo de usuario: Administrador");
                    // Aquí llamarías al menú del administrador
                    // new MenuAdministradorView(usuario).mostrar();
                    System.out.println("Redirigiendo al menú de administrador...");
                    break;

                default:
                    System.out.println("Error: Tipo de usuario no reconocido: " + tipoUsuario);
                    break;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos específicos del usuario: " + e.getMessage());
        }
    }

    // Método para cerrar recursos
    public void cerrarRecursos() {
        if (scanner != null) {
            scanner.close();
        }
    }
}