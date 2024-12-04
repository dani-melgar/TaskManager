package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import controller.Controller;
import model.Task;


public class InteractiveView extends BaseView {

	public Controller controller;
	public void setController(Controller controller) {
		this.controller = controller;
	}

	private static final int BASE_TERMINAL_WIDTH = 80;

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
		controller.end();
		clearScreen();
	}

	/*
	 * MENUS
	*/

	public void mainMenu(Scanner refScanner) {
		String title = "Menu Principal";
		String[] options = {
			"1. Menu CRUD",
			"2. Importar/Exportar",
			"3. Salir"
		};
		boolean salir = false;
		do {
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					menuCrud(refScanner);
					break;
				case 2:
					menuImportExport(refScanner);
					break;
				case 3:
					if (confirmAction("¿Seguro que desea salir?", refScanner)) {
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
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
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

	// Manejar excepciones en la vista
	private void addTask(Scanner refScanner) {
		clearScreen();
		
		System.out.printf("Titulo de la Tarea: ");
		String title = refScanner.nextLine();

		System.out.printf("Fecha: ");
		LocalDate date = readDate(refScanner);

		System.out.printf("Contenido: ");
		String content = refScanner.nextLine();

		System.out.printf("Prioridad: ");
		int priority = validOptionInteger(refScanner, 1, 5);

		// Cambiar maxValue por el num max de un integer
		System.out.printf("Duracion estimada: ");
		int estimatedDuration = validOptionInteger(refScanner, 1, 9999999);

		boolean completada = confirmAction("Tarea completada?: ", refScanner);

		Task t = new Task(title, date, content, priority, estimatedDuration, completada);
		controller.addTask(t);
	}
	

	public LocalDate readDate(Scanner scanner) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		while (true) {
			System.out.printf("Ingrese una fecha (YYYY-MM-DD): ");
			String input = scanner.nextLine().trim();
			try {
				return LocalDate.parse(input, dateFormatter);
			} catch (DateTimeParseException e) {
				System.out.println("Formato de fecha inválido. Intente nuevamente.");
			}
		}
	}



	// Revisar enunciado -> Filtrar Tareas por Completadas / Sin Completar
	private void listTasksMenu(Scanner refScanner) {
		String title = "Listar Tareas";
		String[] options = {
			"1. Ordenadas por prioridad [Ascendente -> Descendente]",
			"2. Todas las tareas [Sin Completar]",
			"3. Volver"
		};
		boolean salir = false;
		do {
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
			switch (option) {
				case 1:
					// Implementar
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
			clearScreen();
			switch (displayOption) {
			case 1:
				tasks = controller.getAllTasks();
				break;
			case 2:
				tasks = controller.getTasksShortedByPriority();
				break;
			}

			displayTasks(tasks);
			System.out.println("-".repeat(getTerminalWidth()));
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion:", refScanner, 1, options.length);
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
		displayTasks(tasks);
		boolean salir = false;
		do {
			try {
				System.out.printf("Seleccione el identificador de una tarea: ");
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
		displayTasks(tasks);
		boolean salir = false;
		do {
			try {
				System.out.printf("Seleccione el identificador de una tarea: ");
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
					System.out.printf("Introduzca el nuevo titulo: ");
					String title = refScanner.nextLine();
					
					System.out.printf("Fecha: ");
					LocalDate date = readDate(refScanner);

					System.out.printf("Introduzca el nuevo contenido: ");
					String content = refScanner.nextLine();

					System.out.printf("Introduzca la prioridad de la tarea: ");
					int priority = validOptionInteger( refScanner, 1, 5);

					System.out.printf("Duracion estimada de la nueva tarea: ");
					int estimatedDuration = validOptionInteger(refScanner, 0, 1000000);

					boolean completada = confirmAction("Tarea completada?: ", refScanner);

					Task t = new Task(selectedTask.getIdentifier(),title, date, content, priority, estimatedDuration, completada);
					controller.editTask(t);

					showMessage("Tarea modificada");
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

	private void modifyTaskExistance(List<Task> tasks, Scanner refScanner) {
		displayTasks(tasks);
		boolean salir = false;
		do {
			try {
				System.out.printf("Seleccione el identificador de una tarea: ");
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


	@SuppressWarnings("unused")
	private void listTasksShortedByPriority(Scanner refScanner) {
		displayTasks(controller.getTasksShortedByPriority());
	}

	@SuppressWarnings("unused")
	private void listAllTasks(Scanner refScanner) {
		displayTasks(controller.getAllTasks());

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
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
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
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
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
		printCenteredText("TAREAS IMPORTADAS");
		displayTasks(importedTasks);
		showMessage("Tarea modificada");
		System.out.printf("Presione una tecla para continuar...");
		controller.mergeImportedTasks(importedTasks, true);
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
			clearScreen();
			printMenu(title, options);
			int option = askValidatedCenteredText("Introduzca una opcion", refScanner, 1, options.length);
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
		controller.exportTasks(format);
	}

	private void displayTasks(List<Task> tasks) {
		int terminalWidth = getTerminalWidth(); // Obtener el ancho de la terminal
		int maxContentWidth = 40; // Ancho máximo para el campo `content`

		// Encabezados de la tabla
		String[] headers = {
			"ID", "Título", "Fecha", "Contenido", "Prioridad", "Duracion Estimada", "Completado"
		};

		// Anchos de las columnas (ajustables según el ancho de la terminal)
		int[] columnWidths = calculateColumnWidths(terminalWidth, maxContentWidth);

		// Imprimir encabezados
		printRow(headers, columnWidths, true);

		// Imprimir separador
		System.out.println("-".repeat(terminalWidth));

		// Imprimir cada tarea
		for (Task task : tasks) {
			String[] row = {
				String.valueOf(task.getIdentifier()),
				task.getTitle(),
				task.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
				task.getContent(),
				String.valueOf(task.getPriority()),
				String.valueOf(task.getEstimatedDuration()),
				task.isCompleted() ? GREEN + "Si" + RESET : RED + "No" + RESET
			};
			printWrappedRow(row, columnWidths, maxContentWidth);
		}
	}

	private int[] calculateColumnWidths(int terminalWidth, int maxContentWidth) {
		int[] columnWidths = {
			12, // ID
			20, // Título
			12, // Fecha
			maxContentWidth, // Contenido
			10, // Prioridad
			20, // Duración Estimada
			12  // Completado
		};

		int totalWidth = 0;
		for (int width : columnWidths) {
			totalWidth += width;
		}

		// Ajustar el ancho si excede el de la terminal
		if (totalWidth > terminalWidth) {
			columnWidths[3] = Math.max(maxContentWidth - (totalWidth - terminalWidth), 10); // Reducir ancho de contenido
		}

		return columnWidths;
	}

	private void printRow(String[] row, int[] columnWidths, boolean isHeader) {
		for (int i = 0; i < row.length; i++) {
			String content = row[i].length() > columnWidths[i] ? row[i].substring(0, columnWidths[i] - 3) + "..." : row[i];
			String format = isHeader ? YELLOW + "%-" + columnWidths[i] + "s" + RESET : "%-" + columnWidths[i] + "s";
			System.out.printf(format, content);
		}
		System.out.println();
	}

	private void printWrappedRow(String[] row, int[] columnWidths, int maxContentWidth) {
		// Dividir el contenido en lineas segun el ancho maximo
		String[] contentLines = wrapText(row[3], maxContentWidth);
		int maxLines = contentLines.length; // Numero maximo de lineas a imprimir

		for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cellContent;

				// Imprimir contenido adicional solo en su columna
				if (colIndex == 3) {
					cellContent = lineIndex < contentLines.length ? contentLines[lineIndex] : ""; // Contenido dividido
				} else if (lineIndex == 0) {
					cellContent = row[colIndex]; // Solo la primera línea muestra otros campos
				} else {
					cellContent = ""; // Columnas vacías para líneas adicionales
				}

				// Ajustar contenido si excede el ancho de la columna
				if (cellContent.length() > columnWidths[colIndex]) {
					cellContent = cellContent.substring(0, columnWidths[colIndex] - 3) + "...";
				}

				// Imprimir columna formateada
					System.out.printf("%-" + columnWidths[colIndex] + "s", cellContent);
				}
			System.out.println(); // Fin de la linea
		}
	}


	private String[] wrapText(String text, int maxWidth) {
		// Dividir texto en líneas según el ancho máximo
		if (text.length() <= maxWidth) {
			return new String[]{ text };
		}

		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();
		List<String> lines = new ArrayList<>();

		for (String word : words) {
			if (line.length() + word.length() + 1 > maxWidth) {
				lines.add(line.toString());
				line = new StringBuilder();
			}
			line.append((line.length() == 0 ? "" : " ") + word);
		}
		lines.add(line.toString());

		return lines.toArray(new String[0]);
	}

	private int askValidatedCenteredText(String question, Scanner refScanner, int minValue, int maxValue) {
		int width = getTerminalWidth();
		while (true) {
			// Imprimir la pregunta centrada
			if (question.length() >= width) {
				System.out.print(question + " ");
			} else {
				int padding = (width - question.length()) / 2;
				System.out.print(" ".repeat(padding) + question + " ");
			}

			try {
				int option = Integer.parseInt(refScanner.nextLine().trim());
				if (option >= minValue && option <= maxValue) {
					return option;
				}
				showErrorMessage(RED + "Seleccione una opcion valida (" + minValue + " - " + maxValue + ")." + RESET);
			} catch (NumberFormatException e) {
				showErrorMessage(RED + "La entrada debe ser un numero." + RESET);
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
			String errorString = "Introduzca " + GREEN + "s" + RESET + " para " + GREEN + "si" + RESET + " o " + RED + "n" + RESET + " para " + RED + "no." + RESET;
			printCenteredText(errorString);
		}
	}

	/**
	 * Imprime un menu centrado en terminal.
	 *
	 * @param text Menu y opciones que se desea centrar.
	 */
	private void printMenu(String title, String[] options) {
		int terminalWidth = getTerminalWidth();
		printCenteredText(title);
		System.out.println("-".repeat(terminalWidth));
		printCenteredAligned(options);
		System.out.println("-".repeat(terminalWidth));
	}

	/**
	 * Imprime un array de texto centrado en terminal.
	 *
	 * @param text Array que se desea centrar.
	 */	
	private void printCenteredAligned(String[] lines) {
		int width = getTerminalWidth();

		// Encontrar la longitud máxima de las cadenas
		int maxLineLength = 0;
		for (String line : lines) {
			if (line.length() > maxLineLength) {
			maxLineLength = line.length();
			}
		}

		// Asegurarse de no exceder el ancho de la terminal
		if (maxLineLength > width) {
			maxLineLength = width;
		}

		// Calcular el padding para centrar el bloque de texto
		int padding = (width - maxLineLength) / 2;

		// Imprimir cada línea con el mismo padding a la izquierda
		for (String line : lines) {
			String trimmedLine = line.length() > maxLineLength ? line.substring(0, maxLineLength) : line;
			System.out.println(" ".repeat(padding) + trimmedLine);
		}
	}

	/**
	 * Centra un texto en la terminal y lo imprime directamente.
	 *
	 * @param text El texto que se desea centrar.
	 */
	private void printCenteredText(String text) {
		int width = getTerminalWidth();
		if (text.length() >= width) {
			System.out.println(text);
			return;
		}
		int padding = (width - text.length()) / 2;
		System.out.println(" ".repeat(padding) + text);
	}


	/**
	 * ChatGPT hizo esta monstruisdad tan bella con el unico proposito de calmar mi toc.
	 * 
	 * Obtains the current width of the terminal.
	 * If the width cannot be determined, it defaults to a predefined constant width.
	 *
	 * @return the width of the terminal as an integer.
	 */
	public static int getTerminalWidth() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			Process process;
			if (os.contains("win")) {
				// Comando para Windows (PowerShell)
				process = new ProcessBuilder("powershell", "-command", "($Host.UI.RawUI.WindowSize.Width)").start();
			} else {
				// Comando para sistemas Unix-like (Linux, macOS)
				process = new ProcessBuilder("sh", "-c", "stty size < /dev/tty").start();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = reader.readLine();

			if (output != null && !output.isEmpty()) {
				if (os.contains("win")) {
					return Integer.parseInt(output.trim());
				} else {
					return Integer.parseInt(output.split(" ")[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BASE_TERMINAL_WIDTH;
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