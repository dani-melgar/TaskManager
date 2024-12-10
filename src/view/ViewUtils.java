package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase de utilidades para manejo y formateo de vistas en terminal.
 */
public class ViewUtils {
	public static final int BASE_TERMINAL_WIDTH = 80;

	public static final String RESET = "\033[0m";
	public static final String RED = "\033[31m";
	public static final String GREEN = "\033[32m";
	public static final String YELLOW = "\033[33m";
	public static final String BLUE = "\033[34m";

	/**
	 * Solicita al usuario que ingrese una fecha en formato "YYYY-MM-DD".
	 *
	 * @param scanner El objeto Scanner para leer la entrada del usuario.
	 * @return La fecha ingresada como un objeto {@link LocalDate}.
	 */
	public static LocalDate readDate(Scanner scanner) {
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

	/**
	 * Solicita una entrada del usuario, permitiendo cancelar escribiendo una palabra clave.
	 * 
	 * @param prompt        El mensaje que se muestra al usuario.
	 * @param cancelKeyword La palabra clave para cancelar (normalmente, "CANCELAR").
	 * @param scanner       El objeto Scanner para leer la entrada.
	 * @return La entrada del usuario o null si se cancelo.
	 */
	public static String requestInputWithCancel(String prompt, String cancelKeyword, Scanner scanner) {
		System.out.printf("%s (o escriba '%s' para cancelar): ", prompt, cancelKeyword);
		String input = scanner.nextLine().trim();

		if (input.equalsIgnoreCase(cancelKeyword)) {
			return null;
		}
		return input;
	}

	/**
	 * Solicita al usuario que seleccione una opción valida, centrando la pregunta en la terminal.
	 *
	 * @param question La pregunta que se mostrara.
	 * @param refScanner El objeto Scanner para leer la entrada del usuario.
	 * @param minValue El valor minimo aceptable.
	 * @param maxValue El valor maximo aceptable.
	 * @return La opcion seleccionada por el usuario dentro del rango especificado.
	 */
	public static int askValidatedCenteredText(String question, Scanner refScanner, int minValue, int maxValue) {
		int width = getTerminalWidth();
		while (true) {
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
				printCenteredText(RED + "Seleccione una opcion valida (" + minValue + " - " + maxValue + ")." + RESET);
			} catch (NumberFormatException e) {
				printCenteredText(RED + "La entrada debe ser un numero." + RESET);
			}
		}
	}

	/**
	 * Solicita al usuario un numero entero dentro de un rango.
	 *
	 * @param refScanner El objeto Scanner para leer la entrada del usuario.
	 * @param minValue El valor minimo aceptable.
	 * @param maxValue El valor maximo aceptable.
	 * @return El numero entero seleccionado por el usuario dentro del rango.
	 */
	public static int validOptionInteger(Scanner refScanner, int minValue, int maxValue) {
		while (true) {
			try {
				int option = Integer.parseInt(refScanner.nextLine().trim());
				if (option >= minValue && option <= maxValue) {
					return option;
				}
				printCenteredText(RED + "Seleccione una opcion valida" + RESET);
			} catch (NumberFormatException e) {
				printCenteredText(RED + "La entrada debe de ser numerica" + RESET);
			}
		}
	}

	/**
	 * Solicita confirmacion con un mensaje.
	 *
	 * @param message El mensaje a mostrar.
	 * @param refScanner El objeto Scanner para leer la entrada del usuario.
	 * @return {@code true} si el usuario confirma, {@code false} en caso contrario.
	 */
	public static boolean confirmAction(String message, Scanner refScanner) {
		while (true) {
			System.out.print(message + " ");
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
	 * Solicita confirmacion con un mensaje centrado en pantalla.
	 *
	 * @param message El mensaje centrado a mostrar.
	 * @param refScanner El objeto Scanner para leer la entrada del usuario.
	 * @return {@code true} si el usuario confirma, {@code false} en caso contrario.
	 */
	public static boolean confirmActionCentered(String message, Scanner refScanner) {
		int width = getTerminalWidth();
		while (true) {
			// Centrar el mensaje
			String fullMessage = message + " (s/n): ";
			if (fullMessage.length() >= width) {
				System.out.print(fullMessage);
			} else {
				int padding = (width - fullMessage.length()) / 2;
				System.out.print(" ".repeat(padding) + fullMessage);
			}

			// Leer la entrada del usuario
			String input = refScanner.nextLine().trim().toLowerCase();

			if (input.equals("s")) {
				return true;
			}

			if (input.equals("n")) {
				return false;
			}

			// Mostrar mensaje de error centrado
			String errorString = "Introduzca " + GREEN + "s" + RESET + " para " + GREEN + "si" + RESET + " o " + RED + "n" + RESET + " para " + RED + "no." + RESET;
			printCenteredText(errorString);
		}
	}


	/**
	 * Calcula los anchos de columna ajustados para el contenido en una tabla.
	 *
	 * @param terminalWidth El ancho de la terminal.
	 * @param maxContentWidth El ancho maximo permitido para el contenido de la columna.
	 * @return Un arreglo de enteros que representa los anchos de cada columna.
	 */
	public static int[] calculateColumnWidths(int terminalWidth, int maxContentWidth) {
		int[] columnWidths = {
			12, // ID
			20, // Titulo
			12, // Fecha
			maxContentWidth, // Contenido
			10, // Prioridad
			20, // Duracion Estimada
			12  // Completado
		};

		int totalWidth = 0;
		for (int width : columnWidths) {
			totalWidth += width;
		}

		if (totalWidth > terminalWidth) {
			columnWidths[3] = Math.max(maxContentWidth - (totalWidth - terminalWidth), 10);
		}

		return columnWidths;
	}

	/**
	 * Calcula los anchos de columna ajustados para el contenido en una tabla.
	 *
	 * @param terminalWidth El ancho de la terminal.
	 * @param maxContentWidth El ancho maximo permitido para el contenido de la columna.
	 * @return Un arreglo de enteros que representa los anchos de cada columna.
	 */
	public static void printMenu(String title, String[] options) {
		int terminalWidth = getTerminalWidth();
		printCenteredText(title);
		System.out.println("-".repeat(terminalWidth));
		printCenteredAligned(options);
		System.out.println("-".repeat(terminalWidth));
	}

	/**
	 * Envuelve un texto en líneas de tamaño fijo.
	 *
	 * @param text El texto que se desea envolver.
	 * @param maxWidth El ancho maximo permitido por línea.
	 * @return Un arreglo de lineas envueltas.
	 */	
	public static void printCenteredAligned(String[] lines) {
		int width = getTerminalWidth();

		// Encontrar la longitud maxima de las cadenas
		int maxLineLength = 0;
		for (String line : lines) {
			if (line.length() > maxLineLength) {
			maxLineLength = line.length();
			}
		}

		if (maxLineLength > width) {
			maxLineLength = width;
		}

		int padding = (width - maxLineLength) / 2;

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
	public static void printCenteredText(String text) {
		int width = getTerminalWidth();
		if (text.length() >= width) {
			System.out.println(text);
			return;
		}
		int padding = (width - text.length()) / 2;
		System.out.println(" ".repeat(padding) + text);
	}

	public static void printRow(String[] row, int[] columnWidths, boolean isHeader) {
		for (int i = 0; i < row.length; i++) {
			String content = row[i].length() > columnWidths[i] ? row[i].substring(0, columnWidths[i] - 3) + "..." : row[i];
			String format = isHeader ? YELLOW + "%-" + columnWidths[i] + "s" + RESET : "%-" + columnWidths[i] + "s";
			System.out.printf(format, content);
		}
		System.out.println();
	}

	public static void printWrappedRow(String[] row, int[] columnWidths, int maxContentWidth) {
		String[] contentLines = wrapText(row[3], maxContentWidth);
		int maxLines = contentLines.length;

		for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cellContent;

				if (colIndex == 3) {
					cellContent = lineIndex < contentLines.length ? contentLines[lineIndex] : ""; // Contenido dividido
				} else if (lineIndex == 0) {
					cellContent = row[colIndex];
				} else {
					cellContent = "";
				}

				if (cellContent.length() > columnWidths[colIndex]) {
					cellContent = cellContent.substring(0, columnWidths[colIndex] - 3) + "...";
				}

					System.out.printf("%-" + columnWidths[colIndex] + "s", cellContent);
				}
			System.out.println();
		}
	}

	public static String[] wrapText(String text, int maxWidth) {
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

	/**
	 * Calculo de la anchura de la terminal.
	 * 
	 * Obtiene la anchura de la terminal, y en caso de que no sea posible
	 * usa un valor predefinido como constante
	 * <p>
	 * Agradecimientos a ChatGPT y
 	 * <a href="https://stackoverflow.com/">Stack Overflow</a> a calmar mi toc
	 * <p>
	 * @return Un entero con la anchura de la terminal.
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
	 * Este metodo simula una barra de carga mostrando el mensaje recibido
	 * por parametro
	 * </p>
	 * @param message El mensaje a mostrar
	 * 
	 * @throws InterruptedException si ocurre un error al ejecutarlo
	 */
	public static void showLoading(String message) {
		System.out.print(message);
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(500);
				System.out.print(".");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		System.out.println();
	}

	/**
	 * Limpia la terminal
	 * 
	 * <p>Este metodo intenta limpiar la pantalla de la terminal usando comandos 
	 * especificos del sistema operativo. En Windows, utiliza "cls" mediante "ProcessBuilder". 
	 * En sistemas Unix/Linux/Mac, no hay que complicarse tanto, con imprimir secuencias de escape 
	 * sirve para limpiarla. Si ambos fallan (por ejemplo, en un entorno 
	 * no soportado o en la lavadora de tu casa), se imprimen saltos de linea.</p>
	 * 
	 * @throws RuntimeException si ocurre un error al ejecutarlo
	 */
	public static void clearScreen() {
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