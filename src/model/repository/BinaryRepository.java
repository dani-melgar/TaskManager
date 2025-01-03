/**
 * @author dani.melgar@usal.es

*/

package model.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import model.Task;


public class BinaryRepository implements IRepository {
	/* Atributos */
	private final String filePath = System.getProperty("user.home") + "/task.bin";
	private List<Task> tasks = new ArrayList<>();
	private Set<Integer> usedIDs = new HashSet<>();


	@Override
	public void loadTasks() throws RepositoryException {
		// Ruta del fichero binario en el directorio del usuario
		File file = new File(filePath);

		// Verificamos si el archivo existe
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				// Leemos el objeto serializado del archivo
				Object obj = ois.readObject();

				// Verificamos que sea una lista
				if (obj instanceof List<?>) {
					// Creamos una lista temporal
					List<?> tempList = (List<?>) obj;

					// Inicializamos la lista 'tasks' (limpia y vacía)
					tasks = new ArrayList<>();

					// Iteramos sobre cada elemento de la lista temporal
					for (Object item : tempList) {
						// Verificamos si el elemento actual es del tipo Task
						if (item instanceof Task) {
							// Hacemos un cast seguro y añadimos el elemento a 'tasks'
							tasks.add((Task) item);

							// Añadir identificador al conjunto de los IDs
							usedIDs.add(((Task) item).getIdentifier());
						} else {
							throw new RepositoryException("Error: El archivo contiene elementos que no son de tipo Task");
						}
					}
					
				} else {
					throw new RepositoryException("Error: El archivo no contiene una lista de tareas");
				}
			} catch (IOException | ClassNotFoundException e) {
				throw new RepositoryException("Error: Carga de Fichero Binario", e);
			}
		}
	}

	@Override
	public void saveTasks() throws RepositoryException {
		// Comprobar si la lista esta vacia
		if (tasks == null) {
			throw new RepositoryException("Error: Las lista de tareas esta vacia");
		}

		// Comprobar si hay tareas vacias
		for (Task task : tasks) {
			if (task == null) {
				throw new RepositoryException("Error: La lista contiene una tarea nula");
			}
		}
		
		// Ruta del fichero binario
		File file = new File(filePath);

		// Comprobar permisos de escritura
		if (file.exists() && !file.canWrite()) {
			throw new RepositoryException("Error: No se puede escribir en fichero: " + filePath);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			// Usar una copia inmutable para evitar modificaciones de la original
			List<Task> tasksToSave = List.copyOf(tasks);
			oos.writeObject(tasksToSave);
		} catch (IOException e) {
			throw new RepositoryException("Error: Fallo en la exportacion de Tareas", e);
		}
	}


	/**
	 * Genera un identificador unico para una nueva tarea.
	 * 
	 * <p>
	 * El metodo utiliza un generador de numeros aleatorios para crear un
	 * identificador diferente a los ya existentes. El identificador
	 * generado se añade al conjunto de identificadores utilizados
	 * para garantizar posibles identificadores unicos futuros.
	 * </p>
	 * 
	 * @return Un identificador unico para una tarea.
	 */
	private int generateUniqueID() {
		Random random = new Random();
		int newID;
		do {
			newID = random.nextInt(Integer.MAX_VALUE);
		} while (usedIDs.contains(newID));

		usedIDs.add(newID);
		return newID;
	}

	
	@Override
	public void addTask(Task t) throws RepositoryException {
		// Comprobar si la tarea esta vacia
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		// Comprobar titulo y contenido no nulos o vacios
		if (t.getTitle() == null || t.getTitle().isEmpty()) {
			throw new RepositoryException("Error: El titulo de la tarea es obligatorio");
		}

		if (t.getContent() == null || t.getContent().isEmpty()) {
			throw new RepositoryException("Error: El contenido de la tarea es obligatorio");
		}
		
		// Comprobaciones futuras demas atributos (Me canse xd)

		if (tasks != null && !tasks.isEmpty()) {
			for (Task task : tasks) {
				if (task.getIdentifier() == t.getIdentifier()) {
					throw new RepositoryException("Error: Tarea con identificador: " + t.getIdentifier() + " ya existe");
				}
			}
		}

		try {
			tasks.add(t);
		} catch (Exception e) {
			throw new RepositoryException("Error al añadir la tarea: " + e.getMessage(), e);
		}
	}


	@Override
	public void createTask(Task t) throws RepositoryException {
		// Comprobar si la tarea esta vacia
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		// Generar un identificador unico
		int uniqueID = generateUniqueID();
		t.setIdentifier(uniqueID);

		// Comprobar titulo y contenido no nulos o vacios
		if (t.getTitle() == null || t.getTitle().isEmpty()) {
			throw new RepositoryException("Error: El titulo de la tarea es obligatorio");
		}

		if (t.getContent() == null || t.getContent().isEmpty()) {
			throw new RepositoryException("Error: El contenido de la tarea es obligatorio");
		}
		
		if (tasks != null && !tasks.isEmpty()) {
			for (Task task : tasks) {
				if (task.getIdentifier() == t.getIdentifier()) {
					throw new RepositoryException("Error: Tarea con identificador: " + t.getIdentifier() + " ya existe");
				}
			}
		}

		try {
			addTask(t);
		} catch (Exception e) {
			throw new RepositoryException("Error al añadir la tarea: " + e.getMessage(), e);
		}
	}


	@Override
	public void removeTask(Task t) throws RepositoryException {
		// Comprobar si la tarea esta vacia
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		// Comprobar si la lista de tareas es nula o vacia
		if (tasks == null || tasks.isEmpty()) {
			throw new RepositoryException("Error: La lista de tareas es nula o vacia");
		}

		// Buscar y eliminar la tarea por su identificador
		boolean removed = false;
		for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext(); ) {
			Task currentTask = iterator.next();
			if (currentTask.getIdentifier() == t.getIdentifier()) {
				// Eliminar usando el iterador para evitar ConcurrentModificationException
				iterator.remove();
				usedIDs.remove(currentTask.getIdentifier());
				removed = true;
				break;
			}
		}
	    
		if (!removed) {
			throw new RepositoryException("Error: No se encontro la tarea con identificador: " + t.getIdentifier());
		}
	}

	@Override
	public void modifyTask(Task t) throws RepositoryException {
		// Validar que la tarea no sea nula
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		// Validar titulo y contenido no sean nulos o esten vacios
		if (t.getTitle() == null || t.getTitle().isEmpty()) {
			throw new RepositoryException("Error: El titulo de la tarea es obligatorio");
		}

		if (t.getContent() == null || t.getContent().isEmpty()) {
			throw new RepositoryException("Error: El contenido de la tarea es obligatorio");
		}

		// Usar un Iterator para buscar y reemplazar la tarea
		boolean found = false;
		ListIterator<Task> iterator = tasks.listIterator();
		while (iterator.hasNext()) {
			Task existingTask = iterator.next();
			if (existingTask.getIdentifier() == t.getIdentifier()) {
				// Reemplazar la tarea existente con la nueva
				iterator.set(t);
				found = true;
				break;
			}
		}

		// Excepcion si no se encuentra la tarea
		if (!found) {
			throw new RepositoryException("Error: Tarea con identificador " + t.getIdentifier() + " no encontrada");
		}
	}

	@Override
	public List<Task> getAllTasks() throws RepositoryException {
		try {
			// Si es nula, devolvemos lista vacia
			if (tasks == null) {
				return new ArrayList<>();
			}
			return new ArrayList<>(tasks);
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas", e);
		}
	}

	@Override
	public List<Task> getTasksSortedByPriority() throws RepositoryException {
		try {
			List<Task> sortedTasks = getAllTasks();
			// Ordenamos las tareas por su atributo "priority" (de mayor a menor)
			sortedTasks.sort((task1, task2) -> Integer.compare(task2.getPriority(), task1.getPriority()));
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas ordenada por prioridad", e);
		}
	}

	@Override
	public List<Task> getTasksSortedByDate() throws RepositoryException {
		try {
			List<Task> sortedTasks = getAllTasks();
			// Ordenamos las tareas por el atributo "date" (de mas antigua a mas nueva)
			sortedTasks.sort((task1, task2) -> task1.getDate().compareTo(task2.getDate()));
			sortedTasks.reversed();
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas ordenada por fecha", e);
		}
	}
	
	@Override
	public List<Task> getTasksSortedByCompletion() throws RepositoryException {
		try {
			List<Task> sortedTasks = getAllTasks();
			// Ordenamos las tareas por su atributo "priority" (de mayor a menor)
			sortedTasks.sort((task1, task2) -> Boolean.compare(!task2.isCompleted(), !task1.isCompleted()));
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas completadas", e);
		}
	}

	// Esta funcion deberia de implementar excepciones y comprobar no sea nula antes
	@Override
	public Set<Integer> getUsedIDs() throws RepositoryException {
		return usedIDs;
	}

}