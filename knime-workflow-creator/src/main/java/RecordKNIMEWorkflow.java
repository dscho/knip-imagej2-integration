/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import imagej.ImageJ;
import imagej.command.Command;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A command that computes some statistics of a dataset.
 * <p>
 * For an even simpler command, see {@link HelloWorld} in this same package!
 * </p>
 */
@Plugin(type = Command.class, headless = false, menuPath = "KNIME>Test", name="KNIME Workflow Recorder")
public class RecordKNIMEWorkflow implements Command {

	@Parameter
	private KNIMERecorder recorder;
	
	// @Parameter(type = ItemIO.INPUT, label = "Number of Events")
	// int numEvents;

	@SuppressWarnings("unused")
	@Parameter(type = ItemIO.OUTPUT)
	private String simpleOutput;

	/**
	 * Computes some statistics on the input dataset, using ImageJ's built-in
	 * statistics service.
	 */
	@Override
	public void run() {
		simpleOutput += recorder.print();
	}

	public static void main(String[] args) {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();
		ij.command().run(RecordKNIMEWorkflow.class);
	}

}
