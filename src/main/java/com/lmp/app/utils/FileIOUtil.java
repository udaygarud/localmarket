package com.lmp.app.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileIOUtil {

  public static void writeProgress(String fName, String data) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fName, true));
    writer.append(data);
    writer.newLine();
    writer.close();
  }

  public static Set<String> readProcessed(String fName) throws IOException {
    try(BufferedReader br = new BufferedReader(new FileReader(fName))) {
      Set<String> set = new HashSet<>();
      String st = "";
      while ((st = br.readLine()) != null){
        set.add(st);
      }
      return set;
    } catch(FileNotFoundException e) {
      return new HashSet<>();
    }
  }

  public static List<File> getAllFilesInDir(String dPath) {
    File folder = new File(dPath);
    File[] listOfFiles = folder.listFiles();
    List<File> files = new ArrayList<>();
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        files.add(listOfFiles[i]);
      }
    }
    return files;
  }

  public static void deleteFile(String fName) {
    File file = new File(fName);
    if(file.exists()) {
      file.delete();
    }
  }
}
