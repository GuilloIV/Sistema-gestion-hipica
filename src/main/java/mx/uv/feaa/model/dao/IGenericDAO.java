package mx.uv.feaa.model.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz base para todos los DAOs (Data Access Objects) del sistema.
 * Define las operaciones CRUD básicas que deben implementar todos los DAOs.
 *
 * @param <T> Tipo de entidad que manejará el DAO
 */
public interface IGenericDAO<T, ID>{

    Optional<T> getById(ID id) throws SQLException;
    List<T> getAll() throws SQLException;
    boolean save(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(ID id) throws SQLException;
}
