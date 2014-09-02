package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import dao.FileManager;

public class Main {
	int choice = 0;
	int mode = 1;
	Path currentRelativePath = Paths.get("");
	String currentPath = currentRelativePath.toAbsolutePath().toString();

	// dirpath: /home/morrisgrill/repositorios/API-Censia/MyHealth_API

	public void showOptions() {
		while (choice != 4) {
			System.out
					.printf("*Markdown Documentator for Node.js projects*\n1)\tBatch document creator\n2)\tSingle document creator\n3)\tBuilder mode\n4)\tExit\n");
			System.out.println("Choice:");
			choice = getNumber();
			switch (choice) {
			case 1:
				System.out
						.println("Write directory path to Node.js project or type Enter for predefined:");
				String dirpath = getText();
				if (dirpath.isEmpty()) {
					dirpath = currentPath;
				}
				File folder = new File(dirpath + "/routes");
				FileManager fm = new FileManager();
				List<File> fileList = fm.getFileList(folder);
				System.out
						.println("Are you sure you want to make documents for each file in this list? (y/n)");
				if (getText().equals("y")) {
					generateBatchDocuments(fileList);
				}

				break;
			case 2:
				System.out
						.println("Write file path to make document or type Enter for predefined:");
				String file1 = getText();
				if (file1.isEmpty()) {
					file1 = currentPath + "/routes/CluesRoute.js";
				}
				try {
					File fileUrl = new File(file1);
					System.out
							.println("Are you sure you want to make document for "
									+ fileUrl.getName() + "? (y/n)");
					if (getText().equals("y")) {
						DocumentConstructor dc = new DocumentConstructor();
						dc.createNewDocument(currentPath, fileUrl.getName(),
								fileUrl, mode);
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.err.println("[ERROR]: File not found " + file1);
				} catch (UnsupportedEncodingException e) {
					System.err.println("[ERROR]: Encoding not supported of " + file1);
				}
				break;
			case 3:
				int opt = 0;
				do {
					System.out.println("*Builder mode options*");
					System.out.println("Working path: " + currentPath);
					System.out
							.println("1)\tTest mode\n2)\tProduction mode\n3)\tCancel\nChoice:");
					opt = getNumber();
					if(opt > 0 || opt < 3){
						this.mode = opt;
					}
					
				} while (opt < 0 || opt > 4);
				break;
			case 4:
				System.out.println("Bye");
				break;
			default:
				System.err.println("Select from 1 to 3 and type 'Enter'\n");
				break;
			}
		}
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.showOptions();
	}

	public String getText() {
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}

	public int getNumber() {
		Scanner scanner = new Scanner(System.in);
		return scanner.nextInt();
	}

	public String generateBatchDocuments(List<File> fileList) {

		for (File fileUrl : fileList) {
			try {
				DocumentConstructor dc = new DocumentConstructor();
				dc.createNewDocument(currentPath, fileUrl.getName(), fileUrl, mode);

			} catch (FileNotFoundException e) {
				System.err.println("[ERROR]: File not found " + fileUrl.getName());
			} catch (UnsupportedEncodingException e) {
				System.err.println("[ERROR]: Encoding not supported of " + fileUrl.getName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return "success";
	}

	public String generateSingleDocument(File filename) {

		return "success";
	}
}
