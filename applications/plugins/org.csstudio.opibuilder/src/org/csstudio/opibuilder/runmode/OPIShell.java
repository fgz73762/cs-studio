
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
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

        shell.setText(path.toString());
       
        shell.pack();
        shell.open();
        
        Rectangle windowBounds = getWindowBounds();
		shell.setSize(windowBounds.width + 18,
				windowBounds.height + 38);
    }
    
    public Shell getShell() {
    	return shell;
    }
    
    private Rectangle getWindowBounds() {
		Rectangle windowBounds = new Rectangle(displayModel.getLocation(),
				displayModel.getSize());
		return windowBounds;
    }
}