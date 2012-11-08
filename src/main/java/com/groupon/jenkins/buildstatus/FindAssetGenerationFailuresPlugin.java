package com.groupon.jenkins.buildstatus;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 11/8/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class FindAssetGenerationFailuresPlugin extends Builder {

    @DataBoundConstructor
    public FindAssetGenerationFailuresPlugin() {

    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {

        try {
            listener.getLogger().println("Parsing for asset generation failures");

            StringBuilder sbLogFile = new StringBuilder();
            Scanner scanner = new Scanner(build.getLogFile());
            Map<String, String> descriptionVars = new HashMap<String, String>();

            boolean runTests = true;
            boolean continueParsing = true;

            while (scanner.hasNextLine() && continueParsing == true) {
                String line = scanner.nextLine();

                if (line.toLowerCase().contains("error public")) {
                    listener.getLogger().println("Error generating assets: Failing the build");
                    build.setResult(Result.FAILURE);
                    runTests = false;
                    continueParsing = false;

                }

            }

            if(runTests == true)
                descriptionVars.put("ASSET_GENERATION_ERROR", "true");
                build.addAction(new DescriptionBaseAction(descriptionVars));

        } catch (Exception e) {

        }

        return true;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Parse for asset generation errors";
        }
    }
}
