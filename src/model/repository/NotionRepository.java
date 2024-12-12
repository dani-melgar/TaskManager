package model.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Task;
import notion.api.v1.NotionClient;
import notion.api.v1.http.OkHttp5Client;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageParent;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.model.pages.PageProperty.RichText.Text;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import notion.api.v1.request.pages.CreatePageRequest;
import notion.api.v1.request.pages.UpdatePageRequest;

public class NotionRepository implements IRepository {

	private Set<Integer> usedIDs = new HashSet<>();
	private List<Task> tasks = new ArrayList<>();

	private final NotionClient client;
	private final String databaseID;
	private final String titleColumnName = "Identifier";
	
	public NotionRepository(String apiToken, String databaseID) {
		// Crear cliente de notion
		this.client = new NotionClient(apiToken);

		// Configurar cliente HTTP adecuadao y tiempos de espera
		client.setHttpClient(new OkHttp5Client(60000, 60000, 60000));
		
		// Configurar loggers
		client.setLogger(new Slf4jLogger());

		// Silenciar/Activar los registros de log de Notion API
		System.setProperty("notion.api.v1.logging.StdoutLogger", "debug");
		
		this.databaseID = databaseID;

	}

	@Override
	public void loadTasks() throws RepositoryException {
		try {
			// Cargar las tareas desde Notion
			List<Task> loadedTasks = getAllTasks();
			
			// Limpiar la lista de tareas locales antes de cargar nuevas
			tasks.clear();

			// Agregar las tareas cargadas desde Notion al repositorio local
			for (Task task : loadedTasks) {
				tasks.add(task);
			}

		} catch (Exception e) {
			throw new RepositoryException("Error al cargar las tareas desde Notion: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void saveTasks() throws RepositoryException {
		try {
			// Recorrer todas las tareas locales y guardarlas en Notion
			for (Task task : tasks) {
				// Comprobar si la tarea ya existe en Notion, si no existe la creas
				String pageId = findPageIdByIdentifier(String.valueOf(task.getIdentifier()), titleColumnName);
					
					if (pageId == null) {
						// Si no existe, crea la tarea en Notion
						createTask(task);
					} else {
						// Si ya existe, actualiza la tarea  || y si la borro?
						modifyTask(task);
					}
			}
		} catch (Exception e) {
			throw new RepositoryException("Error al guardar las tareas en Notion: " + e.getMessage(), e);
		}
	}

	@Override
	public void createTask(Task t) throws RepositoryException {
		// Comprobar si la tarea esta vacia
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		int uniqueID = generateUniqueID();
		t.setIdentifier(uniqueID);

		// Comprobar titulo y contenido no nulos o vacíos
		if (t.getTitle() == null || t.getTitle().isEmpty()) {
			throw new RepositoryException("Error: El titulo de la tarea es obligatorio");
		}

		if (t.getContent() == null || t.getContent().isEmpty()) {
			throw new RepositoryException("Error: El contenido de la tarea es obligatorio");
		}

		// Llamar a addTask para agregar la tarea a la lista
		addTask(t);
	}

	@Override
	public void addTask(Task t) throws RepositoryException {
		// Comprobar si la tarea vacia vacia
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula");
		}

		// Comprobar que no haya una tarea con el mismo identificador
		if (tasks != null && !tasks.isEmpty()) {
			for (Task task : tasks) {
				if (task.getIdentifier() == t.getIdentifier()) {
					throw new RepositoryException("Error: Tarea con identificador: " + t.getIdentifier() + " ya existe");
				}
			}
		}

		// Comprobar titulo y contenido no nulos o vacíos
		if (t.getTitle() == null || t.getTitle().isEmpty()) {
			throw new RepositoryException("Error: El titulo de la tarea es obligatorio");
		}

		if (t.getContent() == null || t.getContent().isEmpty()) {
			throw new RepositoryException("Error: El contenido de la tarea es obligatorio");
		}

		// Crear las propiedades de la pagina
		try {
			Map<String, PageProperty> properties = Map.of(
				"Identifier", createTitleProperty(String.valueOf(t.getIdentifier())),
				"Title", createRichTextProperty(t.getTitle()),
				"Date", createDateProperty(t.getDate().toString()),
				"Content", createRichTextProperty(t.getContent()),
				"Priority", createNumberProperty(t.getPriority()),
				"Estimated Duration", createNumberProperty(t.getEstimatedDuration()),
				"Completed", createCheckboxProperty(t.isCompleted())
			);

			PageParent parent = PageParent.database(databaseID);
			CreatePageRequest request = new CreatePageRequest(parent, properties);
			Page response = client.createPage(request);
			// Actualizar set de IDs
			usedIDs.add(Integer.getInteger(response.getId()));
		} catch (Exception e) {
			throw new RepositoryException("Error al crear la tarea en Notion: " + e.getMessage(), e);
		}
	}

	@Override
	public void removeTask(Task t) throws RepositoryException {
		if (t == null) {
			throw new RepositoryException("La tarea a eliminar no puede ser nula.");
		}

		try {
			String pageId = findPageIdByIdentifier(String.valueOf(t.getIdentifier()), titleColumnName);
			if (pageId == null) {
			throw new RepositoryException("No se encontro la pagina con identificador: " + t.getIdentifier());
			}

			// Archivar la pagina
			UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, Collections.emptyMap(), true);
			client.updatePage(updateRequest);
		} catch (Exception e) {
			throw new RepositoryException("Error al intentar archivar la tarea con identificador: " + t.getIdentifier() + ". Detalles: " + e.getMessage(), e);
		}
	}

	@Override
	public void modifyTask(Task t) throws RepositoryException {
		if (t == null) {
			throw new RepositoryException("Error: La tarea es nula.");
		}

		if (t.getIdentifier() == 0) {
			throw new RepositoryException("Error: La tarea no tiene un identificador válido.");
		}

		try {
			// Buscar el ID interno de Notion usando el titulo
			String pageId = findPageIdByIdentifier(String.valueOf(t.getIdentifier()), titleColumnName);
			if (pageId == null) {
				throw new RepositoryException("Error: No se encontró una tarea con el identificador: " + t.getIdentifier());
			}

			// Crear las propiedades actualizadas
			Map<String, PageProperty> updatedProperties = Map.of(
				"Title", createRichTextProperty(t.getTitle()),
				"Date", createDateProperty(String.valueOf(t.getDate())),
				"Content", createRichTextProperty(t.getContent()),
				"Priority", createNumberProperty(t.getPriority()),
				"Estimated Duration", createNumberProperty(t.getEstimatedDuration()),
				"Completed", createCheckboxProperty(t.isCompleted())
			);

			UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, updatedProperties);
			client.updatePage(updateRequest);
		} catch (Exception e) {
			throw new RepositoryException("Error al actualizar la tarea: " + e.getMessage(), e);
		}
	}

	private String findPageIdByIdentifier(String identifier, String columnName) throws RepositoryException {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("El identificador no puede ser nulo o estar vacio.");
		}

		try {
			// Crear la solicitud para consultar la base de datos
			QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseID);
			QueryResults queryResults = client.queryDatabase(queryRequest);

			// Iterar por las paginas de resultados
			for (Page page : queryResults.getResults()) {
				Map<String, PageProperty> properties = page.getProperties();

				// Comprobar si la propiedad existe y contiene el identificador buscado
				if (properties.containsKey(titleColumnName)) {
					PageProperty property = properties.get(titleColumnName);
					if (property.getTitle() != null && !property.getTitle().isEmpty()) {
						String titleContent = property.getTitle().get(0).getText().getContent();
						// Retornar ID de la pagina
						if (titleContent.equals(identifier)) {
							return page.getId();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<Task> getAllTasks() throws RepositoryException {
		List<Task> loadedTasks = new ArrayList<>();
		try {
			QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseID);
			QueryResults queryResults = client.queryDatabase(queryRequest);

			for (Page page : queryResults.getResults()) {
				Map<String, PageProperty> properties = page.getProperties();
				Task task = mapPageToTask(page.getId(), properties);
				if (task != null) {
					loadedTasks.add(task);
					this.usedIDs.add(task.getIdentifier());
				}
			}
			return loadedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener las tareas desde Notion: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Task> getTasksSortedByPriority() throws RepositoryException {
		try {
			List<Task> sortedTasks = getAllTasks();
			// Ordenamos las tareas por su atributo "priority" (de mayor a menor)
			sortedTasks.sort((task1, task2) -> Integer.compare(task2.getPriority(), task1.getPriority()));
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas ordenada por prioridad", e);
		}
	}

	@Override
	public List<Task> getTasksSortedByCompletion() throws RepositoryException {
		try {
			// Trabajar sobre una copia
			List<Task> sortedTasks = new ArrayList<>(getAllTasks());
			sortedTasks.sort((task1, task2) -> Boolean.compare(!task2.isCompleted(), !task1.isCompleted()));
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas ordenada por estado de completado.", e);
		}
	}

	@Override
	public List<Task> getTasksSortedByDate() throws RepositoryException {
		try {
			List<Task> sortedTasks = new ArrayList<>(getAllTasks());
			sortedTasks.sort((task1, task2) -> task1.getDate().compareTo(task2.getDate()));
			sortedTasks.reversed();
			return sortedTasks;
		} catch (Exception e) {
			throw new RepositoryException("Error al obtener la lista de tareas ordenada por fecha", e);
		}
	}

	@Override
	public Set<Integer> getUsedIDs() throws RepositoryException {
		if (usedIDs == null) {
			return Collections.emptySet();
		}
		// Copia inmutable
		return Collections.unmodifiableSet(new HashSet<>(usedIDs));
	}

	private PageProperty createTitleProperty(String identifier) {
		RichText idText = new RichText();
		idText.setText(new Text(identifier));
		PageProperty idProperty = new PageProperty();
		idProperty.setTitle(Collections.singletonList(idText));
		return idProperty;
	}

	private PageProperty createRichTextProperty(String text) {
		RichText richText = new RichText();
		richText.setText(new Text(text));
		PageProperty property = new PageProperty();
		property.setRichText(Collections.singletonList(richText));
		return property;
	}

	private Task mapPageToTask(String pageId, Map<String, PageProperty> properties) {
		try {
			int identifier = Integer.parseInt(properties.get("Identifier").getTitle().get(0).getText().getContent());
			String title = properties.get("Title").getRichText().get(0).getText().getContent();
			LocalDate date = LocalDate.parse(properties.get("Date").getDate().getStart());
			String content = properties.get("Content").getRichText().get(0).getText().getContent();
			int priority = properties.get("Priority").getNumber().intValue();
			int estimatedDuration = properties.get("Estimated Duration").getNumber().intValue();
			boolean completed = properties.get("Completed").getCheckbox();

			return new Task(identifier, title, date, content, priority, estimatedDuration, completed);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Genera un identificador unico para una nueva tarea.
	 * 
	 * <p>
	 * El metodo utiliza un generador de numeros aleatorios para crear un
	 * identificador diferente a los ya existentes. El identificador
	 * generado se añade al conjunto de identificadores utilizados
	 * para garantizar posibles identificadores unicos futuros.
	 * </p>
	 * 
	 * @return Un identificador unico para una tarea.
	 */
	private int generateUniqueID() {
		Random random = new Random();
		int newID;
		do {
			newID = random.nextInt(Integer.MAX_VALUE);
		} while (usedIDs.contains(newID));

		usedIDs.add(newID);
		return newID;
	}

	private PageProperty createNumberProperty(Integer number) {
		PageProperty property = new PageProperty();
		property.setNumber(number);
		return property;
	    }
	
	private PageProperty createDateProperty(String date) {
		PageProperty property = new PageProperty();
		PageProperty.Date dateProperty = new PageProperty.Date();
		dateProperty.setStart(date);
		property.setDate(dateProperty);
		return property;
	}
	
	private PageProperty createCheckboxProperty(boolean checked) {
		PageProperty property = new PageProperty();
		property.setCheckbox(checked);
		return property;
	}
}