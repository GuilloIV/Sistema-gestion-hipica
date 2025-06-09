package mx.uv.feaa;

import mx.uv.feaa.model.Usuario;
import mx.uv.feaa.model.Apostador;
import mx.uv.feaa.model.Criador;
import mx.uv.feaa.servicios.AutenticacionService;
import mx.uv.feaa.servicios.UsuarioService;
import mx.uv.feaa.servicios.ManejadorDatos;
import mx.uv.feaa.excepciones.CredencialesInvalidasException;
import mx.uv.feaa.excepciones.UsuarioNoEncontradoException;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static AutenticacionService authService;
    private static UsuarioService usuarioService;
    private static ManejadorDatos manejadorDatos;
    private static Usuario usuarioActual; // Para mantener la sesión

    public static void main(String[] args) {
        System.out.println("=== Iniciando Sistema de Carreras ===");

        // Inicializar servicios
        inicializarServicios();

        // Verificar integridad de datos
        verificarIntegridadSistema();

        // Bucle principal del menú
        ejecutarMenuPrincipal();
    }

    /**
     * Inicializa todos los servicios necesarios
     */
    private static void inicializarServicios() {
        try {
            System.out.println("Inicializando servicios...");

            // Inicializar servicios en orden
            manejadorDatos = ManejadorDatos.getInstance();
            usuarioService = UsuarioService.getInstance();
            authService = new AutenticacionService();

            // Cargar datos existentes
            usuarioService.cargarDatos();

            System.out.println("✓ Servicios inicializados correctamente");

        } catch (Exception e) {
            System.err.println("Error al inicializar servicios: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Verifica la integridad del sistema y muestra estadísticas
     */
    private static void verificarIntegridadSistema() {
        try {
            // Verificar archivos críticos
            if (!manejadorDatos.verificarIntegridadArchivo("usuarios.json")) {
                System.out.println("⚠ Archivo de usuarios no encontrado o corrupto. Se creará uno nuevo.");
            }

            // Mostrar estadísticas del sistema
            Map<String, Object> stats = usuarioService.obtenerEstadisticas();
            System.out.println("\n=== Estado del Sistema ===");
            System.out.println("Total usuarios: " + stats.get("totalUsuarios"));
            System.out.println("Usuarios activos: " + stats.get("usuariosActivos"));
            System.out.println("Criadores: " + stats.get("totalCriadores"));
            System.out.println("Apostadores: " + stats.get("totalApostadores"));

            // Información de espacio
            System.out.println("\n" + manejadorDatos.obtenerInfoEspacio());

        } catch (Exception e) {
            System.err.println("Error al verificar integridad: " + e.getMessage());
        }
    }

    /**
     * Ejecuta el menú principal del sistema
     */
    private static void ejecutarMenuPrincipal() {
        while (true) {
            try {
                mostrarMenuPrincipal();
                int opcion = leerOpcion();

                switch (opcion) {
                    case 1:
                        iniciarSesion();
                        break;
                    case 2:
                        registrarUsuario();
                        break;
                    case 3:
                        mostrarEstadisticasSistema();
                        break;
                    case 4:
                        menuMantenimiento();
                        break;
                    case 5:
                        salirDelSistema();
                        return;
                    default:
                        System.out.println("❌ Opción no válida. Intente nuevamente.");
                }

            } catch (Exception e) {
                System.err.println("Error en menú principal: " + e.getMessage());
                pausar();
            }
        }
    }

    /**
     * Muestra el menú principal
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    🏇 SISTEMA DE CARRERAS DE CABALLOS 🏇");
        System.out.println("=".repeat(50));
        System.out.println("1. 🔑 Iniciar sesión");
        System.out.println("2. 📝 Registrarse");
        System.out.println("3. 📊 Ver estadísticas del sistema");
        System.out.println("4. 🔧 Menú de mantenimiento");
        System.out.println("5. 🚪 Salir");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Maneja el proceso de inicio de sesión
     */
    private static void iniciarSesion() {
        System.out.println("\n=== 🔑 INICIAR SESIÓN ===");

        try {
            System.out.print("Nombre de usuario: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("❌ El nombre de usuario no puede estar vacío.");
                return;
            }

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            // Intentar login
            String tipoUsuario = authService.login(username, password);
            usuarioActual = usuarioService.buscarUsuarioPorNombre(username);

            System.out.println("✓ ¡Bienvenido, " + username + "!");
            System.out.println("Tipo de usuario: " + tipoUsuario);

            // Redirigir según tipo
            switch (tipoUsuario) {
                case "Apostador":
                    menuApostador();
                    break;
                case "Criador":
                    menuCriador();
                    break;
                default:
                    System.out.println("❌ Tipo de usuario no reconocido.");
            }

        } catch (UsuarioNoEncontradoException | CredencialesInvalidasException e) {
            System.out.println("❌ Error de autenticación: " + e.getMessage());
            pausar();
        }
    }

    /**
     * Maneja el proceso de registro de usuario
     */
    private static void registrarUsuario() {
        System.out.println("\n=== 📝 REGISTRO DE USUARIO ===");

        try {
            // Seleccionar tipo de usuario
            System.out.println("Tipos de usuario disponibles:");
            System.out.println("1. 🎯 Apostador");
            System.out.println("2. 🐎 Criador");
            System.out.print("Seleccione tipo de usuario: ");

            int tipo = leerOpcion();

            if (tipo < 1 || tipo > 2) {
                System.out.println("❌ Tipo de usuario no válido.");
                return;
            }

            // Datos básicos
            System.out.print("Nombre de usuario: ");
            String username = scanner.nextLine().trim();

            if (usuarioService.existeUsuario(username)) {
                System.out.println("❌ El nombre de usuario ya está en uso.");
                return;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (usuarioService.existeEmail(email)) {
                System.out.println("❌ El email ya está registrado.");
                return;
            }

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            Usuario nuevoUsuario = null;

            // Crear usuario según tipo
            switch (tipo) {
                case 1: // Apostador
                    nuevoUsuario = crearApostador(username, email, password);
                    break;
                case 2: // Criador
                    nuevoUsuario = crearCriador(username, email, password);
                    break;
            }

            if (nuevoUsuario != null) {
                if (usuarioService.registrarUsuario(nuevoUsuario)) {
                    System.out.println("✓ ¡Usuario registrado exitosamente!");
                    System.out.println("ID asignado: " + nuevoUsuario.getId());
                } else {
                    System.out.println("❌ Error al registrar usuario.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
        }

        pausar();
    }

    /**
     * Crea un nuevo apostador con datos específicos
     */
    private static Apostador crearApostador(String username, String email, String password) {
        System.out.println("\n--- Datos del Apostador ---");

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine().trim();

        Apostador apostador = new Apostador(username, email, nombre, telefono);
        apostador.setPassword(password);

        return apostador;
    }

    /**
     * Crea un nuevo criador con datos específicos
     */
    private static Criador crearCriador(String username, String email, String password) {
        System.out.println("\n--- Datos del Criador ---");

        System.out.print("Licencia de criador: ");
        String licencia = scanner.nextLine().trim();

        // Verificar que la licencia no esté en uso
        if (usuarioService.buscarCriadorPorLicencia(licencia) != null) {
            System.out.println("❌ La licencia ya está registrada.");
            return null;
        }

        System.out.print("Nombre del haras: ");
        String haras = scanner.nextLine().trim();

        System.out.print("Dirección: ");
        String direccion = scanner.nextLine().trim();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine().trim();

        // Fecha de vencimiento de licencia (1 año por defecto)
        LocalDate vencimientoLicencia = LocalDate.now().plusYears(1);

        Criador criador = new Criador(username, email, licencia, vencimientoLicencia,
                direccion, telefono, haras);
        criador.setPassword(password);

        return criador;
    }

    /**
     * Menú específico para apostadores
     */
    private static void menuApostador() {
        while (usuarioActual != null) {
            System.out.println("\n=== 🎯 MENÚ APOSTADOR ===");
            System.out.println("Usuario: " + usuarioActual.getNombreUsuario());
            System.out.println("1. 👤 Ver perfil");
            System.out.println("2. 💰 Gestionar saldo");
            System.out.println("3. 🏇 Ver carreras disponibles");
            System.out.println("4. 🎲 Realizar apuesta");
            System.out.println("5. 📈 Ver historial de apuestas");
            System.out.println("6. 🔄 Actualizar perfil");
            System.out.println("7. 🚪 Cerrar sesión");
            System.out.print("Seleccione una opción: ");

            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    mostrarPerfilUsuario();
                    break;
                case 2:
                    System.out.println("🚧 Gestión de saldo - En desarrollo");
                    break;
                case 3:
                    System.out.println("🚧 Carreras disponibles - En desarrollo");
                    break;
                case 4:
                    System.out.println("🚧 Realizar apuesta - En desarrollo");
                    break;
                case 5:
                    System.out.println("🚧 Historial de apuestas - En desarrollo");
                    break;
                case 6:
                    actualizarPerfil();
                    break;
                case 7:
                    cerrarSesion();
                    return;
                default:
                    System.out.println("❌ Opción no válida.");
            }

            pausar();
        }
    }

    /**
     * Menú específico para criadores
     */
    private static void menuCriador() {
        while (usuarioActual != null) {
            System.out.println("\n=== 🐎 MENÚ CRIADOR ===");
            System.out.println("Usuario: " + usuarioActual.getNombreUsuario());
            System.out.println("Haras: " + ((Criador) usuarioActual).getNombreHaras());
            System.out.println("1. 👤 Ver perfil");
            System.out.println("2. 🐴 Gestionar caballos");
            System.out.println("3. 🏁 Inscribir en carreras");
            System.out.println("4. 📊 Ver estadísticas");
            System.out.println("5. 📜 Ver historial de carreras");
            System.out.println("6. 🔄 Actualizar perfil");
            System.out.println("7. 🚪 Cerrar sesión");
            System.out.print("Seleccione una opción: ");

            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    mostrarPerfilUsuario();
                    break;
                case 2:
                    System.out.println("🚧 Gestión de caballos - En desarrollo");
                    break;
                case 3:
                    System.out.println("🚧 Inscripción en carreras - En desarrollo");
                    break;
                case 4:
                    System.out.println("🚧 Estadísticas - En desarrollo");
                    break;
                case 5:
                    System.out.println("🚧 Historial de carreras - En desarrollo");
                    break;
                case 6:
                    actualizarPerfil();
                    break;
                case 7:
                    cerrarSesion();
                    return;
                default:
                    System.out.println("❌ Opción no válida.");
            }

            pausar();
        }
    }

    /**
     * Muestra el perfil del usuario actual
     */
    private static void mostrarPerfilUsuario() {
        if (usuarioActual == null) return;

        System.out.println("\n=== 👤 PERFIL DE USUARIO ===");
        System.out.println("ID: " + usuarioActual.getId());
        System.out.println("Nombre de usuario: " + usuarioActual.getNombreUsuario());
        System.out.println("Email: " + usuarioActual.getEmail());
        System.out.println("Tipo: " + usuarioActual.getTipoUsuario());
        System.out.println("Estado: " + (usuarioActual.isActivo() ? "Activo" : "Inactivo"));
        System.out.println("Último acceso: " + usuarioActual.getUltimoAcceso());

        if (usuarioActual instanceof Apostador) {
            Apostador apostador = (Apostador) usuarioActual;
            System.out.println("Nombre: " + apostador.getNombreUsuario());
            System.out.println("Teléfono: " + apostador.getTelefono());
        } else if (usuarioActual instanceof Criador) {
            Criador criador = (Criador) usuarioActual;
            System.out.println("Licencia: " + criador.getLicenciaCriador());
            System.out.println("Haras: " + criador.getNombreHaras());
            System.out.println("Dirección: " + criador.getDireccion());
            System.out.println("Teléfono: " + criador.getTelefono());
            System.out.println("Vencimiento licencia: " + criador.getFechaVigenciaLicencia());
        }
    }

    /**
     * Permite actualizar el perfil del usuario
     */
    private static void actualizarPerfil() {
        if (usuarioActual == null) return;

        System.out.println("\n=== 🔄 ACTUALIZAR PERFIL ===");
        System.out.println("Presione Enter para mantener el valor actual");

        try {
            System.out.print("Email actual (" + usuarioActual.getEmail() + "): ");
            String nuevoEmail = scanner.nextLine().trim();
            if (!nuevoEmail.isEmpty()) {
                if (!usuarioService.existeEmail(nuevoEmail) || nuevoEmail.equals(usuarioActual.getEmail())) {
                    usuarioActual.setEmail(nuevoEmail);
                } else {
                    System.out.println("❌ El email ya está en uso.");
                    return;
                }
            }

            // Campos específicos según tipo de usuario
            if (usuarioActual instanceof Apostador) {
                actualizarPerfilApostador((Apostador) usuarioActual);
            } else if (usuarioActual instanceof Criador) {
                actualizarPerfilCriador((Criador) usuarioActual);
            }

            // Guardar cambios
            if (usuarioService.actualizarUsuario(usuarioActual)) {
                System.out.println("✓ Perfil actualizado exitosamente.");
            } else {
                System.out.println("❌ Error al actualizar perfil.");
            }

        } catch (Exception e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
        }
    }

    private static void actualizarPerfilApostador(Apostador apostador) {
        System.out.print("Nombre actual (" + apostador.getNombreUsuario() + "): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            apostador.getNombre();
        }

        System.out.print("Teléfono actual (" + apostador.getTelefono() + "): ");
        String nuevoTelefono = scanner.nextLine().trim();
        if (!nuevoTelefono.isEmpty()) {
            apostador.setTelefono(nuevoTelefono);
        }
    }

    private static void actualizarPerfilCriador(Criador criador) {
        System.out.print("Nombre del haras actual (" + criador.getNombreHaras() + "): ");
        String nuevoHaras = scanner.nextLine().trim();
        if (!nuevoHaras.isEmpty()) {
            criador.setNombreHaras(nuevoHaras);
        }

        System.out.print("Dirección actual (" + criador.getDireccion() + "): ");
        String nuevaDireccion = scanner.nextLine().trim();
        if (!nuevaDireccion.isEmpty()) {
            criador.setDireccion(nuevaDireccion);
        }

        System.out.print("Teléfono actual (" + criador.getTelefono() + "): ");
        String nuevoTelefono = scanner.nextLine().trim();
        if (!nuevoTelefono.isEmpty()) {
            criador.setTelefono(nuevoTelefono);
        }
    }

    /**
     * Muestra estadísticas del sistema
     */
    private static void mostrarEstadisticasSistema() {
        System.out.println("\n=== 📊 ESTADÍSTICAS DEL SISTEMA ===");

        Map<String, Object> stats = usuarioService.obtenerEstadisticas();

        System.out.println("👥 Usuarios registrados: " + stats.get("totalUsuarios"));
        System.out.println("✅ Usuarios activos: " + stats.get("usuariosActivos"));
        System.out.println("🐎 Criadores: " + stats.get("totalCriadores") +
                " (Activos: " + stats.get("criadoresActivos") + ")");
        System.out.println("🎯 Apostadores: " + stats.get("totalApostadores") +
                " (Activos: " + stats.get("apostadoresActivos") + ")");

        System.out.println("\n" + manejadorDatos.obtenerInfoEspacio());

        pausar();
    }

    /**
     * Menú de mantenimiento del sistema
     */
    private static void menuMantenimiento() {
        System.out.println("\n=== 🔧 MENÚ DE MANTENIMIENTO ===");
        System.out.println("1. 💾 Crear backup manual");
        System.out.println("2. 📋 Listar backups");
        System.out.println("3. 🗑️ Limpiar archivos temporales");
        System.out.println("4. 🗂️ Limpiar backups antiguos");
        System.out.println("5. 🔄 Recargar datos");
        System.out.println("6. 🔍 Verificar integridad");
        System.out.println("7. 🔙 Volver al menú principal");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1:
                if (usuarioService.guardarDatos()) {
                    System.out.println("✓ Backup creado exitosamente.");
                } else {
                    System.out.println("❌ Error al crear backup.");
                }
                break;
            case 2:
                List<String> backups = manejadorDatos.listarBackups();
                System.out.println("Backups disponibles (" + backups.size() + "):");
                backups.forEach(System.out::println);
                break;
            case 3:
                if (manejadorDatos.limpiarArchivosTemporales()) {
                    System.out.println("✓ Archivos temporales limpiados.");
                } else {
                    System.out.println("❌ Error al limpiar archivos temporales.");
                }
                break;
            case 4:
                System.out.print("¿Cuántos backups mantener? (por defecto 5): ");
                int mantener = leerOpcion();
                if (mantener <= 0) mantener = 5;

                if (manejadorDatos.limpiarBackupsAntiguos(mantener)) {
                    System.out.println("✓ Backups antiguos limpiados.");
                } else {
                    System.out.println("❌ Error al limpiar backups.");
                }
                break;
            case 5:
                if (usuarioService.recargarDatos()) {
                    System.out.println("✓ Datos recargados exitosamente.");
                } else {
                    System.out.println("❌ Error al recargar datos.");
                }
                break;
            case 6:
                verificarIntegridadSistema();
                break;
            case 7:
                return;
            default:
                System.out.println("❌ Opción no válida.");
        }

        pausar();
    }

    /**
     * Cierra la sesión del usuario actual
     */
    private static void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("👋 Sesión cerrada. ¡Hasta pronto, " +
                    usuarioActual.getNombreUsuario() + "!");
            usuarioActual = null;
        }
    }

    /**
     * Sale del sistema guardando todos los datos
     */
    private static void salirDelSistema() {
        System.out.println("\n🔄 Guardando datos antes de salir...");

        if (usuarioService.guardarDatos()) {
            System.out.println("✓ Datos guardados exitosamente.");
        } else {
            System.out.println("⚠ Advertencia: No se pudieron guardar todos los datos.");
        }

        System.out.println("👋 ¡Gracias por usar el Sistema de Carreras!");
        System.out.println("🐎 ¡Que tengas un excelente día!");
    }

    // ===== MÉTODOS UTILITARIOS =====

    /**
     * Lee una opción numérica del usuario con manejo de errores
     */
    private static int leerOpcion() {
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            return opcion;
        } catch (Exception e) {
            scanner.nextLine(); // Limpiar buffer en caso de error
            return -1;
        }
    }

    /**
     * Pausa la ejecución esperando que el usuario presione Enter
     */
    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}