package org.knime.ij.wfrecorder;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.knip.imagej2.core.node.IJNodeSetFactory;
import org.knime.knip.io.nodes.imgreader.ImgReaderNodeFactory;
import org.knime.workbench.repository.model.DynamicNodeTemplate;

public class WorkflowCreationService implements Map<Object, Object> {

    //TODO this hack needs to be removed after I found out how to create empty flows
    private final File EMPTY_FLOW = new File("TestFlow");

    private final IJNodeSetFactory IJ_NODE_SET;

    public WorkflowCreationService() {
        IJ_NODE_SET = new IJNodeSetFactory();
    }

    private final static String[] VALID_COMMANDS = new String[]{
            // opens a workflow. requires path to workflow
            "C_OpenWorkflow",

            // adds a node to a workflow given the IJ Plugin Command
            "C_CreateAndConnectNode",

            // creates a new, empty workflow
            "C_CreateEmptyWorkflow",

            // saves the current workflow
            "C_SaveWorkflow"};

    private Logger log = Logger.getLogger(this.getClass().getName());

    private WorkflowManager m_currentWMF;

    private NodeID m_activeNode;

    public int size() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(final Object key) {
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    public Object get(final Object key) {
        throw new UnsupportedOperationException();
    }

    public Object put(final Object key, final Object value) {
        try {
            log.info("Put '" + key + "' => '" + value + "'");

            if (key instanceof String) {
                String command = (String)key;
                if (command.startsWith("C_")) {

                    // open any workflow
                    if (command.equalsIgnoreCase(VALID_COMMANDS[0])) {

                        return true;
                    }

                    // Add Node
                    if (command.equalsIgnoreCase(VALID_COMMANDS[1])) {
                        log.info("Running " + VALID_COMMANDS[1]);
                        if (!(value instanceof String[])) {
                            throw new IllegalArgumentException(VALID_COMMANDS[1] + " only accepts String[]");
                        }

                        String factoryId = ((String[])value)[0];
                        String name = ((String[])value)[0];

                        NodeFactory<? extends NodeModel> fac =
                                DynamicNodeTemplate.createFactoryInstance(IJ_NODE_SET.getNodeFactory(factoryId),
                                                                          IJ_NODE_SET, factoryId);

                        DynamicNodeTemplate dynamicNodeTemplate =
                                new DynamicNodeTemplate(IJNodeSetFactory.class.getSimpleName(), factoryId, IJ_NODE_SET,
                                        name);
                        dynamicNodeTemplate.setFactory((Class<? extends NodeFactory<? extends NodeModel>>)fac
                                .getClass());

                        NodeID dest = m_currentWMF.addNode(dynamicNodeTemplate.createFactoryInstance());
                        m_currentWMF.addConnection(m_activeNode, 1, dest, 1);
                        m_activeNode = dest;
                        return true;
                    }

                    // creates a new, empty workflow
                    if (command.equalsIgnoreCase(VALID_COMMANDS[2])) {
                        log.info("Running " + VALID_COMMANDS[2]);
                        // Create WorkflowManager for empty
                        m_currentWMF =
                                WorkflowManager.loadProject(EMPTY_FLOW, new ExecutionMonitor(),
                                                            new WorkflowLoadHelper(EMPTY_FLOW)).getWorkflowManager();

                        m_currentWMF.clearWaitingLoopList();

                        m_currentWMF.getClass().getMethod("addNode", NodeFactory.class);

                        System.out.println(WorkflowManager.class.getClassLoader().toString());


                        // Add ImageReader node
                        m_activeNode = m_currentWMF.addNode(new ImgReaderNodeFactory());
                        return true;
                    }

                    // Save the workflow to a given directory
                    if (command.equalsIgnoreCase(VALID_COMMANDS[3])) {
                        log.info("Running " + VALID_COMMANDS[3]);
                        if (!(value instanceof String)) {
                            throw new IllegalArgumentException(VALID_COMMANDS[3] + " only accepts String[]");
                        }
                        m_currentWMF.save(new File((String)value), new ExecutionMonitor(), false);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }

        return value;
    }

    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(final Map map) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    public Collection values() {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        throw new UnsupportedOperationException();
    }
}
