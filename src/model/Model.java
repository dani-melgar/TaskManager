package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.exporter.ExporterException;
import model.exporter.ExporterFactory;
import model.exporter.IExporter;
import model.repository.IRepository;
import model.repository.RepositoryException;

public class Model {
	private IRepository repository;
	private IExporter exporter;
	private List<TaskObserver> observers = new ArrayList<>();

	public Model(IRepository repository) {
		this.repository = repository;
	}
	
	public void addObserver(TaskObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}
	
	public void removeObserver(TaskObserver observer) {
		observers.remove(observer);
	}
	
	public void notifyObservers() throws RepositoryException {
		Set<Integer> taskIDs = repository.getUsedIDs();
		for (TaskObserver observer : observers) {
			observer.update(taskIDs);
		}
	}

	public void loadData() throws RepositoryException{
		repository.loadTasks();
	}

	public void saveData() throws RepositoryException {
		repository.saveTasks();
	}

	public void createTask(Task t) throws RepositoryException {
		repository.createTask(t);
		notifyObservers();
	}
	public void addTask(Task t) throws RepositoryException {
		repository.addTask(t);
		notifyObservers();
	}
	
	public void removeTask(Task t) throws RepositoryException {
		repository.removeTask(t);
		notifyObservers();
	}
	
	public void modifyTask(Task t) throws RepositoryException {
		repository.modifyTask(t);
		notifyObservers();
	}

	public Set<Integer> getUsedIDs() throws RepositoryException {
		return repository.getUsedIDs();
	}
	
	public List<Task> getAllTasks() throws RepositoryException {
		return repository.getAllTasks();
	}

	public List<Task> getTaskSortedByPriority() throws RepositoryException {
		return repository.getTasksSortedByPriority();
	}

	public List<Task> getTaskSortedByCompletion() throws RepositoryException {
		return repository.getTasksSortedByPriority();
	}

	public void setExporter(String format) throws ExporterException {
		this.exporter = ExporterFactory.getExporter(format);
	}

	public void setImporter(String format) throws ExporterException {
		this.exporter = ExporterFactory.getExporter(format);
	}

	public void exportTasks() throws ExporterException, RepositoryException {
		this.exporter.exportTasks(repository.getTasksSortedByDate());
	}

	public List<Task> getImportedTasks() throws ExporterException {
		return exporter.importTasks();
	}

	public void mergeTasks(List<Task> importedTasks, boolean applyMerge) throws RepositoryException {
		if (applyMerge) {
			// Lista para almacenar errores durante la fusion
			List<RepositoryException> errors = new ArrayList<>();
			
			for (Task importedTask : importedTasks) {
				try {
					repository.addTask(importedTask);
				} catch (RepositoryException e) {
					// Registrar el error y continuar con las demas tareas
					errors.add(new RepositoryException("Error al agregar la tarea con ID " + importedTask.getIdentifier() + ": " + e.getMessage(), e));
				}
			}

			// Si hubo errores, lanzar una excepcion que los contenga todos
			if (!errors.isEmpty()) {
				StringBuilder errorMessage = new StringBuilder("Se encontraron errores durante la fusion de tareas:\n");
				for (RepositoryException error : errors) {
					errorMessage.append("- ").append(error.getMessage()).append("\n");
				}
				throw new RepositoryException(errorMessage.toString());
			}
		}
	}

	public void mergeTasks(boolean applyMerge) throws ExporterException, RepositoryException {
		List<Task> importedTasks = exporter.importTasks();
		if (applyMerge) {
			// Lista para almacenar errores durante la fusion
			List<RepositoryException> errors = new ArrayList<>();
			
			for (Task importedTask : importedTasks) {
				try {
					repository.addTask(importedTask);
				} catch (RepositoryException e) {
					// Registrar el error y continuar con las demas tareas
					errors.add(new RepositoryException("Error al agregar la tarea con ID " + importedTask.getIdentifier() + ": " + e.getMessage(), e));
				}
			}

			// Si hubo errores, lanzar una excepcion que los contenga todos
			if (!errors.isEmpty()) {
				StringBuilder errorMessage = new StringBuilder("Se encontraron errores durante la fusion de tareas:\n");
				for (RepositoryException error : errors) {
					errorMessage.append("- ").append(error.getMessage()).append("\n");
				}
				throw new RepositoryException(errorMessage.toString());
			}
		}
	}

	public IRepository getRepository() {
		return repository;
	}

	public void setRepository(IRepository repository) {
		this.repository = repository;
	}

	public IExporter getExporter() {
		return exporter;
	}
}