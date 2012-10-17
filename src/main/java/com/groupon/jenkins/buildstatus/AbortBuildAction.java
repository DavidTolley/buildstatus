package com.groupon.jenkins.buildstatus;

import hudson.model.*;
import hudson.util.RunList;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 10/15/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbortBuildAction implements Action, Saveable {
    private AbstractBuild parentBuild;
    public List<DownstreamBuild> dsbs;

    public AbortBuildAction(AbstractBuild<?, ?> build) {
        this.parentBuild = build;
    }

    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws ServletException,
            IOException, InterruptedException {

        dsbs = new ArrayList<DownstreamBuild>();

        for (Action action : parentBuild.getActions()) {

            if (action.getClass().equals(DownstreamAction.class)) {
                DownstreamAction dsa = (DownstreamAction) action;
                dsbs.add(dsa.getDownstreamBuild());
            }
        }

        try {
            parentBuild.getExecutor().interrupt(Result.ABORTED);
        } catch (Exception e) {

        }

        for (DownstreamBuild dsBuild : dsbs) {
            dsBuild.abortBuild(req, rsp);
            forceChangeStatus();
            save();
        }

        rsp.sendRedirect2("../");
    }


    public String getIconFileName() {
        return "search.png";
    }

    public String getDisplayName() {
        return "Abort Build";
    }

    public String getUrlName() {
        return "abortBuild";
    }

    public AbstractBuild getBuild() {
        return parentBuild;
    }

    private void forceChangeStatus() {
        try {
            Field resultField = Run.class.getDeclaredField("result");
            resultField.setAccessible(true);
            resultField.set(parentBuild, Result.ABORTED);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws IOException {
        parentBuild.save();
    }
}
