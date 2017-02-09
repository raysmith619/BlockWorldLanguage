import javax.swing.JFrame

import sys
import re
import traceback

from BlockWorld import *
from BlockW import *
parms = sys.argv
print("Args:")
for arg in parms:
	print(arg + " ")
print("")
		
trace = BwTrace("BlockWorld.properties")
##trace.setAccept(1)
##trace.setTokenAccept(1)
##trace.setTokQueue(1)
##trace.setAll()
##trace.clearAll()
##trace.setInput(1)
bExec = BwExec(trace)
parser = bExec.getParser()
					# Jython BW script support
bW = BlockW(trace=trace, bExec=bExec)
	
graphicsFrame = javax.swing.JFrame("Graphics") 
graphicsFrame.setVisible(True)       # Display the window.
controls = BwControls(trace, bExec)
controls.setVisible(True)
		 


nfile = 0						# Count files processed
i = 1							# Start with first arg
								# Process flags -<...>
length = len(parms)
while i < length:
	arg = parms[i]
	i += 1 
	match = re.match("^-{1,2}(.*)", arg)
	if not match:
		break		# No more flags
	opt = match.group(1)
	m2 = re.match("^(verbose|v)$", opt)
	if m2:
		trace.setVerbose(1)
	elif re.match("^(ifile|if)$", opt):
		inFile = parms[i]			# Name follows
		i += 1
		if  not bW.procFile(inFile):
			print("Quitting")
			exit(1)

		nfile += 1		# Count files processed

	elif re.match(r"^(run|rl)$", opt):
		runListFile = parms[i]
		i += 1
		if not bW.runList(runListFile):
			print("Quitting\n")
			sys.exit(1)
		nfile += 1		# Count files processed
	elif opt.lower == "trace":
		trace_spec = parms[i]
		i += 1
		trace_levels = trace_spec.split(",")
		for trace_level in trace_levels:
			trace.setLevel(trace_level);
	elif re.match(r"^(timeLimit|tl)$", opt):
		timeLimit = float(parms[i])
		i += 1
		bW.setTimeLimit(timeLimit)		# for all types of files
				
	
	
	elif "trace" == opt.lower():
		trace_spec = parms[i]
		i += 1
		trace_levels = trace_spec.split(",")
		for trace_level in trace_levels:
			trace.setLevel(trace_level)

	else:
		print("Unrecognized option:'%s' - Quitting", arg)
		exit(1)
"""
Use sample if no data commands
and no files processed
"""

if nfile == 0 and i >= length:
	i = 0				# Start at beginning of sample
	parms = [
		#"add block color=red loc=.1 size=.5",
		"add block color=1,0,0 loc=.1 size=.5",
		#"add sphere color=blue loc=.1,.2,.3 size=.5",
		"add block color=1,1,0 loc=-12,-2,-2 size=1",
		"add sphere color=0,0,1 loc=4.5,5,2 size=2",
		"add cone color=0,1,0 loc=5,5,6 size=1,4",
		"add cylinder color=0,1,1 loc=4,4,1 size=1,8",
		"display",
		]


for arg in parms[i:]:
	pat_ends_with_semi = re.match("[ \b\t]*$", arg)
	if not pat_ends_with_semi:
		arg += ";"			# Terminate last command on line

	try:
		if not parser.procInput(arg):
			print("Quitting\n")
			if bExec.isError():
				print("Error: %s\n", bExec.errorDescription())
				sys.stderr.write("Quitting\n")
				exit(1)

			exit(0)

	except:
		# TODO Auto-generated catch block
		traceback.print_exc()

						# Default - display if not already displayed
"""
Why is DISPLAY_SCENE not recognized???
if bExec.size() > 0:
	if bExec.getCmd(bExec.size()-1).getCmd_type() != BwCmdType.DISPLAY_SCENE):
		cmd = BwCmd(BwCmdType.DISPLAY_SCENE)
		cmd.setComplete()
		bExec.addCmd(cmd)
"""

if bExec.isError():
	nerror = bExec.getnError()
	print("%d errors\n", nerror)
	try:
		first_error = bExec.firstError()
		print("First Error: %s\n", first_error.errorDescription())
		exit(1)
	except:
		# TODO Auto-generated catch block
		traceback.print_exc()
	

# Display commands
if bExec.size() == 0:
	print("No Display Commands - quitting\n")
	exit(0)

bExec.display()


