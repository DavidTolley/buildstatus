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
public class AssetGenerationBaseAction extends EnvironmentBaseAction {

    private Map<String, String> descriptionVars;
    public AssetGenerationBaseAction(Map<String, String> descriptionVars) {

        this.descriptionVars = descriptionVars;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {

        env.put("RUN_TESTS", descriptionVars.get("RUN_TESTS"));
    }
}
