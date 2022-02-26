from crossflow.testspython.task_a import TaskABase

from crossflow.testspython.type_a import TypeA


class TaskA(TaskABase):

    def __init__(self):
        super().__init__()
        
    def consume_queue_a(self, input_job: TypeA):
        print(f"{self.workflow.name}:TaskA:consume_queue_a received {input_job}")
    
        # TODO: Add impementation here
        # Send output jobs
        
    
    def on_consume_queue_a_timeout(self, input_job: TypeA):
        super().on_consume_queue_a_timeout(input_job)
        
    def on_consume_queue_a_cancelled(self, input_job: TypeA):
        super().on_consume_queue_a_cancelled(input_job)
        
