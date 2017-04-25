package org.jenkinsci.plugins.post;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 * Sample {@link Builder}.
 * <p>
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link SunjooPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 * <p>
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class SunjooPublisher extends Publisher implements SimpleBuildStep {
    private final String name;
    private final String age;
    private final String config;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public SunjooPublisher(String name, String age, String config) {
        this.name = name;
        this.age = age;
        this.config = config;
    }

    /**
     * We'll use this from the {@code config.jelly}.
     */
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
    public String getConfig() {
        return config;
    }

    @Override
    public boolean prebuild(Build build, BuildListener listener) {
        System.out.println("Test: prebuilt");
        listener.getLogger().println(build.getResult());
        return true;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException{
        if (getDescriptor().getUseFrench())
            listener.getLogger().println("Bonjour, " + name + "!");
        else
            listener.getLogger().println("Hello, " + name + "!");
        listener.getLogger().println(workspace.getName());
        Result result = build.getResult();
        listener.getLogger().println(result);
        File r = build.getLogFile();
        BufferedReader br = new BufferedReader(new FileReader(r.getAbsolutePath()));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                listener.getLogger().println(line);
                String[] lines = line.split(" ");
                if (lines[0].equals("Summary:") && lines[lines.length - 1].equals("failed:") && lines[lines.length - 2].equals("task") ){
                    sb.append(line + "\n");
                    line = br.readLine();
                    lines = line.split(" ");
                    while(!lines[0].equals("Summary:"))
                    {
                        sb.append(line + "\n");
                        line = br.readLine();
                        listener.getLogger().println(line);
                        lines = line.split(" ");
                    }
                    break;
                }
                line = br.readLine();
            }
            String everything = sb.toString();
            build.setDescription("\n" + build.getDescription() + "\n" + everything);
        } catch(Exception e){
            build.setDescription("\n" + "ERROR: Can't find failed bb components");
        }finally {
            br.close();
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link SunjooPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * <p>
     * See {@code src/main/resources/hudson/plugins/hello_world/SunjooPublisher/*.jelly}
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p>
         * <p>
         * If you don't want fields to be persisted, use {@code transient}.
         */
        private boolean useFrench;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Say hello world";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         * <p>
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public boolean getUseFrench() {
            return useFrench;
        }
    }
}

