import controller.Controller;
import model.Model;
import model.repository.BinaryRepository;
import model.repository.IRepository;
import view.FinalView;

public class App {
    public static void main(String[] args) {
        try {
            // Configurar el repositorio según los argumentos
            IRepository repository = null;

            if (args.length > 1 && args[0].equals("--repository")) {
                switch (args[1].toLowerCase()) {
                    case "bin":
                        repository = new BinaryRepository();
                        break;
                    // Podrías añadir aquí más opciones de repositorio en el futuro
                    default:
                        System.err.println("Repositorio no válido. Usando repositorio binario por defecto.");
                        repository = new BinaryRepository();
                        break;
                }
            } else {
                // Usar repositorio binario como opción por defecto
                repository = new BinaryRepository();
            }

            // Configurar el modelo, vista y controlador
            Model modelo = new Model(repository);
            FinalView vista = new FinalView();
            Controller controller = new Controller(modelo, vista);

            // Iniciar el programa
            controller.start();

        } catch (Exception e) {
            System.err.println("Se produjo un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
