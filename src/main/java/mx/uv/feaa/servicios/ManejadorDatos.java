package mx.uv.feaa.servicios;

import mx.uv.feaa.util.JsonAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor centralizado para el manejo de archivos JSON y persistencia de datos
 */
public class ManejadorDatos {
    private static final Logger logger = Logger.getLogger(ManejadorDatos.class.getName());

    // Configuración de rutas
    private static final String BASE_DIR = "data";
    private static final String BACKUP_DIR = BASE_DIR + "/backup";
    private static final String TEMP_DIR = BASE_DIR + "/temp";
    private static final String LOGS_DIR = BASE_DIR + "/logs";

    // Singleton
    private static ManejadorDatos instance;

    private ManejadorDatos() {
        inicializarEstructuraDirectorios();
    }

    public static ManejadorDatos getInstance() {
        if (instance == null) {
            instance = new ManejadorDatos();
        }
        return instance;
    }

    /**
     * Inicializa la estructura completa de directorios
     */
    private void inicializarEstructuraDirectorios() {
        try {
            crearDirectorio(BASE_DIR);
            crearDirectorio(BACKUP_DIR);
            crearDirectorio(TEMP_DIR);
            crearDirectorio(LOGS_DIR);

            logger.info("Estructura de directorios inicializada correctamente");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar directorios", e);
        }
    }

    /**
     * Crea un directorio si no existe
     */
    private void crearDirectorio(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                logger.info("Directorio creado: " + path);
            } else {
                logger.warning("No se pudo crear el directorio: " + path);
            }
        }
    }

    /**
     * Guarda un objeto en archivo JSON con manejo de errores robusto
     */
    public <T> boolean guardarDatos(T objeto, String nombreArchivo) {
        return guardarDatos(objeto, nombreArchivo, true);
    }

    /**
     * Guarda un objeto en archivo JSON
     */
    public <T> boolean guardarDatos(T objeto, String nombreArchivo, boolean crearBackup) {
        if (objeto == null || nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            logger.warning("Datos inválidos para guardar");
            return false;
        }

        String rutaCompleta = BASE_DIR + "/" + nombreArchivo;

        try {
            // Crear backup si se solicita y el archivo existe
            if (crearBackup && new File(rutaCompleta).exists()) {
                crearBackupArchivo(rutaCompleta);
            }

            // Guardar en archivo temporal primero
            String rutaTemporal = TEMP_DIR + "/temp_" + nombreArchivo;
            boolean guardadoTemporal = JsonAdapter.saveToFile(objeto, rutaTemporal);

            if (!guardadoTemporal) {
                logger.severe("Error al guardar archivo temporal: " + rutaTemporal);
                return false;
            }

            // Mover el archivo temporal al definitivo
            if (moverArchivo(rutaTemporal, rutaCompleta)) {
                logger.info("Datos guardados exitosamente: " + rutaCompleta);
                return true;
            } else {
                logger.severe("Error al mover archivo temporal a definitivo");
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar datos en: " + rutaCompleta, e);
            return false;
        }
    }

    /**
     * Carga datos desde archivo JSON
     */
    public <T> T cargarDatos(String nombreArchivo, Class<T> clazz) {
        if (nombreArchivo == null || clazz == null) {
            logger.warning("Parámetros inválidos para cargar datos");
            return null;
        }

        String rutaCompleta = BASE_DIR + "/" + nombreArchivo;

        try {
            File archivo = new File(rutaCompleta);
            if (!archivo.exists()) {
                logger.info("Archivo no encontrado: " + rutaCompleta);
                return null;
            }

            T datos = JsonAdapter.loadFromFile(rutaCompleta, clazz);
            if (datos != null) {
                logger.info("Datos cargados exitosamente desde: " + rutaCompleta);
            } else {
                logger.warning("No se pudieron cargar datos desde: " + rutaCompleta);
            }

            return datos;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar datos desde: " + rutaCompleta, e);
            return null;
        }
    }

    /**
     * Carga una lista de objetos desde archivo JSON
     */
    public <T> List<T> cargarListaDatos(String nombreArchivo, Class<T> clazz) {
        if (nombreArchivo == null || clazz == null) {
            logger.warning("Parámetros inválidos para cargar lista");
            return new ArrayList<>();
        }

        String rutaCompleta = BASE_DIR + "/" + nombreArchivo;

        try {
            File archivo = new File(rutaCompleta);
            if (!archivo.exists()) {
                logger.info("Archivo no encontrado: " + rutaCompleta);
                return new ArrayList<>();
            }

            List<T> datos = JsonAdapter.fromJsonToList(
                    JsonAdapter.loadFromFile(rutaCompleta, String.class), clazz);

            if (datos != null) {
                logger.info("Lista cargada exitosamente desde: " + rutaCompleta +
                        " (" + datos.size() + " elementos)");
                return datos;
            } else {
                logger.warning("No se pudo cargar lista desde: " + rutaCompleta);
                return new ArrayList<>();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar lista desde: " + rutaCompleta, e);
            return new ArrayList<>();
        }
    }

    /**
     * Crea un backup de un archivo específico
     */
    public boolean crearBackupArchivo(String rutaArchivo) {
        try {
            File archivoOriginal = new File(rutaArchivo);
            if (!archivoOriginal.exists()) {
                return false;
            }

            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = archivoOriginal.getName();
            String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.'));

            String rutaBackup = BACKUP_DIR + "/" + nombreSinExtension + "_" + timestamp + extension;

            return copiarArchivo(rutaArchivo, rutaBackup);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al crear backup de: " + rutaArchivo, e);
            return false;
        }
    }

    /**
     * Restaura un archivo desde un backup
     */
    public boolean restaurarDesdeBackup(String nombreArchivo, String timestampBackup) {
        try {
            String rutaBackup = BACKUP_DIR + "/" + nombreArchivo.replace(".json", "") +
                    "_" + timestampBackup + ".json";
            String rutaDestino = BASE_DIR + "/" + nombreArchivo;

            if (!new File(rutaBackup).exists()) {
                logger.warning("Archivo de backup no encontrado: " + rutaBackup);
                return false;
            }

            return copiarArchivo(rutaBackup, rutaDestino);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al restaurar desde backup", e);
            return false;
        }
    }

    /**
     * Lista todos los backups disponibles
     */
    public List<String> listarBackups() {
        List<String> backups = new ArrayList<>();

        try {
            File backupDir = new File(BACKUP_DIR);
            if (backupDir.exists() && backupDir.isDirectory()) {
                File[] archivos = backupDir.listFiles((dir, name) -> name.endsWith(".json"));

                if (archivos != null) {
                    for (File archivo : archivos) {
                        backups.add(archivo.getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al listar backups", e);
        }

        return backups;
    }

    /**
     * Limpia archivos temporales antiguos
     */
    public boolean limpiarArchivosTemporales() {
        try {
            File tempDir = new File(TEMP_DIR);
            if (tempDir.exists() && tempDir.isDirectory()) {
                File[] archivos = tempDir.listFiles();

                if (archivos != null) {
                    for (File archivo : archivos) {
                        if (archivo.delete()) {
                            logger.info("Archivo temporal eliminado: " + archivo.getName());
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al limpiar archivos temporales", e);
            return false;
        }
    }

    /**
     * Limpia backups antiguos (mantiene solo los últimos N)
     */
    public boolean limpiarBackupsAntiguos(int mantenerUltimos) {
        try {
            List<String> backups = listarBackups();

            if (backups.size() > mantenerUltimos) {
                // Ordenar por fecha y eliminar los más antiguos
                backups.sort(String::compareTo);

                for (int i = 0; i < backups.size() - mantenerUltimos; i++) {
                    File backup = new File(BACKUP_DIR + "/" + backups.get(i));
                    if (backup.delete()) {
                        logger.info("Backup antiguo eliminado: " + backups.get(i));
                    }
                }
            }

            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al limpiar backups antiguos", e);
            return false;
        }
    }

    /**
     * Verifica la integridad de un archivo JSON
     */
    public boolean verificarIntegridadArchivo(String nombreArchivo) {
        try {
            String rutaCompleta = BASE_DIR + "/" + nombreArchivo;
            File archivo = new File(rutaCompleta);

            if (!archivo.exists()) {
                return false;
            }

            // Intentar parsear el JSON
            String contenido = JsonAdapter.loadFromFile(rutaCompleta, String.class);
            return contenido != null && !contenido.trim().isEmpty();

        } catch (Exception e) {
            logger.log(Level.WARNING, "Archivo JSON corrupto: " + nombreArchivo, e);
            return false;
        }
    }

    /**
     * Obtiene información sobre el uso de espacio
     */
    public String obtenerInfoEspacio() {
        try {
            long espacioTotal = calcularTamanoDirectorio(new File(BASE_DIR));
            long espacioBackups = calcularTamanoDirectorio(new File(BACKUP_DIR));
            long espacioTemp = calcularTamanoDirectorio(new File(TEMP_DIR));

            return String.format(
                    "Uso de espacio:\n" +
                            "- Total: %.2f MB\n" +
                            "- Backups: %.2f MB\n" +
                            "- Temporales: %.2f MB\n" +
                            "- Datos: %.2f MB",
                    espacioTotal / 1024.0 / 1024.0,
                    espacioBackups / 1024.0 / 1024.0,
                    espacioTemp / 1024.0 / 1024.0,
                    (espacioTotal - espacioBackups - espacioTemp) / 1024.0 / 1024.0
            );

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al calcular espacio", e);
            return "No se pudo calcular el uso de espacio";
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    private boolean copiarArchivo(String origen, String destino) {
        try {
            Path origenPath = Paths.get(origen);
            Path destinoPath = Paths.get(destino);

            Files.copy(origenPath, destinoPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            logger.info("Archivo copiado: " + origen + " -> " + destino);
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al copiar archivo", e);
            return false;
        }
    }

    private boolean moverArchivo(String origen, String destino) {
        try {
            Path origenPath = Paths.get(origen);
            Path destinoPath = Paths.get(destino);

            Files.move(origenPath, destinoPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            logger.info("Archivo movido: " + origen + " -> " + destino);
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al mover archivo", e);
            return false;
        }
    }

    private long calcularTamanoDirectorio(File directorio) {
        long tamano = 0;

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isFile()) {
                        tamano += archivo.length();
                    } else if (archivo.isDirectory()) {
                        tamano += calcularTamanoDirectorio(archivo);
                    }
                }
            }
        }

        return tamano;
    }
}