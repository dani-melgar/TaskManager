package model.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Task;

// Sera neccesario un metodo factory de Task?
// De donde cojones importa
// A donde cojones exporta
// Vamos a suponer que importa del home? O de Documentos

public class CSVExporter implements IExporter {
	/* Atributos */
	private final String directoryPath = System.getProperty("user.home") + "/Tasks";
	private final String filePath = directoryPath + "/task.csv";
	private final String delimitador = ",";

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
			} catch (ExporterException e) {
				// Solucion de ChatGPT: Puedes registrar un mensaje de error y continuar con las siguientes lineas
				// Es una mierda, model no puede imprimir, preguntarle esto al profesor
				System.err.println("Error al procesar una línea del CSV: " + e.getMessage());
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
		Set<Integer> fileTaskIDs = new HashSet<>();
		List<Task> existingTasks = new ArrayList<>();

		if (file.exists()) {
			try {
				createBackup(file);
				existingTasks = readTaskFromCSV();
				fileTaskIDs = readTasksID(existingTasks);
			} catch (IOException e) {
				throw new ExporterException("Error leyendo el archivo existente para evitar duplicados", e);
			}
		}

		// Combinar tareas existentes y nuevas
		List<String> taskLines = new ArrayList<>();
		for (Task existingTask : existingTasks) {
			taskLines.add(existingTask.toDelimitedString(delimitador));
		}

		for (Task task : tasks) {
			if (!fileTaskIDs.contains(task.getIdentifier())) {
				taskLines.add(task.toDelimitedString(delimitador));
			}
		}

		// Guardar todas las tareas en el archivo CSV
		Path savePath = Paths.get(filePath);
		try {
			Files.write(savePath, taskLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ExporterException("Error al exportar tareas al archivo CSV", e);
		}
	}




	@Override
	public Task factoryTask(String delimitedString) throws ExporterException {
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

			Date date = Date.valueOf(taskEntityFields[2]);
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
		} catch (IllegalArgumentException e) {
			throw new ExporterException("Error: Formato de fecha erroneo, valido: YYYY-MM-DD", e);
		}
	}


	/*
	 * Nuevos problemas a tener en cuenta
	 * 1) Directorio existe, crearlo para evitar futuras problematicas pero finalizar
	 * 	devolviendo una lista vacia si no se puede hacer nada
	 * 2) Fichero existe, idem paso 1
	 * 3) Una vez lo anterior, hay dos posibles casos
	 * 	1) No haya tareas en memoria
	 * 	2) Hay tareas en memoria
	 * 4) En ambos casos, se debe de poder controlar el identificador de las tareas
	 * 	evitando duplicados con las posibles futuras tareas, o con las ya existentes
	 * 5) Quien tenia un metodo para controlar todo esto era IRepository, quizas dicho
	 * 	metodo o el array con las IDs de las tareas deberia de estar mas arriba, de forma
	 * 	que mas metodos puedan acceder a el, lo cual generaria mas comprobaciones y
	 * 	cambios al codigo actual
	 * 6) No creo que deba de haber cambios en la exportacion ya realizada puesto que en un prinicipio
	 * 	no se podrian tener tareas duplicadas, otro caso es la exportacion a un fichero con tareas ya existentes
	 * 	ahi creo que si nos tocaria modificar, puesto que si ya hay un archivo con tareas, nos tocaria leer dichas
	 * 	tareas para evitar elementos duplicados
	 */
	@Override
	public List<Task> importTasks() throws ExporterException {
		return null;
	}



}