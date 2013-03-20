package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.php.internal.ui.util.TypedViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.ComposerSection;
import com.dubture.composer.ui.editor.FormEntryAdapter;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.parts.BooleanFormEntry;
import com.dubture.composer.ui.parts.FormEntry;
import com.dubture.composer.ui.parts.IBooleanFormEntryListener;

@SuppressWarnings("restriction")
public class ConfigSection extends ComposerSection {

	public ConfigSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Config");
		section.setDescription("Configure your package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 3));
		section.setClient(client);
		
		createProcessTimeoutEntry(client, toolkit);
		createVendorDirEntry(client, toolkit);
		createBinDirEntry(client, toolkit);
		createNotifyOnInstallEntry(client, toolkit);
	}

	private void createProcessTimeoutEntry(Composite client, FormToolkit toolkit) {
		final FormEntry processTimeoutEntry = new FormEntry(client, toolkit, "process-timeout", null, false);
		Integer processTimeout = composerPackage.getConfig().getProcessTimeout();
		if (processTimeout != null) {
			processTimeoutEntry.setValue("" + processTimeout, true);
		}
		
		processTimeoutEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				if (entry.getValue().isEmpty()) {
					composerPackage.getConfig().remove("process-timeout");	
				} else {
					composerPackage.getConfig().set("process-timeout", Integer.valueOf(entry.getValue()));	
				}
			}
		});
		composerPackage.getConfig().addPropertyChangeListener("process-timeout", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Integer processTimeout = composerPackage.getConfig().getProcessTimeout();
				if (processTimeout == null) {
					processTimeoutEntry.setValue("", true);
				} else {
					processTimeoutEntry.setValue("" + processTimeout, true);
				}
			}
		});
	}
	
	private void createVendorDirEntry(final Composite client, FormToolkit toolkit) {
		final FormEntry vendorDirEntry = new FormEntry(client, toolkit, "vendor-dir", "Browse...", false);
		String vendorDir = composerPackage.getConfig().getVendorDir();
		if (vendorDir != null) {
			vendorDirEntry.setValue(vendorDir, true);
		}
		
		vendorDirEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				if (entry.getValue().isEmpty()) {
					composerPackage.getConfig().remove("vendor-dir");	
				} else {
					composerPackage.getConfig().set("vendor-dir", entry.getValue());
				}
			}

			public void browseButtonSelected(FormEntry entry) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						client.getShell(), 
						new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				
				dialog.addFilter(new TypedViewerFilter(new Class[] { IFolder.class }));
				dialog.setTitle("Select Vendor Dir");
				dialog.setMessage("Select folder:");
				dialog.setInput(getPage().getComposerEditor().getProject());
				dialog.setHelpAvailable(false);
				
				if (dialog.open() == Dialog.OK) {
					Object result = dialog.getFirstResult();
					if (result instanceof IFolder) {
						entry.setValue(((IFolder)result).getProjectRelativePath().toString());
					}
				}
			}
		});
		composerPackage.getConfig().addPropertyChangeListener("vendor-dir", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String vendorDir = composerPackage.getConfig().getVendorDir();
				if (vendorDir == null) {
					vendorDirEntry.setValue("", true);
				} else {
					vendorDirEntry.setValue(composerPackage.getConfig().getVendorDir(), true);
				}
			}
		});
	}
	
	private void createBinDirEntry(final Composite client, FormToolkit toolkit) {
		final FormEntry binDirEntry = new FormEntry(client, toolkit, "bin-dir", "Browse...", false);
		String binDir = composerPackage.getConfig().getBinDir();
		if (binDir != null) {
			binDirEntry.setValue(binDir, true);
		}
		
		binDirEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				if (entry.getValue().isEmpty()) {
					composerPackage.getConfig().remove("bin-dir");
				} else {
					composerPackage.getConfig().set("bin-dir", entry.getValue());
				}
			}
			
			public void browseButtonSelected(FormEntry entry) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						client.getShell(), 
						new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				
				dialog.addFilter(new TypedViewerFilter(new Class[] { IFolder.class }));
				dialog.setTitle("Select Bin Dir");
				dialog.setMessage("Select folder:");
				dialog.setInput(getPage().getComposerEditor().getProject());
				dialog.setHelpAvailable(false);
				
				if (dialog.open() == Dialog.OK) {
					Object result = dialog.getFirstResult();
					if (result instanceof IFolder) {
						entry.setValue(((IFolder)result).getProjectRelativePath().toString());
					}
				}
			}
		});
		composerPackage.getConfig().addPropertyChangeListener("bin-dir", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String binDir = composerPackage.getConfig().getBinDir();
				if (binDir == null) {
					binDirEntry.setValue("", true);
				} else {
					binDirEntry.setValue(binDir, true);
				}
			}
		});
	}

	private void createNotifyOnInstallEntry(Composite client, FormToolkit toolkit) {
		final BooleanFormEntry notifyOnInstallEntry = new BooleanFormEntry(client, toolkit, "notify-on-install");
		notifyOnInstallEntry.setValue(composerPackage.getConfig().getNotifyOnInstall());
		
		notifyOnInstallEntry.addBooleanFormEntryListener(new IBooleanFormEntryListener() {
			public void selectionChanged(BooleanFormEntry entry) {
				if (entry.getValue()) {
					composerPackage.getConfig().remove("notify-on-install");
				} else {
					composerPackage.getConfig().setNotifyOnInstall(entry.getValue());
				}
			}
		});
		composerPackage.getConfig().addPropertyChangeListener("notify-on-install", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				notifyOnInstallEntry.setValue(composerPackage.getConfig().getNotifyOnInstall(), true);
			}
		});
	}

}
