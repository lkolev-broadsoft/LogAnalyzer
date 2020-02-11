package com.cisco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
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
//                openZIPFileReadStats();
            }
            else if(m.group().equals(m.group(2))){
               //openTarGZFile();
//               //openTarGZFileReadStats();
                try {
                    unTarGZ(archivePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                if(!entry.isDirectory() && entry.getName().contains("IMPLog")){
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
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void openZIPFileReadStats(){
        try (ZipFile zipFile = new ZipFile(inputArchiveFilePath)){
            int filecount = 0;
            entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains(statsLogAnalyzer.logType)){
                    ZipEntry stats = zipFile.getEntry(entry.getName());
                    listOfFiles.add(entry.getName());
                    inputStream = zipFile.getInputStream(stats);
                    List<String> statsList;
                    statsList = statsLogAnalyzer.getStatsValues(inputStream);
                    statsLogAnalyzer.getLastPackets(statsList);
                    statsLogAnalyzer.getOverflows(statsList);
                    statsLogAnalyzer.getSessManProcessor(statsList);
                    statsLogAnalyzer.writeToOutputTxtFile(statsLogAnalyzer.statValuesMap, "result_" + (listOfFiles.get(filecount)));
                    filecount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    // Problem with TarArchiveNextEntry - open the file and go though the entries one by one(send the input stream first)
//
//    protected TarArchiveInputStream getTarAchiveInputStream(String inputTarFilePath) throws FileNotFoundException {
//        return new TarArchiveInputStream(new FileInputStream(inputTarFilePath));
//    }
//
////    protected FileInputStream getInputFileStream(String inputFilePath){
////        return new FileInputStream(inputFilePath);
////    }

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

//        protected void openTarGZFile() {
//        try (FileInputStream fileInputStream = new FileInputStream(inputArchiveFilePath);
//             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
//             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)){
//            int filecount = 0;
//            TarArchiveEntry currentEntry;
//            while (((currentEntry = tarArchiveInputStream.getNextTarEntry()) != null)) {
//                if (!currentEntry.isDirectory() && currentEntry.getName().contains("IMPLog")) {
//                    listOfFiles.add(currentEntry.getName());
//                    impLogAnalyzer.writeToOutputTxtFile(("result" + listOfFiles.get(filecount)), impLogAnalyzer.analyzeLog(tarArchiveInputStream));
//                    filecount++;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


// java.io.IOException: Stream Closed ??
//    protected void openTarGZFile() {
//        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(inputArchiveFilePath)))) {
//            int filecount = 0;
//            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
//            while (currentEntry != null) {
//                if (!currentEntry.isDirectory() && currentEntry.getName().contains("IMPLog")) {
//                    listOfFiles.add(currentEntry.getName());
//                    System.out.println("In the while loop");
//                    impLogAnalyzer.writeToOutputTxtFile(("result" + listOfFiles.get(filecount)), impLogAnalyzer.analyzeLog(tarInput));
//                    filecount++;
//                    currentEntry = tarInput.getNextEntry(); // iterate to the next file
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    //Something is wrong with reading from tar.gz => java.lang.NullPointerException
    //	at org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream.read(GzipCompressorInputStream.java:296)
    //REWORK!

//    protected void openTarGZFileReadStats(){
//        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(inputArchiveFilePath)))) {
//            int filecount = 0;
//            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
//            while (currentEntry != null) {
//                if (!currentEntry.isDirectory() && currentEntry.getName().contains(statsLogAnalyzer.logType)) {
//                    listOfFiles.add(currentEntry.getName());
//                    statsLogAnalyzer.writeToOutputTxtFile(("result_" + listOfFiles.get(filecount)), statsLogAnalyzer.getLastPackets(tarInput));
//                    filecount++;
//                    currentEntry = tarInput.getNextTarEntry(); // iterate to the next file
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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
//    }

}