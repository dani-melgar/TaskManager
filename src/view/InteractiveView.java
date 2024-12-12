package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import controller.Controller;
import model.Task;


public class InteractiveView extends BaseView {

	/* Atributos */
	public Controller controller;

	/* Metodos */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void init() {
		ViewUtils.clearScreen();
		ViewUtils.showLoading("Iniciando el programa");
		Scanner refScanner = new Scanner(System.in);
		mainMenu(refScanner);
	}

	@Override
	public void showMessage(String message) {
		System.out.println(ViewUtils.GREEN + "Mensaje: " + message + ViewUtils.RESET);
	}

	@Override
	public void showErrorMessage(String message) {
		System.err.println(ViewUtils.RED + "Error: " + message + ViewUtils.RESET);
	}

	@Override
	public void end() {
		ViewUtils.clearScreen();
		controller.end();
	}

	public void mainMenu(Scanner refScanner) {
		String title = "Menu Principal";
		String[] options = {
			"1. Menu CRUD",
			"2. Importar/Exportar",
			"3. Salir"
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					menuCrud(refScanner);
					break;
				case 2:
					menuImportExport(refScanner);
					break;
				case 3:
					if (ViewUtils.confirmActionCentered("¿Seguro que desea salir?", refScanner)) {
						salir = true;
					}
					break;
			}
		} while (!salir);
		refScanner.close();
		end();
	}

	private void menuCrud(Scanner refScanner) {
		String title = "Menu CRUD";
		String[] options = {
			"1. Agregar Tarea",
			"2. Listar Tareas",
			"3. Volver"
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					addTask(refScanner);
					break;
				case 2:
					listTasksMenu(refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
		} while (!salir);

	}

	private void addTask(Scanner refScanner) {
		ViewUtils.clearScreen();
		
		System.out.printf("Titulo de la Tarea: ");
		String title = refScanner.nextLine();

		System.out.printf("Fecha: ");
		LocalDate date = ViewUtils.readDate(refScanner);

		System.out.printf("Contenido: ");
		String content = refScanner.nextLine();

		System.out.printf("Prioridad: ");
		int priority = ViewUtils.validOptionInteger(refScanner, 1, 5);

		// Cambiar maxValue por el num max de un integer
		System.out.printf("Duracion estimada: ");
		int estimatedDuration = ViewUtils.validOptionInteger(refScanner, 1, 9999999);

		boolean completada = ViewUtils.confirmAction("Tarea completada?:", refScanner);

		Task t = new Task(title, date, content, priority, estimatedDuration, completada);
		controller.createTask(t);
	}

	// Revisar enunciado -> Filtrar Tareas por Completadas / Sin Completar
	private void listTasksMenu(Scanner refScanner) {
		String title = "Listar Tareas";
		String[] options = {
			"1. Ordenadas por prioridad [Ascendente -> Descendente]",
			"2. Ordenadas por Estado [Completadas]",
			"3. Volver"
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					modifyTaskMenu(refScanner, option);
					break;
				case 2:
					modifyTaskMenu(refScanner, option);
					break;
				case 3:
					salir = true;
					break;
				default:
					break;
			}
		} while (!salir);

	}

	private void modifyTaskMenu(Scanner refScanner, int displayOption) {
		List<Task> tasks = new ArrayList<>();
		String title = "Modificar Detalles de una Tarea";
		String[] options = {
			"1. Marcar [Completa/Incompleta]",
			"2. Modificar Informacion Tarea",
			"3. Eliminar Tarea",
			"4. Volver"
		};
		
		boolean salir = false;
		do {
			switch (displayOption) {
				case 1:
					tasks = controller.getTasksSortedByPriority();
					break;
				case 2:
					tasks = controller.getTasksSortedByCompletion();
					break;
			}
			
			ViewUtils.clearScreen();
			displayTasks(tasks);
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					modifyTaskStatus(tasks, refScanner);
					break;
				case 2:
					modifyTaskInfo(tasks, refScanner);
					break;
				case 3:
					modifyTaskExistance(tasks, refScanner);
					break;
				case 4:
					salir = true;
					break;
			}
		} while (!salir);
	}

	private void modifyTaskStatus(List<Task> tasks, Scanner refScanner) {
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			displayTasks(tasks);
			try {
				System.out.printf("Seleccione el identificador de una tarea: ");
				int idUserTask = Integer.parseInt(refScanner.nextLine().trim());
				
				Task selectedTask = null;
				for (Task task : tasks) {
					if (task.getIdentifier() == idUserTask) {
						selectedTask = task;
						break;
					}
				}

				if (selectedTask != null) {
					boolean newStatus = !selectedTask.isCompleted();
					// Necesita tantos parametros por las comprobaciones del BaseView
					Task updatedTask = new Task(
						selectedTask.getIdentifier(),
						selectedTask.getTitle(),
						selectedTask.getDate(),
						selectedTask.getContent(),
						selectedTask.getPriority(),
						selectedTask.getEstimatedDuration(),
						newStatus
					);

					controller.editTask(updatedTask);
					showMessage("El estado '" + selectedTask.getTitle() + "' ha cambiado a: " + (newStatus ? "Completada" : "Pendiente"));

					System.out.printf("Presione una tecla para continuar...");
					refScanner.nextLine();
					salir = true;
				} else {
					showErrorMessage("No se encontro una tarea con el identificador proporcionado.");
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Por favor, introduzca un numero.");
			}
		} while (!salir);
	}

	private void modifyTaskInfo(List<Task> tasks, Scanner refScanner) {
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			displayTasks(tasks);

			try {
				String input = ViewUtils.requestInputWithCancel("Seleccione el identificador de una tarea", "CANCELAR", refScanner);
				if (input == null) {
					showMessage("Operación cancelada.");
					salir = true;
					continue;
				}

				int idUserTask = Integer.parseInt(input);

				Task selectedTask = null;
				for (Task task : tasks) {
					if (task.getIdentifier() == idUserTask) {
					selectedTask = task;
					break;
					}
				}

				if (selectedTask != null) {
					// Solicitar nuevos datos
					String title = ViewUtils.requestInputWithCancel("Introduzca el nuevo título", "CANCELAR", refScanner);
					if (title == null) {
						showMessage("Operacion cancelada.");
						salir = true;
						continue;
					}

					System.out.printf("Introduzca la nueva fecha: ");
					LocalDate date = ViewUtils.readDate(refScanner);

					String content = ViewUtils.requestInputWithCancel("Introduzca el nuevo contenido", "CANCELAR", refScanner);
					if (content == null) {
						showMessage("Operación cancelada.");
						salir = true;
						continue;
					}

					System.out.printf("Introduzca la prioridad de la tarea: ");
					int priority = ViewUtils.validOptionInteger(refScanner, 1, 5);

					System.out.printf("Duracion estimada de la nueva tarea (En minutos): ");
					int estimatedDuration = ViewUtils.validOptionInteger(refScanner, 0, Integer.MAX_VALUE);

					boolean completada = ViewUtils.confirmAction("¿Tarea completada?: ", refScanner);

					Task t = new Task(selectedTask.getIdentifier(), title, date, content, priority, estimatedDuration, completada);
					controller.editTask(t);

					showMessage("Tarea modificada");
					System.out.printf("Presione una tecla para continuar...");
					refScanner.nextLine();
					salir = true;
				} else {
					showErrorMessage("No se encontro una tarea con el identificador proporcionado.");
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Por favor, introduzca un numero valido.");
			}
		} while (!salir);
	}


	private void modifyTaskExistance(List<Task> tasks, Scanner refScanner) {
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			displayTasks(tasks);
			try {
				System.out.printf("Seleccione el identificador de una tarea: ");
				int idUserTask = Integer.parseInt(refScanner.nextLine().trim());
				
				Task selectedTask = null;
				for (Task task : tasks) {
					if (task.getIdentifier() == idUserTask) {
						selectedTask = task;
						break;
					}
				}

				if (selectedTask != null) {
					Task deleteTask = new Task(selectedTask.getIdentifier());
					controller.deleteTask(deleteTask);
					showMessage("Tarea Eliminada Correctamente");

					System.out.printf("Presione una tecla para continuar...");
					refScanner.nextLine();
					salir = true;
				} else {
					showErrorMessage("No se encontro una tarea con el identificador proporcionado.");
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Por favor, introduzca un numero.");
			}
		} while (!salir);	
	}

	private void menuImportExport(Scanner refScanner) {
		String title = "Menu Importar/Exportar";
		String[] options = {
			"1. Importar Tareas",
			"2. Exportar Tareas",
			"3. Volver",
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
			switch (option) {
				case 1:
					handleImport(refScanner);
					break;
				case 2:
					handleExport(refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
		} while (!salir);

	}

	private void handleImport(Scanner refScanner) {
		String title = "Importar Tareas";
		String[] options = {
			"1. Importar desde CSV",
			"2. Importar desde JSON",
			"3. Volver",
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
			switch (option) {
				case 1:
					importTasks("csv", refScanner);
					break;
				case 2:
					importTasks("json", refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
		} while (!salir);
	}

	private void importTasks(String format, Scanner refScanner) {
		List<Task> importedTasks = controller.importTasks(format);
		displayTasks(importedTasks);

		System.out.printf("Presione una tecla para continuar...");
		refScanner.nextLine();

		mergeTasks(importedTasks, refScanner);
	}

	private void mergeTasks(List<Task> importedTasks, Scanner refScanner) {
		ViewUtils.clearScreen();
		System.out.println("-".repeat(ViewUtils.getTerminalWidth()));;
		ViewUtils.printCenteredText("RESULTADOS DE IMPORTACION");
		System.out.println("-".repeat(ViewUtils.getTerminalWidth()));;
		displayTasks(importedTasks);
		System.out.println("-".repeat(ViewUtils.getTerminalWidth()));;

		if (ViewUtils.confirmAction("Desea juntar las tareas con las ya existentes?", refScanner)) {
			controller.mergeImportedTasks(importedTasks, true);

			System.out.printf("Presione una tecla para continuar...");
			refScanner.nextLine();
		}
	}

	private void handleExport(Scanner refScanner) {
		String title = "Exportar Tareas";
		String[] options = {
			"1. Exportar a CSV",
			"2. Exportar a JSON",
			"3. Volver",
		};
		boolean salir = false;
		do {
			ViewUtils.clearScreen();
			ViewUtils.printMenu(title, options);
			int option = ViewUtils.askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
			switch (option) {
				case 1:
					exportTasks("csv", refScanner);
					break;
				case 2:
					exportTasks("json", refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
		} while (!salir);

	}
	
	private void exportTasks(String format, Scanner refScanner) {
		ViewUtils.clearScreen();
		ViewUtils.showLoading("Exportando Tareas");

		controller.exportTasks(format);
		System.out.printf("Presione una tecla para continuar...");
		refScanner.nextLine();
	}

	private void displayTasks(List<Task> tasks) {
		int terminalWidth = ViewUtils.getTerminalWidth();
		int maxContentWidth = 40;

		ViewUtils.printCenteredText("TAREAS");
		System.out.println("-".repeat(ViewUtils.getTerminalWidth()));
		String[] headers = {
			"ID", "Título", "Fecha", "Contenido", "Prioridad", "Duracion Estimada", "Completado"
		};

		int[] columnWidths = ViewUtils.calculateColumnWidths(terminalWidth, maxContentWidth);

		ViewUtils.printRow(headers, columnWidths, true);

		System.out.println("-".repeat(terminalWidth));

		for (Task task : tasks) {
			String[] row = {
				String.valueOf(task.getIdentifier()),
				task.getTitle(),
				task.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
				task.getContent(),
				String.valueOf(task.getPriority()),
				String.valueOf(task.getEstimatedDuration()),
				task.isCompleted() ? ViewUtils.GREEN + "Si" + ViewUtils.RESET : ViewUtils.RED + "No" + ViewUtils.RESET
			};
			ViewUtils.printWrappedRow(row, columnWidths, maxContentWidth);
		}
		System.out.println();
	}
}