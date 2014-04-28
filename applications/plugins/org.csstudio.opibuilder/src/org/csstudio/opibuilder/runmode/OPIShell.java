
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class OPIShell {
	
	private final Shell shell;
	private final DisplayModel displayModel;

    public OPIShell(Display display, IPath path) {
        shell = new Shell(display);
        displayModel = new DisplayModel();
        final GraphicalViewer viewer = new GraphicalViewerImpl();

       
        shell.setLayout(new FillLayout());
       
        try {
            XMLUtil.fillDisplayModelFromInputStream(ResourceUtil.pathToInputStream(path), displayModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.RUN_MODE));
        viewer.createControl(shell);
        viewer.setRootEditPart(new ScalableFreeformRootEditPart());
       
        viewer.setContents(displayModel);
        displayModel.setViewer(viewer);

        shell.setText(path.toString()); // Set title
        // Resize the shell after it's open, so we can take into account different window borders
        shell.addShellListener(new ShellListener() {
			public void shellIconified(ShellEvent e) {}
			public void shellDeiconified(ShellEvent e) {}
			public void shellDeactivated(ShellEvent e) {}
			public void shellClosed(ShellEvent e) {}
			public void shellActivated(ShellEvent e) {
				int frameX = shell.getSize().x - shell.getClientArea().width;
				int frameY = shell.getSize().y - shell.getClientArea().height;
				shell.setSize(displayModel.getSize().width + frameX, displayModel.getSize().height + frameY);
			}
		});
        shell.pack();
        shell.open();
    }
    
    public Shell getShell() {
    	return shell;
    }
}