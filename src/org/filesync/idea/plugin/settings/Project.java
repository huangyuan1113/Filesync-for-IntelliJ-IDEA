package org.filesync.idea.plugin.settings;

public class Project {
    private String source;
    private String target;
    private String filter;

    public Project(String source, String target, String filter) {
        this.source = source;
        this.target = target;
        this.filter = filter;
    }

    public Project() {
    }

    /**
     * Returns an empty project with a foo name to force input.
     */
    public static Project empty() {
        Project project = new Project();
        project.source = "???";

        return project;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (!source.equals(project.source)) return false;
        if (target != null ? !target.equals(project.target) : project.target != null) return false;
        if (filter != null ? !filter.equals(project.filter) : project.filter != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + (target != null ? target.hashCode() : 0) + (filter != null ? filter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return source;
    }

}