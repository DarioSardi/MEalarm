# MEalarm system
This is a simplified version of the structure of the alarms present on medical equipment.
Safety conditions and requirements are defined as in IEC 60601-1-8.

## alarm specifications
There are two types of alarm:
- non-latching that automaticaly reset after the triggering event no longer exist.
- latching alarms that needs a manual reset performed by an operator.

Both alarm types have one or two treshold values that are used to determine the alarm condition.
If the alarm condition is met an auditory alarm signal is generated and a visual alarm symbol is displayed for the operator to see.<br/>
The alarm condition can be acknowledged by an operator disabling for a short  or indefinite period of time the audio relative to that single alarm condition.
The acknowledgment status cease to exist when the relative alarm condition is no longer true.<br/>
For each alarm audio can be paused or turned off and the alarm itself can be disabled by an operator.

## alarm timers
- minimum time for the alarm to be active avoiding alarm burst too short to be noticed
- delay between the time when the alarm condition is rised and when the alarm is triggered. If the alarm condition cease to exist too fast no alarm is activated but the condition is logged.
- maximum time for the timed ack to be active. After that delay the audio signals are turned on again.

## tresholds
for this project each alarm has a min_th and a max_th value where a value is considered safe if is between the two.<br/>
All values considered are between 0 and 100. 

## Data stream
Only four values are analyzed by the program.<br/>
After the inizialization the values can increase/decrease following the idea of a 1D perlin noise to generate data with smooth transition between "alarmCondition" and "safeCondition".<br/>
For testing purposes will be implemented a function for injecting "cooked" data streams from files.

## Data processing
The model follow a linear read->analyze model for updating data and alarm status due to the low number of data streams.

## GUI
A visual gui can be used to visualize data and alarms in real time.
coloured buttons indicate the alarm status:
- Green: no alarm
- Orange: alarm condition rised but no alarm triggered
- Red: visual and acustic alarm are triggered

Clicking on a "red" signal will send a timed ack to the related alarm.