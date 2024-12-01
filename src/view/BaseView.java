package view;

import controller.Controller;

public abstract class BaseView {
    /* Atributos */
	// Cambiar visibilidad a private, public es solo por el porculo
	// del resaltado amarillo
    public Controller controller;

    /* Metodos */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public abstract void init();

    public abstract void showMessage(String message);

    public abstract void showErrorMessage(String message);

    public abstract void end();
}
