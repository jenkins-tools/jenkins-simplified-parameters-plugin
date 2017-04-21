package org.jenkinsci.plugins.post;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created by sunjoo on 21/04/2017.
 */
@Extension
public class SunjooPostBuilderDescriptor extends BuildWrapperDescriptor {
    private String name;
    private String email;

    public SunjooPostBuilderDescriptor(){
        super(SunjooPostBuilder.class);
        load();
    }

    public boolean configure(final StaplerRequest request, final JSONObject formData) {
        request.bindJSON(this, formData);
        save();
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Email test";
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
        return true;
    }
}
