package model.repository;

import java.util.List;
import java.util.Set;

import model.Task;

/**
 * Interfaz que define las operaciones basicas de un repositorio para la gestion de tareas.
 */
public interface IRepository {

	/**
	 * Carga una lista de tareas desde un fichero binario ubicado en el home del usuario.
	 * <p>
	 * Comprueba que el archivo exista antes de intentar cargar las tareas. Comprueba que el
	 * fichero contenga una lista valida de objetos {@code Task}. Realiza comprobaciones
	 * para asegurarse de que todos los elementos deserializados sean instancias de la
	 * clase {@code Task}.
	 * </p>
	 * <p>
	 * Los identificadores de las tareas deserializadas se añaden al conjunto de IDs 
	 * utilizados por el repositorio para evitar futuros elementos duplicados.
	 * </p>
	 * 
	 * @throws RepositoryException Si ocurre algun problema al leer o deserializar el fichero
	 *                              o si los datos no son correctos.
	 */
	public void loadTasks() throws RepositoryException;
	
	/**
	 * Guarda la lista de tareas en un fichero binario ubicado en el home del usuario.
	 * <p>
	 * Comprueba que la lista de tareas este cargada y no contenga elementos nulos. 
	 * Utiliza una copia inmutable de la lista para evitar modificaciones.
	 * </p>
	 * 
	 * @throws RepositoryException Si ocurre algun problema al guardar las tareas, 
	 *                              como problemas de acceso al fichero.
	 */
	public void saveTasks() throws RepositoryException;

	/**
	 * Agrega una tarea al repositorio.
	 * <p>
	 * Comprueba que la tarea no sea nula, que cumpla con los requisitos de campos obligatorios
	 * y que no existan duplicados antes de añadirla.
	 * </p>
	 * 
	 * @param t La tarea que se desea agregar.
	 * @throws RepositoryException Si la tarea es erronea o ocurre un error al agregarla.
	 */	
	void addTask(Task t) throws RepositoryException;

	/**
	 * Crea una nueva tarea y la agrega al repositorio.
	 * <p>
	 * Genera un identificador unico para la tarea, comprueba que los campos obligatorios 
	 * no esten vacios o sean nulos y garantiza que la nueva tarea sea unica antes de agregarla.
	 * </p>
	 * 
	 * @param t La tarea que se desea crear.
	 * @throws RepositoryException Si la tarea es incorrecta o ocurre un error al agregarla.
	 */
	void createTask(Task t) throws RepositoryException;

	/**
	 * Elimina una tarea del repositorio por su identificador.
	 * <p>
	 * Busca y elimina la tarea en funcion de su identificador. Si la tarea no existe
	 * en el repositorio, se lanza una excepcion.
	 * </p>
	 * 
	 * @param t La tarea que se desea eliminar.
	 * @throws RepositoryException Si la tarea no se encuentra o no puede ser eliminada.
	 */
	void removeTask(Task t) throws RepositoryException;
	
	/**
	 * Modifica una tarea ya existente en el repositorio.
	 * <p>
	 * Reemplaza una tarea existente con la nueva tarea proporcionada. Comprueba que los 
	 * campos requeridos no esten vacios o sean nulos y que la tarea exista.
	 * </p>
	 * 
	 * @param t La tarea que sera reemplazada por la existente con el mismo identificador.
	 * @throws RepositoryException Si la tarea es incorrecta, nula o no existe.
	 */
	void modifyTask(Task t) throws RepositoryException;
	
	/**
	 * Devuelve una lista con todas las tareas almacenadas.
	 * <p>
	 * Devuelve una copia de la lista para evitar modificaciones externas. Si no hay tareas,
	 * devuelve una lista vacia.
	 * </p>
	 * 
	 * @return Una lista con todas las tareas almacenadas.
	 * @throws RepositoryException Si ocurre un error inesperado al obtener las tareas.
	 */
	List<Task> getAllTasks() throws RepositoryException;


	/**
	 * Devuelve una lista de tareas ordenadas por prioridad en orden descendente.
	 * <p>
	 * Genera y devuelve una copia ordenada de la lista de tareas, sin modificar la original.
	 * </p>
	 * 
	 * @return Una lista de tareas ordenadas por prioridad en orden descendente.
	 * @throws RepositoryException Si ocurre un error al procesar las tareas.
	 */
	List<Task> getTasksSortedByPriority() throws RepositoryException;

	/**
	 * Devuelve una lista de tareas no completadas.
	 * <p>
	 * Filtra y devuelve una lista de tareas cuya propiedad {@code completed} sea {@code false}.
	 * </p>
	 * 
	 * @return Una lista de tareas no completadas.
	 * @throws RepositoryException Si ocurre un error al procesar las tareas.
	 */
	List<Task> getTasksSortedByCompletion() throws RepositoryException;

	/**
	 * Devuelve un conjunto con los identificadores unicos de las tareas.
	 * <p>
	 * Utilidad para garantizar la unicidad de los IDs en el repositorio.
	 * </p>
	 * 
	 * @return Un conjunto con los identificadores unicos de las tareas.
	 * @throws RepositoryException Si ocurre un error inesperado.
	 */
	Set<Integer> getUsedIDs() throws RepositoryException;
}