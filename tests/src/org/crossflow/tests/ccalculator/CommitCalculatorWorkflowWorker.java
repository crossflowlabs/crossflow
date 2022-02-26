package org.crossflow.tests.ccalculator;

import org.crossflow.runtime.Mode;
import java.util.concurrent.ThreadLocalRandom;

public class CommitCalculatorWorkflowWorker {

    public static void main(String[] args) throws Exception {

        CommitCalculatorWorkflow worker1 = new CommitCalculatorWorkflow(Mode.WORKER);
        //worker1.setMaster("localhost");
        worker1.setName("Worker " + ThreadLocalRandom.current().nextInt(0, 10));
        worker1.getCalculator().setDelay(1000);
        worker1.setInstanceId("commit-calculator");
        worker1.run();

		/*
		CalculatorWorkflow worker2 = new CalculatorWorkflow(Mode.WORKER);
		worker2.setName("Worker 2");
		worker2.getCalculator().setDelay(1000);
		worker2.setInstanceId("calculator");
		worker2.run();
		*/

        while (!worker1.hasTerminated() /*|| !worker2.hasTerminated()*/) {
            Thread.sleep(100);
        }

    }

}
