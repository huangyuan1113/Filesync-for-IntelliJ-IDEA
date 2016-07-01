package org.filesync.idea.plugin.files;

import com.intellij.history.LocalHistory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiManager;
import org.filesync.idea.plugin.settings.Project;
import org.filesync.idea.plugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.List;

public class SynchronizerListener implements BulkFileListener {

    private static final Logger LOGGER = Logger.getInstance(SynchronizerListener.class);

    private static boolean isValid(VFileEvent event) {
        final Object requestor = event.getRequestor();

        return requestor instanceof FileDocumentManager
                || requestor instanceof PsiManager
                || requestor == LocalHistory.VFS_EVENT_REQUESTOR
                || event.isFromRefresh();
    }

    private static boolean isInSettingsProjects(VFileEvent event) {
        for (Project project : Settings.getInstance().getProjects()) {
            if (Paths.get(event.getPath()).startsWith(Paths.get(project.getSource()))) {
                return true;
            }
        }

        return false;
    }

    public void before(@NotNull List<? extends VFileEvent> vFileEvents) {
    }

    public void after(@NotNull List<? extends VFileEvent> vFileEvents) {
        for (VFileEvent event : vFileEvents) {
            if (isValid(event) && isInSettingsProjects(event)) {
                LOGGER.debug("Launch synchronization...");
                Synchronyzer.sync();
                break;

            }
        }
    }

}
