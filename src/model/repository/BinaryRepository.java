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


	/**
	 * Carga una lista de tareas desde un fichero binario ubicado en el home del usuario.
	 * <p>
	 * Este metodo comprueba que el archivo exista antes de intentar cargar las tareas.
	 * Si el fichero no contiene una lista valida de objetos "Task", se lanza una excepcion.
	 * Tambien realiza comprobaciones para asegurarse de que todos los elementos
	 * deserializados sean objetos de la clase "Task".
	 * </p>
	 *
	 * <p>
	 * Mientras se cargan las tareas, los identificadores de las tareas deserializadas
	 * se añaden al conjunto de identificadores unicos utilizados por el repositorio
	 * para garantizar la unicidad de los IDs en el futuro.
	 * </p>
	 * @throws RepositoryException en caso de algun problema al leer o deserializar el fichero,
	 *                             o si el archivo contiene datos no valido.
	 */
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


	/**
	 * Guarda la lista de tareas en un fichero binario en el home del usuario.
	 * <p>
	 * Este metodo comprueba que la lista de tareas este cargada y no contenga
	 * elementos nulos antes de intentar guardarla. Tambien comprueba si el fichero
	 * existe y permite la escritura antes de intentarlo.
	 * Se utiliza una copia inmutable de la lista para evitar modificaciones a la lista
	 * original mientras se escribe.
	 * </p>
	 * 
	 * @throws RepositoryException en caso de algun problema al guardar las tareas,
	 *                             como un fichero inexistente o problemas de escritura.
	 */
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
		
		// Comprobaciones futuras demas atributos (Me canse xd)

		// DUDA: Es necesario comprobar si el identificador es unico ,aunque ya lo estamos generando de manera unica?
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

	// Esta funcion deberia de implementar excepciones y comprobar no sea nula antes
	@Override
	public Set<Integer> getUsedIDs() throws RepositoryException {
		return usedIDs;
	}

}