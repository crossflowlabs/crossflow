/** This class was automatically generated and should not be modified */
package typeInheritanceAdditionWorkflow;

import javax.annotation.Generated;
import javax.jms.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.Job;
import org.crossflow.runtime.JobStream;
import org.crossflow.runtime.Task;
import org.apache.activemq.command.ActiveMQBytesMessage;

@Generated(value = "org.crossflow.java.Steam2Class", date = "2022-01-05T17:43:28.490240Z")
public class AdditionResults extends JobStream<Number> {
		
	protected Task cacheManagerTask = new Task() {

		@Override
		public Workflow<?> getWorkflow() {
			return workflow;
		}

		@Override
		public String getId() {
			return "CacheManager:AdditionResults";
		}

		@Override
		public String getName() {
			return "CacheManager:AdditionResults";
		}
	};	
		
	public AdditionResults(Workflow<AdditionWorkflowTasks> workflow, boolean enablePrefetch) throws Exception {
		super(workflow);
		
		ActiveMQDestination postQ;
			pre.put("AdditionResultsSink", (ActiveMQDestination) session.createQueue("AdditionResultsPre.AdditionResultsSink." + workflow.getInstanceId()));
			destination.put("AdditionResultsSink", (ActiveMQDestination) session.createQueue("AdditionResultsDestination.AdditionResultsSink." + workflow.getInstanceId()));
			postQ = (ActiveMQDestination) session.createQueue("AdditionResultsPost.AdditionResultsSink." + workflow.getInstanceId()
					+ (enablePrefetch?"":"?consumer.prefetchSize=1"));		
			post.put("AdditionResultsSink", postQ);			
		
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
							MessageProducer producer = session.createProducer(destQueue);
							producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
							producer.send(message);
							producer.close();
						
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
	
	public void addConsumer(AdditionResultsConsumer consumer, String consumerId) throws Exception {
	
		ActiveMQDestination postQueue = post.get(consumerId);
		
		//only connect if the consumer exists (for example it will not in a master_bare situation)
		if (consumer != null) {		
			MessageConsumer messageConsumer = session.createConsumer(postQueue);
			consumers.add(messageConsumer);
			messageConsumer.setMessageListener(message -> {
				try {
					String messageText = getMessageText(message);
					Number number = null;
					if(messageText != null && messageText.length() > 0)
						number = workflow.getSerializer().deserialize(messageText);
					if(number != null)
						consumer.consumeAdditionResultsWithNotifications(number);
					else
						System.err.println("INFO: AdditionResults ignoring null message.");
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

