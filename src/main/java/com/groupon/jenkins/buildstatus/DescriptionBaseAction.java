package com.groupon.jenkins.buildstatus;

import hudson.EnvVars;
import hudson.model.AbstractBuild;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 10/31/12
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DescriptionBaseAction extends EnvironmentBaseAction {

    private Map<String, String> descriptionVars;
    public DescriptionBaseAction(Map<String, String> descriptionVars) {

        this.descriptionVars = descriptionVars;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {

        env.put("MERGE_CONFLICT", descriptionVars.get("MERGE_CONFLICT"));
        env.put("PUSH_ERROR",descriptionVars.get("PUSH_ERROR"));
        env.put("PRE_RECEIVE_HOOK_ERROR",descriptionVars.get("PRE_RECEIVE_HOOK_ERROR"));
        env.put("CI_INFRASTRUCTURE_ERROR", descriptionVars.get("CI_INFRASTRUCTURE_ERROR"));
        env.put("ASSET_GENERATION_ERROR", descriptionVars.get(("ASSET_GENERATION_ERROR")));
    }
}
