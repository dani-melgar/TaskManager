package model;

import java.util.Set;

/**
 * Esta interfaz define un observador que es notificado de cambios en los identificadores de tareas.
 * El observador debe implementar el metodo {@code update} para recibir las actualizaciones.
 */
public interface TaskObserver {
	/**
	 * Este metodo es llamado para notificar al observador cuando hay cambios en los identificadores de tareas.
	 * 
	 * @param taskIDs el set de identificadores de tareas que han sido modificadas.
	 */
	void update(Set<Integer> taskIDs);
}