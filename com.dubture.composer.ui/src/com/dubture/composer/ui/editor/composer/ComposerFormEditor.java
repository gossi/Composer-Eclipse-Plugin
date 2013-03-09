package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.getcomposer.ComposerPackage;

import com.dubture.composer.ui.actions.InstallAction;
import com.dubture.composer.ui.actions.SelfUpdateAction;
import com.dubture.composer.ui.actions.UpdateAction;

public class ComposerFormEditor extends SharedHeaderFormEditor {
	protected boolean dirty = false;
	protected ComposerPackage composerPackage = null;
	protected IDocumentProvider documentProvider;
	
	private ISharedImages sharedImages = null;
	private IProject project;
	
	private IAction installAction = null;
	private IAction updateAction = null;
	private IAction selfUpdateAction = null;
	
	// TODO JsonTextEditor some day...
	protected ComposerTextEditor textEditor = new ComposerTextEditor(); 

	public ComposerFormEditor() {
		super();
		documentProvider = textEditor.getDocumentProvider();
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			documentProvider.connect(input);
			
			// TODO some sort of listener to get notified when the file changes
			//
			// 1) document listener: documentProvider.getDocument(input).addDocumentListener
			// 2) documentProvider.addElementStateListener()
			// 3) Resource Listener
			//
			// see: https://github.com/pulse00/Composer-Eclipse-Plugin/issues/23
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		
		// eclipse way to get input, unfortunately does not exist for some
		// reasons?
//		File json = ((IFileEditorInput) input).getFile().getFullPath().toFile();
			
		// workaround
//		String composerJsonFilePath =
//				ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
//				((IFileEditorInput) input).getFile().getFullPath().toString();
//		
//		composerFile = new File(composerJsonFilePath);
		
		if (input instanceof IFileEditorInput) {
			project = ((IFileEditorInput)input).getFile().getProject();
		}
			
		// ok, cool way here we go
		String json = documentProvider.getDocument(input).get();
		
		composerPackage = new ComposerPackage(json);
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getOldValue() != e.getNewValue()) {
//					System.out.println("Composer Property Changed: " + e.getPropertyName() + ", old: " + e.getOldValue() + ", new: " + e.getNewValue());
					setDirty(true);
				}
			}
		});
		
		
	}
		
	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		ScrolledForm header = headerForm.getForm();
		header.setText("Composer");

		FormToolkit toolkit = headerForm.getToolkit();
		toolkit.decorateFormHeading(header.getForm());
		
		ToolBarManager manager = (ToolBarManager) header.getToolBarManager();
		contributeToToolbar(manager);
	    manager.update(true);
	    
	}
	
	protected void contributeToToolbar(ToolBarManager manager) {
		// this does not work for some reasons? how to make it working and get rid of the action package?
//		IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
//		menuService.populateContributionManager(manager, "toolbar:com.dubture.composer.ui.editor.toolbar");
		manager.add(getInstallAction());
		manager.add(getUpdateAction());
		manager.add(getSelfUpdateAction());
	}
	
	protected ISharedImages getSharedImages() {
		if (sharedImages == null) {
			getSite().getPage().getWorkbenchWindow().getWorkbench().getSharedImages();
		}
		
		return sharedImages;
	}
	
	protected IAction getInstallAction() {
		if (installAction == null) {
			installAction = new InstallAction(project, getSite());
		}
		
		return installAction;
	}
	
	protected IAction getUpdateAction() {
		if (updateAction == null) {
			updateAction = new UpdateAction(project, getSite());
		}
		
		return updateAction;
	}
	
	protected IAction getSelfUpdateAction() {
		if (selfUpdateAction == null) {
			selfUpdateAction = new SelfUpdateAction(project, getSite());
		}
		
		return selfUpdateAction;
	}

	@Override
	protected void addPages() {
		try {
			addOverview();
			addDependencies();
//			addDependencyGraph();
//			setActivePage(DependenciesPage.ID);

			// Aww, can't use jsonedit here :(
			// addPage(new JsonTextEditor(), getEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void addOverview() throws PartInitException {
		addPage(new OverviewPage(this, OverviewPage.ID, "Overview"));
	}

	protected void addDependencies() throws PartInitException {
		addPage(new DependenciesPage(this, DependenciesPage.ID, "Dependencies"));
	}
	
	protected void addDependencyGraph() throws PartInitException {
		addPage(new DependencyGraphPage(this, DependencyGraphPage.ID, "Dependency Graph"));
	}
	
	

	public void doSave(IProgressMonitor monitor) {
		try {
			IDocument document = documentProvider.getDocument(getEditorInput());
			documentProvider.aboutToChange(getEditorInput());
			document.set(composerPackage.toJson());
			documentProvider.saveDocument(monitor, getEditorInput(), document, true);
			documentProvider.changed(getEditorInput());

			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean value) {
		this.dirty = value;
		editorDirtyStateChanged();
	}

	public ComposerPackage getComposerPackge() {
		return composerPackage;
	}
	
}