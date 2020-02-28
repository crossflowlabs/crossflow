from crossflow.runtime import JobStream, QueueInfo, Workflow


class RepositoryAnalysisResults(JobStream):
    """NOTE: Auto-generated by Steam2Class on 2019-12-19T16:51:07.797Z    
    Do not edit this class manually
    """

    def __init__(self, workflow: Workflow, enable_prefetch: bool):
        super().__init__("RepositoryAnalysisResults", workflow)

        # RepositoryAnalysisResultSink
        self._pre["RepositoryAnalysisResultSink"] = QueueInfo(
            f"RepositoryAnalysisResultsPre.RepositoryAnalysisResultSink.{self.workflow.instance}",
            False
        )
        self._destination['RepositoryAnalysisResultSink'] = QueueInfo(
            f"RepositoryAnalysisResultsDestination.RepositoryAnalysisResultSink.{self.workflow.instance}",
            False
        )
        self._post['RepositoryAnalysisResultSink'] = QueueInfo(
            f"RepositoryAnalysisResultsPost.RepositoryAnalysisResultSink.{self.workflow.instance}",
            False,
            0 if enable_prefetch else 1
        )
        
    
    def add_consumer(self, consumer, consumer_id: str):
        post_queue = self._post[consumer_id]
        # only connect if the consumer exists (for example it will not in a master_bare situation)
        if consumer is not None:
            def handler(message, ack_func=None):        
                try:
                    obj = self.workflow.serializer.deserialize(message)
                    consumer.consumeRepositoryAnalysisResultsWithNotifications(obj)
                    if ack_func is not None:
                        ack_func()
                except Exception as ex:
                    self.workflow.local_logger.exception("")
                    self.workflow.report_internal_exception(ex)

            self.subscribe(post_queue, handler)
