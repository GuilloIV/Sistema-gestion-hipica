package mx.uv.feaa.model.entidades;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;




public class Criador extends Usuario {
    private String licenciaCriador;
    private LocalDate fechaVigenciaLicencia;
    private List<Caballo> caballos;
    private String direccion;
    private String telefono;
    private String nombreHaras;
    private int caballosRegistrados;
    private int caballosActivos;

    // Constructor por defecto
    public Criador() {
        super();
        this.caballos = new ArrayList<>();
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    // Constructor básico
    public Criador(String nombreUsuario, String email) {
        puedeRegistrarCaballos();
        this.caballos = new ArrayList<>();
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    // Constructor completo
    public Criador(String idUsuario, String nombreUsuario, String email, String password,
                   String licenciaCriador, LocalDate fechaVigenciaLicencia,
                   String direccion, String telefono, String nombreHaras) {
        super(idUsuario, nombreUsuario, email, password);
        this.licenciaCriador = licenciaCriador;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
        this.direccion = direccion;
        this.telefono = telefono;
        this.nombreHaras = nombreHaras;
        this.caballos = new ArrayList<>();
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    @Override
    public String getTipoUsuarioEspecifico() {
        return "CRIADOR";
    }

    // Método para validar si la licencia está vigente
    public boolean validarLicencia() {
        if (licenciaCriador == null || licenciaCriador.trim().isEmpty() || fechaVigenciaLicencia == null) {
            return false;
        }
        LocalDate ahora = LocalDate.now();
        return !fechaVigenciaLicencia.isBefore(ahora);
    }

    // Registrar un nuevo caballo
    public boolean registrarCaballo(Caballo caballo) {
        if (caballo == null || !validarLicencia() || !isActivo() ||
                caballos.contains(caballo) ||
                caballo.getIdCaballo() == null || caballo.getIdCaballo().trim().isEmpty() ||
                caballo.getNombre() == null || caballo.getNombre().trim().isEmpty() ||
                caballo.getFechaNacimiento() == null) {
            return false;
        }

        caballos.add(caballo);
        caballosRegistrados++;
        actualizarContadorActivos();
        return true;
    }

    // Actualizar información de un caballo existente
    public boolean actualizarInfoCaballo(Caballo caballo) {
        if (caballo == null || !validarLicencia() || !isActivo()) {
            return false;
        }

        for (int i = 0; i < caballos.size(); i++) {
            if (caballos.get(i).equals(caballo)) {
                caballos.set(i, caballo);
                actualizarContadorActivos();
                return true;
            }
        }
        return false;
    }

    // Consultar historial de carreras de un caballo
    public List<HistorialCarrera> consultarHistorialCaballo(Caballo caballo) {
        if (caballo == null || !caballos.contains(caballo)) {
            return new ArrayList<>();
        }
        return caballo.getHistorialCarreras();
    }

    // Obtener todos los caballos
    public List<Caballo> obtenerCaballos() {
        return new ArrayList<>(caballos);
    }

    // Obtener solo caballos activos que pueden participar
    public List<Caballo> obtenerCaballosActivos() {
        return caballos.stream()
                .filter(Caballo::puedeParticipar)
                .collect(Collectors.toList());
    }

    // Obtener caballos por rango de edad
    public List<Caballo> obtenerCaballosPorEdad(int edadMinima, int edadMaxima) {
        return caballos.stream()
                .filter(caballo -> {
                    int edad = caballo.getEdadEnAnios();
                    return edad >= edadMinima && edad <= edadMaxima;
                })
                .collect(Collectors.toList());
    }

    // Obtener caballos debutantes
    public List<Caballo> obtenerCaballosDebutantes() {
        return caballos.stream()
                .filter(Caballo::esDebutante)
                .collect(Collectors.toList());
    }

    // Obtener caballos veteranos
    public List<Caballo> obtenerCaballosVeteranos() {
        return caballos.stream()
                .filter(Caballo::esVeterano)
                .collect(Collectors.toList());
    }

    // Buscar caballo por ID
    public Caballo buscarCaballo(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return caballos.stream()
                .filter(caballo -> caballo.getIdCaballo().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Buscar caballos por nombre (búsqueda parcial)
    public List<Caballo> buscarCaballosPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String nombreBusqueda = nombre.toLowerCase().trim();
        return caballos.stream()
                .filter(caballo -> caballo.getNombre().toLowerCase().contains(nombreBusqueda))
                .collect(Collectors.toList());
    }

    // Método privado para actualizar el contador de caballos activos
    private void actualizarContadorActivos() {
        this.caballosActivos = (int) caballos.stream()
                .filter(Caballo::puedeParticipar)
                .count();
    }

    // Obtener estadísticas del criador
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
                getNombreUsuario(),
                nombreHaras != null ? nombreHaras : "Sin especificar",
                caballosRegistrados, caballosActivos,
                totalCarreras, totalVictorias,
                validarLicencia() ? "Sí" : "No");
    }

    // Renovar licencia
    public boolean renovarLicencia(LocalDate nuevaFechaVigencia) {
        if (nuevaFechaVigencia != null && nuevaFechaVigencia.isAfter(LocalDate.now())) {
            this.fechaVigenciaLicencia = nuevaFechaVigencia;
            return true;
        }
        return false;
    }

    // Verificar si puede registrar caballos
    public boolean puedeRegistrarCaballos() {
        return validarLicencia() && isActivo();
    }

    // Eliminar caballo (método adicional útil)
    public boolean eliminarCaballo(String idCaballo) {
        if (idCaballo == null || idCaballo.trim().isEmpty() || !validarLicencia()) {
            return false;
        }

        boolean eliminado = caballos.removeIf(caballo -> caballo.getIdCaballo().equals(idCaballo));
        if (eliminado) {
            caballosRegistrados--;
            actualizarContadorActivos();
        }
        return eliminado;
    }

    // Getters y Setters
    public String getLicenciaCriador() {
        return licenciaCriador;
    }

    public void setLicenciaCriador(String licenciaCriador) {
        this.licenciaCriador = licenciaCriador;
    }

    public LocalDate getFechaVigenciaLicencia() {
        return fechaVigenciaLicencia;
    }

    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    public List<Caballo> getCaballos() {
        return new ArrayList<>(caballos);
    }

    public void setCaballos(List<Caballo> caballos) {
        this.caballos = caballos != null ? new ArrayList<>(caballos) : new ArrayList<>();
        this.caballosRegistrados = this.caballos.size();
        actualizarContadorActivos();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreHaras() {
        return nombreHaras;
    }

    public void setNombreHaras(String nombreHaras) {
        this.nombreHaras = nombreHaras;
    }

    public int getCaballosRegistrados() {
        return caballosRegistrados;
    }

    public void setCaballosRegistrados(int caballosRegistrados) {
        this.caballosRegistrados = caballosRegistrados;
    }

    public int getCaballosActivos() {
        return caballosActivos;
    }

    public void setCaballosActivos(int caballosActivos) {
        this.caballosActivos = caballosActivos;
    }

    @Override
    public String toString() {
        return String.format("Criador{usuario='%s', haras='%s', caballos=%d, licencia='%s', vigente=%s}",
                getNombreUsuario(),
                nombreHaras != null ? nombreHaras : "N/A",
                caballosRegistrados,
                licenciaCriador != null ? licenciaCriador : "N/A",
                validarLicencia() ? "Sí" : "No");
    }
}
