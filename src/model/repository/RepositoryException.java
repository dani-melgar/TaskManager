package model.repository;

/**
 * Clase de excepciones personalizadas para manejar errores relacionados con los repositorios.
 * Se utiliza cuando hay un problema relacionado con las operaciones de las tareas.
 */
public class RepositoryException extends Exception {
	/**
	 * Crea una instancia de {@code RepositoryException} con un mensaje
	 * con el motivo del error.
	 *
	 * @param mensaje El mensaje que describe el error.
	 */
	public RepositoryException(String mensaje) {
		super(mensaje);
	}

	/**
	 * Crea una instancia de {@code RepositoryException} con un mensaje y una causa.
	 *
	 * @param mensaje El mensaje que describe el error.
	 * @param causa   La causa orignial del error, puede ser otra excepcion.
	 */
	public RepositoryException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}