package org.filesync.idea.plugin.files;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.filesync.idea.plugin.FileSyncPlugin;
import org.filesync.idea.plugin.settings.Project;
import org.filesync.idea.plugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Synchronyzer {

    private static final Logger LOGGER = Logger.getInstance(Synchronyzer.class);

    public static void sync() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        com.intellij.openapi.project.Project currentProject = DataKeys.PROJECT.getData(dataContext);

        Task.Backgroundable backgroundable = new Task.Backgroundable(currentProject, FileSyncPlugin.PLUGIN_DISPLAY_NAME) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                List<Project> projects = Settings.getInstance().getProjects();
                for (Project project : projects) {
                    progressIndicator.setText("Synchronizing " + project.getSource() + "...");
                    doSync(project);
                }
            }
        };
        backgroundable.queue();
    }

    public static void doSync(Project project) {
        Path source = Paths.get(project.getSource());
        Path target = source.resolveSibling(project.getTarget());
        String filter = project.getFilter();

        SourceCopier sourceCopier = new SourceCopier(source, target, filter);
        TargetCleaner targetCleaner = new TargetCleaner(source, target);

        try {
            Files.walkFileTree(source, sourceCopier);
            Files.walkFileTree(target, targetCleaner);
        } catch (IOException e) {
            LOGGER.error("Error during " + project.getSource() + " synchronization", e);
        }
    }

}
