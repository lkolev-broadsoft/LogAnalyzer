package com.cisco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;


public class LogAnalyzer {

    protected InputStream inputStream;

    protected String inputArchiveFilePath;

    protected IMPLogAnalyzer impLogAnalyzer = new IMPLogAnalyzer();

    protected StatsLogAnalyzer statsLogAnalyzer = new StatsLogAnalyzer();

    protected List<String> listOfFiles = new ArrayList<>();

    protected Enumeration<? extends ZipEntry> entries;

    protected Path archivePath;

    public LogAnalyzer(String inputArchiveFilePath){
        this.inputArchiveFilePath = inputArchiveFilePath;
        this.archivePath = Paths.get(inputArchiveFilePath);
        determineArchiveType(inputArchiveFilePath);
    }

    //Take actions according to the type of logs provided.
    //Go through all of the logs

    protected void determineArchiveType(String inputArchiveFile){
        String pattern = "(\\.zip)|(\\.tar\\.gz)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputArchiveFile);
        if(m.find()){
            if(m.group().equals(m.group(1))){
                openZIPFile();
//                openZIPFileReadStats(); // Call zipFactory
            }
            else if(m.group().equals(m.group(2))){
               openTarGZFile();
//               //openTarGZFileReadStats(); // Call targzFactory
            }
            else {
                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            }
        }
    }

    //Use factory pattern to create the type of logAnalyser when the String type of log is encountered. Better then if else and switch maybe.



    // Add listing of All entries in zip to CallIMPLogAnalyser method as ZipFIle = null. In the future rename it to callLogAnalyser or analyserLogs
    protected void openZIPFile(){
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains("IMPLog")){ //Use factory pattern to create IMPLogAnalyzer or PresenceLogAnalyzer
                    ZipEntry imp = zipFile.getEntry(entry.getName());
                    listOfFiles.add(entry.getName());
                    inputStream = zipFile.getInputStream(imp);
                    impLogAnalyzer.writeToOutputTxtFile(("result" + (impLogAnalyzer.getFileNames(listOfFiles).get(filecount))), impLogAnalyzer.analyzeLog(inputStream));
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
    }

//    protected void openZIPFileReadStats(){
//        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
//            int filecount = 0;
//            entries = zipFile.entries();
//            while(entries.hasMoreElements()){
//                ZipEntry entry = entries.nextElement();
//                if(!entry.isDirectory() && entry.getName().contains(statsLogAnalyzer.logType)){
//                    ZipEntry stats = zipFile.getEntry(entry.getName());
//                    listOfFiles.add(entry.getName());
//                    inputStream = zipFile.getInputStream(stats);
//                    List<String> statsList;
//                    statsList = statsLogAnalyzer.getStatsValues(inputStream);
//                    statsLogAnalyzer.getLastPackets(statsList);
//                    statsLogAnalyzer.getOverflows(statsList);
//                    statsLogAnalyzer.getSessManProcessor(statsList);
//                    statsLogAnalyzer.writeToOutputTxtFile(statsLogAnalyzer.statValuesMap, "result_" + (listOfFiles.get(filecount)));
//                    filecount++;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    //Try with using both apache commons and Java NIO(Path from String instead of file)

    protected void unTarGZ(Path pathInput) throws IOException {
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(Files.newInputStream(pathInput))));
        ArchiveEntry currentEntry = null;
        int filecount = 0;
        while ((currentEntry = tarArchiveInputStream.getNextEntry()) != null) {
            if (!currentEntry.isDirectory() && currentEntry.getName().contains("IMPLog")) {
                listOfFiles.add(currentEntry.getName());
                impLogAnalyzer.writeToOutputTxtFile(("result" + listOfFiles.get(filecount)), impLogAnalyzer.analyzeLog(tarArchiveInputStream));
                filecount++;
            }
            tarArchiveInputStream.close();
        }
    }

            protected void openTarGZFile() {
        try (FileInputStream fileInputStream = new FileInputStream(inputArchiveFilePath);
             GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(fileInputStream);
             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)){
            int filecount = 0;
            TarArchiveEntry currentEntry;
            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
                if (!currentEntry.isDirectory() && currentEntry.getName().contains("IMPLog")) {
                    listOfFiles.add(currentEntry.getName());
                    Map<String, Long> results = impLogAnalyzer.analyzeLog(tarArchiveInputStream);
                    impLogAnalyzer.writeToOutputTxtFile("result" + listOfFiles.get(filecount), results);
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}