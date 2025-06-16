package mx.uv.feaa.view;

import mx.uv.feaa.model.dao.CaballoDAO;
import mx.uv.feaa.model.dao.EstadisticasRendimientoDAO;
import mx.uv.feaa.model.dao.HistorialCarreraDAO;
import mx.uv.feaa.model.entidades.Caballo;
import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.model.entidades.EstadisticasRendimiento;
import mx.uv.feaa.model.entidades.HistorialCarrera;
import mx.uv.feaa.enumeracion.SexoCaballo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class MenuCriadorView {
    private final Scanner scanner;
    private final Criador criador;
    private final CaballoDAO caballoDAO;
    private final EstadisticasRendimientoDAO estadisticasDAO;
    private final HistorialCarreraDAO historialDAO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MenuCriadorView(Criador criador) {
        this.scanner = new Scanner(System.in);
        this.criador = criador;
        this.caballoDAO = new CaballoDAO();
        this.estadisticasDAO = new EstadisticasRendimientoDAO();
        this.historialDAO = new HistorialCarreraDAO();
    }

    public void mostrar() {
        System.out.println("\n=== MENÚ CRIADOR ===");
        System.out.println("Bienvenido, " + criador.getNombreUsuario());
        if (criador.getNombreHaras() != null) {
            System.out.println("Haras: " + criador.getNombreHaras());
        }
        System.out.println("Licencia: " + (criador.validarLicencia() ? "VIGENTE" : "VENCIDA"));

        if (!criador.validarLicencia()) {
            System.out.println("⚠️  ATENCIÓN: Su licencia ha vencido. Contacte al administrador para renovarla.");
        }

        while (true) {
            mostrarMenuPrincipal();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    listarCaballos();
                    break;
                case "2":
                    registrarCaballo();
                    break;
                case "3":
                    editarCaballo();
                    break;
                case "4":
                    eliminarCaballo();
                    break;
                case "5":
                    verEstadisticasCaballo();
                    break;
                case "6":
                    verHistorialCaballo();
                    break;
                case "7":
                    verResumenGeneral();
                    break;
                case "0":
                    System.out.println("Cerrando sesión...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }

            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("          GESTIÓN DE CABALLOS");
        System.out.println("=".repeat(50));
        System.out.println("1. Ver mis caballos registrados");
        System.out.println("2. Registrar nuevo caballo");
        System.out.println("3. Editar información de caballo");
        System.out.println("4. Eliminar caballo");
        System.out.println("5. Ver estadísticas de caballo");
        System.out.println("6. Ver historial de carreras");
        System.out.println("7. Resumen general del haras");
        System.out.println("0. Cerrar sesión");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
    }

    private void listarCaballos() {
        System.out.println("\n=== MIS CABALLOS REGISTRADOS ===");

        try {
            List<Caballo> caballos = caballoDAO.getByCriador(criador.getIdUsuario());

            if (caballos.isEmpty()) {
                System.out.println("No tiene caballos registrados.");
                return;
            }

            System.out.println("Total de caballos: " + caballos.size());
            System.out.println("-".repeat(80));
            System.out.printf("%-15s %-20s %-12s %-8s %-10s %-15s%n",
                    "ID", "NOMBRE", "F.NACIMIENTO", "SEXO", "PESO (KG)", "ÚLTIMA CARRERA");
            System.out.println("-".repeat(80));

            for (Caballo caballo : caballos) {
                String ultimaCarrera = caballo.getUltimaCarrera() != null ?
                        caballo.getUltimaCarrera().format(formatter) : "Sin carreras";

                System.out.printf("%-15s %-20s %-12s %-8s %-10.1f %-15s%n",
                        caballo.getIdCaballo().substring(0, Math.min(15, caballo.getIdCaballo().length())),
                        caballo.getNombre(),
                        caballo.getFechaNacimiento().format(formatter),
                        caballo.getSexo(),
                        caballo.getPeso(),
                        ultimaCarrera);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de caballos: " + e.getMessage());
        }
    }

    private void registrarCaballo() {
        System.out.println("\n=== REGISTRAR NUEVO CABALLO ===");

        if (!criador.validarLicencia()) {
            System.out.println("No puede registrar caballos con licencia vencida.");
            return;
        }

        try {
            System.out.print("Nombre del caballo: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }

            System.out.print("Fecha de nacimiento (dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine().trim();
            LocalDate fechaNacimiento;
            try {
                fechaNacimiento = LocalDate.parse(fechaStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha incorrecto. Use dd/MM/yyyy");
                return;
            }

            System.out.println("Sexo del caballo:");
            System.out.println("1. MACHO");
            System.out.println("2. HEMBRA");
            System.out.print("Seleccione (1-2): ");
            String sexoOpcion = scanner.nextLine().trim();

            SexoCaballo sexo;
            switch (sexoOpcion) {
                case "1":
                    sexo = SexoCaballo.MACHO;
                    break;
                case "2":
                    sexo = SexoCaballo.HEMBRA;
                    break;
                default:
                    System.out.println("Opción de sexo no válida.");
                    return;
            }

            System.out.print("Peso (kg): ");
            String pesoStr = scanner.nextLine().trim();
            double peso;
            try {
                peso = Double.parseDouble(pesoStr);
                if (peso <= 0) {
                    System.out.println("El peso debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Peso no válido.");
                return;
            }

            System.out.print("Pedigrí: ");
            String pedigri = scanner.nextLine().trim();
            if (pedigri.isEmpty()) {
                System.out.println("El pedigrí no puede estar vacío.");
                return;
            }

            // Crear el caballo
            Caballo nuevoCaballo = new Caballo();
            nuevoCaballo.setIdCaballo(UUID.randomUUID().toString());
            nuevoCaballo.setNombre(nombre);
            nuevoCaballo.setFechaNacimiento(fechaNacimiento);
            nuevoCaballo.setSexo(sexo);
            nuevoCaballo.setPeso(peso);
            nuevoCaballo.setPedigri(pedigri);
            nuevoCaballo.setCriadorId(criador.getIdUsuario());
            nuevoCaballo.setUltimaCarrera(null);
            //Tuve un problema al registrar el 8 parametro

            // Guardar en la base de datos
            if (caballoDAO.save(nuevoCaballo)) {
                System.out.println("✅ Caballo registrado exitosamente.");
                System.out.println("ID asignado: " + nuevoCaballo.getIdCaballo());
            } else {
                System.out.println("❌ Error al registrar el caballo.");
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar el caballo: " + e.getMessage());
        }
    }

    private void editarCaballo() {
        System.out.println("\n=== EDITAR CABALLO ===");

        if (!criador.validarLicencia()) {
            System.out.println("No puede editar caballos con licencia vencida.");
            return;
        }

        System.out.print("Ingrese el ID del caballo a editar: ");
        String idCaballo = scanner.nextLine().trim();

        try {
            Optional<Caballo> caballoOpt = caballoDAO.getById(idCaballo);

            if (!caballoOpt.isPresent()) {
                System.out.println("No se encontró un caballo con ese ID.");
                return;
            }

            Caballo caballo = caballoOpt.get();

            // Verificar que el caballo pertenece al criador
            if (!caballo.getCriadorId().equals(criador.getIdUsuario())) {
                System.out.println("No tiene permisos para editar este caballo.");
                return;
            }

            System.out.println("Editando caballo: " + caballo.getNombre());
            System.out.println("Deje en blanco para mantener el valor actual.");

            // Editar nombre
            System.out.print("Nuevo nombre [" + caballo.getNombre() + "]: ");
            String nuevoNombre = scanner.nextLine().trim();
            if (!nuevoNombre.isEmpty()) {
                caballo.setNombre(nuevoNombre);
            }

            // Editar peso
            System.out.print("Nuevo peso [" + caballo.getPeso() + " kg]: ");
            String nuevoPesoStr = scanner.nextLine().trim();
            if (!nuevoPesoStr.isEmpty()) {
                try {
                    double nuevoPeso = Double.parseDouble(nuevoPesoStr);
                    if (nuevoPeso > 0) {
                        caballo.setPeso(nuevoPeso);
                    } else {
                        System.out.println("Peso no válido, manteniendo el actual.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Peso no válido, manteniendo el actual.");
                }
            }

            // Editar pedigrí
            System.out.print("Nuevo pedigrí [" + caballo.getPedigri() + "]: ");
            String nuevoPedigri = scanner.nextLine().trim();
            if (!nuevoPedigri.isEmpty()) {
                caballo.setPedigri(nuevoPedigri);
            }

            // Guardar cambios
            if (caballoDAO.update(caballo)) {
                System.out.println("✅ Caballo actualizado exitosamente.");
            } else {
                System.out.println("❌ Error al actualizar el caballo.");
            }

        } catch (SQLException e) {
            System.err.println("Error al editar el caballo: " + e.getMessage());
        }
    }

    private void eliminarCaballo() {
        System.out.println("\n=== ELIMINAR CABALLO ===");

        if (!criador.validarLicencia()) {
            System.out.println("No puede eliminar caballos con licencia vencida.");
            return;
        }

        System.out.print("Ingrese el ID del caballo a eliminar: ");
        String idCaballo = scanner.nextLine().trim();

        try {
            Optional<Caballo> caballoOpt = caballoDAO.getById(idCaballo);

            if (!caballoOpt.isPresent()) {
                System.out.println("No se encontró un caballo con ese ID.");
                return;
            }

            Caballo caballo = caballoOpt.get();

            // Verificar que el caballo pertenece al criador
            if (!caballo.getCriadorId().equals(criador.getIdUsuario())) {
                System.out.println("No tiene permisos para eliminar este caballo.");
                return;
            }

            System.out.println("Caballo a eliminar: " + caballo.getNombre());
            System.out.println("⚠️  ADVERTENCIA: Esta acción no se puede deshacer.");
            System.out.print("¿Está seguro de eliminar este caballo? (sí/no): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("sí") || confirmacion.equals("si")) {
                if (caballoDAO.delete(idCaballo)) {
                    System.out.println("✅ Caballo eliminado exitosamente.");
                } else {
                    System.out.println("❌ Error al eliminar el caballo.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar el caballo: " + e.getMessage());
        }
    }

    private void verEstadisticasCaballo() {
        System.out.println("\n=== ESTADÍSTICAS DE CABALLO ===");

        System.out.print("Ingrese el ID del caballo: ");
        String idCaballo = scanner.nextLine().trim();

        try {
            Optional<Caballo> caballoOpt = caballoDAO.getById(idCaballo);

            if (!caballoOpt.isPresent()) {
                System.out.println("No se encontró un caballo con ese ID.");
                return;
            }

            Caballo caballo = caballoOpt.get();

            // Verificar que el caballo pertenece al criador
            if (!caballo.getCriadorId().equals(criador.getIdUsuario())) {
                System.out.println("No tiene permisos para ver este caballo.");
                return;
            }

            System.out.println("\n--- INFORMACIÓN DEL CABALLO ---");
            System.out.println("Nombre: " + caballo.getNombre());
            System.out.println("Fecha de nacimiento: " + caballo.getFechaNacimiento().format(formatter));
            System.out.println("Sexo: " + caballo.getSexo());
            System.out.println("Peso: " + caballo.getPeso() + " kg");
            System.out.println("Pedigrí: " + caballo.getPedigri());

            // Obtener estadísticas de rendimiento
            Optional<EstadisticasRendimiento> estadisticasOpt = estadisticasDAO.getByCaballoId(idCaballo);

            if (estadisticasOpt.isPresent()) {
                EstadisticasRendimiento stats = estadisticasOpt.get();
                System.out.println("\n--- ESTADÍSTICAS DE RENDIMIENTO ---");
                System.out.println("Total de carreras: " + stats.getTotalCarreras());
                System.out.println("Victorias: " + stats.getVictorias());
                System.out.println("Colocaciones: " + stats.getColocaciones());
                System.out.println("Porcentaje de victorias: " + String.format("%.2f%%", stats.getPorcentajeVictorias()));
                if (stats.getPromedioTiempo() != null) {
                    System.out.println("Tiempo promedio: " + stats.getPromedioTiempo());
                }
            } else {
                System.out.println("\n--- ESTADÍSTICAS DE RENDIMIENTO ---");
                System.out.println("Sin estadísticas registradas para este caballo.");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener las estadísticas: " + e.getMessage());
        }
    }

    private void verHistorialCaballo() {
        System.out.println("\n=== HISTORIAL DE CARRERAS ===");

        System.out.print("Ingrese el ID del caballo: ");
        String idCaballo = scanner.nextLine().trim();

        try {
            Optional<Caballo> caballoOpt = caballoDAO.getById(idCaballo);

            if (!caballoOpt.isPresent()) {
                System.out.println("No se encontró un caballo con ese ID.");
                return;
            }

            Caballo caballo = caballoOpt.get();

            // Verificar que el caballo pertenece al criador
            if (!caballo.getCriadorId().equals(criador.getIdUsuario())) {
                System.out.println("No tiene permisos para ver este caballo.");
                return;
            }

            List<HistorialCarrera> historial = historialDAO.getByCaballoId(idCaballo);

            System.out.println("\nHistorial de " + caballo.getNombre());

            if (historial.isEmpty()) {
                System.out.println("No hay carreras registradas para este caballo.");
                return;
            }

            System.out.println("-".repeat(70));
            System.out.printf("%-12s %-8s %-12s %-20s%n", "FECHA", "POSICIÓN", "TIEMPO", "HIPÓDROMO");
            System.out.println("-".repeat(70));

            for (HistorialCarrera h : historial) {
                String fecha = h.getFecha() != null ? h.getFecha().format(formatter) : "Sin fecha";
                String tiempo = h.getTiempo() != null ? h.getTiempo().toString() : "N/A";
                String hipodromo = h.getHipodromo() != null ? h.getHipodromo() : "N/A";

                System.out.printf("%-12s %-8d %-12s %-20s%n",
                        fecha, h.getPosicion(), tiempo, hipodromo);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el historial: " + e.getMessage());
        }
    }

    private void verResumenGeneral() {
        System.out.println("\n=== RESUMEN GENERAL DEL HARAS ===");

        try {
            List<Caballo> caballos = caballoDAO.getByCriador(criador.getIdUsuario());

            if (caballos.isEmpty()) {
                System.out.println("No tiene caballos registrados.");
                return;
            }

            System.out.println("Haras: " + (criador.getNombreHaras() != null ? criador.getNombreHaras() : "Sin nombre"));
            System.out.println("Total de caballos: " + caballos.size());

            // Contar por sexo
            long machos = caballos.stream().filter(c -> c.getSexo() == SexoCaballo.MACHO).count();
            long hembras = caballos.stream().filter(c -> c.getSexo() == SexoCaballo.HEMBRA).count();

            System.out.println("Machos: " + machos);
            System.out.println("Hembras: " + hembras);

            // Peso promedio
            double pesoPromedio = caballos.stream().mapToDouble(Caballo::getPeso).average().orElse(0.0);
            System.out.printf("Peso promedio: %.1f kg%n", pesoPromedio);

            // Caballos con carreras recientes
            long conCarreras = caballos.stream().filter(c -> c.getUltimaCarrera() != null).count();
            System.out.println("Caballos con carreras registradas: " + conCarreras);

            // Mostrar top 5 caballos más recientes
            System.out.println("\n--- ÚLTIMOS CABALLOS REGISTRADOS ---");
            caballos.stream()
                    .sorted((c1, c2) -> c2.getFechaNacimiento().compareTo(c1.getFechaNacimiento()))
                    .limit(5)
                    .forEach(c -> System.out.println("• " + c.getNombre() + " (" +
                            c.getFechaNacimiento().format(formatter) + ")"));

        } catch (SQLException e) {
            System.err.println("Error al generar el resumen: " + e.getMessage());
        }
    }
}