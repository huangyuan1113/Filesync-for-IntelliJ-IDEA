package org.filesync.idea.plugin.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "ConfigPersistentService",
        storages = {
                @Storage(file = StoragePathMacros.APP_CONFIG + "/filesync_settings.xml")
        }
)
public class Settings implements PersistentStateComponent<Element> {

    private static final Logger LOGGER = Logger.getInstance(Settings.class);

    private static final String ELEMENT_ROOT = "ROOT";
    private static final String ELEMENT_PROJECTS = "PROJECTS";
    private static final String ELEMENT_PROJECT = "PROJECT";
    private static final String ELEMENT_PRJ_SOURCE = "SOURCE";
    private static final String ELEMENT_PRJ_TARGET = "TARGET";
    private static final String ELEMENT_PRJ_FILTER = "FILTER";

    private List<Project> projects = new ArrayList<Project>();

    public static Settings getInstance() {
        return ServiceManager.getService(Settings.class);
    }

    private static Element createElement(Project project) {
        Element element = new Element(ELEMENT_PROJECT);
        element.setAttribute(ELEMENT_PRJ_SOURCE, project.getSource() == null ? "" : project.getSource());
        element.setAttribute(ELEMENT_PRJ_TARGET, project.getTarget() == null ? "" : project.getTarget());
        element.setAttribute(ELEMENT_PRJ_FILTER, project.getFilter() == null ? "" : project.getFilter());

        return element;
    }

    public Element getState() {
        LOGGER.info("Saving settings");

        final Element eltRoot = new Element(ELEMENT_ROOT);

        try {
            Element eltProjects = new Element(ELEMENT_PROJECTS);
            for (Project project : projects) {
                LOGGER.info("Adding " + project);
                eltProjects.addContent(createElement(project));
            }

            eltRoot.addContent(eltProjects);
        } catch (Exception e) {
            LOGGER.error("Error during saving settings...", e);
        }

        return eltRoot;
    }

    public void loadState(@NotNull Element element) {
        LOGGER.info("Loading state");

        try {
            for (Element project : (List<Element>) element.getChildren(ELEMENT_PROJECTS)) {
                for (Object object : project.getChildren(ELEMENT_PROJECT)) {
                    Element elt = (Element) object;
                    String source = elt.getAttributeValue(ELEMENT_PRJ_SOURCE);
                    String target = elt.getAttributeValue(ELEMENT_PRJ_TARGET);
                    String filter = elt.getAttributeValue(ELEMENT_PRJ_FILTER);
                    addProject(source, target, filter);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while loading settings", e);
        }
    }

    private void addProject(String source, String target, String filter) {
        LOGGER.info("Adding dir source " + source + " and target " + target + " and filter " + filter);

        projects.add(new Project(source, target, filter));
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void clear() {
        projects = new ArrayList<Project>();
    }


}