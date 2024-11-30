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
	 * Exporta una lista de tareas a un fichero ubicado en la carpeta {@code ~/Tasks}.
	 * <p>
	 * Este metodo realiza varias operaciones y verificaciones para garantizar una exportacion segura:
	 * </p>
	 * <ul>
	 *   <li>Comprueba la lista de tareas utilizando {@link #validateTasks(List)} para garantizar que no es nula ni contiene valores erroneos.</li>
	 *   <li>Comprueba la existencia del directorio donde se exporta y lo crea si es necesario mediante {@link #ensureDirectoryExists()}.</li>
	 *   <li>Si ya existe un fichero en la ruta especificada, lee las tareas existentes con {@link #readTaskFromCSV()} y 
	 *       crea una copia de seguridad con {@link #createBackup(File)}.</li>
	 *   <li>Combina las tareas nuevas con las existentes, evitando duplicados basados en el identificador de las tareas.</li>
	 * </ul>
	 * <p>
	 * Cuando acaba, escribe todas las tareas (existentes y nuevas) en el fichero. Cada tarea se representa en una linea 
	 * y sus atributos están delimitados por el delimitador requerido por la extension.
	 * </p>
	 * 
	 * @param tasks la lista de tareas a exportar.
	 * @throws ExporterException si ocurren errores durante las comprobaciones, la creacion del directorio, 
	 *                           la copia de seguridad, la lectura del fichero o la escritura de las tareas.
	 */
	void exportTasks(List<Task> tasks) throws ExporterException;

	/**
	 * Crea una instancia de {@link Task} a partir de una cadena delimitada.
	 * <p>
	 * Este metodo convierte una cadena delimitada en un objeto de tipo {@link Task}.
	 * Comprueba que la cadena no sea nula ni vacia y que contenga el numero correcto de campos,
	 * separados por el delimitador correspondiente.
	 * </p>
	 * 
	 * @param delimitedString la cadena de texto que representa una tarea, con los atributos separados por un delimitador.
	 * @return una instancia de {@link Task} creada a partir de los valores de la cadena.
	 * @throws ExporterException si la cadena es nula, esta vacia, su formato es incorrecto o no puede convertirse en una tarea.
	 */
	Task factoryTask(String delimitedString) throws ExporterException;
	
	List<Task> importTasks() throws ExporterException;
}