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
        return "Email test";
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
