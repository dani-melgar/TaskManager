package model.exporter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Task;

public interface IExporter {

	/**
	 * Comprueba si el directorio de exportacion existe.
	 * Si no existe, intenta crearlo.
	 * 
	 * @throws ExporterException si el directorio no se puede crear por cualquier motivo.
	 */
	void ensureDirectoryExists() throws ExporterException;

	/**
	 * Comprueba una lista de tareas.
	 * <p>
	 * Este metodo comprueba que la lista no sea nula, que no este vacía y que no contenga
	 * elementos nulos. Si alguna de estas condiciones no se cumple, lanza una excepcion.
	 * </p>
	 * 
	 * @param tasks la lista de tareas a validar.
	 * @throws ExporterException si la lista es nula, esta vacia o contiene tareas nulas.
	 */
	void validateTasks(List<Task> tasks) throws ExporterException;

	/**
	 * Crea una copia de seguridad del archivo a exportar.
	 * <p>
	 * Si el fichero existe, este metodo genera una copia con la extension {@code .bak}.
	 * Si no se puede crear la copia de seguridad, lanza una excepcion.
	 * </p>
	 * 
	 * @param file la ruta del fichero original.
	 * @throws ExporterException si ocurre un error al intentar crear la copia de seguridad.
	 */
	void createBackup(File file) throws IOException;
	
	/**
	 * Exporta una lista de tareas a un fichero ubicado en el directorio {@code ~/Tasks}.
	 * <p>
	 * Este metodo hace varias comprobaciones y operaciones previas para garantizar
	 * un proceso de exportacion sin errores:
	 * </p>
	 * <ul>
	 *   <li>Comprueba la lista de tareas utilizando {@link #validateTasks(List)}.</li>
	 *   <li>Comprueba que el directorio de exportacion existe y, si es necesario, lo crea 
	 *       con {@link #ensureDirectoryExists()}.</li>
	 *   <li>Si ya existe un ficheo con la extension correspondiente en la ruta especificada,
	 *       genera una copia de seguridad con {@link #createBackup(File)}.</li>
	 * </ul>
	 * <p>
	 * Por ultimo, escribe las tareas en el fichero, donde cada tarea ocupa una linea y sus 
	 * atributos estan separados por el delimitador configurado.
	 * </p>
	 * 
	 * @param tasks la lista de tareas a exportar.
	 * @throws ExporterException si ocurre algun error durante las comprobaciones, la creacion
	 *                           del directorio, la copia de seguridad o la escritura del fichero.
	 */
	void exportTasks(List<Task> tasks) throws ExporterException;

	// Es posible heredar un estatico?
	Task factoryTask(String delimitedString) throws ExporterException;
	
	List<Task> importTasks() throws ExporterException;
}