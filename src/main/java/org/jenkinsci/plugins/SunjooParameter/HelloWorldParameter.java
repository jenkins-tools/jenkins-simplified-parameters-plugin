package org.jenkinsci.plugins.SunjooParameter;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//

public class HelloWorldParameter extends SimpleParameterDefinition {

    private String defaultValue;

    @DataBoundConstructor
    public HelloWorldParameter(String name, String defautValue, String description) {
        super(name, description);
        System.out.println("## check1");
        this.defaultValue = defautValue;
    }

    public HelloWorldParameter(String name, String defaultValue) {
        this(name, defaultValue, null);
        System.out.println("## check2");
    }


    @Override
    public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof StringParameterValue) {
            StringParameterValue value = (StringParameterValue) defaultValue;
            return new HelloWorldParameter(getName(), value.value, getDescription());
        } else {
            return this;
        }
    }

    @Exported
    public List<String> getChoices() {
        List<String> choices= new ArrayList<String>();
        choices.add("starfish/build-starfish");
        choices.add("starfish/meta-lg-webos-tv");
        choices.add("webos-pro/meta-lg-webos");
        choices.add("webos-pro/starfish-luna-surface-manager");
        return choices;
    }

    public String getDefaultValue(){
        return defaultValue;
    }

    @DataBoundSetter
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        StringParameterValue v = new StringParameterValue(getName(), defaultValue, getDescription());
        return v;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return "My String";
        }

        @Override
        public String getHelpFile() {
            return "/help/parameter/string.html";
        }
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    public ParameterValue createValue(String value) {
        return new StringParameterValue(getName(), value, getDescription());
    }
}
