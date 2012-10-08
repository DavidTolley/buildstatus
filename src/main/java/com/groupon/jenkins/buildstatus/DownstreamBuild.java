package com.groupon.jenkins.buildstatus;

import hudson.FilePath;
import hudson.model.*;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.test.AbstractTestResultAction;
import jenkins.model.Jenkins;
import org.apache.tools.ant.DirectoryScanner;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

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
@ExportedBean
public class DownstreamBuild {

    public String jobName;
    public int buildNumber;
    public Run buildRun;
    public AbstractProject project;
    public String[] failedTests;
    ArrayList<FailureJSON> failures;

    public DownstreamBuild(String project, int buildNumber) {

        this.jobName = project;
        this.buildNumber = buildNumber;
    }

    @Exported
    public String getJobName() {
        return this.jobName;
    }

    @Exported
    public int getBuildNumber() {
        return this.buildNumber;
    }

    @Exported
    public boolean currentlyRunning() {
        this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
        this.buildRun = this.project.getBuildByNumber(this.buildNumber);

        if (this.buildRun.isBuilding())
            return true;
        return false;
    }

    @Exported
    public String getRunDuration() {
        if (this.buildRun == null) {
            this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
            this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        }

        return this.buildRun.getDurationString();
    }

    @Exported
    public String getBuildStatus() {
        if (this.buildRun == null) {
            this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
            this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        }

        if(this.currentlyRunning())
            return "RUNNING";
        return this.buildRun.getResult().toString();
    }

    public String getBuildConsoleURL() {
        if (this.buildRun == null) {
            this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
            this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        }

        return this.getBuildURL() + "console";
    }

    @Exported
    public String getBuildURL() {
        if (this.buildRun == null) {
            this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
            this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        }

        Jenkins jenkins = Jenkins.getInstance();
        return jenkins.getRootUrl() + this.buildRun.getUrl();
    }

    public ArrayList<FailureJSON> getDetailedErrors() {
        if (this.buildRun == null) {
            this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
            this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        }

        if (!this.currentlyRunning()) {
            ParseJSON parseJSON = new ParseJSON(buildRun);
            failures = parseJSON.parse();
            return failures;
        }
        return null;
    }

    @Exported
    public int getErrorCount() {

        if (!this.currentlyRunning()) {

            try {
                if (this.getBuildStatus().equals(Result.SUCCESS)) {
                    return 0;
                } else {
                    return getDetailedErrors().size();
                }
            } catch (Exception e) {

            }
        }

        return -1;
    }
}
