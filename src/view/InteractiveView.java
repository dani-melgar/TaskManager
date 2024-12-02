package view;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import model.Task;

public class InteractiveView extends BaseView {

	private final int TERMINAL_WIDTH = 80;

	public static final String RESET = "\033[0m";
	public static final String RED = "\033[31m";
	public static final String GREEN = "\033[32m";
	public static final String YELLOW = "\033[33m";
	public static final String BLUE = "\033[34m";


	@Override
	public void init() {
		Scanner refScanner = new Scanner(System.in);
		clearScreen();
		mainMenu(refScanner);
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
	}

	private void mainMenu(Scanner refScanner) {
		String[] options = { "Menu CRUD", "Importar/Exportar", "Salir" };
		boolean salir = false;
		while (!salir) {
			int opcion = showMenuAndGetOption("Menu Principal", options, refScanner);
			switch (opcion) {
				case 1:
					menuCRUD(refScanner);
					break;
				case 2:
					importExportMenu(refScanner);
					break;
				case 3:
					confirmAction("¿Seguro que desea salir?", refScanner);
				 	refScanner.close();
					salir = true;
					break;
				default:
					showErrorMessage("Opcion no valida.");
					break;
			}
		}
		// Finalizar de manera ordenada
		end();
	}

	private void menuCRUD(Scanner refScanner) {
		String[] options = { "Agregar nueva tarea", "Listar tareas", "Volver" };
		boolean salir = false;
		while (!salir) {
			int opcion = showMenuAndGetOption("Menu CRUD", options, refScanner);
			switch (opcion) {
				case 1:
					addTask(refScanner);
					break;
				case 2:
					listTasksMenu(refScanner);
					break;
				case 3:
				 	// Seguramente confirmAction aqui estorbaria
					salir = true;
					break;
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
		}
	}

	private void addTask(Scanner refScanner) {
		String title = refScanner.nextLine();
		Date date = null;
		String content = refScanner.nextLine();
		int priority = askOption("Prioridad de la tarea", refScanner, 1, 5);
		int estimatedDuration = askOption("Duracion estimada: ", refScanner, 0, 1000000);
		boolean completada = confirmAction("Tarea completada?: ", refScanner);

		Task t = new Task(title, date, content, priority, estimatedDuration, completada);
		controller.addTask(t);
	}

	private void listTasksMenu(Scanner refScanner) {
		String title = "Listar Tareas";
		String[] options = {"Listar por prioridad", "Mostrar Todas", "Volver"};
		boolean salir = false;

		do {
			int opcion = showMenuAndGetOption(title, options, refScanner);
			switch (opcion) {
				case 1:
					showTasksPriority(refScanner);
					break;
				case 2:
					showTasks(refScanner);
					break;
				case 3:
					salir = true;
					break;
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
		} while (!salir);
	}

	private void showTasksPriority(Scanner refScanner) {
		List<Task> tasks = controller.getTasksByPriority();
		for (Task task : tasks) {
			System.out.println(task);
		}
		if (confirmAction("¿Desea modificar alguna tarea?", refScanner)) {
			int idTaskToEdit = Integer.parseInt(refScanner.nextLine());
			editTaskMenu(idTaskToEdit, refScanner);
		}
	}

	private void showTasks(Scanner rScanner) {
		List<Task> tasks = controller.getAllTasks();
		for (Task task : tasks) {
			System.out.println(task);
		}
	}

	private void editTaskMenu(int idTaskToEdit, Scanner refScanner) {
		String title = "Modificar Tarea";
		String[] options = {"Modificar Atributos", "Eliminar Tarea", "Volver"};
		boolean salir = false;

		do {
			int opcion = showMenuAndGetOption(title, options, refScanner);
			switch (opcion) {
				case 1:
				 	editTask(idTaskToEdit, refScanner);
					break;
				case 2:
				 	deleteTask(idTaskToEdit);
					break;
				case 3:
					salir = true;
					break;
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
		} while (!salir);
	}

	private void editTask(int idTaskToEdit, Scanner refScanner) {
		String title = refScanner.nextLine();
		Date date = null;
		String content = refScanner.nextLine();
		int priority = askOption("Prioridad de la tarea", refScanner, 1, 5);
		int estimatedDuration = askOption("Duracion estimada: ", refScanner, 0, 1000000);
		boolean completada = confirmAction("Tarea completada?: ", refScanner);

		Task t = new Task(idTaskToEdit, title, date, content, priority, estimatedDuration, completada);
		controller.editTask(t);
	}

	private void deleteTask(int idTaskToDelete) {
		controller.deleteTask(new Task(idTaskToDelete));
	}

	private void importExportMenu(Scanner refScanner) {
		String title = "Menu Importar/Exportar";
		String[] options = {"Importar tareas", "Exportar tareas", "Volver"};
		boolean salir = false;

		do {
			int opcion = showMenuAndGetOption(title, options, refScanner);
			switch (opcion) {
			case 1:
				importFormat(refScanner);
				break;
			case 2:
				exportFormat(refScanner);
				break;
			case 3:
				salir = true;
				break;
			}
		} while (!salir);
	}

	private void importFormat(Scanner refScanner) {
		String title = "Formato de Importacion";
		String[] options = {"Importar en CSV", "Importar en JSON", "Volver"};
		boolean salir = false;
			do {
				int opcion = showMenuAndGetOption(title, options, refScanner);
				switch (opcion) {
				case 1:
					handleImport("CSV", refScanner);
					break;
				case 2:
					handleImport("JSON", refScanner);
					break;
				case 3:
					salir = true;
					break;
				}
		} while (!salir);
	}

	private void exportFormat(Scanner refScanner) {
		String title = "Formato de Exportacion";
		String[] options = {"Exportar en CSV", "Exportar en JSON",  "Volver"};

		boolean salir = false;
		do {
			int opcion = showMenuAndGetOption(title, options, refScanner);
			switch (opcion) {
				case 1:
					handleExport("CSV", refScanner);
					break;
				case 2:
					handleExport("JSON", refScanner);
					break;
				case 3:
					salir = true;
					break;
			}
		} while (!salir);
	}

	private void handleExport(String format, Scanner refScanner) {
		showLoading("Exportando desde " + format + " ...");
		// Si sucede algun error, aqui que pasaria?
		controller.exportFormat(format);
		showMessage("Exportacion desde " + format + " completada.");
	}

	private void handleImport(String format, Scanner refScanner) {
		showLoading("Importando desde " + format + " ...");
		// Si sucede algun error, aqui que pasaria?
		controller.exportFormat(format);
		showMessage("Importacion desde " + format + " completada.");
		if (confirmAction("¿Desea ver las tareas antes de agregarlas a las actuales?", refScanner)) {
			// Obtener las tareas como String, por comodidad usaremos tasks
			List<Task> importedTasks = controller.getImportedTasks();
			for (Task task : importedTasks) {
				// No se si el metodo va
				System.out.println(task.toString());
			}
			if (confirmAction("¿Importarlas?", refScanner)) {
				controller.importTasks(importedTasks);
			}
		} else {
			controller.importTasks();
		}
	}

	private int showMenuAndGetOption(String title, String[] options, Scanner refScanner) {
		clearScreen();
		System.out.println(title);
		for (int i = 0; i < options.length; i++) {
			System.out.println((i + 1) + ". " + options[i]);
		}
		System.out.printf("Seleccione una opcion: ");
		return getValidOption(refScanner, "Seleccione una opcion valida.", 1, options.length);
	}

	// Pa que se calle esta public
	public int askOption(String prompt, Scanner refScanner ,int min, int max) {
		System.out.println(prompt);
		return getValidOption(refScanner, "Introduzca un numero entre " + min + " y " + max + ".", min, max);
	}

	private int getValidOption(Scanner refScanner, String errorMessage, int min, int max) {
		while (true) {
			try {
				int option = Integer.parseInt(refScanner.nextLine().trim());
				if (option >= min && option <= max) {
					return option;
				}
				System.out.println(RED + errorMessage + RESET);
			} catch (NumberFormatException e) {
				System.out.println(RED + "Entrada erronea. Introduzca un numero." + RESET);
			}
		}
	}

	public void printMenu(String title, String[] options) {
		int terminalWidth = getTerminalWidth();
		System.out.println(centerText(title, terminalWidth));
		System.out.println("-".repeat(terminalWidth));
		for (String option : options) {
			printJustifiedText(option, terminalWidth);
		}
		System.out.println("-".repeat(terminalWidth));
	}


	private boolean confirmAction(String message, Scanner refScanner) {
		System.out.println(message + " (s/n):");
		while (true) {
			String input = refScanner.nextLine().trim().toLowerCase();
			if (input.equals("s")) {
				return true;
			}
			if (input.equals("n")) {
				return false;
			}
			System.out.println("Introduzca " + GREEN + "s" + RESET + " para " + GREEN + "si" + RESET + " o " + RED + "n" + RESET + "para " + RED + "no." + RESET);
		}
	}


	private int getTerminalWidth() {
		try {
			ProcessBuilder pb = new ProcessBuilder("tput", "cols");
			Process process = pb.start();
			try (Scanner scanner = new Scanner(process.getInputStream())) {
				return scanner.hasNextInt() ? scanner.nextInt() : TERMINAL_WIDTH;
			}
		} catch (Exception e) {
			return TERMINAL_WIDTH;
		}
	}

	private String centerText(String text, int width) {
		if (text.length() >= width) {
			return text;
		}
		int padding = (width - text.length()) / 2;
		return " ".repeat(padding) + text;
	}

	private void printJustifiedText(String text, int width) {
		String[] words = text.split("\\s+");
		StringBuilder line = new StringBuilder();
		for (String word : words) {
			if (line.length() + word.length() + 1 > width) {
				System.out.println(line);
				line = new StringBuilder(word);
			} else {
				if (line.length() > 0) line.append(" ");
				line.append(word);
			}
		}
		if (line.length() > 0) System.out.println(line);
	}

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
	 * Limpia la pantalla de la terminal independientemente del sistema operativo.
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