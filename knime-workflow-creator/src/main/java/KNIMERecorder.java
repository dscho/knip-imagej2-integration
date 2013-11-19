import imagej.command.CommandInfo;
import imagej.module.ModuleInfo;
import imagej.module.event.ModuleEvent;
import imagej.module.event.ModuleExecutedEvent;

import java.util.ArrayList;
import java.util.List;

import org.scijava.event.EventHandler;
import org.scijava.event.SciJavaEvent;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;

@Plugin(type = KNIMERecorder.class)
public class KNIMERecorder extends AbstractService {

	private List<ModuleInfo> moduleInfos = new ArrayList<ModuleInfo>();

	public KNIMERecorder() {
		super();
	}

	@EventHandler
	protected void onEvent(final SciJavaEvent evt) {
		if (ModuleEvent.class.isAssignableFrom(evt.getClass())) {
			if (evt instanceof ModuleExecutedEvent) {
				moduleInfos.add(((ModuleExecutedEvent) evt).getModule()
						.getInfo());
			}
		}
	}

	public String print() {
		StringBuffer sb = new StringBuffer("");
		for (ModuleInfo info : moduleInfos) {
			if (info instanceof CommandInfo) {
				sb.append(((CommandInfo) info).getClassName());
			}
		}
		return sb.toString();
	}
}