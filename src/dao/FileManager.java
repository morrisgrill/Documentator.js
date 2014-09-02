package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import beans.ParamBean;
import beans.SchemaBean;

public class FileManager {
	PrintWriter writer;
	File filename;
	String encoding = "UTF-8";

	public FileManager() {
	}

	public FileManager(File filename) throws FileNotFoundException,
			UnsupportedEncodingException {
		this.filename = filename;
		writer = new PrintWriter(filename.getAbsolutePath(), encoding);
	}

	public void addLine(String text) {
		writer.println(text);
	}

	public void saveFile() {
		writer.close();
		System.out.println("[INFO]: Document " + filename.getName()
				+ " created successfully");
	}

	public String getFromTag(File filename, String tag)
			throws FileNotFoundException, IOException {
		String found = null;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains("@" + tag + "=")) {
					found = line
							.trim()
							.replaceAll("\\s*(\\/\\/|\\/\\*)\\s*@\\w+\\s*=\\s*", "")
							.trim();
					break;
				}
			}
		}
		return found;
	}

	public String getFromSearch(File filename, String search)
			throws FileNotFoundException, IOException {
		String found = null;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains(search)) {
					found = line;
				} else {
					// not found
				}
			}
		}
		return found;
	}

	public String getFromFunctionTag(File filename, String function, String tag)
			throws FileNotFoundException, IOException {
		String found = "Sin información\n";
		boolean founded = false;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains(function + " = function(")) {
					founded = true;
				} else {
					if (founded) {
						if (line.contains("@" + tag + "=")) {
							found = line
									.trim()
									.replaceAll(
											"\\s*(\\/\\/|\\/\\*)\\s*@\\w+\\s*=\\s*",
											"").trim();
							break;
						}
					}
				}
			}
		}
		return found;
	}

	public List<ParamBean> getListFromFunctionTag(File filename, String function, String tag)
			throws FileNotFoundException, IOException {
		String found = "Sin información\n";
		List<ParamBean> paramsList = new ArrayList<ParamBean>();
		ParamBean bean;
		String[] arrFound;
		boolean founded = false;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains(function + " = function(")) {
					founded = true;
				} else {
					if (founded) {
						if (line.contains("@" + tag + "=")) {
							bean = new ParamBean();
							found = line
									.trim()
									.replaceAll(
											"\\s*(\\/\\/|\\/\\*)\\s*@\\w+\\s*=\\s*",
											"").trim();
							arrFound = found.split(",\\s*");
							bean.setName(arrFound[1]);
							bean.setType(arrFound[0]);
							bean.setReceive(arrFound[2]);
							bean.setDescription(arrFound[3]);
							paramsList.add(bean);
						}
						if(line.trim().contains("}")) {
							break;
						}
					}
				}
			}
		}
		return paramsList;
	}

	
	public List<String> getListFromSearch(File filename, String search)
			throws FileNotFoundException, IOException {
		String found = null;
		List<String> searchedList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.contains(search)) {
					searchedList.add(line);
				} else {
					// not found
				}
			}
		}
		return searchedList;
	}

	public List<SchemaBean> getSchema(File filename)
			throws FileNotFoundException, IOException {
		boolean foundSchema = false;
		List<SchemaBean> beans = new ArrayList<SchemaBean>();
		SchemaBean bean;

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if (!foundSchema) {
					if (line.contains("Schema({")) {
						foundSchema = true;
					}
				} else {
					if (line.contains("});")) {
						break;
					}
					String[] lineResult = line.replaceAll(
							"([^a-zA-Z_]+|:'\\s)", ",").split(",");
					boolean first = true;
					String lastWord = "";
					bean = new SchemaBean();

					for (int i = 1; i < lineResult.length; i++) {
						if (first) {
							first = false;
							bean.setName(lineResult[i]);
						} else {
							if (lineResult[i].equals("type")
									|| lineResult[i].equals("ref")
									|| lineResult[i].equals("require")
									|| lineResult[i].equals("default")) {
								lastWord = lineResult[i];
							} else {
								if (lastWord.equals("type")) {
									bean.setType(lineResult[i]);
								} else if (lastWord.equals("require")) {
									bean.setName(bean.getName() + " *");
								} else if (lastWord.equals("ref")) {
									bean.setReference(lineResult[i]);
								} else if (lastWord.equals("default")) {
									bean.setDefaultValue(lineResult[i]);
								}
							}
						}
					}
					beans.add(bean);
					lastWord = "";
				}
			}
		} catch (Exception ex) {
			ex.getStackTrace();
		}
		return beans;
	}

	public List<File> getFileList(final File dirpath) {
		List<File> fileList = new ArrayList<File>();
		File[] listOfFiles = dirpath.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println(file.getName());
				fileList.add(file);
			}
		}
		return (List<File>) fileList;
	}
}
