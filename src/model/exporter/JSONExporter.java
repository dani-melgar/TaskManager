package model.exporter;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import model.Task;
import model.TaskObserver;

public class JSONExporter implements IExporter, TaskObserver {
	/* Atributos */
	private final String directoryPath = System.getProperty("user.home") + "/Tasks";
	private final String filePath = directoryPath + "/task.json";

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
		
		// Si el archivo existe, hacer una copia de seguridad y cargar las tareas existentes
		if (file.exists()) {
			try {
				createBackup(file);
				existingTasks = importTasks();
			} catch (IOException e) {
				throw new ExporterException("Error leyendo el fichero existente para evitar duplicados", e);
			}
		}

		// Usar la cache de IDs para evitar duplicados
		Set<Integer> existingTaskIDs = readTasksID(existingTasks);
		List<Task> allTasks = new ArrayList<>(existingTasks);
		for (Task task : tasks) {
			if (!existingTaskIDs.contains(task.getIdentifier())) {
				allTasks.add(task);
			}
		}

		// Usar Gson para convertir la lista de tareas a JSON
		Gson gson = new Gson();
		String json = gson.toJson(allTasks);

		// Guardar el JSON en el archivo
		Path savePath = Paths.get(filePath);
		try {
			Files.write(savePath, json.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new ExporterException("Error al exportar tareas al fichero JSON", e);
		}
	}
	
	public List<Task> importTasks() throws ExporterException {
		ensureDirectoryExists();

		File file = new File(filePath);
		if (!file.exists() || file.length() == 0) {
			return new ArrayList<>();
		}

		try {
			// Leer el contenido del archivo JSON
			String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

			// Pasar el JSON a una lista de objetos Task
			Gson gson = new Gson();
			Task[] taskArray = gson.fromJson(json, Task[].class);
			List<Task> tasks = List.of(taskArray);

			// Filtrar tareas con identificadores únicos
			List<Task> newTasks = new ArrayList<>();
			for (Task task : tasks) {
				if (!cachedTaskIDs.contains(task.getIdentifier())) {
					newTasks.add(task);
				}
			}
			return newTasks;
		} catch (IOException e) {
			throw new ExporterException("Error leyendo el fichero JSON", e);
		}
	}

}