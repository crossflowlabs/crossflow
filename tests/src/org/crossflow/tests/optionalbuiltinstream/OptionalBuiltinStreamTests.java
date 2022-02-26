package org.crossflow.tests.optionalbuiltinstream;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.crossflow.runtime.utils.StreamMetadataSnapshot;
import org.crossflow.runtime.utils.TaskStatus;
import org.crossflow.tests.WorkflowTests;
import org.crossflow.tests.minimal.MinimalWorkflow;
import org.crossflow.tests.util.BuiltinStreamRecorder;
import org.junit.Test;

public class OptionalBuiltinStreamTests extends WorkflowTests {

	@Test
	public void testOptionalStreams() throws Exception {
		testOptionalStreamsActual(true);
		testOptionalStreamsActual(false);
	}

	public void testOptionalStreamsActual(boolean enable) throws Exception {

		MinimalWorkflow workflow = new MinimalWorkflow();
		workflow.createBroker(createBroker);
		workflow.enableStreamMetadataTopic(enable);
		workflow.enableTaskMetadataTopic(enable);
		workflow.getMinimalSource().setNumbers(Arrays.asList(1));

		BuiltinStreamRecorder<StreamMetadataSnapshot> smr = new BuiltinStreamRecorder<StreamMetadataSnapshot>();
		BuiltinStreamRecorder<TaskStatus> tsr = new BuiltinStreamRecorder<TaskStatus>();

		workflow.getStreamMetadataTopic().addConsumer(smr);
		workflow.getTaskMetadataTopic().addConsumer(tsr);

		workflow.run();

		waitFor(workflow);

		System.out.println(smr.getRecorded().size() + ", " + enable);
		System.out.println(tsr.getRecorded().size() + ", " + enable);
		assertEquals(smr.getRecorded().size() != 0, enable);
		assertEquals(tsr.getRecorded().size() != 0, enable);

	}

}
