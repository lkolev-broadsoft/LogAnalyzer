package com.cisco;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    private static TreeMap<String, Long> logsPerSecond = new TreeMap<>();

    private static List<String> pauses = new ArrayList<>();

    private static Enumeration<? extends ZipEntry> entries;

    private static List<String> listOfFiles = new ArrayList<>();

    private static List<String> getTimeFrameList(InputStream inputStream) {

        String pattern = "(19|20\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])\\s(0[1-9]|1[0-9]|2[0-3])\\:([0-5][0-9])\\:([0-5][0-9])\\:([0-9]{3})\\s(\\w+)\\s\\|\\s(\\bFieldDebug\\b|\\bInfo\\b|\\bDebug\\b|\\bNotice\\b|\\bWarn\\b)\\s+\\|\\s(\\w+)";
        List<String> timeFrame = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = r.matcher(line);
                if(m.find()){
                    timeFrame.add(m.group(1) + "-" + m.group(2) + "-" + m.group(3) + " " + m.group(4) + ":" + m.group(5) + ":" + m.group(6));
                }
            }
            return timeFrame;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", inputStream);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static TreeMap<String, Long> countSeconds(List<String> timeFrame){
        Map<String, Long> inputMap = timeFrame.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        return new TreeMap<>(inputMap); //Added the resulting map to treemap
    }

    private static void writeToOuputTxtFile(Map<String, Long> inputMap, String filename){
        try (FileWriter writer = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for(Map.Entry<String, Long> entry : inputMap.entrySet())
                bw.write((entry.getKey() + " - " + entry.getValue().toString() + "\n"));
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

//    private static void writeToOutputTxtFile(List<String> inputList, String filename){
//        try (FileWriter writer = new FileWriter(filename);
//             BufferedWriter bw = new BufferedWriter(writer)) {
//            for(String s: inputList) {
//                bw.write((s  + "\n"));
//            }
//        } catch (IOException e) {
//            System.err.format("IOException: %s%n", e);
//        }
//    }

    private static List<String> getTimeFrame(SortedMap<String, Long> sortedMap){
        String startTime = sortedMap.firstKey();   // Get String with the starting time
        String endTime = sortedMap.lastKey();        // Get String with the ending time
        String pattern = "(20\\d\\d)\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])\\s(0[1-9]|1[0-9]|2[0-3])\\:([0-5][0-9])\\:([0-5][0-9])";
        Pattern r = Pattern.compile(pattern);
        Matcher startMatcher = r.matcher(startTime);
        startMatcher.matches();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startMatcher.group(1) + "-" + startMatcher.group(2) + "-" + startMatcher.group(3) + " " + startMatcher.group(4) + ":" + startMatcher.group(5) + ":" + startMatcher.group(6), formatter);
        Matcher endMatcher = r.matcher(endTime);
        endMatcher.matches();
        LocalDateTime end = LocalDateTime.parse(endMatcher.group(1) + "-" + endMatcher.group(2) + "-" + endMatcher.group(3) + " " + endMatcher.group(4) + ":" + endMatcher.group(5) + ":" + endMatcher.group(6), formatter);
        String timePhrase = start.format(formatter);
        List<String> timeFrame = new ArrayList<>();

        while(!start.equals(end)){
            timeFrame.add(timePhrase);
            start = start.plusSeconds(1);  // update startTime
            timePhrase = start.format(formatter); // update timephrase
        }
        return timeFrame;
    }

    private static List<String> findPauses(List<String> timeFrame, TreeMap<String, Long> sortedMap){
        List<String > missingSeconds = new ArrayList<>();

        for(String second: timeFrame){
            if(!sortedMap.containsKey(second)){
                missingSeconds.add(second);
            }
        }
        return missingSeconds;
    }

    private static TreeMap<String, Long> addPausesToMap(List<String> timeFrame, TreeMap<String, Long> sortedMap){

        for(String second: timeFrame){
            if(!sortedMap.containsKey(second)){
                sortedMap.put(second,  (long) 0);
            }
        }
        return sortedMap;
    }

//    public static ZipEntry getLogType(String zipfilePath){
//        try(ZipFile zipFile = new ZipFile(zipfilePath)){
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static List<String> getIMPFileNames(List <String> listOfEntries) {
        List<String> filenames = new ArrayList<>();
        String pattern = "(IMPLog)(.*)(.txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }

    public static List<String> getDBFileNames(List <String> listOfEntries) {
        List<String> filenames = new ArrayList<>();
        String pattern = "(dbConnectorLog)(.*)(txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }

    public static List<String> getGateWayFileNames(List <String> listOfEntries) {
        List<String> filenames = new ArrayList<>();
        String pattern = "(GateWayLog)(.*)(txt)";
        Pattern r = Pattern.compile(pattern);
        for(String entry : listOfEntries){
            Matcher m = r.matcher(entry);
            if(m.find()){
                filenames.add(m.group());
            }
        }
        return filenames;
    }

    private static void listEntriesInZip(String inputZipFilePath) {
        try (ZipFile zipFile = new ZipFile(inputZipFilePath)) {
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    listOfFiles.add(entry.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void extractAll(URI fromZip, Path toDirectory) throws IOException{
//        FileSystems.newFileSystem(fromZip, Collections.emptyMap())
//                .getRootDirectories()
//                .forEach(root -> {
//                    // in a full implementation, you'd have to
//                    // handle directories
//                    try {
//                        Files.walk(root).forEach(path -> {
//                            try {
//                                Files.copy(path, toDirectory);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//    }

    public static void main(String[] args) {
        // Assign zipfilePath to the provided in the main input arguments.
        String  zipfilePath = args[0];
        List<String> filenames = new ArrayList<>();
        InputStream inputStream = null;
        listEntriesInZip(zipfilePath);
        filenames = getIMPFileNames(listOfFiles);

        try (ZipFile zipFile = new ZipFile(zipfilePath)){
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int filecount = 0;
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().contains("IMPLog")){
                    ZipEntry imp = zipFile.getEntry(entry.getName());
                    inputStream = zipFile.getInputStream(imp);
                    List<String> seconds = getTimeFrameList(inputStream);
                    logsPerSecond = countSeconds(seconds);
                    pauses = findPauses(getTimeFrame(logsPerSecond),logsPerSecond);
                    writeToOuputTxtFile(addPausesToMap(pauses, logsPerSecond), ("result" + filenames.get(filecount)));
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


//
//        LogAnalyzer logA = new LogAnalyzer(args[0]);
//        logA.callIMPLogAnalyzer();

    }
}
