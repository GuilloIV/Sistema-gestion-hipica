package mx.uv.feaa.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adaptador para serialización/deserialización JSON usando Jackson.
 * Soporta tipos Java 8 (LocalDate, LocalDateTime) y manejo de colecciones.
 */
public class JsonAdapter {
    private static final ObjectMapper mapper = createObjectMapper();
    private static final Logger logger = Logger.getLogger(JsonAdapter.class.getName());

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Registrar módulo para fechas Java 8
        objectMapper.registerModule(new JavaTimeModule());

        // Configuración adicional
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }

    /**
     * Serializa un objeto a JSON (String)
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error al serializar objeto a JSON", e);
            return null;
        }
    }

    /**
     * Deserializa JSON a un objeto del tipo especificado
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error al deserializar JSON a objeto", e);
            return null;
        }
    }

    /**
     * Deserializa JSON a una lista de objetos
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error al deserializar JSON a lista", e);
            return null;
        }
    }

    /**
     * Deserializa JSON con tipos complejos (ej. Map<Enum, Object>)
     */
    public static <T> T fromJsonWithTypeReference(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error al deserializar JSON con TypeReference", e);
            return null;
        }
    }

    /**
     * Lee un archivo JSON y lo convierte a objeto
     */
    public static <T> T loadFromFile(String filePath, Class<T> clazz) {
        try {
            return mapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al leer archivo JSON: " + filePath, e);
            return null;
        }
    }

    /**
     * Guarda un objeto en archivo JSON
     */
    public static boolean saveToFile(Object object, String filePath) {
        try {
            mapper.writeValue(new File(filePath), object);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar archivo JSON: " + filePath, e);
            return false;
        }
    }
}
