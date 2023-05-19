package org.crossflow.runtime;

import java.util.*;
import javax.jms.*;
import javax.jms.IllegalStateException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQDestination;
import org.crossflow.runtime.utils.KVSRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public abstract class JobStream<T extends Job> implements Stream {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobStream.class);

	protected Map<String, ActiveMQDestination> destination;
	protected Map<String, ActiveMQDestination> pre;
	protected Map<String, ActiveMQDestination> post;
	protected Connection connection;
	protected Session session;
	protected Workflow<?> workflow;
	protected List<MessageConsumer> consumers = new LinkedList<>();

	public JobStream(Workflow<?> workflow) throws Exception {
		this.workflow = workflow;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(workflow.getBroker());
		connectionFactory.setTrustAllPackages(true);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

		destination = new HashMap<>();
		pre = new HashMap<>();
		post = new HashMap<>();
	}

	private Long lastIllegalStateException = 0L;

	public void send(T job, String taskId) {

		try {

			ActiveMQDestination d = null;
			// if the sender is one of the targets of this stream, it has re-sent a message
			// so it should only be put in the relevant physical queue
			if ((d = pre.get(taskId)) != null) {
				MessageProducer producer = session.createProducer(d);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				job.setDestination(getClass().getSimpleName());
				//TODO 25.05.: Is job already issued to that task? It's not issued to the same worker, as it should be changed
				// Need to change preferred worker before returning to queue, this puts back to CalculatorPre,
				// it will go down to dest, and then will be forwarded to particular dedicated queue

//				if (job.getDesignatedWorkerId() != null) {
//					workflow.issueJob(job, job.getDesignatedWorkerId());
//				}
				producer.send(session.createTextMessage(workflow.getSerializer().serialize(job)));
				producer.close();
			} else {
				// otherwise, the sender must be the source of this stream so intends to
				// propagate its messages to all the physical queues
				for (Destination dest : pre.values()) {
					MessageProducer producer = session.createProducer(dest);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
					job.setDestination(this.getClass().getSimpleName());
					/** PROPOSAL2: Issue a job to a worker, so we know it's in the progress*/
					if (job.getDesignatedWorkerId() != null) {
						workflow.issueJob(job, job.getDesignatedWorkerId());
					}
					producer.send(session.createTextMessage(workflow.getSerializer().serialize(job)));
					producer.close();
				}
			}
		} catch (Exception ex) {
			if (!(ex instanceof IllegalStateException))
				workflow.reportInternalException(ex);
			else {
				LOGGER.error("ISE encountered in JobStream", ex);
				Long currentTime = System.currentTimeMillis();
				if (currentTime - lastIllegalStateException > 1000) {
					System.err.println(ex.getMessage() + ", suppressing similar errors for 1 second.");
					lastIllegalStateException = currentTime;
				}
			}
		}
	}

	@Override
	public Collection<String> getDestinationNames() {
		List<String> ret = new ArrayList<>(pre.size() + destination.size() + post.size());
		for (ActiveMQDestination d : pre.values())
			ret.add(d.getPhysicalName());
		for (ActiveMQDestination d : destination.values())
			ret.add(d.getPhysicalName());
		for (ActiveMQDestination d : post.values())
			ret.add(d.getPhysicalName());
		return ret;
	}

	@Override
	public void stop() throws JMSException {
		for (MessageConsumer c : consumers) {
			c.close();
		}
		session.close();
		connection.close();
	}

	@Override
	public boolean isBroadcast() {
		return destination.values().iterator().next().isTopic();
	}

	/**
	 * 
	 * @return A set containing all ActiveMQDestination objects used by this
	 *         JobStream
	 */
	protected Set<ActiveMQDestination> getAllQueues() {
		Set<ActiveMQDestination> ret = new HashSet<>();
		ret.addAll(pre.values());
		ret.addAll(post.values());
		ret.addAll(destination.values());
		return ret;
	}

    public void addDedicatedQueue(String consumerId, String destinationId) {}
}
