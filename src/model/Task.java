package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
	public static final long serialVerisionUID = 1L;

	/* Atributos */
	private int identifier;
	private String title;
	private LocalDate date;
	private String content;
	private int priority; // Entero entre 1 y 5
	private int estimatedDuration;
	private boolean completed;

	/* Constructor */
	public Task(int identifier, String title, LocalDate date, String content, int priority, int estimatedDuration, boolean completed) {
		this.identifier = identifier;
		this.title = title;
		this.date = date;
		this.content = content;
		this.priority = priority;
		this.estimatedDuration = estimatedDuration;
		this.completed = completed;
	}

	/* Constructor SIN Identificador */
	public Task(String title, LocalDate date, String content, int priority, int estimatedDuration, boolean completed) {
		this.title = title;
		this.date = date;
		this.content = content;
		this.priority = priority;
		this.estimatedDuration = estimatedDuration;
		this.completed = completed;
	}

	public Task(int identifier) {
		this.identifier = identifier;
	}

	/* Metodos */
	public String toDelimitedStringOLD(String delimiter) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return escapeField(String.valueOf(identifier)) + delimiter
			+ escapeField(title) + delimiter
			+ escapeField(date.format(dateFormatter)) + delimiter
			+ escapeField(content) + delimiter
			+ escapeField(String.valueOf(priority)) + delimiter
			+ escapeField(String.valueOf(estimatedDuration)) + delimiter
		+ escapeField(String.valueOf(completed));
	}

	public String toDelimitedString(String delimiter) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return identifier + delimiter + title + delimiter + dateFormat.format(date) + delimiter + content + delimiter + priority + delimiter + estimatedDuration + delimiter + completed;
	}

	private String escapeField(String field) {
		if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
			// Escapar comillas dobles
			field = field.replace("\"", "\"\"");
			// Envolver el campo con comillas dobles
			return "\"" + field + "\"";
		}
		return field;
	}

	@Override
	public String toString() {
		return identifier + " " + title + " "  + date + " "  + content + " "  + priority + " "  + estimatedDuration + " " + completed;
	}

	// Numero actual de atributos
	public static int getFieldCount() {
		return 7;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
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
