
package mx.uv.feaa.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa un criador de caballos en el sistema (hereda de Usuario)
 */
public class Criador extends Usuario {
    private String licenciaCriador;
    private LocalDate fechaVigenciaLicencia;
    private List<Caballo> caballos;
    private String direccion;
    private String telefono;
    private String nombreHaras;
    private int caballosRegistrados;
    private int caballosActivos;

    // Constructor
    public Criador() {
        super();
        this.caballos = new ArrayList<>();
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    public Criador(String nombreUsuario, String email, String licenciaCriador,
                   LocalDate fechaVigenciaLicencia, String direccion, String telefono, String nombreHaras) {
        super(nombreUsuario, email);
        this.licenciaCriador = licenciaCriador;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
        this.direccion = direccion;
        this.telefono = telefono;
        this.nombreHaras = nombreHaras;
        this.caballos = new ArrayList<>();
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    // Métodos de negocio
    /**
     * Valida si la licencia del criador está vigente
     */
    public boolean validarLicencia() {
        if (licenciaCriador == null || licenciaCriador.trim().isEmpty()) {
            return false;
        }

        if (fechaVigenciaLicencia == null) {
            return false;
        }

        LocalDate ahora = LocalDate.now();
        return !fechaVigenciaLicencia.isBefore(ahora);
    }

    /**
     * Registra un nuevo caballo
     */
    public boolean registrarCaballo(Caballo caballo) {
        if (caballo == null) {
            return false;
        }

        // Validar que la licencia esté vigente
        if (!validarLicencia()) {
            return false;
        }

        // Validar que la cuenta esté activa
        if (!isActivo()) {
            return false;
        }

        // Verificar que no esté ya registrado
        if (caballos.contains(caballo)) {
            return false;
        }

        // Validar datos básicos del caballo
        if (caballo.getId() == null || caballo.getId().trim().isEmpty() ||
                caballo.getNombre() == null || caballo.getNombre().trim().isEmpty() ||
                caballo.getFechaNacimiento() == null) {
            return false;
        }

        // Agregar el caballo
        caballos.add(caballo);
        caballosRegistrados++;
        actualizarContadorActivos();

        return true;
    }

    /**
     * Actualiza la información de un caballo
     */
    public boolean actualizarInfoCaballo(Caballo caballo) {
        if (caballo == null || !validarLicencia() || !isActivo()) {
            return false;
        }

        // Buscar el caballo en la lista
        for (int i = 0; i < caballos.size(); i++) {
            if (caballos.get(i).equals(caballo)) {
                caballos.set(i, caballo);
                actualizarContadorActivos();
                return true;
            }
        }

        return false;
    }

    /**
     * Consulta el historial de carreras de un caballo específico
     */
    public List<HistorialCarrera> consultarHistorialCaballo(Caballo caballo) {
        if (caballo == null || !caballos.contains(caballo)) {
            return new ArrayList<>();
        }

        return caballo.getHistorialCarreras();
    }

    /**
     * Obtiene todos los caballos del criador
     */
    public List<Caballo> obtenerCaballos() {
        return new ArrayList<>(caballos);
    }

    /**
     * Obtiene caballos activos (que pueden participar)
     */
    public List<Caballo> obtenerCaballosActivos() {
        return caballos.stream()
                .filter(Caballo::puedeParticipar)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene caballos por edad
     */
    public List<Caballo> obtenerCaballosPorEdad(int edadMinima, int edadMaxima) {
        return caballos.stream()
                .filter(caballo -> {
                    int edad = caballo.getEdadEnAnios();
                    return edad >= edadMinima && edad <= edadMaxima;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene caballos debutantes
     */
    public List<Caballo> obtenerCaballosDebutantes() {
        return caballos.stream()
                .filter(Caballo::esDebutante)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene caballos veteranos
     */
    public List<Caballo> obtenerCaballosVeteranos() {
        return caballos.stream()
                .filter(Caballo::esVeterano)
                .collect(Collectors.toList());
    }

    /**
     * Busca un caballo por ID
     */
    public Caballo buscarCaballo(String id) {
        return caballos.stream()
                .filter(caballo -> caballo.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca caballos por nombre (búsqueda parcial)
     */
    public List<Caballo> buscarCaballosPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String nombreBusqueda = nombre.toLowerCase().trim();
        return caballos.stream()
                .filter(caballo -> caballo.getNombre().toLowerCase().contains(nombreBusqueda))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el contador de caballos activos
     */
    private void actualizarContadorActivos() {
        this.caballosActivos = (int) caballos.stream()
                .filter(Caballo::puedeParticipar)
                .count();
    }

    /**
     * Obtiene estadísticas del criador
     */
    public String obtenerEstadisticas() {
        int totalCarreras = caballos.stream()
                .mapToInt(caballo -> caballo.getHistorialCarreras().size())
                .sum();

        int totalVictorias = caballos.stream()
                .mapToInt(caballo -> (int) caballo.getHistorialCarreras().stream()
                        .filter(HistorialCarrera::esVictoria)
                        .count())
                .sum();

        return String.format(
                "Estadísticas del Criador '%s':\n" +
                        "- Haras: %s\n" +
                        "- Caballos registrados: %d\n" +
                        "- Caballos activos: %d\n" +
                        "- Total de carreras: %d\n" +
                        "- Total de victorias: %d\n" +
                        "- Licencia vigente: %s",
                getNombreUsuario(), nombreHaras, caballosRegistrados, caballosActivos,
                totalCarreras, totalVictorias, validarLicencia() ? "Sí" : "No"
        );
    }

    /**
     * Renueva la licencia del criador
     */
    public boolean renovarLicencia(LocalDate nuevaFechaVigencia) {
        if (nuevaFechaVigencia != null && nuevaFechaVigencia.isAfter(LocalDate.now())) {
            this.fechaVigenciaLicencia = nuevaFechaVigencia;
            return true;
        }
        return false;
    }

    /**
     * Verifica si puede registrar más caballos
     */
    public boolean puedeRegistrarCaballos() {
        return validarLicencia() && isActivo();
    }

    // Getters y Setters
    public String getLicenciaCriador() { return licenciaCriador; }
    public void setLicenciaCriador(String licenciaCriador) { this.licenciaCriador = licenciaCriador; }

    public LocalDate getFechaVigenciaLicencia() { return fechaVigenciaLicencia; }
    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    public List<Caballo> getCaballos() { return new ArrayList<>(caballos); }
    public void setCaballos(List<Caballo> caballos) {
        this.caballos = caballos != null ? new ArrayList<>(caballos) : new ArrayList<>();
        this.caballosRegistrados = this.caballos.size();
        actualizarContadorActivos();
    }
    public void setCaballosRegistrados(int caballosRegistrados) {
        this.caballosRegistrados = caballosRegistrados;
    }



    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombreHaras() { return nombreHaras; }
    public void setNombreHaras(String nombreHaras) { this.nombreHaras = nombreHaras; }

    public int getCaballosRegistrados() { return caballosRegistrados; }
    public int getCaballosActivos() { return caballosActivos; }

    @Override
    public String toString() {
        return String.format("Criador{usuario='%s', haras='%s', caballos=%d, licencia='%s', vigente=%s}",
                getNombreUsuario(), nombreHaras, caballosRegistrados, licenciaCriador,
                validarLicencia() ? "Sí" : "No");
    }
}