package com.cisco;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;



public class LogAnalyzer {

    private static final int BUFFER = 2048;

    protected InputStream inputStream;

    protected String inputArchiveFilePath;

    protected IMPLogAnalyzer impLogAnalyzer = new IMPLogAnalyzer();

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    public LogAnalyzer(String inputArchiveFilePath){
        this.inputArchiveFilePath = inputArchiveFilePath;
        determineArchiveType(inputArchiveFilePath);
    }

    protected void determineArchiveType(String inputArchiveFile){
        String pattern = "(\\.zip)|(\\.tar\\.gz)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputArchiveFile);
        if(m.find()){
            if(m.group().equals(m.group(1))){
                callIMPLogAnalyzer();
            }
            else if(m.group().equals(m.group(2))){
               openTarGZFile();
            }
            else {
                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            }
            }
    }


    // Add listing of All entries in zip to CallIMPLogAnalyser method as ZipFIle = null. In the future rename it to callLogAnalyser or analyserLogs
    protected void callIMPLogAnalyzer(){
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains("IMPLog")){
                    ZipEntry imp = zipFile.getEntry(entry.getName());
                    listOfFiles.add(entry.getName());
                    inputStream = zipFile.getInputStream(imp);
                    impLogAnalyzer.writeToOuputTxtFile(impLogAnalyzer.analyzeLog(inputStream), ("result" + (impLogAnalyzer.getFileNames(listOfFiles).get(filecount))));
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void openTarGZFile(){
        try(TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(inputArchiveFilePath)))){
            int filecount = 0;
            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            while (currentEntry != null){
                if(!currentEntry.isDirectory() && currentEntry.getName().contains("IMPLog")){
                    listOfFiles.add(currentEntry.getName());
                    impLogAnalyzer.writeToOuputTxtFile(impLogAnalyzer.analyzeLog(tarInput), ("result" + listOfFiles.get(filecount)));
                    filecount++;
                    currentEntry = tarInput.getNextTarEntry(); // iterate to the next file
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream("c://temp//test.tar.gz")));
////        TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
////        BufferedReader br = null;
////        StringBuilder sb = new StringBuilder();
////        while (currentEntry != null) {
////            br = new BufferedReader(new InputStreamReader(tarInput)); // Read directly from tarInput
////            System.out.println("For File = " + currentEntry.getName());
////            String line;
////            while ((line = br.readLine()) != null) {
////                System.out.println("line="+line);
////            }
////            currentEntry = tarInput.getNextTarEntry(); // You forgot to iterate to the next file
////        }
    }
}