package controller;

import java.util.ArrayList;
import java.util.List;

import model.Model;
import model.Task;
import model.exporter.ExporterException;
import model.repository.RepositoryException;
import view.BaseView;

public class Controller {
	/* Atributos */
	private Model model;
	private BaseView view;

	public Controller(Model model, BaseView view) {
		this.model = model;
		this.view = view;
		// Asegurar la inyeccion del controlador
		view.setController(this);
	}

	// Deberia el controlador extender el error y que la vista haga try-catch
	public void exportFormat(String format) {
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

	public void importFormat(String format) {
		try {
			model.setExporter(format);
		} catch (ExporterException e) {
			view.showErrorMessage(e.getMessage());
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

	public void importTasks(List<Task> importedTasks) {
		try {
			model.mergeTasks(importedTasks, true);
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public void importTasks() {
		try {
			model.mergeTasks(true);
		} catch (Exception e) {
			view.showErrorMessage(e.getMessage());
		}
	}

	public void addTask(Task t) {
		try {
			model.addTask(t);
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

	public List<Task> getTasksByPriority() {
		List<Task> list = new ArrayList<>();
		try {
			list = model.getImportedTasks();
		} catch (Exception e) {
			view.showErrorMessage(e.getMessage());
		}
		return list;
	}

	public List<Task> getAllTasks() {
		try {
			return model.getAllTasks();
		} catch (RepositoryException e) {
			view.showErrorMessage("Error al obtener las tareas: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Task> getSortedTasks() {
		try {
			return model.getTaskShortedByPriority();
		} catch (RepositoryException e) {
			view.showErrorMessage("No se pudieron obtener las tareas ordenadas: " + e.getMessage());
			return new ArrayList<>();
	}
	}

	/*
	 * Ignorar el codigo de debajo, solo es para quitar el puto
	 * resaltado del Errorlens y VSCode
	 */
	public void start() {
		try {
			model.loadData();
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
			view.end();
		}
	}

}