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
public class DownstreamAction implements Action {

    public DownstreamBuild dsb;

    public DownstreamAction(Build build) {

        this.dsb = new DownstreamBuild(build);
    }

    public DownstreamBuild getDownstreamBuild() {
        return this.dsb;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "downstreambuild";
    }

    public String getUrlName() {
        return null;
    }
}