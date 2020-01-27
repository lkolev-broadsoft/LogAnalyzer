package com.cisco;

public class Main {

//    public static List<String> getIMPFileNames(List <String> listOfEntries) {
//        List<String> filenames = new ArrayList<>();
//        String pattern = "(IMPLog)(.*)(.txt)";
//        Pattern r = Pattern.compile(pattern);
//        for(String entry : listOfEntries){
//            Matcher m = r.matcher(entry);
//            if(m.find()){
//                filenames.add(m.group());
//            }
//        }
//        return filenames;
//    }

//    public static List<String> getDBFileNames(List <String> listOfEntries) {
//        List<String> filenames = new ArrayList<>();
//        String pattern = "(dbConnectorLog)(.*)(txt)";
//        Pattern r = Pattern.compile(pattern);
//        for(String entry : listOfEntries){
//            Matcher m = r.matcher(entry);
//            if(m.find()){
//                filenames.add(m.group());
//            }
//        }
//        return filenames;
//    }

//    public static List<String> getGateWayFileNames(List <String> listOfEntries) {
//        List<String> filenames = new ArrayList<>();
//        String pattern = "(GateWayLog)(.*)(txt)";
//        Pattern r = Pattern.compile(pattern);
//        for(String entry : listOfEntries){
//            Matcher m = r.matcher(entry);
//            if(m.find()){
//                filenames.add(m.group());
//            }
//        }
//        return filenames;
//    }

    public static void main(String[] args) {
        LogAnalyzer logA = new LogAnalyzer(args[0]);
        logA.callIMPLogAnalyzer();

    }
}
