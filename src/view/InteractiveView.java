package view;

public class InteractiveView extends BaseView {

	@Override
	public void init() {
		clearScreen();
		initialMessage();
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
		throw new UnsupportedOperationException("Unimplemented method 'end'");
	}

	// Cutre, mejorarlo una vez funcione
	private void initialMessage() {
		System.out.println("        BASE VIEW        ");
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