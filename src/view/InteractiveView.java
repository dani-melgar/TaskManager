package view;

import java.util.Scanner;

public class InteractiveView extends BaseView {

	@Override
	public void init() {
		// Scanner
		Scanner refScanner = new Scanner(System.in);
		clearScreen();
		initialMessage();
		runView(refScanner);
	}

	@Override
	public void showMessage(String message) {
		System.out.println("Mensaje: " + message);
	}

	@Override
	public void showErrorMessage(String message) {
		System.err.println("Error: " + message);
	}

	@Override
	public void end() {
	}

	private void initialMessage() {
		System.out.println("        BASE VIEW        ");
	}

	/*
	 * Vista principal del programa una vez iniciado
	*/
	private void runView(Scanner refScanner) {
			boolean salir = false;
			do {
				mainMenu();
				int opcion = Integer.parseInt(refScanner.nextLine());
				switch (opcion) {
					case 1:
						menuCRUD(refScanner);
						break;
					case 2:
						importExportMenu(refScanner);
						break;
					case 3:
						refScanner.close();
						salir = true;
						break;
					default:
						showErrorMessage("Opcion no valida");
						break;
				}
			
		} while (!salir);

	}

	/*
	 * Menu principal
	*/
	private void mainMenu() {
		System.out.println("1. Menu CRUD");
		System.out.println("2. Importar/Exportar");
		System.out.println("3. Salir");
	}

	/* 
	 * Menu CRUD?
	 */
	private void menuCRUD(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("1. Agregar una nueva [tarea]");
			System.out.println("2. Listar tareas");
			System.out.println("3. Menu principal");
			int opcion = Integer.parseInt(refScanner.nextLine());
			switch (opcion) {
				case 1:
				 	// Implementar
					break;
				case 2:
					taskListMenu(refScanner);
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

	/*
	 * Opciones para mostrar todas las tareas, una vez mostradas
	 * se podra modificar una tarea seleccionada
	*/
	private void taskListMenu(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("   Listar tareas");
			System.out.println("1. Ordenadas por prioridad [Mayor a menor]");
			System.out.println("2. Todas las tareas [Completadas/Incompletas]");
			System.out.println("3. Volver");
			int opcion = Integer.parseInt(refScanner.nextLine());
			switch (opcion) {
				case 1:
					// Implementar mostrar tareas
					taskListMenuOptions(refScanner);
					break;
				case 2:
					// Implementar mostrar tareas
					taskListMenuOptions(refScanner);
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

	/*
	 * Una vez mostradas las tareas, se podra elegir una de las tareas
	 * y sobre ella ejecutar alguna de las siguientes opciones
	*/
	private void taskListMenuOptions(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("1. Marcar [Completa/Incompleta]");
			System.out.println("2. Modificar [tarea]");
			System.out.println("3. Eliminar [tarea]");
			System.out.println("4. Volver");
			int opcion = Integer.parseInt(refScanner.nextLine());
			switch (opcion) {
				case 1:
					// Implementar
					break;
				case 2:
					// Implementar
					break;
				case 3:
					// Implementar
					break;
				case 4:
				 	salir = true;
					break;
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
			
		} while (!salir);
	}
	
	/*
	 * Se debera poder exportar/importar todas las tareas a un fichero
	 * en formato JSON o CSV, el usuario elige el formato
	 */
	private void importExportMenu(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("1. Importar tareas");
			System.out.println("2. Exportar tareas");
			System.out.println("3. Volver");
			int opcion = Integer.parseInt(refScanner.nextLine());
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
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
		} while (!salir);
	}

	private void importFormat(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("1. Importar en CSV");
			System.out.println("2. Importar en JSON");
			System.out.println("3. Volver");
			int opcion = Integer.parseInt(refScanner.nextLine());
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
				default:
					showErrorMessage("Opcion no valida");
					break;
			}
		} while (!salir);
	}

	private void exportFormat(Scanner refScanner) {
		clearScreen();
		boolean salir = false;
		do {
			System.out.println("1. Exportar en CSV");
			System.out.println("2. Exportar en JSON");
			System.out.println("3. Volver");
			int opcion = Integer.parseInt(refScanner.nextLine());
			switch (opcion) {
				case 1:
				 	// Implementar
					break;
				case 2:
					// Implementar
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