package model;

import java.util.HashSet;
import java.util.Set;

import model.exporter.IExporter;
import model.repository.BinaryRepository;
import model.repository.IRepository;

public class Model {
	/* Atributos */
	private IRepository repository;
	private IExporter exporter;

	/* Constructor */
	public Model(IRepository repository) {
		this.repository = repository;
	}

	public void setExporter(IExporter exporter) {
		this.exporter = exporter;
	}

	/* Metodos */
	public void loadData() {
		// Implementar
	}

	/* Obtener los identificadores en memoria que ha obtenido 
	 * la clase BinaryRepository
	*/

	public Set<Integer> getUsedIDs() {
		if (repository instanceof BinaryRepository) {
			return ((BinaryRepository) repository).getUsedIDs();
		}

		return new HashSet<>();
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
