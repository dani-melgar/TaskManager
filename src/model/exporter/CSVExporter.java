package model.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Task;
import model.TaskObserver;


public class CSVExporter implements IExporter, TaskObserver {
	/* Atributos */
	private final String directoryPath = System.getProperty("user.home") + "/Tasks";
	private final String filePath = directoryPath + "/task.csv";
	private final String delimitador = ";";

	// Cache local de los IDs de las tareas
	private Set<Integer> cachedTaskIDs = new HashSet<>();

	@Override
	public void update(Set<Integer> taskIDs) {
		this.cachedTaskIDs = new HashSet<>(taskIDs);
	}

	@Override
	public void ensureDirectoryExists() throws ExporterException {
		File directoryTasks = new File(directoryPath);
		if (!directoryTasks.exists()) {
			if (!directoryTasks.mkdirs()) {
				throw new ExporterException("Error: No se pudo crear el Directorio: " + directoryPath);
			}
		}

		if (!directoryTasks.isDirectory()) {
			throw new ExporterException("Error: La ruta no es un directorio: " + directoryPath);
		}

		if (!directoryTasks.canWrite()) {
			throw new ExporterException("Error: No se tiene permiso de escritura en el directorio: " + directoryPath);
		}
	}

	@Override
	public void validateTasks(List<Task> tasks) throws ExporterException {
		if (tasks == null || tasks.isEmpty()) {
			throw new ExporterException("Error La lista esta vacia o es nula");
		} 

		for (Task task : tasks) {
			if (task == null) {
				throw new ExporterException("Error: La lista contiene tarea/s nula/s");
			}
		}
	}

	@Override
	public void createBackup(File file) throws IOException {
		Path backupPath = Paths.get(filePath + ".bak");
		Files.copy(file.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Lee una lista de tareas de un fichero CSV existente.
	 * <p>
	 * Este metodo lee todas las líneas de un archivo y convierte cada línea en una instancia de
	 * {@link Task} usando {@link #factoryTask(String)}.
	 * Si una linea no puede convertirse en una tarea, se omite y se registra un mensaje de error.
	 * </p>
	 * 
	 * @return una lista de tareas leidas desde el fichero. Si el fichero no existe, devuelve una lista vacia.
	 * @throws IOException si ocurre un error al acceder o leer el fichero.
	 */
	public List<Task> readTaskFromCSV() throws IOException {
		Path path = Paths.get(filePath);
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		List<Task> taskCSV = new ArrayList<>();

		for (String line : lines) {
			try {
				taskCSV.add(factoryTask(line));
			} catch (Exception e) {
				// Nose
			}
		}

		return taskCSV;
	}

	/**
	 * Obtiene un conjunto con los identificadores de una lista de tareas.
	 * <p>
	 * Este metodo recorre la lista de tareas proporcionada y extrae los identificadores de cada tarea.
	 * Si alguna tarea en la lista es nula, se ignora.
	 * </p>
	 * 
	 * @param tasks la lista de tareas de las cuales se desea obtener los identificadores.
	 * @return un conjunto de enteros que representan los identificadores unicos de las tareas.
	 */
	public Set<Integer> readTasksID(List<Task> tasks) {
		Set<Integer> taskID = new HashSet<>();
		for (Task task : tasks) {
			if (task != null) {
				taskID.add(task.getIdentifier());
			}
		}
		return taskID;
	}

	@Override
	public void exportTasks(List<Task> tasks) throws ExporterException {
		validateTasks(tasks);
		ensureDirectoryExists();

		File file = new File(filePath);
		List<Task> existingTasks = new ArrayList<>();

		if (file.exists()) {
			try {
				createBackup(file);
				existingTasks = readTaskFromCSV();
			} catch (IOException e) {
				throw new ExporterException("Error leyendo el fichero existente para evitar duplicados", e);
			}
		}

		// Usar la cache de IDs para verificar duplicados
		List<String> taskLines = new ArrayList<>();
		for (Task existingTask : existingTasks) {
			// Mantener tareas existentes
			taskLines.add(existingTask.toDelimitedString(delimitador));
		}

		for (Task task : tasks) {
			// Comprobar elementos duplicadosd desde la cache
			if (!cachedTaskIDs.contains(task.getIdentifier())) {
				taskLines.add(task.toDelimitedString(delimitador));
			}
		}

		// Guardar todas las tareas en el archivo CSV
		Path savePath = Paths.get(filePath);
		try {
			Files.write(savePath, taskLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ExporterException("Error al exportar tareas al fichero CSV", e);
		}
	}

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
	private Task factoryTask(String delimitedString) throws ExporterException {
		if (delimitedString == null || delimitedString.trim().isEmpty()) {
			throw new ExporterException("Error: La cadena leida esta vacia o es nula");
		}

		String[] taskEntityFields = delimitedString.split(delimitador);
		if (taskEntityFields.length != Task.getFieldCount()) {
			throw new ExporterException("Error: La tarea no contiene los " + Task.getFieldCount() + " atributos esperados");
		}

		try {
			int identifier = Integer.parseInt(taskEntityFields[0]);
			if (identifier < 0) {
				throw new ExporterException("Error: El identificador no puede ser negativo");
			}

			String title = taskEntityFields[1];
			if (title == null || title.trim().isEmpty()) {
				throw new ExporterException("Error: El titulo no puede estar vacio");
			}

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(taskEntityFields[2], dateFormatter);

			String content = taskEntityFields[3];

			int priority = Integer.parseInt(taskEntityFields[4]);
			if (priority < 1 || priority > 5) {
				throw new ExporterException("Error: La prioridad debe estar entre 1 y 5");
			}

			int estimatedDuration = Integer.parseInt(taskEntityFields[5]);
			if (estimatedDuration <= 0) {
				throw new ExporterException("Error: La duracion estimada debe ser mayor a 0");
			}

			boolean completed = Boolean.parseBoolean(taskEntityFields[6]);

			return new Task(identifier, title, date, content, priority, estimatedDuration, completed);

		} catch (NumberFormatException e) {
			throw new ExporterException("Error: Atributo numerico erroneo en los atributos de la tarea", e);
		} catch (DateTimeParseException e) {
			throw new ExporterException("Error: Formato de fecha erroneo, valido: YYYY-MM-DD", e);
		}
	}


	@Override
	public List<Task> importTasks() throws ExporterException {
		ensureDirectoryExists();

		File file = new File(filePath);
		List<Task> fileTasks = new ArrayList<>();
		if (file.exists() && file.length() > 0) {
			try {
				createBackup(file);
				fileTasks = readTaskFromCSV();
			} catch (Exception e) {
				throw new ExporterException("Error leyendo el fichero", e);
			}
		}

		// Filtrar tareas con identificadores unicos
		List<Task> newTasks = new ArrayList<>();
		for (Task task : fileTasks) {
			if (!cachedTaskIDs.contains(task.getIdentifier())) {
				newTasks.add(task);
			}
		}

		// Devolver solo las tareas nuevas
		return newTasks;
	}

}