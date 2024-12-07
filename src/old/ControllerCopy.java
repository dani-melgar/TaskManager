package old;

import java.util.ArrayList;
import java.util.List;

import model.Model;
import model.Task;
import model.exporter.ExporterException;
import model.repository.RepositoryException;
import view.InteractiveView;

public class ControllerCopy {
	/* Atributos */
	private Model model;
	private InteractiveView view;

	public void start() {
		try {
			model.loadData();
			view.init();
		} catch (RepositoryException e) {
			view.init();
			view.showErrorMessage(e.getMessage());
		}
	}

	public void end() {
		try {
			model.saveData();
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		} finally {
			view.end();
		}
	}

	/*--------------------------------------------------------------------------------------------------------------------*/
	/*							CRUD		                       		              */
	/*--------------------------------------------------------------------------------------------------------------------*/
	
	public void addTask(Task t) {
		try {
			model.addTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public void createTask(Task t) {
		try {
			model.createTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public void editTask(Task t) {
		try {
			model.modifyTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public void deleteTask(Task t) {
		try {
			model.removeTask(t);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public List<Task> getAllTasks() {
		try {
			return model.getAllTasks();
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al obtener las tareas: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Task> getTasksSortedByPriority() {
		try {
			return model.getTaskSortedByPriority();
		} catch (RepositoryException e) {
			view.showErrorMessage("No se pudieron obtener las tareas ordenadas: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Task> getTasksSortedByCompletion() {
		try {
			return model.getTaskSortedByCompletion();
		} catch (RepositoryException e) {
			view.showErrorMessage("No se pudieron obtener las tareas ordenadas: " + e.getMessage());
			return new ArrayList<>();
		}
	}


	/*--------------------------------------------------------------------------------------------------------------------*/
	/*							IEXPORTER		                       		      */
	/*--------------------------------------------------------------------------------------------------------------------*/

	public void exportTasks(String format) {
		try {
			model.setExporter(format);
			try {
				model.exportTasks();
				// Cual usara aqui? Exporter o Repository
			} catch (Exception e) {
				view.showErrorMessage(e.getMessage());
			}
		} catch (ExporterException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public List<Task> importTasks(String format) {
		try {
			model.setImporter(format);
			try {
				return model.getImportedTasks();
			} catch (Exception e) {
				view.showErrorMessage(e.getMessage());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			view.showErrorMessage(e.getMessage());
			return new ArrayList<>();
		}

	}

	// Basura, revisar
	public List<Task> getImportedTasks() {
		List<Task> list = new ArrayList<>();
		try {
			list = model.getImportedTasks();
		} catch (Exception e) {
			view.showErrorMessage(e.getMessage());
		}
		return list;
	}

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

	public void mergeImportedTasks(boolean applyMerge) {
		try {
			model.mergeTasks(applyMerge);
			if (applyMerge) {
				view.showMessage("Tareas fusionadas con exito.");
			} else {
				view.showMessage("La fusion de tareas fue cancelada.");
			}
		} catch (ExporterException e) {
			view.showErrorMessage("Error al importar tareas: " + e.getMessage());
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al fusionar tareas: " + e.getMessage());
		}
	}
}