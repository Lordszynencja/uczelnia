function valueToHeight(val) {
	if (val > 2) return 0.9;
	return val*0.9-0.9;
}

function positionToWidth(pos) {
	return pos*1.8/10000-0.9;
}

function positionToWidthLog(pos) {
	return Math.log(pos)*1.8/Math.log(10000)-0.9;
}

function compute(n, a) {
	return Math.sqrt((n-399)/(n*a*398));
}

function drawResult(name, colorId) {
	var color = colors[colorId];
	var data = results[name];
	for (var i=0;i<10000;i++) {
		var x = positionToWidth(i+1);
		var y = valueToHeight(data[i]/(i+1));
		addPoint([x, y], color);
	}
	console.log(name + " - " + colors_names[colorId]);
}

function printResults() {
	drawResult('k400_1', 4);
	drawResult('k400_2', 5);
	drawResult('k400_3', 6);
}

function addSigmasPoints(i, sigm1, sigm2, sigm3) {
	addPoint([positionToWidth(i+1), valueToHeight(1+sigm1)], black);
	addPoint([positionToWidth(i+1), valueToHeight(1+sigm2)], black);
	addPoint([positionToWidth(i+1), valueToHeight(1+sigm3)], black);
	addPoint([positionToWidth(i+1), valueToHeight(1-sigm1)], black);
	addPoint([positionToWidth(i+1), valueToHeight(1-sigm2)], black);
	addPoint([positionToWidth(i+1), valueToHeight(1-sigm3)], black);
}

function printCzeb() {
	printResults();
	for (var i=0;i<10000;i++) {
		addSigmasPoints(i, compute(i+1, 0.05), compute(i+1, 0.01), compute(i+1, 0.005));
	}
	draw();
}

var CzernoffSigmas = [0.135, 0.1685, 0.182];

function printCzern() {
	printResults();
	for (var i=0;i<10000;i++) {
		addSigmasPoints(i, CzernoffSigmas[0], CzernoffSigmas[1], CzernoffSigmas[2]);
	}
	draw();
}

function printBoth() {
	printResults();
	for (var i=0;i<10000;i++) {
		addSigmasPoints(i, compute(i+1, 0.05), compute(i+1, 0.01), compute(i+1, 0.005));
		addSigmasPoints(i, CzernoffSigmas[0], CzernoffSigmas[1], CzernoffSigmas[2]);
	}
	draw();
}

printBoth();
