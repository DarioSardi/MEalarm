asm MeAlarm

import StandardLibrary
import CTLlibrary
signature:
	enum domain AlarmStatus = {OFF | ALARMCONDITION | ON_LATCHING | WAITING_RESET | ON | DISABLED }
	enum domain AudioStatus = {AUDIOON | AUDIOOFF}
	domain Percentage subsetof Integer
	static minValue : Percentage
	static maxValue : Percentage
	static isLatching : Boolean
	dynamic controlled alarm : AlarmStatus
	dynamic controlled audio : AudioStatus	
	dynamic controlled acked : Boolean
	dynamic monitored newValue: Percentage
	dynamic monitored changeAudioStatusTo : Boolean
	dynamic monitored wantToAckIt : 		Boolean
	derived isValueAllarming: Percentage -> Boolean
	

definitions:
	domain Percentage = {0,1,2,3,4,5,6,7,8,9,10} //smv
	function minValue = 7
	function maxValue = 10
	function isLatching = false
	function isValueAllarming($v in Percentage)=($v>maxValue or $v<minValue) 
	
	
	rule r_alarmConditionTrigger =
		if((alarm=OFF or alarm=WAITING_RESET)and isValueAllarming(newValue)) then alarm := ALARMCONDITION endif 
	
	
	rule r_switchOff =
		if( (exist $p in {OFF,WAITING_RESET,ALARMCONDITION,DISABLED,ON} with $p=alarm) and isValueAllarming(newValue)=false )then 
			par
			alarm := OFF
			audio:=AUDIOOFF
			endpar
		endif
	
	rule r_switchOn =
		if ((exist $p in {ALARMCONDITION,ON} with $p=alarm) and isLatching=false and isValueAllarming(newValue)=true) then 
			seq
			alarm:=ON
			if(changeAudioStatusTo and audio=AUDIOOFF and not acked) then 
				audio:=AUDIOON 
			else if(changeAudioStatusTo=false and audio=AUDIOON and not acked) then 
				audio:=AUDIOOFF 
			endif endif
			if(acked) then
				seq
				audio:=AUDIOON 
				acked:=false
				endseq
			endif
			endseq
		endif
	
	rule r_latching =
		if ((exist $p in {ALARMCONDITION,ON_LATCHING} with alarm=$p) and isLatching and isValueAllarming(newValue)) then 
			seq
			alarm:=ON_LATCHING
			if(changeAudioStatusTo and audio=AUDIOOFF and not acked) then 
				audio:=AUDIOON 
			else if(changeAudioStatusTo=false and audio=AUDIOON and not acked) then 
				audio:=AUDIOOFF 
			endif endif
			if(acked) then
				seq
				audio:=AUDIOON 
				acked:=false
				endseq
			endif
			endseq
		endif
	
	rule r_waitingReset =
		if((exist $p in {WAITING_RESET,ON_LATCHING} with alarm=$p) and isLatching and isValueAllarming(newValue)=false) then 
			par
			alarm:=WAITING_RESET
			audio:=AUDIOOFF
			endpar
		endif
	

	rule r_ack =
		if (( alarm=ON or alarm=ON_LATCHING) and not(acked) and audio=AUDIOON and isValueAllarming(newValue)) then 
			if(wantToAckIt) then
				seq
				audio:=AUDIOOFF
				acked := true
				endseq
			endif endif
				

	invariant over isValueAllarming	: isValueAllarming(newValue)=true implies (newValue>maxValue or newValue<minValue)
	invariant over acked			: acked implies audio=AUDIOOFF
	/*invariant over audio,alarm		: isValueAllarming(newValue) implies (alarm=ON or alarm=ON_LATCHING or alarm=ALARMCONDITION) */
	
	CTLSPEC ag(acked and audio=AUDIOOFF and (alarm=ON or alarm=ON_LATCHING))
	CTLSPEC ag( (alarm=ON or alarm=ON_LATCHING or alarm=ALARMCONDITION ) implies isValueAllarming(newValue))
	CTLSPEC ag( (alarm=OFF or alarm=WAITING_RESET) implies  isValueAllarming(newValue))
	CTLSPEC ef(alarm=ON or alarm=ON_LATCHING)
	CTLSPEC ef(audio=AUDIOON or audio=AUDIOOFF)
	
	
	
main rule r_Main =  
	par
		r_switchOn[]
		r_alarmConditionTrigger[]
		r_switchOff[]
		r_latching[]
		r_waitingReset[]
		r_ack[]
	endpar
		
default init s0:
	function alarm=OFF
	function audio=AUDIOOFF
	function acked=false