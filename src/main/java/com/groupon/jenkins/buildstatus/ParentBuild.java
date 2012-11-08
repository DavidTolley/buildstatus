package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.AbstractProject;
import hudson.model.Api;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

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
public class ParentBuild {

    public String jobName;
    public int buildNumber;
    public Run buildRun;
    public AbstractProject project;
    public List<DownstreamBuild> downstreamBuilds;

    public ParentBuild(String project, int buildNumber, List<DownstreamBuild> dsbs) {

        this.jobName = project;
        this.buildNumber = buildNumber;
        this.downstreamBuilds = dsbs;
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
    public List<DownstreamBuild> getDownstreamBuilds() {
        return this.downstreamBuilds;
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
        this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
        this.buildRun = this.project.getBuildByNumber(this.buildNumber);

        return this.buildRun.getDurationString();
    }

    @Exported
    public String getBuildStatus() {
        this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
        this.buildRun = this.project.getBuildByNumber(this.buildNumber);
        if (this.currentlyRunning())
            return "RUNNING";

        EnvVars envVars = null;

        try {
            envVars = this.buildRun.getEnvironment();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (envVars != null && envVars.get("PUSH_ERROR") != null) {

            if (envVars.get("PUSH_ERROR").equals("true")) {
                return "PushError";
            }
        }

        if (envVars != null && envVars.get("MERGE_CONFLICT") != null) {

            if (envVars.get("MERGE_CONFLICT").equals("true")) {
                return "MergeConflict";
            }
        }

        if (envVars != null && envVars.get("PRE_RECEIVE_HOOK_ERROR") != null) {

            if (envVars.get("PRE_RECEIVE_HOOK_ERROR").equals("true")) {
                return "PreReceiveHookError";
            }
        }

        return this.buildRun.getResult().toString();
    }

    public boolean getPushErrors() {

        EnvVars envVars = null;

        try {
            envVars = this.buildRun.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("PUSH_ERROR") != null && envVars.get("PUSH_ERROR").equals("true") && this.buildRun.getResult().equals(Result.FAILURE))
            return true;

        return false;
    }

    public boolean getMergeConflicts() {

        EnvVars envVars = null;

        try {
            envVars = this.buildRun.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("MERGE_CONFLICT") != null && envVars.get("MERGE_CONFLICT").equals("true"))
            return true;

        return false;
    }

    public String getBuildConsoleURL() {
        this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
        this.buildRun = this.project.getBuildByNumber(this.buildNumber);

        return this.getBuildURL() + "console";
    }

    @Exported
    public String getBuildURL() {
        this.project = (AbstractProject) Jenkins.getInstance().getItem(this.jobName);
        this.buildRun = this.project.getBuildByNumber(this.buildNumber);

        Jenkins jenkins = Jenkins.getInstance();
        return jenkins.getRootUrl() + this.buildRun.getUrl();
    }

    public Api getApi() {
        return new Api(this);
    }
}
