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

        for(Action action: build.getActions()){

            if(action.getClass().equals(DownstreamAction.class)){
                DownstreamAction dsa = (DownstreamAction) action;
                dsbs.add(dsa.getDownstreamBuild());
            }
        }
    }

    public List<DownstreamBuild> getDownstreamBuilds() {
        return dsbs;
    }

    @Exported
    public ParentBuild getParentBuild(){
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
        if(this.isParentRunning())
            return "RUNNING";
        return this.parentBuild.getResult().toString();
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