from typing import Union


from crossflow.runtime import Job


class SleepTime(Job):
    """NOTE: Auto-generated by Type2Class on 2022-01-05T17:43:30.820254Z
    Do not edit this class manually
    """

    def __init__(self,
        seconds: int = None,
        correlation_id: Union["SleepTime", Job, str] = None
    ):
        super().__init__(correlation_id=correlation_id)
        self._seconds = seconds        

    @property
    def seconds(self) -> int:
        return self._seconds

    @seconds.setter
    def seconds(self, seconds: int):
        self._seconds = seconds

    def __str__(self):
        return f"SleepTime ( seconds={str(self.seconds)}, job_id={str(self.job_id)}, correlation_id={str(self.correlation_id)}, destination={str(self.destination)} )"