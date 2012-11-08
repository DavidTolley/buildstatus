package com.groupon.jenkins.buildstatus;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
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
public class GitMergePushErrorPublisher extends Recorder {

    @DataBoundConstructor
    public GitMergePushErrorPublisher(AbstractBuild build) {

    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();

        logger.println("Parsing build for merge, push, and git-hook issues.");

        boolean pushErrors = false;
        boolean mergeConflicts = false;
        boolean preReceiveHook = false;
        boolean ciInfrastructureError = false;
        boolean assetGenerationError = false;

        StringBuilder sbLogFile = new StringBuilder();
        Scanner scanner = new Scanner(build.getLogFile());

        boolean continueParsing = true;

        while (scanner.hasNextLine() && continueParsing == true) {
            String line = scanner.nextLine();

            if (line.toLowerCase().contains("pre-receive hook declined")) {
                preReceiveHook = true;
                continueParsing = false;
            } else if (line.toLowerCase().contains("error: failed to push some refs to") && build.getResult().equals(Result.FAILURE) && preReceiveHook == false) {
                pushErrors = true;
                continueParsing = false;
            } else if (line.toLowerCase().contains("failed to merge in the changes.")) {
                mergeConflicts = true;
                continueParsing = false;
            } else if (line.toLowerCase().contains("fatal: null") && build.getResult().equals(Result.FAILURE) && preReceiveHook == false && mergeConflicts == false) {
                ciInfrastructureError = true;
                continueParsing = false;
            } else if (line.toLowerCase().contains("fatal: sleep interrupted") && build.getResult().equals(Result.FAILURE) && preReceiveHook == false && mergeConflicts == false) {
                ciInfrastructureError = true;
                continueParsing = false;
            } else if (line.toLowerCase().contains("error public") && build.getResult().equals(Result.FAILURE) && preReceiveHook == false && mergeConflicts == false && ciInfrastructureError == false) {
                assetGenerationError = true;
                continueParsing = false;
            }

        }


        if (pushErrors) {

            logger.println("Failed to push current branch into master.");
            logger.println("Master's code has changed since your tests started to run.");
            build.addAction(new GitMergePushErrorAction(build, false, true, false, false, false));
            logger.println("Attempting to re-queue this build.");

            ParametersDefinitionProperty paramDefProp = build.getProject().getProperty(
                    ParametersDefinitionProperty.class);
            List<ParameterValue> values = new ArrayList<ParameterValue>();
            ParametersAction paramAction = build.getAction(ParametersAction.class);

            values.addAll(paramAction.getParameters());

            int pos = 0;
            int rebuiltPos = -1;
            for (ParameterValue paramValue : values) {

                if (paramValue.getName().equals("REBUILT_JOB"))
                    rebuiltPos = pos;
                pos++;
            }
            if (rebuiltPos != -1)
                values.remove(rebuiltPos);
            values.add(new StringParameterValue("REBUILT_JOB", "true"));

            try {
                Hudson.getInstance().getQueue().schedule(build.getProject(), 0, new ParametersAction(values),
                        new CauseAction(new Cause.UpstreamCause(build)));
            } catch (Exception e) {

                logger.println("Error attempting to re-queue your build.");
                logger.println("Please re-push your code to restart the verification process");
            }

            logger.println("Successfully re-queued this build.");

        } else if (ciInfrastructureError) {

            logger.println("CI has experience an error outside of your code.");
            build.addAction(new GitMergePushErrorAction(build, false, false, false, true, false));
            logger.println("Attempting to re-queue this build.");

            ParametersDefinitionProperty paramDefProp = build.getProject().getProperty(
                    ParametersDefinitionProperty.class);
            List<ParameterValue> values = new ArrayList<ParameterValue>();
            ParametersAction paramAction = build.getAction(ParametersAction.class);

            values.addAll(paramAction.getParameters());

            int pos = 0;
            int rebuiltPos = -1;
            for (ParameterValue paramValue : values) {

                if (paramValue.getName().equals("REBUILT_JOB"))
                    rebuiltPos = pos;
                pos++;
            }
            if (rebuiltPos != -1)
                values.remove(rebuiltPos);
            values.add(new StringParameterValue("REBUILT_JOB", "true"));

            try {
                Hudson.getInstance().getQueue().schedule(build.getProject(), 0, new ParametersAction(values),
                        new CauseAction(new Cause.UpstreamCause(build)));
            } catch (Exception e) {

                logger.println("Error attempting to re-queue your build.");
                logger.println("Please re-push your code to restart the verification process");
            }

            logger.println("Successfully re-queued this build.");

        } else if (mergeConflicts) {

            logger.println("Merge conflicts found while merging current branch with master.");
            logger.println("Please merge master into your local repo, fix merge conflicts, and push once those are resolved.");
            build.addAction(new GitMergePushErrorAction(build, true, false, false, false, false));
        } else if (preReceiveHook) {

            logger.println("Pre-receive hook failed.");
            logger.println("Please check console log for failure details.");
            build.addAction(new GitMergePushErrorAction(build, false, false, true, false, false));
        } else if (assetGenerationError) {

            logger.println("Failure Generating Assets.");
            logger.println("Please check the log, fix locally and then re-push.");
            build.addAction(new GitMergePushErrorAction(build, false, false, false, false, true));
        } else {

            logger.println("No issues found with merging/pushing your code.");
        }

        return true;
    }

    @Extension
    public static class GitMergePushErrorPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Check for merge and push issues";
        }
    }

    @Override
    public GitMergePushErrorPublisherDescriptor getDescriptor() {
        return (GitMergePushErrorPublisherDescriptor) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}