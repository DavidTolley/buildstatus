package com.groupon.jenkins.buildstatus;

import hudson.FilePath;
import hudson.model.*;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.test.AbstractTestResultAction;
import jenkins.model.Jenkins;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 9/26/12
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class DownstreamBuild {

    public String jobName;
    public int buildNumber;
    public Run buildRun;
    public String[] failedTests;
    ArrayList<FailureJSON> failures;

    public DownstreamBuild(Build build) {

        this.jobName = build.getProject().getName();
        this.buildNumber = build.getNumber();
        this.buildRun = build;
    }

    public String getJobName() {
        return this.jobName;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public boolean currentlyRunning() {
        if (this.buildRun.isBuilding())
            return true;
        return false;
    }

    public String getRunDuration() {
        return this.buildRun.getDurationString();
    }

    public String getBuildStatus() {
        return this.buildRun.getResult().toString();
    }

    public ArrayList<FailureJSON> getDetailedErrors() {
        if (!this.currentlyRunning()) {
            ParseJSON parseJSON = new ParseJSON(buildRun);
            failures = parseJSON.parse();
            return failures;
        }
        return null;
    }

    public int getErrorCount() {

        if (!this.currentlyRunning()) {

            try {
                return getDetailedErrors().size();
            } catch (Exception e) {

            }
        }

        return -1;
    }
}
