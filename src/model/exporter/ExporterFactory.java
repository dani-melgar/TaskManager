package model.exporter;

public class ExporterFactory {
	public static IExporter getExporter(String type) throws ExporterException {
		switch (type.toLowerCase()) {
		case "csv":
			return new CSVExporter();
		case "json":
			return new JSONExporter();
		default:
			throw new ExporterException("Error: Sin soporte para formato: " + type);
		}
	}
}