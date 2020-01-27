package com.cisco;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class LogAnalyzer {

    protected TreeMap<String, Long> logsPerSecond = new TreeMap<>();

    protected List<String> secondsMissingInLog = new ArrayList<>();

    protected List<String> seconds = new ArrayList<>();

    protected InputStream inputStream;

    protected String inputZipFilePath;

    protected IMPLogAnalyzer impLogAnalyzer = new IMPLogAnalyzer();

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    private ZipFile zipFile;


    public LogAnalyzer(String inputZipFile){
        inputZipFilePath = inputZipFile;
        listEntriesInZip();
    }

    private void listEntriesInZip(){
        try {
            ZipFile zipFile = new ZipFile(inputZipFilePath);
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    listOfFiles.add(entry.getName());
                }
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void callIMPLogAnalyzer(){
        try (ZipFile zipFile = new ZipFile(inputZipFilePath)){
            int filecount = 0;
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains("IMPLog")){
                    ZipEntry imp = zipFile.getEntry(entry.getName());
                    inputStream = zipFile.getInputStream(imp);
                    impLogAnalyzer.writeToOuputTxtFile(impLogAnalyzer.analyzeLog(inputStream), ("result" + filecount));
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
// Null pointer when closing the InputStream.

    }

}