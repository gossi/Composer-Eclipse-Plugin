package com.dubture.composer.core.job;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pex.core.launch.ConsoleResponseHandler;
import org.pex.core.launch.DefaultExecutableLauncher;

import com.dubture.composer.core.CorePlugin;

abstract public class ComposerJob extends Job
{
    protected String composer;
    
    protected static final IStatus ERROR_STATUS = new Status(Status.ERROR, CorePlugin.ID, 
            "Error installing composer dependencies, see log for details");

    public ComposerJob(String name)
    {
        super(name);
    }

    protected void execute(String argument) throws IOException, InterruptedException
    {
        DefaultExecutableLauncher launcher = new DefaultExecutableLauncher();
        String[] arg = new String[]{argument};
        launcher.launch(composer, arg, new ConsoleResponseHandler());

    }
}