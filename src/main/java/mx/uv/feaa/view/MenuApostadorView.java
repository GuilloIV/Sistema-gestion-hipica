package mx.uv.feaa.view;

import mx.uv.feaa.model.dao.ApuestaDAO;
import mx.uv.feaa.model.dao.ApostadorDAO;
import mx.uv.feaa.model.entidades.Apostador;
import mx.uv.feaa.model.entidades.Apuesta;
import mx.uv.feaa.enumeracion.EstadoApuesta;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MenuApostadorView {
    private final Scanner scanner;
    private final Apostador apostador;
    private final ApostadorDAO apostadorDAO;
    private final ApuestaDAO apuestaDAO;

    public MenuApostadorView(Apostador apostador) {
        this.scanner = new Scanner(System.in);
        this.apostador = apostador;
        this.apostadorDAO = new ApostadorDAO();
        this.apuestaDAO = new ApuestaDAO();
    }

    public void mostrar() {
        System.out.println("\n=== MENÚ APOSTADOR ===");
        System.out.println("Bienvenido, " + apostador.getNombreUsuario());
        System.out.println("Saldo actual: $" + String.format("%.2f", apostador.getSaldo()));

        while (true) {
            mostrarMenuPrincipal();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    consultarSaldo();
                    break;
                case "2":
                    consultarApuestas();
                    break;
                case "3":
                    realizarDeposito();
                    break;
                case "4":
                    realizarRetiro();
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
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         GESTIÓN DE CUENTA");
        System.out.println("=".repeat(40));
        System.out.println("1. Consultar saldo");
        System.out.println("2. Ver mis apuestas");
        System.out.println("3. Realizar depósito");
        System.out.println("4. Realizar retiro");
        System.out.println("0. Cerrar sesión");
        System.out.println("=".repeat(40));
        System.out.print("Seleccione una opción: ");
    }

    private void consultarSaldo() {
        System.out.println("\n=== CONSULTA DE SALDO ===");

        try {
            // Obtener datos actualizados del apostador
            var apostadorActual = apostadorDAO.getById(apostador.getIdUsuario());

            if (apostadorActual.isPresent()) {
                Apostador datos = apostadorActual.get();
                apostador.setSaldo(datos.getSaldo()); // Actualizar saldo local

                System.out.println("Nombre: " + datos.getNombre());
                System.out.println("Usuario: " + datos.getNombreUsuario());
                System.out.println("Saldo disponible: $" + String.format("%.2f", datos.getSaldo()));
                System.out.println("Límite de apuesta: $" + String.format("%.2f", datos.getLimiteApuesta()));
                System.out.println("Teléfono: " + datos.getTelefono());
            } else {
                System.out.println("Error al obtener los datos actuales.");
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el saldo: " + e.getMessage());
        }
    }

    private void consultarApuestas() {
        System.out.println("\n=== MIS APUESTAS ===");

        try {
            List<Apuesta> apuestas = apuestaDAO.getByApostadorId(apostador.getIdUsuario());

            if (apuestas.isEmpty()) {
                System.out.println("No tiene apuestas registradas.");
                return;
            }

            System.out.println("Total de apuestas: " + apuestas.size());
            System.out.println("-".repeat(80));
            System.out.printf("%-15s %-12s %-10s %-12s %-15s%n",
                    "ID APUESTA", "TIPO", "MONTO", "ESTADO", "GANANCIA");
            System.out.println("-".repeat(80));

            double totalApostado = 0;
            double totalGanado = 0;

            for (Apuesta apuesta : apuestas) {
                String idCorto = apuesta.getId().substring(0, Math.min(15, apuesta.getId().length()));

                System.out.printf("%-15s %-12s $%-9.2f %-12s $%-14.2f%n",
                        idCorto,
                        apuesta.getTipoApuesta(),
                        apuesta.getMontoApostado(),
                        apuesta.getEstado(),
                        apuesta.getMontoGanado());

                totalApostado += apuesta.getMontoApostado();
                if (apuesta.getEstado() == EstadoApuesta.GANADORA || apuesta.getEstado() == EstadoApuesta.PAGADA) {
                    totalGanado += apuesta.getMontoGanado();
                }
            }

            System.out.println("-".repeat(80));
            System.out.printf("Total apostado: $%.2f%n", totalApostado);
            System.out.printf("Total ganado: $%.2f%n", totalGanado);
            System.out.printf("Balance: $%.2f%n", totalGanado - totalApostado);

        } catch (SQLException e) {
            System.err.println("Error al consultar las apuestas: " + e.getMessage());
        }
    }

    private void realizarDeposito() {
        System.out.println("\n=== REALIZAR DEPÓSITO ===");
        System.out.println("Saldo actual: $" + String.format("%.2f", apostador.getSaldo()));

        System.out.print("Ingrese el monto a depositar: $");
        String montoStr = scanner.nextLine().trim();

        try {
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
                System.out.println("El monto debe ser mayor a 0.");
                return;
            }

            if (monto > 10000) {
                System.out.println("El monto máximo de depósito es $10,000.00");
                return;
            }

            double nuevoSaldo = apostador.getSaldo() + monto;

            System.out.printf("Confirmar depósito de $%.2f%n", monto);
            System.out.printf("Nuevo saldo será: $%.2f%n", nuevoSaldo);
            System.out.print("¿Confirmar operación? (sí/no): ");

            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("sí") || confirmacion.equals("si")) {
                if (apostadorDAO.actualizarSaldo(apostador.getIdUsuario(), nuevoSaldo)) {
                    apostador.setSaldo(nuevoSaldo);
                    System.out.println("✅ Depósito realizado exitosamente.");
                    System.out.printf("Nuevo saldo: $%.2f%n", nuevoSaldo);
                } else {
                    System.out.println("❌ Error al procesar el depósito.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Monto no válido.");
        } catch (SQLException e) {
            System.err.println("Error al realizar el depósito: " + e.getMessage());
        }
    }

    private void realizarRetiro() {
        System.out.println("\n=== REALIZAR RETIRO ===");
        System.out.println("Saldo actual: $" + String.format("%.2f", apostador.getSaldo()));

        if (apostador.getSaldo() <= 0) {
            System.out.println("No tiene saldo disponible para retirar.");
            return;
        }

        System.out.print("Ingrese el monto a retirar: $");
        String montoStr = scanner.nextLine().trim();

        try {
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
                System.out.println("El monto debe ser mayor a 0.");
                return;
            }

            if (monto > apostador.getSaldo()) {
                System.out.println("No tiene suficiente saldo para retirar esa cantidad.");
                System.out.printf("Saldo disponible: $%.2f%n", apostador.getSaldo());
                return;
            }

            double nuevoSaldo = apostador.getSaldo() - monto;

            System.out.printf("Confirmar retiro de $%.2f%n", monto);
            System.out.printf("Nuevo saldo será: $%.2f%n", nuevoSaldo);
            System.out.print("¿Confirmar operación? (sí/no): ");

            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("sí") || confirmacion.equals("si")) {
                if (apostadorDAO.actualizarSaldo(apostador.getIdUsuario(), nuevoSaldo)) {
                    apostador.setSaldo(nuevoSaldo);
                    System.out.println("✅ Retiro realizado exitosamente.");
                    System.out.printf("Nuevo saldo: $%.2f%n", nuevoSaldo);
                } else {
                    System.out.println("❌ Error al procesar el retiro.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Monto no válido.");
        } catch (SQLException e) {
            System.err.println("Error al realizar el retiro: " + e.getMessage());
        }
    }
}