from crossflow.concurrency.sleeper import SleeperBase

from crossflow.concurrency.sleep_time import SleepTime
from crossflow.concurrency.result import Result


class Sleeper(SleeperBase):

    def __init__(self):
        super().__init__()
        
    def consume_sleep_times(self, input_job: SleepTime):
        print(f"{self.workflow.name}:Sleeper:consume_sleep_times received {input_job}")
    
        # TODO: Add impementation here
        # Send output jobs
        
        Result_output = Result()
        return Result_output
    
    def on_consume_sleep_times_timeout(self, input_job: SleepTime):
        super().on_consume_sleep_times_timeout(input_job)
        
    def on_consume_sleep_times_cancelled(self, input_job: SleepTime):
        super().on_consume_sleep_times_cancelled(input_job)
        
