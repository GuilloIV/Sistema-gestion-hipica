package mx.uv.feaa.servicios;



import mx.uv.feaa.model.*;
import mx.uv.feaa.util.JsonAdapter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión completa de usuarios del sistema
 */
public class UsuarioService {
    private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());

    // Rutas de archivos JSON
    private static final String USUARIOS_FILE = "data/usuarios.json";
    private static final String CRIADORES_FILE = "data/criadores.json";
    private static final String APOSTADORES_FILE = "data/apostadores.json";
    private static final String BACKUP_DIR = "data/backup/";

    // Cache de usuarios en memoria
    private Map<String, Usuario> usuarios;
    private Map<String, Criador> criadores;
    private Map<String, Apostador> apostadores;

    // Singleton
    private static UsuarioService instance;

    private UsuarioService() {
        this.usuarios = new HashMap<>();
        this.criadores = new HashMap<>();
        this.apostadores = new HashMap<>();
        inicializarDirectorios();
        cargarDatos();
    }

    public static UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }

    /**
     * Inicializa los directorios necesarios
     */
    private void inicializarDirectorios() {
        try {
            new File("data").mkdirs();
            new File(BACKUP_DIR).mkdirs();
            logger.info("Directorios inicializados correctamente");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear directorios", e);
        }
    }

    /**
     * Carga todos los datos desde archivos JSON
     */
    public boolean cargarDatos() {
        try {
            cargarUsuarios();
            cargarCriadores();
            cargarApostadores();
            logger.info("Datos cargados exitosamente");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar datos", e);
            return false;
        }
    }

    /**
     * Guarda todos los datos en archivos JSON
     */
    public boolean guardarDatos() {
        try {
            // Crear backup antes de guardar
            crearBackup();

            // Guardar datos actuales
            guardarUsuarios();
            guardarCriadores();
            guardarApostadores();

            logger.info("Datos guardados exitosamente");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar datos", e);
            return false;
        }
    }

    /**
     * Carga usuarios base desde JSON
     */
    private void cargarUsuarios() {
        File file = new File(USUARIOS_FILE);
        if (file.exists()) {
            List<Usuario> listaUsuarios = JsonAdapter.fromJsonToList(
                    JsonAdapter.loadFromFile(USUARIOS_FILE, String.class), Usuario.class);

            if (listaUsuarios != null) {
                usuarios.clear();
                for (Usuario usuario : listaUsuarios) {
                    usuarios.put(usuario.getId(), usuario);
                }
            }
        }
    }

    /**
     * Carga criadores desde JSON
     */
    private void cargarCriadores() {
        File file = new File(CRIADORES_FILE);
        if (file.exists()) {
            List<Criador> listaCriadores = JsonAdapter.fromJsonToList(
                    JsonAdapter.loadFromFile(CRIADORES_FILE, String.class), Criador.class);

            if (listaCriadores != null) {
                criadores.clear();
                for (Criador criador : listaCriadores) {
                    criadores.put(criador.getId(), criador);
                    usuarios.put(criador.getId(), criador); // También en el mapa general
                }
            }
        }
    }

    /**
     * Carga apostadores desde JSON
     */
    private void cargarApostadores() {
        File file = new File(APOSTADORES_FILE);
        if (file.exists()) {
            List<Apostador> listaApostadores = JsonAdapter.fromJsonToList(
                    JsonAdapter.loadFromFile(APOSTADORES_FILE, String.class), Apostador.class);

            if (listaApostadores != null) {
                apostadores.clear();
                for (Apostador apostador : listaApostadores) {
                    apostadores.put(apostador.getId(), apostador);
                    usuarios.put(apostador.getId(), apostador); // También en el mapa general
                }
            }
        }
    }

    /**
     * Guarda usuarios en JSON
     */
    private void guardarUsuarios() {
        List<Usuario> lista = new ArrayList<>(usuarios.values());
        JsonAdapter.saveToFile(lista, USUARIOS_FILE);
    }

    /**
     * Guarda criadores en JSON
     */
    private void guardarCriadores() {
        List<Criador> lista = new ArrayList<>(criadores.values());
        JsonAdapter.saveToFile(lista, CRIADORES_FILE);
    }

    /**
     * Guarda apostadores en JSON
     */
    private void guardarApostadores() {
        List<Apostador> lista = new ArrayList<>(apostadores.values());
        JsonAdapter.saveToFile(lista, APOSTADORES_FILE);
    }

    /**
     * Crea backup de los datos actuales
     */
    private void crearBackup() {
        try {
            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            String backupSuffix = "_backup_" + timestamp + ".json";

            // Backup de cada archivo
            if (new File(USUARIOS_FILE).exists()) {
                JsonAdapter.saveToFile(new ArrayList<>(usuarios.values()),
                        BACKUP_DIR + "usuarios" + backupSuffix);
            }

            if (new File(CRIADORES_FILE).exists()) {
                JsonAdapter.saveToFile(new ArrayList<>(criadores.values()),
                        BACKUP_DIR + "criadores" + backupSuffix);
            }

            if (new File(APOSTADORES_FILE).exists()) {
                JsonAdapter.saveToFile(new ArrayList<>(apostadores.values()),
                        BACKUP_DIR + "apostadores" + backupSuffix);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al crear backup", e);
        }
    }

    // ===== MÉTODOS DE GESTIÓN DE USUARIOS =====

    /**
     * Registra un nuevo usuario
     */
    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        // Validar datos básicos
        if (!usuario.validarEmail() || !usuario.validarNombreUsuario()) {
            return false;
        }

        // Verificar unicidad
        if (existeUsuario(usuario.getNombreUsuario()) || existeEmail(usuario.getEmail())) {
            return false;
        }

        // Generar ID si no tiene
        if (usuario.getId() == null || usuario.getId().trim().isEmpty()) {
            usuario.setId(generarIdUsuario());
        }

        // Agregar al mapa correspondiente
        usuarios.put(usuario.getId(), usuario);

        if (usuario instanceof Criador) {
            criadores.put(usuario.getId(), (Criador) usuario);
        } else if (usuario instanceof Apostador) {
            apostadores.put(usuario.getId(), (Apostador) usuario);
        }

        // Guardar cambios
        return guardarDatos();
    }

    /**
     * Actualiza información de un usuario
     */
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            return false;
        }

        if (!usuarios.containsKey(usuario.getId())) {
            return false;
        }

        // Validar datos
        if (!usuario.validarEmail() || !usuario.validarNombreUsuario()) {
            return false;
        }

        // Actualizar en todos los mapas
        usuarios.put(usuario.getId(), usuario);

        if (usuario instanceof Criador) {
            criadores.put(usuario.getId(), (Criador) usuario);
        } else if (usuario instanceof Apostador) {
            apostadores.put(usuario.getId(), (Apostador) usuario);
        }

        return guardarDatos();
    }

    /**
     * Elimina un usuario (desactivación lógica)
     */
    public boolean eliminarUsuario(String id) {
        Usuario usuario = usuarios.get(id);
        if (usuario == null) {
            return false;
        }

        usuario.desactivarCuenta();
        return actualizarUsuario(usuario);
    }

    /**
     * Busca usuario por ID
     */
    public Usuario buscarUsuarioPorId(String id) {
        return usuarios.get(id);
    }

    /**
     * Busca usuario por nombre de usuario
     */
    public Usuario buscarUsuarioPorNombre(String nombreUsuario) {
        return usuarios.values().stream()
                .filter(u -> u.getNombreUsuario().equals(nombreUsuario))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca usuario por email
     */
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si existe un usuario con ese nombre
     */
    public boolean existeUsuario(String nombreUsuario) {
        return buscarUsuarioPorNombre(nombreUsuario) != null;
    }

    /**
     * Verifica si existe un usuario con ese email
     */
    public boolean existeEmail(String email) {
        return buscarUsuarioPorEmail(email) != null;
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarios.values().stream()
                .filter(Usuario::isActivo)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene usuarios por tipo
     */
    public List<Usuario> obtenerUsuariosPorTipo(String tipo) {
        return usuarios.values().stream()
                .filter(u -> u.getTipoUsuario().equals(tipo))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS ESPECÍFICOS PARA CRIADORES =====

    /**
     * Obtiene todos los criadores
     */
    public List<Criador> obtenerCriadores() {
        return new ArrayList<>(criadores.values());
    }

    /**
     * Obtiene criadores activos
     */
    public List<Criador> obtenerCriadoresActivos() {
        return criadores.values().stream()
                .filter(Usuario::isActivo)
                .collect(Collectors.toList());
    }

    /**
     * Busca criador por licencia
     */
    public Criador buscarCriadorPorLicencia(String licencia) {
        return criadores.values().stream()
                .filter(c -> c.getLicenciaCriador().equals(licencia))
                .findFirst()
                .orElse(null);
    }

    // ===== MÉTODOS ESPECÍFICOS PARA APOSTADORES =====

    /**
     * Obtiene todos los apostadores
     */
    public List<Apostador> obtenerApostadores() {
        return new ArrayList<>(apostadores.values());
    }

    /**
     * Obtiene apostadores activos
     */
    public List<Apostador> obtenerApostadoresActivos() {
        return apostadores.values().stream()
                .filter(Usuario::isActivo)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene apostadores con actividad reciente
     */
    public List<Apostador> obtenerApostadoresConActividad(int diasRecientes) {
        return apostadores.values().stream()
                .filter(a -> a.getUltimaActividad() != null)
                .filter(a -> a.getUltimaActividad().isAfter(
                        java.time.LocalDate.now().minusDays(diasRecientes)))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS UTILITARIOS =====

    /**
     * Genera un ID único para usuario
     */
    private String generarIdUsuario() {
        return "USR_" + System.currentTimeMillis() + "_" +
                String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * Obtiene estadísticas generales
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsuarios", usuarios.size());
        stats.put("usuariosActivos", obtenerUsuariosActivos().size());
        stats.put("totalCriadores", criadores.size());
        stats.put("criadoresActivos", obtenerCriadoresActivos().size());
        stats.put("totalApostadores", apostadores.size());
        stats.put("apostadoresActivos", obtenerApostadoresActivos().size());

        return stats;
    }

    /**
     * Limpia los datos en memoria (usar con cuidado)
     */
    public void limpiarCache() {
        usuarios.clear();
        criadores.clear();
        apostadores.clear();
    }

    /**
     * Recarga los datos desde archivos
     */
    public boolean recargarDatos() {
        limpiarCache();
        return cargarDatos();
    }
}