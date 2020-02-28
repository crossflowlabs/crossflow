from org.crossflow.runtime import Mode
from org.crossflow.examples.wordcount.xtext.WordCountWorkflow import WordCountWorkflow
import time

worker = WordCountWorkflow(mode=Mode.WORKER)
worker.setMaster('localhost')
worker.setInstanceId('wc')
worker.setName('workerPY')
worker.run()
while (not worker.hasTerminated()):
    time.sleep(0.1)
print("terminated")