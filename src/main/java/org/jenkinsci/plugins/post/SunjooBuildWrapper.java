package org.jenkinsci.plugins.post;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * Created by sunjoo on 21/04/2017.
 */
public class SunjooBuildWrapper extends BuildWrapper{
    final String name;
    final String email;

    @DataBoundConstructor
    public SunjooBuildWrapper(final String name, final String email){
        this.name = name;
        this.email = email;
    }

    public String getName() { return name;}

    public String getEmail() { return email;}

    @Override
    public Environment setUp(final AbstractBuild build, final Launcher launcer, final BuildListener listener){
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
//                return super.tearDown(build, listener);
                return runTearDown(build, listener);
            }

        };
    }

    @Override
    public void  preCheckout(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException{
        listener.getLogger().println("Pre checkout: " + getDescriptor().getEmail());
    }

    private boolean runTearDown(final AbstractBuild build, final BuildListener listener) throws  IOException {
        listener.getLogger().println("Name: " + name);
        listener.getLogger().println("Email: " + email);
        listener.getLogger().println("Build: " + build.getHudsonVersion());
        Result result = build.getResult();
        listener.getLogger().println(result);
        build.setDescription("runTearDown\n\n");
        return true;
    }

    @Override
    public SunjooBuildWrapperDescriptor getDescriptor(){
        return Hudson.getInstance().getDescriptorByType(SunjooBuildWrapperDescriptor.class);
    }

    @Extension
    public static class SunjooBuildWrapperDescriptor extends BuildWrapperDescriptor {
        private String name;
        private String email;

        public SunjooBuildWrapperDescriptor(){
            super(SunjooBuildWrapper.class);
//            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            name = formData.getString("name");
            email = formData.getString("email");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        @Override
        public String getDisplayName() {
            return "Check Build Wrapper";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }

        public String getName(){
            return name;
        }
        public String getEmail(){
            return email;
        }
    }
}
