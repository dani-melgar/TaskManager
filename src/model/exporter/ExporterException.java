package model.exporter;

/**
 * Clase de Excepciones personalizadas para manejar errores relacionados con la de exportacion.
 * Se utiliza cuando hay un problema al generar o procesar un exportador de tareas.
 */
public class ExporterException extends Exception {
	/**
	 * Crea una nueva instancia de {@code ExporterException} con un mensaje
	 * con el motivo del error.
	 *
	 * @param mensaje El mensaje que describe el error.
	 */
	public ExporterException(String mensaje) {
		super(mensaje);
	}

	/**
	 * Crea una nueva instancia de {@code ExporterException} con un mensaje y una causa.
	 *
	 * @param mensaje El mensaje que describe el error.
	 * @param causa   Ea causa original del error, que puede ser otra excepcion.
	 */
	public ExporterException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}