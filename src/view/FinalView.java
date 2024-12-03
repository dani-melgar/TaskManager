package view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import controller.Controller;
import model.Task;


public class FinalView extends BaseView {

	public Controller controller;
	public void setController(Controller controller) {
		this.controller = controller;
	}

	private static final int BASE_TERMINAL_WIDTH = 80;
	private static final int TERMINAL_WIDTH;

	static {
		TERMINAL_WIDTH = getTerminalWidth();
	}

	public static final String RESET = "\033[0m";
	public static final String RED = "\033[31m";
	public static final String GREEN = "\033[32m";
	public static final String YELLOW = "\033[33m";
	public static final String BLUE = "\033[34m";

	/*
	 * METODOS HEREDADOS DE BASEVIEW
	 */
	@Override
	public void init() {
		clearScreen();
		showLoading("Iniciando el programa");
		Scanner refScanner = new Scanner(System.in);
		mainMenu(refScanner);
		refScanner.close();
	}

	@Override
	public void showMessage(String message) {
		System.out.println(GREEN + "Mensaje: " + message + RESET);
	}

	@Override
	public void showErrorMessage(String message) {
		System.err.println(RED + "Error: " + message + RESET);
	}

	@Override
	public void end() {
		clearScreen();
	}

	/*
	 * MENUS
	*/

	public void mainMenu(Scanner refScanner) {
		String title = "Menu Principal";
		String[] options = {
			"Menu CRUD",
			"Importar/Exportar",
			"Salir"
		};
		boolean salir = false;
		do {
			printMenu(title, options);
			int option = validOptionInteger(refScanner, 1, options.length);
			switch (option) {
				case 1:
					menuCrud(refScanner);
					break;
				case 2:
					break;
				case 3:
					if (confirmAction("¿Seguro que desea salir?", refScanner)) {
						refScanner.close();
						salir = true;
					}
					break;
			}
		} while (!salir);
	}

	private void menuCrud(Scanner refScanner) {
		String title = "Menu CRUD";
		String[] options = {
			"Agregar Tarea",
			"Listar Tareas",
			"Volver"
		};
		boolean salir = false;
		do {
			printMenu(title, options);
			int option = validOptionInteger(refScanner, 1, options.length);
			switch (option) {
				case 1:
					addTask(refScanner);
					break;
				case 2:
					listTasksMenu(refScanner);
					break;
				case 3:
					if (confirmAction("¿Seguro que desea salir?", refScanner)) {
						refScanner.close();
						salir = true;
					}
					break;
			}
		} while (!salir);

	}

	// Manejar excepciones en la vista
	private void addTask(Scanner refScanner) {
		printCenteredText("Titulo de la Tarea:");
		String title = refScanner.nextLine();

		printCenteredText("Fecha:");
		Date date = null;

		printCenteredText("Contenido:");
		String content = refScanner.nextLine();

		printCenteredText("Prioridad:");
		int priority = validOptionInteger(refScanner, 1, 5);

		// Cambiar maxValue por el num max de un integer
		printCenteredText("Duracion estimada:");
		int estimatedDuration = validOptionInteger(refScanner, 1, 999999);

		boolean completada = confirmAction("Tarea completada?: ", refScanner);

		Task t = new Task(title, date, content, priority, estimatedDuration, completada);
		controller.addTask(t);
	}

	// Revisar enunciado -> Filtrar Tareas por Completadas / Sin Completar
	private void listTasksMenu(Scanner refScanner) {
		String title = "Listar Tareas";
		String[] options = {
			"Ordenadas por prioridad [Ascendente -> Descendente]",
			"Todas las tareas [Sin Completar]",
			"Volver"
		};
		boolean salir = false;
		do {
			printMenu(title, options);
			int option = validOptionInteger(refScanner, 1, options.length);
			switch (option) {
				case 1:
					// Implementar
					modifyTaskMenu(refScanner, option);
					break;
				case 2:
					modifyTaskMenu(refScanner, option);
					break;
				case 3:
					if (confirmAction("¿Seguro que desea salir?", refScanner)) {
						refScanner.close();
						salir = true;
					}
					break;
				default:
					break;
			}
		} while (!salir);

	}

	private void modifyTaskMenu(Scanner refScanner, int displayOption) {
		List<Task> tasks = new ArrayList<>();
		switch (displayOption) {
			case 1:
				tasks = controller.getAllTasks();
				break;
			case 2:
				tasks = controller.getTasksByPriority();
				break;
		}
		showTasks(tasks);
		// Quizas agregar un pequenio wait o pausa entre tarea y tarea

		String title = "Modificar Detalles de una Tarea";
		String[] options = {
			"Marcar [Completa/Incompleta]",
			"Modificar Informacion Tarea",
			"Eliminar Tarea",
		};
		
		boolean salir = false;
		do {
			printMenu(title, options);
			printCenteredText("Seleccione una opcion: ");
			int option = validOptionInteger(refScanner, 1, options.length);
			switch (option) {
				case 1:
					modifyTaskStatus(tasks, refScanner);
					break;
				case 2:
					modifyTaskInfo(tasks, refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
			
		} while (!salir);
	}

	private void modifyTaskStatus(List<Task> tasks, Scanner refScanner) {
		showTasks(tasks);
		boolean salir = false;
		do {
			try {
				printCenteredText("Seleccione el identificador de una tarea: ");
				int idUserTask = Integer.parseInt(refScanner.nextLine().trim());

				Task selectedTask = null;

				// Buscar la tarea con el ID proporcionado
				for (Task task : tasks) {
					if (task.getIdentifier() == idUserTask) {
						selectedTask = task;
						break;
					}
				}

				if (selectedTask != null) {
					// Cambiar el estado de la tarea
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
					salir = true;
				} else {
					showErrorMessage("No se encontro una tarea con el identificador proporcionado.");
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Por favor, introduzca un número.");
			}
		} while (!salir);
	}

	private void modifyTaskInfo(List<Task> tasks, Scanner refScanner) {
		showTasks(tasks);
		boolean salir = false;
		do {
			try {
				printCenteredText("Seleccione el identificador de una tarea: ");
				int idUserTask = Integer.parseInt(refScanner.nextLine().trim());

				Task selectedTask = null;

				// Buscar la tarea con el ID proporcionado
				for (Task task : tasks) {
					if (task.getIdentifier() == idUserTask) {
						selectedTask = task;
						break;
					}
				}

				if (selectedTask != null) {
					printCenteredText("Introduzca el nuevo titulo: ");
					String title = refScanner.nextLine();
					
					printCenteredText("Introduzca nueva fecha: ");
					Date date = null;

					printCenteredText("Introduzca el nuevo contenido: ");
					String content = refScanner.nextLine();

					printCenteredText("Introduzca la prioridad de la tarea: ");
					int priority = validOptionInteger( refScanner, 1, 5);

					printCenteredText("Duracion estimada de la nueva tarea: ");
					int estimatedDuration = validOptionInteger(refScanner, 0, 1000000);

					boolean completada = confirmAction("Tarea completada?: ", refScanner);

					Task t = new Task(title, date, content, priority, estimatedDuration, completada);
					controller.editTask(t);

					showMessage("Tarea modificada");
					salir = true;
				} else {
					showErrorMessage("No se encontro una tarea con el identificador proporcionado.");
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Por favor, introduzca un numero.");
			}
		} while (!salir);
	}



	@SuppressWarnings("unused")
	private void listTasksShortedByPriority(Scanner refScanner) {
		showTasks(controller.getTasksByPriority());
	}

	@SuppressWarnings("unused")
	private void listAllTasks(Scanner refScanner) {
		showTasks(controller.getAllTasks());

	}

	private void showTasks(List<Task> tasks) {
		if (tasks != null) {
			for (Task task : tasks) {
				if (task != null) {
					printCenteredText(task.toString());
				}	
			}
		}
	}

	private int validOptionInteger(Scanner refScanner, int minValue, int maxValue) {
		while (true) {
			try {
				int option = Integer.parseInt(refScanner.nextLine().trim());
				if (option >= minValue && option <= maxValue) {
					return option;
				}
				showErrorMessage(RED + "Seleccione una opcion valida" + RESET);
			} catch (NumberFormatException e) {
				showErrorMessage(RED + "La entrada debe de ser numerica" + RESET);
			}
		}
	}


	private boolean confirmAction(String message, Scanner refScanner) {
		printCenteredText(message + " (s/n:)");
		while (true) {
			String input = refScanner.nextLine().trim().toLowerCase();
			if (input.equals("s")) {
				return true;
			}
			if (input.equals("n")) {
				return false;
			}
			String errorString = "Introduzca " + GREEN + "s" + RESET + " para " + GREEN + "si" + RESET + " o " + RED + "n" + RESET + "para " + RED + "no." + RESET;
			printCenteredText(errorString);
		}
	}

	/**
	 * Imprime un menu centrado en terminal.
	 *
	 * @param text Menu y opciones que se desea centrar.
	 */
	private void printMenu(String title, String[] options) {
		printCenteredText(title);
		System.out.println("-".repeat(TERMINAL_WIDTH));
		printCenteredText(options);
		System.out.println("-".repeat(TERMINAL_WIDTH));
	}

	/**
	 * Imprime un array de texto centrado en terminal.
	 *
	 * @param text Array que se desea centrar.
	 */	
	private void printCenteredText(String[] textArray) {
		for (String line : textArray) {
			printCenteredText(line);
		}
	}
	
	/**
	 * Centra un texto en la terminal y lo imprime directamente.
	 *
	 * @param text El texto que se desea centrar.
	 */
	private void printCenteredText(String text) {
		if (text.length() >= TERMINAL_WIDTH) {
			System.out.println(text);
			return;
		}
		int padding = (TERMINAL_WIDTH - text.length()) / 2;
		System.out.println(" ".repeat(padding) + text);
	}

	/**
	 * Obtains the current width of the terminal.
	 * If the width cannot be determined, it defaults to a predefined constant width.
	 *
	 * @return the width of the terminal as an integer.
	 */
	private static int getTerminalWidth() {
		try {
			ProcessBuilder pb = new ProcessBuilder("tput", "cols");
			Process process = pb.start();
			try (Scanner scanner = new Scanner(process.getInputStream())) {
				return scanner.hasNextInt() ? scanner.nextInt() : BASE_TERMINAL_WIDTH;
			}
		} catch (Exception e) {
			return BASE_TERMINAL_WIDTH;
		}
	}

	/**
	 * Barra de carga
	 * 
	 * <p>
	 * Este metodo intenta mpiar la pantalla de la terminal usando comandos 
	 * específicos del sistema operativo. En Windows, utiliza "cls" mediante "ProcessBuilder". 
	 * En sistemas Unix/Linux/Mac, no hay que complicarse tanto, con imprimir secuencias de escape 
	 * sirve para limpiarla. Si ambos fallan (por ejemplo, en un entorno 
	 * no soportado o en la lavadora de tu casa), se imprimen saltos de linea.
	 * </p>
	 * 
	 * @throws RuntimeException si ocurre un error al ejecutarlo
	 */
	private void showLoading(String message) {
		System.out.print(message);
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(500);
				System.out.print(".");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				showErrorMessage("Error en la animacion de carga.");
			return;
			}
		}
		System.out.println();
	}

	/**
	 * Limpia la terminal
	 * 
	 * <p>Este metodo intenta limpiar la pantalla de la terminal usando comandos 
	 * específicos del sistema operativo. En Windows, utiliza "cls" mediante "ProcessBuilder". 
	 * En sistemas Unix/Linux/Mac, no hay que complicarse tanto, con imprimir secuencias de escape 
	 * sirve para limpiarla. Si ambos fallan (por ejemplo, en un entorno 
	 * no soportado o en la lavadora de tu casa), se imprimen saltos de linea.</p>
	 * 
	 * @throws RuntimeException si ocurre un error al ejecutarlo
	 */
	private void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (Exception e) {
			// Si alguien esta ejecutando esto en una nevera con pantalla o dios sabe que
			for (int i = 0; i < 100; i++) {
				System.out.println();
			}
		}

	}
}