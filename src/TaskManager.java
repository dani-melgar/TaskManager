import controller.Controller;
import model.Model;
import model.repository.BinaryRepository;
import model.repository.IRepository;
import view.InteractiveView;

public class TaskManager {
	public static void main(String[] args) {
		try {
			IRepository repository = null;

			if (args.length > 1 && args[0].equals("--repository")) {
				switch (args[1].toLowerCase()) {
					case "bin":
						repository = new BinaryRepository();
						break;
					default:
						System.err.println("Repositorio no valido. Usando repositorio binario por defecto.");
						repository = new BinaryRepository();
						break;
				}
			} else {
				// Usar repositorio binario como opción por defecto
				repository = new BinaryRepository();
			}

			// Configurar el modelo, vista y controlador
			Model modelo = new Model(repository);
			InteractiveView vista = new InteractiveView();
			Controller controller = new Controller(modelo, vista);

			// Iniciar el programa
			controller.start();

		} catch (Exception e) {
			System.err.println("Se produjo un error inesperado: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
