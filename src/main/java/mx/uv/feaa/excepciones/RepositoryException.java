package mx.uv.feaa.excepciones;

/**
 * Excepción específica para errores en operaciones de repositorio
 */
public class RepositoryException extends RuntimeException {

  public RepositoryException(String mensaje) {
    super(mensaje);
  }

  public RepositoryException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }

  public RepositoryException(Throwable causa) {
    super(causa);
  }
}