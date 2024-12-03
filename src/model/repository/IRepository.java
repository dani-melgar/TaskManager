package model.repository;

import java.util.List;
import java.util.Set;

import model.Task;

public interface IRepository {
	
	/**
	 * Añade una nueva tarea.
	 * 
	 * <p>
	 * Este metodo genera un identificador unico para la tarea, comprueba que los
	 * campos obligatorios no esten vacios y comprueba que no existan duplicados
	 * en el repositorio antes de añadirla. Si la tarea es nula o no cumple con
	 * los requisitos, se lanza una excepcion.
	 * </p>
	 * 
	 * @param t : la tarea que se va a añadir.
	 * @throws RepositoryException Si la tarea es nula, tiene campos obligatorios vacíos,
	 *                              o si ocurre un error al añadirla.
	 */
	void addTask(Task t) throws RepositoryException;
	
	/**
	 * Elimina una tarea del repositorio por su identificador.
	 * 
	 * <p>
	 * Este metodo busca en la lista de tareas una que coincida con el identificador
	 * de la tarea proporcionada. Si se encuentra, se elimina tanto de la lista como
	 * del conjunto de identificadores.
	 * </p>
	 * 
	 * @param t la tarea que se desea eliminar.
	 * @throws RepositoryException si la tarea es nula, la lista de tareas esta vacia
	 *                             o si no se encuentra una tarea con el mismo identificador.
	 */
	void removeTask(Task t) throws RepositoryException;
	
	/**
	 * Modifica una tarea existente.
	 * <p>
	 * Este metodo reemplaza la tarea en la lista que coincide con el identificador 
	 * de la tarea proporcionada por la nueva tarea pasada como parametro. 
	 * Comprobaciones para garantizar que la tarea no sea nula y que 
	 * tanto el título como el contenido no sean nulos ni esten vacios. 
	 * Si la tarea no se encuentra, se lanza una excepcion.
	 * </p>
	 *
	 * @param t la nueva tarea que remplazara a la existente con el mismo identificador.
	 * @throws RepositoryException si:
	 *         <ul>
	 *             <li>La tarea es nula.</li>
	 *             <li>Atributos de la tarea son nulos o estan vacios.</li>
	 *             <li>No se encuentra la tarea.</li>
	 *         </ul>
	 */
	void modifyTask(Task t) throws RepositoryException;
	
	/**
	 * Devuelve una lista con todas las tareas almacenadas.
	 * <p>
	 * Este metodo devuelve una copia de la lista de tareas para evitar modificaciones externas 
	 * que puedan afectar a los datos. Si no hay tareas almacenadas, devuelve una lista vacía.
	 * </p>
	 * 
	 * @return una lista con todas las tareas. Nunca devuelve {@code null}.
	 * @throws RepositoryException si ocurre un error inesperado al generar la lista.
	 */
	List<Task> getAllTasks() throws RepositoryException;

	/**
	 * Devuelve una lista de tareas ordenadas por prioridad en orden descendente.
	 * 
	 * <p>
	 * La lista original de tareas no se modifica. En su lugar, este metodo genera una copia,
	 * la ordena y la devuelve. Las tareas con mayor prioridad (valor num mas alto)
	 * apareceran primero en la lista.
	 * <p>
	 * 
	 * @return una lista de tareas ordenadas por prioridad en orden descendente.
	 * @throws RepositoryException si ocurre un error al obtener o procesar las tareas.
	 */
	List<Task> getTasksShortedByPriority() throws RepositoryException;

	/**
	 * Devuelve una lista con todos los identificadores de las tareas.
	 * <p>
	 * Este metodo devuelve una lista con los identificadores unicos de todas
	 * las tareas en memoria, para poder modificar la lista de tareas garantizando
	 * tareas con identificadores unicos
	 * </p>
	 * 
	 * @return una lista con todas los identificadores de las tareas. Nunca devuelve {@code null}.
	 * @throws RepositoryException si ocurre un error inesperado.
	 */
	Set<Integer> getUsedIDs() throws RepositoryException;
}