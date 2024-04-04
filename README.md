# Multi-threaded Cooperative Operating System Emulator
This is a cooperative single core OS made in java for my ICSI 412 class. 

## How it works:
This OS Emulator creates a seperate thread for each individual UserLand Process, and a thread for the Kernel. Each UserLand Process runs int it's own thread and pauses it's own thread and passes the necessary information to a designated spot in the OS. Then the kernel thread runs and performs the requested action.

## Supported Kernel Calls:
### Process Management:
- OS.createProcess(): Create a new process. Optionaly with a specified priority.
- OS.switchProcess(): Switch between processes.
- OS.sleep(): Puts the calling process to sleep for a specified amount of time.
### Device I/O:
- OS.open(): Opens a device, given a device and a specifier, and returns a file ID.
- OS.read(): Reads from the specified device.
- OS.write(): Writes to a specified device.
- OS.seek(): Moves the filePointer within the device.
- OS.close(); Safely closes the speficied device.
### Interprocess Communication:
- OS.getPID(): Gets the PID of the current process.
- OS.searchPID(): Finds a process with a specified name.
- OS.sendMessage(): Sends a message to the process with the specified PID.
- OS.waitMessage(): Retrieves the first message on it's message queue, or waits until it receives a new message.
### Virtal Memory Management:
- OS.allocateMemory(): Allocates simulated physical pages on the hard drive with continuous virtual page numbers.
- OS.freeMemory(); Frees allocated memory for use by other processes.
