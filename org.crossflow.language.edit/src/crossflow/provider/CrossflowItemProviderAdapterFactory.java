/**
 */
package crossflow.provider;

import crossflow.util.CrossflowAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CrossflowItemProviderAdapterFactory extends CrossflowAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CrossflowItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Workflow} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkflowItemProvider workflowItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Workflow}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createWorkflowAdapter() {
		if (workflowItemProvider == null) {
			workflowItemProvider = new WorkflowItemProvider(this);
		}

		return workflowItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Topic} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TopicItemProvider topicItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Topic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createTopicAdapter() {
		if (topicItemProvider == null) {
			topicItemProvider = new TopicItemProvider(this);
		}

		return topicItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Queue} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected QueueItemProvider queueItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Queue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createQueueAdapter() {
		if (queueItemProvider == null) {
			queueItemProvider = new QueueItemProvider(this);
		}

		return queueItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Task} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TaskItemProvider taskItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Task}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createTaskAdapter() {
		if (taskItemProvider == null) {
			taskItemProvider = new TaskItemProvider(this);
		}

		return taskItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Source} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SourceItemProvider sourceItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Source}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSourceAdapter() {
		if (sourceItemProvider == null) {
			sourceItemProvider = new SourceItemProvider(this);
		}

		return sourceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.CsvSource} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CsvSourceItemProvider csvSourceItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.CsvSource}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCsvSourceAdapter() {
		if (csvSourceItemProvider == null) {
			csvSourceItemProvider = new CsvSourceItemProvider(this);
		}

		return csvSourceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Sink} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SinkItemProvider sinkItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Sink}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSinkAdapter() {
		if (sinkItemProvider == null) {
			sinkItemProvider = new SinkItemProvider(this);
		}

		return sinkItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.CsvSink} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CsvSinkItemProvider csvSinkItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.CsvSink}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCsvSinkAdapter() {
		if (csvSinkItemProvider == null) {
			csvSinkItemProvider = new CsvSinkItemProvider(this);
		}

		return csvSinkItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.CommitmentTask} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CommitmentTaskItemProvider commitmentTaskItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.CommitmentTask}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCommitmentTaskAdapter() {
		if (commitmentTaskItemProvider == null) {
			commitmentTaskItemProvider = new CommitmentTaskItemProvider(this);
		}

		return commitmentTaskItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.OpinionatedTask} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OpinionatedTaskItemProvider opinionatedTaskItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.OpinionatedTask}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createOpinionatedTaskAdapter() {
		if (opinionatedTaskItemProvider == null) {
			opinionatedTaskItemProvider = new OpinionatedTaskItemProvider(this);
		}

		return opinionatedTaskItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.ScriptedTask} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ScriptedTaskItemProvider scriptedTaskItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.ScriptedTask}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createScriptedTaskAdapter() {
		if (scriptedTaskItemProvider == null) {
			scriptedTaskItemProvider = new ScriptedTaskItemProvider(this);
		}

		return scriptedTaskItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Type} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TypeItemProvider typeItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Type}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createTypeAdapter() {
		if (typeItemProvider == null) {
			typeItemProvider = new TypeItemProvider(this);
		}

		return typeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.DataField} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataFieldItemProvider dataFieldItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.DataField}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createDataFieldAdapter() {
		if (dataFieldItemProvider == null) {
			dataFieldItemProvider = new DataFieldItemProvider(this);
		}

		return dataFieldItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.EnumField} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EnumFieldItemProvider enumFieldItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.EnumField}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createEnumFieldAdapter() {
		if (enumFieldItemProvider == null) {
			enumFieldItemProvider = new EnumFieldItemProvider(this);
		}

		return enumFieldItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Language} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LanguageItemProvider languageItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Language}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createLanguageAdapter() {
		if (languageItemProvider == null) {
			languageItemProvider = new LanguageItemProvider(this);
		}

		return languageItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Parameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterItemProvider parameterItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createParameterAdapter() {
		if (parameterItemProvider == null) {
			parameterItemProvider = new ParameterItemProvider(this);
		}

		return parameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link crossflow.Serializer} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SerializerItemProvider serializerItemProvider;

	/**
	 * This creates an adapter for a {@link crossflow.Serializer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createSerializerAdapter() {
		if (serializerItemProvider == null) {
			serializerItemProvider = new SerializerItemProvider(this);
		}

		return serializerItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void dispose() {
		if (workflowItemProvider != null) workflowItemProvider.dispose();
		if (topicItemProvider != null) topicItemProvider.dispose();
		if (queueItemProvider != null) queueItemProvider.dispose();
		if (taskItemProvider != null) taskItemProvider.dispose();
		if (sourceItemProvider != null) sourceItemProvider.dispose();
		if (csvSourceItemProvider != null) csvSourceItemProvider.dispose();
		if (sinkItemProvider != null) sinkItemProvider.dispose();
		if (csvSinkItemProvider != null) csvSinkItemProvider.dispose();
		if (commitmentTaskItemProvider != null) commitmentTaskItemProvider.dispose();
		if (opinionatedTaskItemProvider != null) opinionatedTaskItemProvider.dispose();
		if (scriptedTaskItemProvider != null) scriptedTaskItemProvider.dispose();
		if (typeItemProvider != null) typeItemProvider.dispose();
		if (dataFieldItemProvider != null) dataFieldItemProvider.dispose();
		if (enumFieldItemProvider != null) enumFieldItemProvider.dispose();
		if (languageItemProvider != null) languageItemProvider.dispose();
		if (parameterItemProvider != null) parameterItemProvider.dispose();
		if (serializerItemProvider != null) serializerItemProvider.dispose();
	}

}
