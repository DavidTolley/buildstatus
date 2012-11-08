package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.*;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 9/25/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
@ExportedBean
public class BuildStatusAction implements Action {
    private AbstractBuild parentBuild;
    public List<DownstreamBuild> dsbs;

    public BuildStatusAction(AbstractBuild<?, ?> build) {
        this.parentBuild = build;
        dsbs = new ArrayList<DownstreamBuild>();

        for (Action action : build.getActions()) {

            if (action.getClass().equals(DownstreamAction.class)) {
                DownstreamAction dsa = (DownstreamAction) action;
                dsbs.add(dsa.getDownstreamBuild());
            }
        }
    }

    public List<DownstreamBuild> getDownstreamBuilds() {
        return dsbs;
    }

    @Exported
    public ParentBuild getParentBuild() {
        return new ParentBuild(parentBuild.getProject().getName(), parentBuild.getNumber(), getDownstreamBuilds());
    }

    public String getParentDuration() {
        return parentBuild.getDurationString();
    }

    public String getIconFileName() {
        return "search.png";
    }

    public String getDisplayName() {
        return "Build Status";
    }

    public String getUrlName() {
        return "buildstatus";
    }

    public String getParentBuildName() {
        return parentBuild.getProject().getName();
    }

    public int getParentBuildNumber() {
        return parentBuild.getNumber();
    }

    public boolean isParentRunning() {
        if (parentBuild.isBuilding())
            return true;
        return false;
    }

    public String getBuildStatus() {
        if (this.isParentRunning())
            return "RUNNING";

        if (this.isParentRunning())
            return "RUNNING";

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
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

        if (envVars != null && envVars.get("CI_INFRASTRUCTURE_ERROR") != null) {

            if (envVars.get("CI_INFRASTRUCTURE_ERROR").equals("true")) {
                return "CIInfrastructureError";
            }
        }

        if (envVars != null && envVars.get("ASSET_GENERATION_ERROR") != null) {

            if (envVars.get("ASSET_GENERATION_ERROR").equals("true")) {
                return "AssetGenerationError";
            }
        }

        return this.parentBuild.getResult().toString();
    }

    public boolean getPreReceiveHookError() {

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("PRE_RECEIVE_HOOK_ERROR") != null && envVars.get("PRE_RECEIVE_HOOK_ERROR").equals("true"))
            return true;

        return false;
    }

    public boolean getPushErrors() {

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("PUSH_ERROR") != null && envVars.get("PUSH_ERROR").equals("true") && this.parentBuild.getResult().equals(Result.FAILURE))
            return true;

        return false;
    }

    public boolean getMergeConflicts() {

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("MERGE_CONFLICT") != null && envVars.get("MERGE_CONFLICT").equals("true"))
            return true;

        return false;
    }

    public boolean getAssetGenerationError() {

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("ASSET_GENERATION_ERROR") != null && envVars.get("ASSET_GENERATION_ERROR").equals("true"))
            return true;

        return false;
    }

    public boolean getCIInfraError() {

        EnvVars envVars = null;

        try {
            envVars = this.parentBuild.getEnvironment();
        } catch (Exception e) {

        }

        if (envVars != null && envVars.get("CI_INFRASTRUCTURE_ERROR") != null && envVars.get("CI_INFRASTRUCTURE_ERROR").equals("true"))
            return true;

        return false;
    }

    public String getBuildConsoleURL() {
        Jenkins jenkins = Jenkins.getInstance();
        return jenkins.getRootUrl() + this.parentBuild.getUrl() + "console";
    }

    public String getBuildURL() {
        Jenkins jenkins = Jenkins.getInstance();
        return jenkins.getRootUrl() + this.parentBuild.getUrl();
    }

    public AbstractBuild<?, ?> getBuild() {
        return parentBuild;
    }

}