//worker.js A simple Gears WorkerPool demo
google.gears.workerPool.onmessage = function(a, b, message) {
	google.gears.workerPool.sendMessage(doWork(message.body), message.sender);
};

function doWork(message) {
	message.alarm('YEAH');
	//alert(message);
	// Busy wait for 5 seconds
	/*var start = new Date();
	while (true) {
		var busyCounter = 10000;
		var busyVar;
		while (busyCounter-- > 0) {
			busyVar = "garbage" + busyCounter;
		}
		var now = new Date();
		if (now.getTime() - start.getTime() > 5000) {
			break;
		}
	}*/

	return "Approved: " + message;
};