package org.filesync.idea.plugin.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.filesync.idea.plugin.FileSyncPlugin;
import org.filesync.idea.plugin.settings.Project;
import org.filesync.idea.plugin.settings.Settings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collections;

public class Configuration implements Configurable {

    private static final Logger LOGGER = Logger.getInstance(Configuration.class);

    private JPanel jContainer;
    private JList jProjects;
    private JTextField jProjectSource;
    private JTextField jProjectTarget;
    private JButton jBtnProjectRemove;
    private JButton jBtnProjectAdd;
    private JButton jBtnProjectSource;
    private JButton jBtnProjectTarget;
    private JTextField jFilter;

    private DefaultListModel<Project> jProjectsModel;
    private JFileChooser jChooser;

    private boolean modified = false;

    public Configuration() {
        init();
    }

    private void init() {
        LOGGER.info("Init configuration...");
        initChooser();
        initListeners();
        initModel();
    }

    // 初始化选择器
    private void initChooser() {
        jChooser = new JFileChooser();
        jChooser.setAcceptAllFileFilterUsed(false);
        jChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    private void initModel() {
        jProjectsModel = new DefaultListModel<Project>();
        Settings settings = Settings.getInstance();
        for (Project project : settings.getProjects()) {
            jProjectsModel.addElement(project);
        }

        jProjects.setModel(jProjectsModel);
        selectFirstProject();
    }

    private void initListeners() {
        // 初始添加新项
        initOnAddNewProject();
        // 初始选择目录
        initOnDirectoryChooser();
        // 初始删除项目
        initOnRemoveProject();
        // 初始选择项目
        initOnSelectProject();
        // 文本监听
        jFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setFilterValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setFilterValue();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setFilterValue();
            }

            public void setFilterValue() {
                modified = true;
                Project selectedProject = getSelectedProject();
                if (selectedProject != null) {
                    selectedProject.setFilter(jFilter.getText());
                }
            }
        });
    }

    private void initOnAddNewProject() {
        jBtnProjectAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project emptyProject = Project.empty();
                if (!jProjectsModel.contains(emptyProject)) {
                    LOGGER.info("Add new empty project");
                    modified = true;

                    addProject(emptyProject);
                    selectLastProject();
                    setData(emptyProject);
                }
            }
        });
    }

    private void selectLastProject() {
        int nbProjects = getNbProjects();
        if (nbProjects > 0) {
            jProjects.setSelectedIndex(nbProjects - 1);
        }
    }

    private void selectFirstProject() {
        if (getNbProjects() > 0) {
            jProjects.setSelectedIndex(0);
        }
    }

    public void setData(Project data) {
        jProjectSource.setText(data.getSource());
        jProjectTarget.setText(data.getTarget());
        jFilter.setText(data.getFilter());
    }

    private void clearData() {
        jProjectSource.setText("");
        jProjectTarget.setText("");
        jFilter.setText("");
    }

    private void addProject(Project project) {
        jProjectsModel.addElement(project);
    }

    private void initOnDirectoryChooser() {
        jBtnProjectSource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File selectedFile = getSelectedFile();
                if (selectedFile != null) {
                    LOGGER.info("Select source directory " + selectedFile.toString());
                    Project selectedProject = getSelectedProject();
                    selectedProject.setSource(selectedFile.toString());
                    setData(selectedProject);
                }
            }
        });

        jBtnProjectTarget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File selectedFile = getSelectedFile();
                if (selectedFile != null) {
                    LOGGER.info("Select target directory " + selectedFile.toString());

                    Project selectedProject = getSelectedProject();
                    selectedProject.setTarget(selectedFile.toString());
                    setData(selectedProject);
                }
            }
        });

    }

    private File getSelectedFile() {
        int option = jChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            LOGGER.debug("Selected file from JFileChooser " + jChooser.getSelectedFile());
            return jChooser.getSelectedFile();
        }

        return null;
    }

    private void initOnRemoveProject() {
        jBtnProjectRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasSelectedProject()) {
                    LOGGER.info("Remove project " + getSelectedProject());

                    modified = true;
                    removeAndSelectNextElement();
                    selectLastProject();
                }
            }

            private void removeAndSelectNextElement() {
                int selectedIndex = jProjects.getSelectedIndex();
                jProjectsModel.remove(selectedIndex);

                checkIfNeedsToClearData();
                checkOtherComponentsState();
            }

            private void checkIfNeedsToClearData() {
                if (getNbProjects() == 0) {
                    clearData();
                }
            }
        });
    }

    private void initOnSelectProject() {
        jProjects.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (hasSelectedProject()) {
                    Project selectedValue = getSelectedProject();
                    LOGGER.info("Select project " + selectedValue);

                    setData(selectedValue);
                    checkOtherComponentsState();
                }
            }
        });
    }

    private boolean hasSelectedProject() {
        return !jProjects.isSelectionEmpty();
    }

    private Project getSelectedProject() {
        return (Project) jProjects.getSelectedValue();
    }

    private void checkOtherComponentsState() {
        boolean hasOneOrMoreProjects = getNbProjects() > 0;
        jBtnProjectRemove.setEnabled(hasOneOrMoreProjects);
        jBtnProjectSource.setEnabled(hasOneOrMoreProjects);
        jBtnProjectTarget.setEnabled(hasOneOrMoreProjects);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return FileSyncPlugin.PLUGIN_DISPLAY_NAME;
    }

    public int getNbProjects() {
        return jProjectsModel.size();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return jContainer;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        LOGGER.info("Apply configuration");
        Settings settings = Settings.getInstance();
        settings.clear();
        settings.setProjects(Collections.list(jProjectsModel.elements()));
    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }
}
