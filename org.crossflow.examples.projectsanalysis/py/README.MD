# ProjectsAnalysis Python Worker
This a Python3 based worker for ProjectsAnalysis

## Setup
Python 3 is required to run this worker and can be acquired from [here](https://www.python.org/).

### Dependencies
The following third-party dependencies are also required. It is recommended that a [virtual env is setup before installing any python dependencies](https://docs.python-guide.org/dev/virtualenvs/)
* [stomp.py v4.1.22](https://pypi.org/project/stomp.py/)
* [xmltodict v0.12.0 or higher](https://pypi.org/project/xmltodict/)

For convienence a `requirements.txt` has been generated containing these dependencies and can be installed using `pip install -r requirements.txt`

### Python Path
Ensure that all generated and user code is on the same PYTHONPATH variable. Within Eclipse this is achieved by marking the relevant source folder roots.

## Running
Once the above setup has been completed the worker can be run using 
```
python main.py
```
For a list of applicable command line options:
```
python main.py -h
```