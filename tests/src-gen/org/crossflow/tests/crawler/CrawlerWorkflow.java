/** This class was automatically generated and should not be modified */
package org.crossflow.tests.crawler;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Generated;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.ParallelTaskList;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.serialization.Serializer;
import org.crossflow.runtime.serialization.JsonSerializer;import org.crossflow.runtime.utils.ControlSignal;
import org.crossflow.runtime.utils.ControlSignal.ControlSignals;
import org.crossflow.runtime.utils.LogLevel;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:31.905448Z")
public class CrawlerWorkflow extends Workflow<CrawlerWorkflowTasks> {
	
	// Streams
	protected Urls urls;
	protected UrlsToAnalyse urlsToAnalyse;
	
	// Tasks
	protected UrlSource urlSource;
	protected ParallelTaskList<UrlCollector> urlCollectors = new ParallelTaskList<>();
	protected ParallelTaskList<UrlAnalyser> urlAnalysers = new ParallelTaskList<>();

	protected Set<CrawlerWorkflowTasks> tasksToExclude = EnumSet.noneOf(CrawlerWorkflowTasks.class);

	public CrawlerWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public CrawlerWorkflow(Mode m) {
		this(m, 1);
	}
	
	public CrawlerWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "CrawlerWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			urlSource = new UrlSource();
			urlSource.setWorkflow(this);
			tasks.add(urlSource);
			for(int i=1;i<=parallelization;i++){
				UrlCollector task = new UrlCollector();
				task.setWorkflow(this);
				tasks.add(task);
				urlCollectors.add(task);
			}
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(CrawlerWorkflowTasks.URL_ANALYSER)) {
				for(int i=1;i<=parallelization;i++){
					UrlAnalyser task = new UrlAnalyser();
					task.setWorkflow(this);
					tasks.add(task);
					urlAnalysers.add(task);
				}
			}
		}
	}
	
	/**
	 * Run with initial delay in ms before starting execution (after creating broker
	 * if master)
	 * 
	 * @param delay
	 */
	@Override
	public void run(long delay) throws Exception {
		this.getSerializer();
		this.delay=delay;
	
		try {
						
			if (isMaster()) {
				if (createBroker) {
					if (activeMqConfig != null && activeMqConfig != "") {
						brokerService = BrokerFactory.createBroker(new URI("xbean:" + activeMqConfig));
					} else {
						brokerService = new BrokerService();
					}
				
					//activeMqConfig
					brokerService.setUseJmx(true);
					brokerService.addConnector(getBroker());
					if(enableStomp)
						brokerService.addConnector(getStompBroker());
					if(enableWS)	
						brokerService.addConnector(getWSBroker());
					brokerService.start();
				}
			}
	
			Thread.sleep(delay);
		
			urlCollectors.init(this);
			urlAnalysers.init(this);
	
			urls = new Urls(CrawlerWorkflow.this, enablePrefetch);
			activeStreams.add(urls);
			urlsToAnalyse = new UrlsToAnalyse(CrawlerWorkflow.this, enablePrefetch);
			activeStreams.add(urlsToAnalyse);
		
			if (isMaster()) {
					urlSource.setUrls(urls);
					for(int i = 0; i <urlCollectors.size(); i++){
						UrlCollector task = urlCollectors.get(i);
						urls.addConsumer(task, "UrlCollector");			
						task.setUrlsToAnalyse(urlsToAnalyse);
					}
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(CrawlerWorkflowTasks.URL_ANALYSER)) {
						for(int i = 0; i <urlAnalysers.size(); i++){
							UrlAnalyser task = urlAnalysers.get(i);
							urlsToAnalyse.addConsumer(task, "UrlAnalyser");			
							task.setUrls(urls);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread urlSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(urlSource);
						urlSource.produce();
						setTaskWaiting(urlSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(urlSourceThread);
				urlSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Urls getUrls() {
		return urls;
	}
	public UrlsToAnalyse getUrlsToAnalyse() {
		return urlsToAnalyse;
	}
	
	public UrlSource getUrlSource() {
		return urlSource;
	}
	public UrlCollector getUrlCollector() {
		if(urlCollectors.size()>0)
			return urlCollectors.get(0);
		else 
			return null;
	}
	public ParallelTaskList<UrlCollector> getUrlCollectors() {
		return urlCollectors;	
	}	
	public UrlAnalyser getUrlAnalyser() {
		if(urlAnalysers.size()>0)
			return urlAnalysers.get(0);
		else 
			return null;
	}
	public ParallelTaskList<UrlAnalyser> getUrlAnalysers() {
		return urlAnalysers;	
	}	
	
	public CrawlerWorkflow createWorker() {
		CrawlerWorkflow worker = new CrawlerWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public CrawlerWorkflow excludeTask(CrawlerWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public CrawlerWorkflow excludeTasks(Collection<CrawlerWorkflowTasks> tasks) {
		for (CrawlerWorkflowTasks t : tasks) {
			excludeTask(t);
		}
		return this;
	}
	
	@Override
	protected Serializer createSerializer() {
		return new JsonSerializer();
	}
	
	@Override
	protected void registerCustomSerializationTypes(Serializer serializer) {
		checkNotNull(serializer);
		serializer.registerType(Url.class);
	}
	
	public static CrawlerWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		CrawlerWorkflow argsHolder = new CrawlerWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		CrawlerWorkflow app = new CrawlerWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		CrawlerWorkflow argsHolder = new CrawlerWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("CrawlerWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(UrlCollector v : urlCollectors){
			ret = v.cancelJob(id) || ret;
		}
		for(UrlAnalyser v : urlAnalysers){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
