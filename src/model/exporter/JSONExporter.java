package model.exporter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Task;

public class JSONExporter implements IExporter {

	@Override
	public void exportTasks(List<Task> tasks) {
	}

	@Override
	public List<Task> importTasks() {
		return null;
	}

	@Override
	public void ensureDirectoryExists() throws ExporterException {
		throw new UnsupportedOperationException("Unimplemented method 'ensureDirectoryExists'");
	}

	@Override
	public void createBackup(File file) throws IOException {
		throw new UnsupportedOperationException("Unimplemented method 'createBackup'");
	}

	@Override
	public void validateTasks(List<Task> tasks) throws ExporterException {
		throw new UnsupportedOperationException("Unimplemented method 'validateTasks'");
	}

	@Override
	public Task factoryTask(String delimitedString) throws ExporterException {
		throw new UnsupportedOperationException("Unimplemented method 'factoryTask'");
	}
}