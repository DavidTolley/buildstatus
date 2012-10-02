package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.*;
import jenkins.model.Jenkins;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 9/25/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
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

    public String getParentDuration() {
        return parentBuild.getDurationString();
    }

    public String getIconFileName() {
        return null;
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
        return this.parentBuild.getResult().toString();
    }

}