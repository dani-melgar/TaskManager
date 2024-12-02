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
		List<Task> tasks = new ArrayList<>();
		try {
			tasks = model.getTasks();
		} catch (RepositoryException e) {
			view.showErrorMessage(e.getMessage());
		}
		return tasks;
	}

	/*
	 * Ignorar el codigo de debajo, solo es para quitar el puto
	 * resaltado del Errorlens y VSCode
	 */
	public void start() {
		model.loadData();
		view.init();
	}

	public void end() {
		view.end();
	}

}