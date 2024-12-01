package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.exporter.ExporterException;
import model.exporter.IExporter;
import model.repository.IRepository;
import model.repository.RepositoryException;

public class Model {
	/* Atributos */
	private IRepository repository;
	private IExporter exporter;
	private List<TaskObserver> observers = new ArrayList<>();

	/* Constructor */
	public Model(IRepository repository) {
		this.repository = repository;
	}

	public void setExporter(IExporter exporter) {
		this.exporter = exporter;
	}

	/*
	 * METODOS DE TaskObserver (Cambiarle el nombre a IObserver)
	 */
	public void addObserver(TaskObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(TaskObserver observer) {
		observers.remove(observer);
	}

	// Revisar las excepciones de getUsedIDs
	public void notifyObservers() throws RepositoryException {
		Set<Integer> taskIDs = repository.getUsedIDs();
		for (TaskObserver observer : observers) {
			observer.update(taskIDs);
		}
	}

	/*
	 * METODOS DE IRepository
	 */
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

	/*
	 * METODOS DE IExporter
	*/
	public void mergeTasks(List<Task> importedTasks, boolean applyMerge) throws RepositoryException {
		if (applyMerge) {
			for (Task importedTask : importedTasks) {
				try {
					repository.addTask(importedTask);
				} catch (RepositoryException e) {
					// Mismo problema en funcion readTaskFromCSV
					System.err.println("No se pudo agregar la tarea: " + importedTask + ". Motivo: " + e.getMessage());
				}
			}
			System.out.println("Tareas fusionadas con éxito.");
		} else {
			System.out.println("La fusión de tareas fue cancelada.");
		}
	}

	public void mergeTasks(boolean applyMerge) throws ExporterException, RepositoryException {
		List<Task> importedTasks = exporter.importTasks();

		if (applyMerge) {
			for (Task importedTask : importedTasks) {
				try {
					repository.addTask(importedTask);
				} catch (RepositoryException e) {
					System.err.println("No se pudo agregar la tarea: " + importedTask + ". Motivo: " + e.getMessage());
				}
			}
			System.out.println("Tareas fusionadas con éxito.");
		} else {
			System.out.println("La fusión de tareas fue cancelada.");
		}
	}

	public void loadData() {
		// Implementar
	}

	public void saveData() {
		// Implementar
	}

	/* Getters & Setters */
	// Creados por VSCode, para que no den por culo con el resaltado
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
