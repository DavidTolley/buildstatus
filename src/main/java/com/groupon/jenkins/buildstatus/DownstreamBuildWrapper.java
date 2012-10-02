package com.groupon.jenkins.buildstatus;

import com.groupon.jenkins.buildstatus.DownstreamAction;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.scm.SCM;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("unchecked")
@Extension
public class DownstreamBuildWrapper extends BuildWrapper {

    @DataBoundConstructor
    public DownstreamBuildWrapper() {

    }

    @Override
    public void preCheckout(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        CauseAction ca = build.getAction(CauseAction.class);
        AbstractBuild upBuild = null;

        for (Cause cause : ca.getCauses()) {
            if (cause instanceof Cause.UpstreamCause) {
                Cause.UpstreamCause upcause = (Cause.UpstreamCause) cause;
                String upProjectName = upcause.getUpstreamProject();
                int buildNumber = upcause.getUpstreamBuild();
                AbstractProject project = Hudson.getInstance().getItemByFullName(upProjectName, AbstractProject.class);
                upBuild = (AbstractBuild) project.getBuildByNumber(buildNumber);
            }

            listener.getLogger().println("Setting " + build.getDisplayName() + "as a downstream build of: " + upBuild.getProject().toString() + " " + upBuild.getDisplayName());
            upBuild.addAction(new DownstreamAction(build.getProject(), build.getNumber()));
            upBuild.save();
        }
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        return new Environment() {
        };
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Downstream Build";
        }
    }
}
