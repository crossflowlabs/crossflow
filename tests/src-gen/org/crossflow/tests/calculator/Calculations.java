/** This class was automatically generated and should not be modified */
package org.crossflow.tests.calculator;

import javax.annotation.Generated;
import javax.jms.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.Job;
import org.crossflow.runtime.JobStream;
import org.crossflow.runtime.Task;
import org.apache.activemq.command.ActiveMQBytesMessage;

@Generated(value = "org.crossflow.java.Steam2Class", date = "2022-01-05T17:43:28.963438Z")
public class Calculations extends JobStream<Calculation> {
		
	protected Task cacheManagerTask = new Task() {

		@Override
		public Workflow<?> getWorkflow() {
			return workflow;
		}

		@Override
		public String getId() {
			return "CacheManager:Calculations";
		}

		@Override
		public String getName() {
			return "CacheManager:Calculations";
		}
	};	
		
	public Calculations(Workflow<CalculatorWorkflowTasks> workflow, boolean enablePrefetch) throws Exception {
		super(workflow);
		
		ActiveMQDestination postQ;
			pre.put("Calculator", (ActiveMQDestination) session.createQueue("CalculationsPre.Calculator." + workflow.getInstanceId()));
			destination.put("Calculator", (ActiveMQDestination) session.createQueue("CalculationsDestination.Calculator." + workflow.getInstanceId()));
			postQ = (ActiveMQDestination) session.createQueue("CalculationsPost.Calculator." + workflow.getInstanceId()
					+ (enablePrefetch?"":"?consumer.prefetchSize=1"));		
			post.put("Calculator", postQ);			
		
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
								if (output.getDestination().equals("CalculationResults")) {
									workflow.cancelTermination();
									((CalculatorWorkflow) workflow).getCalculationResults().send((CalculationResult) output, consumerId);
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
	
	public void addConsumer(CalculationsConsumer consumer, String consumerId) throws Exception {
	
		ActiveMQDestination postQueue = post.get(consumerId);
		
		//only connect if the consumer exists (for example it will not in a master_bare situation)
		if (consumer != null) {		
			MessageConsumer messageConsumer = session.createConsumer(postQueue);
			consumers.add(messageConsumer);
			messageConsumer.setMessageListener(message -> {
				try {
					String messageText = getMessageText(message);
					Calculation calculation = null;
					if(messageText != null && messageText.length() > 0)
						calculation = workflow.getSerializer().deserialize(messageText);
					if(calculation != null)
						consumer.consumeCalculationsWithNotifications(calculation);
					else
						System.err.println("INFO: Calculations ignoring null message.");
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

