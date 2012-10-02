package com.groupon.jenkins.buildstatus;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Created with IntelliJ IDEA.
 * User: dtolley
 * Date: 7/17/12
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
@ExportedBean
public class FailureJSON {

    private String failureName;
    private String failureMessage;
    private String failureLine;
    private String [] failureBacktrace;
    private String file;
    private String testName;

    public FailureJSON(String failureName, String failureMessage, String failureLine, String [] failureBacktrace, String file, String testName){

        this.failureName = failureName;
        this.failureMessage = failureMessage;
        this.failureLine = failureLine;
        this.failureBacktrace = failureBacktrace;
        this.file = file;
        this.testName = testName;

    }

    @Exported
    public String getFailureName(){

        return failureName;
    }

    @Exported
    public String getFailureMessage(){

        return failureMessage;
    }

    @Exported
    public String getFailureLine(){

        return failureLine;
    }

    @Exported
    public String getFailureBacktrace(){
        StringBuilder bt = new StringBuilder();
        for(int pos = 0; pos < this.failureBacktrace.length; pos++){
            bt.append(this.failureBacktrace[pos] + "<br/>");
        }

        return bt.toString();
    }

    @Exported
    public String getFile(){
        return file;
    }

    @Exported
    public String getTestName(){

        return testName;
    }
}
