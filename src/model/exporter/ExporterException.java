package model.exporter;

public class ExporterException extends Exception {
	public ExporterException(String mensaje) {
		super(mensaje);
	}

	public ExporterException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}