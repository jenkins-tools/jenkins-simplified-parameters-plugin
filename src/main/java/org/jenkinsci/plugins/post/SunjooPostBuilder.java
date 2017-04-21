package org.jenkinsci.plugins.post;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by sunjoo on 21/04/2017.
 */
public class SunjooPostBuilder extends BuildWrapper{
    final String name;
    final String email;

    @DataBoundConstructor
    public SunjooPostBuilder(final String name, final String email){
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

    private boolean runTearDown(final AbstractBuild build, final BuildListener listener) {
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Build: " + build.getHudsonVersion());
        return true;
    }

    @Override
    public SunjooPostBuilderDescriptor getDescriptor(){
        return Hudson.getInstance().getDescriptorByType(SunjooPostBuilderDescriptor.class);
    }
}
