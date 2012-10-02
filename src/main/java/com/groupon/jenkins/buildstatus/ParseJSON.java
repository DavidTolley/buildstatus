package com.groupon.jenkins.buildstatus;

import hudson.FilePath;
import hudson.model.Build;
import hudson.model.Run;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 9/27/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseJSON {

    public FailureJSON[] failureJSONArray;
    public final String fileToParse;
    private final Run run;

    public ParseJSON(Run run) {
        fileToParse = null;
        this.run = run;
    }

    public ArrayList<FailureJSON> parse() {

        StringBuilder stringBuilder = new StringBuilder();
        Map<String, File> mapOfFiles = new HashMap<String, File>();
        Map<String, File> mapOfTmpArtifacts = new HashMap<String, File>();
        final List<Run.Artifact> ws = run.getArtifacts();
        for (Run.Artifact artifacts : ws) {

            if (artifacts.getFileName().equals("json_output.log")) {

                try {
                    Scanner scanner = new Scanner(artifacts.getFile());
                    while(scanner.hasNextLine()){
                        stringBuilder.append(scanner.nextLine());
                    }
                } catch (Exception e) {

                }
            }
        }

        JSONParser parser = new JSONParser();

        List<FailureJSON> listOfFailure = new ArrayList<FailureJSON>();

        try {
            Object obj = parser.parse(stringBuilder.toString());
            org.json.simple.JSONObject jsonFile = (org.json.simple.JSONObject) obj;

            if (jsonFile.get("failures") != null) {

                org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) jsonFile.get("failures");

                org.json.simple.JSONObject[] failures = new org.json.simple.JSONObject[jsonArray.size()];

                if (jsonArray.size() > 0) {
                    for (int pos = 0; pos < jsonArray.size(); pos++) {

                        failures[pos] = (org.json.simple.JSONObject) jsonArray.get(pos);

                        String name = (String) failures[pos].get("name");

                        String testName = new String();
                        if (failures[pos].get("test_name") != null) {
                            testName = (String) failures[pos].get("test_name");
                        } else {
                            testName = "Test";
                        }

                        String message = (String) failures[pos].get("message");

                        String line = (String) failures[pos].get("line");

                        org.json.simple.JSONArray backTrace = new org.json.simple.JSONArray();

                        backTrace = (org.json.simple.JSONArray) failures[pos].get("backtrace");
                        Iterator<String> iterator = backTrace.iterator();

                        String[] backTraceText = new String[backTrace.size()];
                        int btPos = 0;
                        while (iterator.hasNext()) {
                            backTraceText[btPos] = iterator.next();
                            btPos = btPos + 1;
                        }

                        for (int position = 0; position < btPos; position++) {

                            String currentLine = backTraceText[position];

                            backTraceText[position] = currentLine;
                        }

                        String file = (String) failures[pos].get("file");

                        listOfFailure.add(new FailureJSON(name, message, line, backTraceText, file, testName));
                    }
                }

            }
        } catch (Exception e) {

        }
        return (ArrayList) listOfFailure;
    }
}
