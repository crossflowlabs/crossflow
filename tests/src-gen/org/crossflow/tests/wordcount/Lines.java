/** This class was automatically generated and should not be modified */
package org.crossflow.tests.wordcount;

import javax.annotation.Generated;
import javax.jms.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.Job;
import org.crossflow.runtime.JobStream;
import org.crossflow.runtime.Task;
import org.apache.activemq.command.ActiveMQBytesMessage;

@Generated(value = "org.crossflow.java.Steam2Class", date = "2022-01-05T17:43:35.469246Z")
public class Lines extends JobStream<Line> {
		
	protected Task cacheManagerTask = new Task() {

		@Override
		public Workflow<?> getWorkflow() {
			return workflow;
		}

		@Override
		public String getId() {
			return "CacheManager:Lines";
		}

		@Override
		public String getName() {
			return "CacheManager:Lines";
		}
	};	
		
	public Lines(Workflow<WordCountWorkflowTasks> workflow, boolean enablePrefetch) throws Exception {
		super(workflow);
		
		ActiveMQDestination postQ;
			pre.put("WordCounter", (ActiveMQDestination) session.createQueue("LinesPre.WordCounter." + workflow.getInstanceId()));
			destination.put("WordCounter", (ActiveMQDestination) session.createQueue("LinesDestination.WordCounter." + workflow.getInstanceId()));
			postQ = (ActiveMQDestination) session.createQueue("LinesPost.WordCounter." + workflow.getInstanceId()
					+ (enablePrefetch?"":"?consumer.prefetchSize=1"));		
			post.put("WordCounter", postQ);			
		
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
								if (output.getDestination().equals("WordFrequencies")) {
									workflow.cancelTermination();
									((WordCountWorkflow) workflow).getWordFrequencies().send((WordFrequency) output, consumerId);
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
	
	public void addConsumer(LinesConsumer consumer, String consumerId) throws Exception {
	
		ActiveMQDestination postQueue = post.get(consumerId);
		
		//only connect if the consumer exists (for example it will not in a master_bare situation)
		if (consumer != null) {		
			MessageConsumer messageConsumer = session.createConsumer(postQueue);
			consumers.add(messageConsumer);
			messageConsumer.setMessageListener(message -> {
				try {
					String messageText = getMessageText(message);
					Line line = null;
					if(messageText != null && messageText.length() > 0)
						line = workflow.getSerializer().deserialize(messageText);
					if(line != null)
						consumer.consumeLinesWithNotifications(line);
					else
						System.err.println("INFO: Lines ignoring null message.");
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
