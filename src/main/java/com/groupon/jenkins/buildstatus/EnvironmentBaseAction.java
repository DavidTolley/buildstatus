package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 10/31/12
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class EnvironmentBaseAction implements EnvironmentContributingAction {
    public abstract void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env);

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }
}
