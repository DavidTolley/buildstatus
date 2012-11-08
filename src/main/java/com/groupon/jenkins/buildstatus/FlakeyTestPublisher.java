package com.groupon.jenkins.buildstatus;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.AbstractTestResultAction;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 10/30/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class FlakeyTestPublisher extends Recorder {

    AbstractBuild build;
    ArrayList<FailureJSON> failures;

    @DataBoundConstructor
    public FlakeyTestPublisher(AbstractBuild build) {

    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        try {
            this.build = build;

            ArrayList<FailureJSON> failureList = getDetailedErrors();
            if (failureList.size() <= 0)
                return true;

            listener.getLogger().println("Flakey tests found. Marking build as Unstable");
            if (build.getResult().equals(Result.SUCCESS))
                build.setResult(Result.UNSTABLE);
        } catch (Exception e) {
            listener.getLogger().println("Error finding flakey tests. Skipping this step.");
        }

        return true;
    }

    public ArrayList<FailureJSON> getDetailedErrors() {

        ParseJSON parseJSON = new ParseJSON(build);
        failures = parseJSON.parse();
        return failures;
    }

    public boolean currentlyRunning() {

        if (this.build.isBuilding())
            return true;
        return false;
    }

    @Extension
    public static class FlakeyTestPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Enable Flakey Test Finder";
        }
    }

    @Override
    public FlakeyTestPublisherDescriptor getDescriptor() {
        return (FlakeyTestPublisherDescriptor) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}