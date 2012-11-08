package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 10/30/12
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
@ExportedBean
public class GitMergePushErrorAction implements Action {

    private AbstractBuild build;
    private boolean mergeConflicts;
    private boolean pushErrors;
    private boolean jobRebuilt;
    private boolean preReceiveHookError;
    private boolean ciInfrastructureError;
    private boolean assetGenerationError;

    public GitMergePushErrorAction(AbstractBuild build, boolean mergeConflicts, boolean pushErrors, boolean preReceiveHookError, boolean ciInfrastructureError, boolean assetGenerationError) {

        this.build = build;
        this.mergeConflicts = mergeConflicts;
        this.pushErrors = pushErrors;
        this.preReceiveHookError = preReceiveHookError;
        this.ciInfrastructureError = ciInfrastructureError;
        this.assetGenerationError = assetGenerationError;

        try {
            EnvVars envVars = build.getEnvironment();
            if (envVars.get("REBUILT_JOB") != null) {

                if (envVars.get("REBUILT_JOB").equals("true")) {
                    this.jobRebuilt = true;
                } else {
                    this.jobRebuilt = false;
                }

            } else {
                this.jobRebuilt = false;
            }

            Map<String, String> descriptionVars = new HashMap<String, String>();

            if (mergeConflicts)
                descriptionVars.put("MERGE_CONFLICT", "true");
            else
                descriptionVars.put("MERGE_CONFLICT", "false");

            if(pushErrors)
                descriptionVars.put("PUSH_ERROR", "true");
            else
                descriptionVars.put("PUSH_ERROR", "false");

            if(preReceiveHookError)
                descriptionVars.put("PRE_RECEIVE_HOOK_ERROR", "true");
            else
                descriptionVars.put("PRE_RECEIVE_HOOK_ERROR", "false");

            if(ciInfrastructureError)
                descriptionVars.put("CI_INFRASTRUCTURE_ERROR", "true");
            else
                descriptionVars.put("CI_INFRASTRUCTURE_ERROR", "false");

            if(assetGenerationError)
                descriptionVars.put("ASSET_GENERATION_ERROR", "true");
            else
                descriptionVars.put("ASSET_GENERATION_ERROR", "false");

            build.addAction(new DescriptionBaseAction(descriptionVars));

        } catch (Exception e) {
            System.out.println("Error retrieving REBUILT_JOB env variable");
        }

    }

    @Exported
    public boolean getJobRebuilt() {
        return this.jobRebuilt;
    }

    @Exported
    public boolean getAssetGenerationError(){
        return this.assetGenerationError;
    }

    @Exported
    public boolean getMergeConflicts() {
        return this.mergeConflicts;
    }

    @Exported
    public boolean getPreReceiveHookError() {
        return this.preReceiveHookError;
    }

    @Exported
    public boolean getPushErrors() {
        return this.pushErrors;
    }

    @Exported
    public boolean getCIInfraError() {
        return this.ciInfrastructureError;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "gitmergepusherrors";
    }

    public String getUrlName() {
        return null;
    }
}