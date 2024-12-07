package view;

import controller.Controller;

/**
 * Clase base abstracta para las vistas en la aplicacion.
 * Proporciona una estructura para la interaccion entre la vista y el controlador.
 */
public abstract class BaseView {
    /* Atributos */
    public Controller controller;

    /**
     * Establece el controlador para la vista.
     *
     * @param controller El controlador a asociar.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Metodo abstracto para inicializar la vista.
     * Debe ser implementado por las clases que extiendan esta clase base.
     */
    public abstract void init();

    /**
     * Muestra un mensaje al usuario.
     *
     * @param message El mensaje a mostrar.
     */
    public abstract void showMessage(String message);

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param message El mensaje de error a mostrar.
     */
    public abstract void showErrorMessage(String message);

    /**
     * Finaliza la vista, realizando las tareas necesarias de cierre o limpieza.
     */
    public abstract void end();
}