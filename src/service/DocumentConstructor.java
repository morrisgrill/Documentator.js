package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.Element;

import utilities.RelativePath;
import utilities.Slug;
import dao.FileManager;
import beans.ParamBean;
import beans.SchemaBean;

public class DocumentConstructor {
	File newFile;
	File readedFile;
	File modelFile;
	Slug slug;
	FileManager fm;
	String homepath;
	List<String> functionList;

	public void createNewDocument(String dirpath, String name,
			File readThisFile, int mode) throws FileNotFoundException,
			UnsupportedEncodingException {
		this.readedFile = readThisFile;
		this.homepath = dirpath;
		String onlyName = name.replaceFirst("[.][^.]+$", "");
		if (mode == 1) {
			newFile = new File(dirpath + "/docs/" + onlyName + ".rev.md");
		} else {
			newFile = new File(dirpath + "/docs/" + onlyName + ".md");
		}

		fm = new FileManager(newFile);

		String modelname = "";

		try {
			modelname = fm
					.getFromSearch(readedFile, "require('../models/")
					.split("(\\s*require\\(('|\")\\.\\.\\/models\\/|'\\)|\"\\))")[1]
					.trim();
			modelFile = new File(dirpath + "/models/" + modelname);
		} catch (IOException e) {
			System.err.println("Error reading model file");
		}

		slug = new Slug();
		functionList = new ArrayList<String>();
		fm.addLine(getDescriptionSection());
		fm.addLine(getUseSection());
		fm.addLine(getRoutesSection());
		fm.addLine(getFunctionsSection());
		fm.addLine(getModelSection());
		fm.addLine(getDependenciesSection());
		fm.addLine(getOtherInfoSection());
		fm.saveFile();
	}

	/**
	 * Start Sections
	 */

	public String getDescriptionSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Descripción\n");

		try {
			s.append(fm.getFromTag(readedFile, "maindescription"));
			System.out.println();
		} catch (FileNotFoundException e) {
			System.err.println("Not found: " + readedFile.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Error reading document: "
					+ readedFile.getAbsolutePath());
		}
		return s.toString();
	}

	public String getUseSection() {
		return "##Modo de Uso\nPara utilizar este servicio, realice peticiones REST al servicio utilizando las [Rutas de acceso](#rutas-de-acceso)";
	}

	public String getRoutesSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Rutas de acceso\n|Petición|Ruta|Función|\n|--------|----|-------|\n");

		try {
			List<String> getList = fm.getListFromSearch(readedFile, "app.");

			for (String element : getList) {
				String[] arrGet = element.split("(app\\.|\\('|',\\s|\\);)");
				functionList.add(arrGet[3]);
				s.append("|" + arrGet[1].toUpperCase() + "|" + arrGet[2] + "|["
						+ arrGet[3] + "](#" + slug.makeSlug(arrGet[3]) + ")|\n");
			}

		} catch (FileNotFoundException e) {
			System.err.println("Not found: " + readedFile.getName());
		} catch (IOException e) {
			System.err.println("Error reading document: "
					+ readedFile.getName());
		}
		return s.toString();
	}

	public String getFunctionsSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Funciones\n");
		for (String element : functionList) {
			try {
				s.append("####" + element + "\n");
				String description = fm.getFromFunctionTag(readedFile, element,
						"description").trim();
				s.append(description + "\n\n");
				s.append("|Parámetro|Tipo|Recibe|Descripción|\n"
						+ "|---------|----|------|-----------|\n");
				List<ParamBean> paramsList = fm.getListFromFunctionTag(
						readedFile, element, "param");
				for (ParamBean param : paramsList) {
					s.append("|" + param.getName() + "|" + param.getType()
							+ "|" + param.getReceive() + "|"
							+ param.getDescription() + "|\n");
				}
				s.append("\n\tEjemplo: "
						+ fm.getFromFunctionTag(readedFile, element, "example")
								.trim() + "\n");

			} catch (FileNotFoundException e) {
				System.err
						.println("Not found: " + readedFile.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Error reading document: "
						+ readedFile.getAbsolutePath());
			}
		}
		return s.toString();
	}

	public String getModelSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Modelo\n");
		s.append("|Atributo|Tipo|Referencia|Predeterminado|Descripción|\n"
				+ "|--------|----|----------|--------------|-----------|\n");

		try {
			for (SchemaBean bean : fm.getSchema(modelFile)) {
				s.append("|" + bean.getName() + "|" + bean.getType() + "|"
						+ bean.getReference() + "|" + bean.getDefaultValue()
						+ "|" + bean.getDescription() + "|\n");
			}

		} catch (FileNotFoundException e) {
			System.err.println("Not found: " + readedFile.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Error reading schema: "
					+ readedFile.getAbsolutePath());
		}

		return s.toString();
	}

	public String getDependenciesSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Dependencias\n"
				+ "Este servicio depende de los siguientes módulos NPM:\n");
		try {
			List<String> list = fm.getListFromSearch(readedFile, "require(");
			for (String element : list) {
				String str = element.replaceAll(
						"^(?:(?![\"']+).)*|[^a-zA-Z-_./]+", "");
				s.append("* [" + str + "](https://www.npmjs.org/package/"
						+ slug.makeSlug(str) + ")\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s.toString();
	}

	public String getOtherInfoSection() {
		StringBuilder s = new StringBuilder();
		s.append("##Otra Información\n");

		try {
			File home = new File(homepath);
			s.append("- Ubicación del archivo: "
					+ RelativePath.getRelativePath(home, readedFile));
			s.append("\n- Autor: " + fm.getFromTag(readedFile, "author"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.append("\n- Fecha de desarrollo: "
				+ new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date(
						readedFile.lastModified())));
		s.append("\n- Fecha de elaboración de este documento: "
				+ new SimpleDateFormat("dd-MM-yyyy HH-mm-ss")
						.format(new Date()));

		return s.toString();
	}
}
