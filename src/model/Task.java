package model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
	// Cambiar la visibilidad luego a private
	public static final long serialVerisionUID = 1L;

	/* Atributos */
	private int identifier;
	private String title;
	private Date date;
	private String content;
	private int priority; // Entero entre 1 y 5
	private int estimatedDuration;
	private boolean completed;

	/* Constructor */
	public Task(int identifier, String title, Date date, String content, int priority, int estimatedDuration, boolean completed) {
		this.identifier = identifier;
		this.title = title;
		this.date = date;
		this.content = content;
		this.priority = priority;
		this.estimatedDuration = estimatedDuration;
		this.completed = completed;
	}

	/* Constructor SIN Identificador */
	public Task(String title, Date date, String content, int priority, int estimatedDuration, boolean completed) {
		this.title = title;
		this.date = date;
		this.content = content;
		this.priority = priority;
		this.estimatedDuration = estimatedDuration;
		this.completed = completed;
	}

	/* Metodos */
	public String toDelimitedString(String delimiter) {
		return Integer.toString(identifier) + delimiter + title + delimiter + date + delimiter
						    + content + delimiter + Integer.toString(priority) + delimiter + Integer.toString(estimatedDuration)
						    + delimiter + completed;
	}

	/* Getters & Setters */
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(int estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}



}
