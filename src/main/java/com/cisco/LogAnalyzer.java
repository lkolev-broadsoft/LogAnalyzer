package com.cisco;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogAnalyzer {

    protected String inputArchiveFilePath;

    protected Path archivePath;

    protected ZIPedLogsFactory zipLogs = new ZIPedLogsFactory();

    protected TARGZedLogsFactory tarGZLogs = new TARGZedLogsFactory();

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
                zipLogs.openZIPFile(inputArchiveFilePath, "stats");
            }
            else if(m.group().equals(m.group(2))){
                tarGZLogs.openTarGZFile(inputArchiveFilePath,"IMPLog");
            }
            else {
                System.out.println("Unsupported input type, only zip and tar.gz files are accepted.");
            }
        }
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

}