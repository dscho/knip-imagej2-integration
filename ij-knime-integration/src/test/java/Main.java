import org.knime.ij.IOSGiStarter;
import org.knime.ij.KNIMEBridge;
import org.knime.ij.WorkflowAccess;

public class Main {

	public static void main(String[] args) throws Exception {
		IOSGiStarter starter = KNIMEBridge.starter();
		WorkflowAccess access = starter.workflowAccess();
		access.createEmptyWorkflow("Hallo", "Welt");
	}

}
