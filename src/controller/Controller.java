package controller;

import java.util.ArrayList;
import java.util.List;

import model.Model;
import model.Task;
import model.exporter.ExporterException;
import model.repository.RepositoryException;
import view.InteractiveView;

/**
 * Controlador que maneja la logica de la aplicación y la 
 * hace de intermediario entre el modelo y la vista.
 * Gestiona los metodos de las tareas (CRUD) y la
 * exportacion/importacion de datos.
 */
public class Controller {

	/* Atributos */
	private Model model;
	private InteractiveView view;

	/**
	 * Constructor que inicializa el controlador con el modelo y la vista.
	 * Tambien, se asegura de que el controlador este inyectado en la vista.
	 *
	 * @param model el modelo que contiene los datos de las tareas.
	 * @param view  la vista que interactuara con el usuario.
	 */
	public Controller(Model model, InteractiveView view) {
		this.model = model;
		this.view = view;
		view.setController(this);
	}

	/**
	 * Inicia la app cargando los datos desde el modelo e iniciando la vista.
	 */
	public void start() {
		try {
			model.loadData();
			view.init();
		} catch (RepositoryException e) {
			view.init();
			view.showErrorMessage(e.getMessage());
		}
	}

	/**
	 * Finaliza la app guardando los datos del modelo.
	 */
	public void end() {
		try {
			model.saveData();
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
			view.end();
		}
	}

	/*--------------------------------------------------------------------------------------------------------------------*/
	/* CRUD (Create, Read, Update, Delete) */
	/*--------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Añade una nueva tarea.
	 *
	 * @param t la tarea a añadir.
	 */
	public void addTask(Task t) {
		try {
			model.addTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	/**
	 * Crea una nueva tarea.
	 *
	 * @param t la tarea a crear.
	 */
	public void createTask(Task t) {
		try {
			model.createTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	/**
	 * Edita una tarea existente.
	 *
	 * @param t la tarea modificada.
	 */
	public void editTask(Task t) {
		try {
			model.modifyTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	/**
	 * Elimina una tarea.
	 *
	 * @param t la tarea a eliminar.
	 */
	public void deleteTask(Task t) {
		try {
			model.removeTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	/**
	 * Obtiene todas las tareas almacenadas.
	 *
	 * @return una lista de todas las tareas.
	 */
	public List<Task> getAllTasks() {
		try {
			return model.getAllTasks();
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al obtener las tareas: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene todas las tareas ordenadas por prioridad.
	 *
	 * @return una lista de tareas ordenadas por prioridad.
	 */
	public List<Task> getTasksSortedByPriority() {
		try {
			return model.getTaskSortedByPriority();
		} catch (RepositoryException e) {
			view.showErrorMessage("No se pudieron obtener las tareas ordenadas por prioridad: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene todas las tareas ordenadas por su estado de completado.
	 *
	 * @return una lista de tareas ordenadas por estado de completado.
	 */
	public List<Task> getTasksSortedByCompletion() {
		try {
			return model.getTaskSortedByCompletion();
		} catch (RepositoryException e) {
			view.showErrorMessage("No se pudieron obtener las tareas ordenadas por completado: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/*--------------------------------------------------------------------------------------------------------------------*/
	/* Exportacion e Importacion de Tareas */
	/*--------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Exporta las tareas en el formato especificado.
	 *
	 * @param format el formato en que se exportacion las tareas (CSV, JSON, etc.).
	 */
	public void exportTasks(String format) {
		try {
			model.setExporter(format);
			model.exportTasks();
		} catch (ExporterException e) {
			view.showErrorMessage("Error al exportar tareas: " + e.getMessage());
		} catch (Exception e) {
			view.showErrorMessage("Error desconocido al exportar tareas: " + e.getMessage());
		}
	}

	/**
	 * Importa tareas desde un formato especificado.
	 *
	 * @param format el formato desde el cual se importaran las tareas (CSV, JSON, etc.).
	 * @return una lista de tareas importadas.
	 */
	public List<Task> importTasks(String format) {
		try {
			model.setImporter(format);
			return model.getImportedTasks();
		} catch (ExporterException e) {
			view.showErrorMessage("Error al importar tareas: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Fusiona las tareas importadas con las tareas existentes en el modelo.
	 * Si se indica que se debe aplicar la fusion, las tareas serán combinadas, de
	 * lo contrario, no se fusionaran.
	 *
	 * @param importedTasks las tareas importadas a fusionar.
	 * @param applyMerge    indica si las tareas deben ser fusionadas.
	 */
	public void mergeImportedTasks(List<Task> importedTasks, boolean applyMerge) {
		try {
			model.mergeTasks(importedTasks, applyMerge);
			if (applyMerge) {
				view.showMessage("Tareas fusionadas con exito.");
			} else {
				view.showMessage("La fusion de tareas fue cancelada.");
			}
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al fusionar tareas: " + e.getMessage());
		}
	}

	/**
	 * Fusiona las tareas importadas sin parametros adicionales.
	 * Si se indica que se debe aplicar la fusion, las tareas seran combinadas, de
	 * lo contrario, no se aplicara ninguna fusion.
	 *
	 * @param applyMerge indica si las tareas deben ser fusionadas.
	 */
	public void mergeImportedTasks(boolean applyMerge) {
		try {
			model.mergeTasks(applyMerge);
			if (applyMerge) {
				view.showMessage("Tareas fusionadas con exito.");
			} else {
				view.showMessage("La fusion fue cancelada.");
			}
		} catch (ExporterException e) {
			view.showErrorMessage("Error al importar tareas: " + e.getMessage());
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al fusionar tareas: " + e.getMessage());
		}
	}
}