package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 9/25/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuildStatusPlugin extends Builder {

    @DataBoundConstructor
    public BuildStatusPlugin() {

    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {

        listener.getLogger().println("Build Status page is now available");

        try {
            build.addAction(new BuildStatusAction(build));
        } catch (Exception e) {

        }

        return true;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Display Build Status page";
        }
    }
}
