package isse.mbr.tools.execution;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instances of this class are used for holding and persisting working MiniZinc models.
 *
 * @author Elias
 */
public class WorkingModelManager implements AutoCloseable {
	private File file;
	private String model;
	private final boolean writeIntermediateFiles;
	private final boolean keepWorkingFiles;

	public WorkingModelManager(File file, String initialModel, boolean writeIntermediateFiles, boolean keepWorkingFiles) {
		this.file = file;
		this.model = initialModel;
		this.writeIntermediateFiles = writeIntermediateFiles;
		this.keepWorkingFiles = keepWorkingFiles;
	}

	public static WorkingModelManager create(File originalFile, boolean writeIntermediateFiles, boolean keepWorkingFiles) throws IOException {
		File workingFile = getNextWorkingFile(originalFile);
		String model = FileUtils.readFileToString(originalFile, StandardCharsets.UTF_8);
		WorkingModelManager manager = new WorkingModelManager(workingFile, model, writeIntermediateFiles, keepWorkingFiles);
		manager.writeWorkingMiniZincFile();
		return manager;
	}

	public void appendToModel(String additionalCode) throws IOException {
		// update in-memory model
		additionalCode = "\n" + additionalCode;
		model += additionalCode;

		if (writeIntermediateFiles) {
			writeNewWorkingMiniZincFile();
		} else {
			// we can reuse the existing file and only need to append the new code
			try (FileWriter fw = new FileWriter(file, true)) {
				fw.write(additionalCode);
			}
		}
	}

	public void replaceModel(String newCode) throws IOException {
		model = newCode;

		if (writeIntermediateFiles) {
			writeNewWorkingMiniZincFile();
		} else {
			writeWorkingMiniZincFile();
		}
	}

	private void writeNewWorkingMiniZincFile() throws IOException {
		File oldFile = file;
		file = getNextWorkingFile(oldFile);
		writeWorkingMiniZincFile();
		cleanup(oldFile);
	}

	private void writeWorkingMiniZincFile() throws IOException {
		FileUtils.writeStringToFile(file, model, StandardCharsets.UTF_8);
	}

	private static File getNextWorkingFile(File miniZincFile) {
		String name = FilenameUtils.removeExtension(miniZincFile.getName());
		int modelIndex = 0;
		Matcher modelIndexMatcher = Pattern.compile(".*_([0-9]+)$").matcher(name);
		if (modelIndexMatcher.matches()) {
			String modelIndexText = modelIndexMatcher.group(1);
			name = name.substring(0, name.length() - modelIndexText.length() - 1);
			modelIndex = Integer.parseInt(modelIndexText) + 1;
		}
		String nextName = String.format("%s_%d.mzn", name, modelIndex);
		return new File(miniZincFile.getParentFile(), nextName);
	}

	public void cleanup() {
		cleanup(file);
	}

	private void cleanup(File miniZincFile) {
		if (!keepWorkingFiles)
			FileUtils.deleteQuietly(miniZincFile);
	}

	public File getFile() {
		return file;
	}

	public String getModel() {
		return model;
	}

	@Override
	public void close() throws Exception {
		cleanup();
	}
}
