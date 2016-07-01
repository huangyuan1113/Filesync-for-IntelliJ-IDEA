package org.filesync.idea.plugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import org.filesync.idea.plugin.files.SynchronizerListener;
import org.jetbrains.annotations.NotNull;

public class FileSyncPlugin implements ProjectComponent, ApplicationComponent {

    private static final String PLUGIN_NAME = "FileSyncPlugin";
    public static final String PLUGIN_DISPLAY_NAME = "File Synchronization";

    private SynchronizerListener listener;
    private MessageBusConnection connection;
    private Project project;

    public FileSyncPlugin(Project project) {
        this.project = project;
        this.connection = ApplicationManager.getApplication().getMessageBus().connect();
        this.listener = new SynchronizerListener();
    }

    public void initComponent() {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, listener);
    }

    public void disposeComponent() {
        connection.disconnect();
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return PLUGIN_NAME + "Component";
    }

}
