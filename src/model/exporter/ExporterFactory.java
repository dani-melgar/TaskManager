package model.exporter;

/**
 * Fabrica de exportadores que crea instancias de {@link IExporter} segun el tipo especificado.
 */
public class ExporterFactory {
	// Tipos soportados
	public static final String CSV = "csv";
	public static final String JSON = "json";

	/**
	 * Devuelve una instancia de {@link IExporter} segun el tipo.
	 *
	 * @param type el tipo de exportador requerido.
	 * @return una instancia de la clase correspondiente de {@code IExporter}.
	 * @throws ExporterException si el tipo no es soportado o es invalido.
	 */
	public static IExporter getExporter(String type) throws ExporterException {
		if (type == null || type.trim().isEmpty()) {
			throw new ExporterException("Error: El tipo de exportador no puede ser nulo o vacio.");
		}

		switch (type.trim().toLowerCase()) {
		case CSV:
			return new CSVExporter();
		case JSON:
			return new JSONExporter();
		default:
			throw new ExporterException("Error: El formato: " + type + " aun no es soportado");
		}
	}
}