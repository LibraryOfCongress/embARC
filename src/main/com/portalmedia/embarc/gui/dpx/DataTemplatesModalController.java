package com.portalmedia.embarc.gui.dpx;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.parser.dpx.DPXDataTemplate;

/**
 * Controls data templates modal
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class DataTemplatesModalController {

	private static class ControllerHolder {
		private static final DataTemplatesModalController INSTANCE = new DataTemplatesModalController();
	}

	public static DataTemplatesModalController getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private DataTemplatesModalController() {}

	// New blank data template
	public void showDataTemplatesDialogue() {
		final DataTemplateDialog d = new DataTemplateDialog();
		d.initOwner(Main.getPrimaryStage());
	}

	// Edit existing data template
	public void showDataTemplatesDialogue(DPXDataTemplate template) {
		final DataTemplateDialog d = new DataTemplateDialog(template);
		d.initOwner(Main.getPrimaryStage());
	}
	
}
