package org.csstudio.utility.quickstart;

import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Class for extension point 'popupMenu'. Executes the popupMenu action
 * for sds files and adds the selected file to the preferences of
 * quickstart menu.
 * 
 * @author jhatje
 *
 */
public class AddToQuickstartAction implements IObjectActionDelegate {
	
	/**
	 * The current selection.
	 */
	private IStructuredSelection _selection;
	
	public AddToQuickstartAction() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * Get the selected sds file in the navigator and adds the file with
	 * path to the preference 'sdsFile'. The preference is a string with all
	 * quickstart files separated with ';'.
	 */
	public void run(IAction action) {
		if (_selection != null) {
			Object element = _selection.getFirstElement();
			if (element instanceof IFile) {
				Preferences prefs = Activator.getDefault().getPluginPreferences();
				String sdsFileList = prefs.getString(PreferenceConstants.SDS_FILES);
				IFile file = (IFile) element;
				sdsFileList = trimSdsFileList(sdsFileList);
				sdsFileList = file.getLocation().toString() + ";" + sdsFileList;
				prefs.setValue("sdsFiles", sdsFileList);
			}
		}
	}
	
	/**
	 * Delete all entries at the end in quickstart list until the
	 * number matches the predefined number of menu items.
	 * 
	 * @param sdsFileList
	 * @return
	 */
	private String trimSdsFileList(String sdsFileList) {
		//list length is ok.
		if (sdsFileList.split(";").length < 20) {
			return sdsFileList;
		//list length is too long.	
		} else {
			int indexOfColon = sdsFileList.lastIndexOf(";");
			sdsFileList = sdsFileList.substring(0, (indexOfColon + 1));
		}
		return sdsFileList;
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selection = (IStructuredSelection) selection;
		}
	}

}
