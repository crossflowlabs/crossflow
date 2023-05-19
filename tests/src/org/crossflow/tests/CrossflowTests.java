package org.crossflow.tests;

import org.crossflow.runtime.serializer.tests.RuntimeSerializationTests;
import org.crossflow.runtime.tests.RuntimeTests;
import org.crossflow.tests.ack.AckWorkflowTests;
import org.crossflow.tests.addition.AdditionWorkflowCommandLineTests;
import org.crossflow.tests.addition.AdditionWorkflowTests;
import org.crossflow.tests.cache.DirectoryCacheCommandLineTests;
import org.crossflow.tests.cache.DirectoryCacheTests;
import org.crossflow.tests.commitment.CommitmentWorkflowTests;
import org.crossflow.tests.configurable.addition.ConfigurableWorkflowTests;
import org.crossflow.tests.crawler.CrawlerWorkflowTests;
import org.crossflow.tests.exceptions.ExceptionsWorkflowTests;
import org.crossflow.tests.matrix.MatrixWorkflowTests;
import org.crossflow.tests.minimal.MinimalWorkflowTests;
import org.crossflow.tests.multiflow.MultiflowTests;
import org.crossflow.tests.opinionated.OccurencesWorkflowTests;
import org.crossflow.tests.optionalbuiltinstream.OptionalBuiltinStreamTests;
import org.crossflow.tests.parallel.ParallelWorkflowTests;
import org.crossflow.tests.transactionalcaching.TransactionalCachingTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AdditionWorkflowTests.class,
	AdditionWorkflowCommandLineTests.class,
	DirectoryCacheTests.class,
	DirectoryCacheCommandLineTests.class,
	OccurencesWorkflowTests.class,
	CrawlerWorkflowTests.class,
	MatrixWorkflowTests.class,
	MultiflowTests.class,
	MinimalWorkflowTests.class,
	CommitmentWorkflowTests.class,
	ExceptionsWorkflowTests.class,
	AckWorkflowTests.class,
	ParallelWorkflowTests.class,
	TransactionalCachingTests.class,
	ConfigurableWorkflowTests.class,
	RuntimeTests.class,
	RuntimeSerializationTests.class,
	OptionalBuiltinStreamTests.class
})
public class CrossflowTests {

}
