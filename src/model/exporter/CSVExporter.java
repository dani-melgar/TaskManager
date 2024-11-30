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
import java.util.List;
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


	@Override
	public void exportTasks(List<Task> tasks) throws ExporterException {
		validateTasks(tasks);
		ensureDirectoryExists();

		List <String> taskLines = new ArrayList<>();
		for (Task task : tasks) {
			taskLines.add(task.toDelimitedString(delimitador));
		}
		
		// Ruta del fichero CSV
		Path savePath = Paths.get(filePath);
		File file = savePath.toFile();

		try {
			if (file.exists()) {
				createBackup(file);
			}

			Files.write(savePath, taskLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ExporterException("Error al exportar tareas al archivo CSV", e);
		}
	}

	@Override
	public Task factoryTask(String delimitedString) throws ExporterException {
		if (delimitedString == null || delimitedString.isEmpty()) {
			throw new ExporterException("Error: Tarea nula o vacia");
		}

		String[] taskEntityFields = delimitedString.split(delimitador);
		
		// Es posible saber el numero de atributos de una clase? Para no tener el numero magico "7" ahi
		if (taskEntityFields.length != 7) {
			throw new ExporterException("Error: Tarea no contiene todos los atributos");
		}

		try {
			int identifier = Integer.parseInt(taskEntityFields[0]);
			String title = taskEntityFields[1];
			Date date = Date.valueOf(taskEntityFields[2]);
			String content = taskEntityFields[3];
			int priority = Integer.parseInt(taskEntityFields[4]);
			int estimatedDuration = Integer.parseInt(taskEntityFields[5]);
			boolean completed = Boolean.parseBoolean(taskEntityFields[6]);

			Task task = new Task(identifier, title, date, content, priority, estimatedDuration, completed);
			return task;
		} catch(Exception e) {
			throw new ExporterException("Error: No se pudo crear la tarea");
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
		// Implementar
		return null;
	}



}