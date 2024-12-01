package controller;

import model.Model;
import view.BaseView;

public class Controller {
	/* Atributos */
	private Model model;
	private BaseView view;

	public Controller(Model model, BaseView view) {
		this.model = model;
		this.view = view;
		// Asegurar la inyeccion del controlador
		view.setController(this);
	}

	/*
	 * Ignorar el codigo de debajo, solo es para quitar el puto
	 * resaltado del Errorlens y VSCode
	 */
	public void start() {
		model.loadData();
		view.init();
	}

	public void end() {
		view.end();
	}

}