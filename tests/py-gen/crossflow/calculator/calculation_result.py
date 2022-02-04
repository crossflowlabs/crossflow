from enum import Enum, unique
from typing import Union


from crossflow.runtime import Job
from crossflow.calculator.calculation import Calculation


@unique
class WorkerLang(Enum):
    """NOTE: Auto-generated by Type2Class on 2022-01-05T17:43:29.450708Z
    Do not edit this class manually
    """

    PYTHON = "python"
    JAVA = "java"


class CalculationResult(Calculation):
    """NOTE: Auto-generated by Type2Class on 2022-01-05T17:43:29.450708Z
    Do not edit this class manually
    """

    def __init__(self,
        worker: str = None,
        result: str = None,
        worker_lang: WorkerLang = None,
        a: int = None,
        b: int = None,
        operator: str = None,
        correlation_id: Union["CalculationResult", Job, str] = None
    ):
        super().__init__(correlation_id=correlation_id)
        self._worker = worker        
        self._result = result        
        self._worker_lang = worker_lang        
        self._a = a        
        self._b = b        
        self._operator = operator        

    @property
    def worker(self) -> str:
        return self._worker

    @worker.setter
    def worker(self, worker: str):
        self._worker = worker

    @property
    def result(self) -> str:
        return self._result

    @result.setter
    def result(self, result: str):
        self._result = result

    @property
    def worker_lang(self) -> WorkerLang:
        return self._worker_lang

    @worker_lang.setter
    def worker_lang(self, worker_lang: WorkerLang):
        self._worker_lang = worker_lang

    def __str__(self):
        return f"CalculationResult ( worker={str(self.worker)}, result={str(self.result)}, worker_lang={str(self.worker_lang)}, job_id={str(self.job_id)}, correlation_id={str(self.correlation_id)}, destination={str(self.destination)} )"