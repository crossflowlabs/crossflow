/** This class was automatically generated and should not be modified */
package orphanqueuesworkflow;

import javax.annotation.Generated;
import javax.jms.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.Job;
import org.crossflow.runtime.JobStream;
import org.crossflow.runtime.Task;
import org.apache.activemq.command.ActiveMQBytesMessage;

@Generated(value = "org.crossflow.java.Steam2Class", date = "2021-10-26T15:08:27.076721Z")
public class q1 extends JobStream<ty1> {
		
	protected Task cacheManagerTask = new Task() {

		@Override
		public Workflow<?> getWorkflow() {
			return workflow;
		}

		@Override
		public String getId() {
			return "CacheManager:q1";
		}

		@Override
		public String getName() {
			return "CacheManager:q1";
		}
	};	
		
	public q1(Workflow<orphanqueuesworkflowTasks> workflow, boolean enablePrefetch) throws Exception {
		super(workflow);
		
		ActiveMQDestination postQ;
			pre.put("t1", (ActiveMQDestination) session.createQueue("q1Pre.t1." + workflow.getInstanceId()));
			destination.put("t1", (ActiveMQDestination) session.createQueue("q1Destination.t1." + workflow.getInstanceId()));
			postQ = (ActiveMQDestination) session.createQueue("q1Post.t1." + workflow.getInstanceId()
					+ (enablePrefetch?"":"?consumer.prefetchSize=1"));		
			post.put("t1", postQ);			
		
		for (String consumerId : pre.keySet()) {
			ActiveMQDestination preQueue = pre.get(consumerId);
			ActiveMQDestination destQueue = destination.get(consumerId);
			ActiveMQDestination postQueue = post.get(consumerId);
			
			if (workflow.isMaster()) {
				MessageConsumer preConsumer = session.createConsumer(preQueue);
				consumers.add(preConsumer);
				preConsumer.setMessageListener(message -> {
					try {
						workflow.cancelTermination();
						Job job = workflow.getSerializer().deserialize(getMessageText(message));
						
						if (workflow.getCache() != null && workflow.getCache().hasCachedOutputs(job)) {
							
							workflow.setTaskInProgess(cacheManagerTask);
							Iterable<Job> cachedOutputs = workflow.getCache().getCachedOutputs(job);
							workflow.setTaskWaiting(cacheManagerTask);
							
							for (Job output : cachedOutputs) {
								if (output.getDestination().equals("q2")) {
									workflow.cancelTermination();
									((orphanqueuesworkflow) workflow).getq2().send((ty1) output);
								}
								
							}
						} else {
							MessageProducer producer = session.createProducer(destQueue);
							producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
							producer.send(message);
							producer.close();
						}
						
					} catch (Exception ex) {
						workflow.reportInternalException(ex);
					} finally { 
						try {
							message.acknowledge();
						} catch (Exception ex) {
							workflow.reportInternalException(ex);
						} 
					}				
				});
				
				MessageConsumer destinationConsumer = session.createConsumer(destQueue);
				consumers.add(destinationConsumer);
				destinationConsumer.setMessageListener(message -> {
					try {
						workflow.cancelTermination();
						Job job = workflow.getSerializer().deserialize(getMessageText(message));
						
						if (workflow.getCache() != null && !job.isCached())
							if(job.isTransactional())
								workflow.getCache().cacheTransactionally(job);
							else
								workflow.getCache().cache(job);
						if(job.isTransactionSuccessMessage())
							return;
						MessageProducer producer = session.createProducer(postQueue);
						producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
						producer.send(message);
						producer.close();
					}
					catch (Exception ex) {
						workflow.reportInternalException(ex);
					} finally { 
						try {
							message.acknowledge();
						} catch (Exception ex) {
							workflow.reportInternalException(ex);
						} 
					}				
				});
			}
		}
	}
	
	public void addConsumer(q1Consumer consumer, String consumerId) throws Exception {
	
		ActiveMQDestination postQueue = post.get(consumerId);
		
		//only connect if the consumer exists (for example it will not in a master_bare situation)
		if (consumer != null) {		
			MessageConsumer messageConsumer = session.createConsumer(postQueue);
			consumers.add(messageConsumer);
			messageConsumer.setMessageListener(message -> {
				try {
					String messageText = getMessageText(message);
					ty1 ty1 = null;
					if(messageText != null && messageText.length() > 0)
						ty1 = workflow.getSerializer().deserialize(messageText);
					if(ty1 != null)
						consumer.consumeq1WithNotifications(ty1);
					else
						System.err.println("INFO: q1 ignoring null message.");
				} catch (Exception ex) {
					workflow.reportInternalException(ex);
				} finally { 
					try {
						message.acknowledge();
					} catch (Exception ex) {
						workflow.reportInternalException(ex);
					} 
				}
			});
		}
	}
	
	private String getMessageText(Message message) throws Exception {
		if (message instanceof TextMessage) {
			return ((TextMessage) message).getText();
		}
		else if (message instanceof ActiveMQBytesMessage) {
			ActiveMQBytesMessage bm = (ActiveMQBytesMessage) message;
			byte data[] = new byte[(int) bm.getBodyLength()];
			bm.readBytes(data);
			return new String(data);
		}
		else return "";
	}
}

