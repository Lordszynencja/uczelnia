const tests = 10000;

var results = {};

function valueToHeight(val) {
	return val*2*1.8-0.9;
}

function positionToWidth(pos) {
	return pos*1.8/length-0.9;
}

function calcVariance(data) {
	var n = data.length;
	var sum = 0;
	for (var i=0;i<n;i++) {
		sum += data[i];
	}
	var expected = sum/n;
	var varSum = 0;
	for (i=0;i<n;i++) {
		var x = data[i]-expected;
		varSum += x*x;
	}
	return varSum/n;
}

function calculateResults(data, n) {
	var result = {
		n : n,
		data : data
	};
	var sum = 0;
	for (var i=0;i<tests;i++) {
		sum += data[i];
	}
	var expected = sum/tests;
	result.expected = expected;
	var varSum = 0;
	for (i=0;i<tests;i++) {
		var x = data[i]-expected;
		varSum += x*x;
	}
	result.variance = varSum/tests;
	return result;
}

function printResult(result, color) {
	var values = [];
	var data = result.data;
	for (var i=0;i<data.length;i++) {
		if (values[data[i]]==undefined) values[data[i]] = 0;
		values[data[i]]++;
	}
	
	var previousValue = values[1]/tests;
	if (previousValue == undefined) {
		previousValue = 0;
	}
	addPoint([positionToWidth(1), valueToHeight(previousValue)], color);
	for (var i=2;i<values.length;i++) {
		var newValue = values[i]/tests;
		if (isNaN(newValue)) {
			newValue = 0;
		}
		addLine([positionToWidth(i-1), valueToHeight(previousValue)], [positionToWidth(i), valueToHeight(newValue)], color, color);
		addPoint([positionToWidth(i), valueToHeight(newValue)], color);
		previousValue = newValue;
	}
}

function try1(n) {
	var tries = 0;
	var sending = 0;
	while (sending != 1) {
		tries++;
		sending = 0;
		for (var j=0;j<n;j++) {
			if (Math.random() <= 1/n) {
				sending++;
			}
		}
	}
	return tries;
}

var t = [];

function try2(n, u) {
	var minChance = 1/u;
	var chance = 1;
	var tries = 0;
	var sending = 0;
	while (sending != 1) {
		chance = chance/2;
		tries++;
		sending = 0;
		for (var j=0;j<n;j++) {
			if (Math.random() <= chance) {
				sending++;
			}
		}
		t[tries] = chance;
		if (chance<minChance) chance = 1;
	}
	return tries;
}

function test1a(name, n) {
	if (name != undefined && n != undefined && results[name] == undefined) {
		results[name] = {};
		var data = [];
		for (var i=0;i<tests;i++) {
			data[i] = try1(n);
		}
		results[name] = calculateResults(data, n);
	}
}

function test1b(name, n, u) {
	if (name != undefined && n != undefined && results[name] == undefined) {
		results[name] = {};
		var data = [];
		var roundLength = Math.ceil(Math.log2(u));
		var lambda = 0;
		for (var i=0;i<tests;i++) {
			data[i] = try2(n, u);
			if (data[i]<=roundLength) lambda++;
		}
		results[name] = calculateResults(data, n);
		results[name].u = u;
		results[name].roundLength = roundLength;
		results[name].lambda = lambda/tests;
	}
}

function test2(name = "test", u = 1000, testsNo = 10000) {
	if (name != undefined && results[name] == undefined) {
		results[name] = {};
		var data = [];
		var roundLength = Math.ceil(Math.log2(u));
		var lambda = 0;
		for (var i=0;i<testsNo;i++) {
			var n = 0;
			while (n<=1) n = Math.random()*u;
			n = Math.ceil(n);
			data[i] = try2(n, u);
			if (data[i]<=roundLength) lambda++;
		}
		results[name] = calculateResults(data, u);
		results[name].u = u;
		results[name].roundLength = roundLength;
		results[name].lambda = lambda/testsNo;
	}
} 

function z1a() {
	var time = new Date();
	test1a("z1a1", 10);
	test1a("z1a2", 100);
	test1a("z1a3", 1000);
	test1a("z1a4", 10000);
	console.log("a finished, "+(new Date()-time)/1000);
}

function z1b() {
	var time = new Date();
	test1b("z1b1", 2, 100);
	test1b("z1b2", 50, 100);
	test1b("z1b3", 100, 100);
	console.log("b finished, "+(new Date()-time)/1000);
}

function z1c() {
	var time = new Date();
	test1b("z1c1", 2, 10000);
	test1b("z1c2", 5000, 10000);
	test1b("z1c3", 10000, 10000);
	console.log("c finished, "+(new Date()-time)/1000);
}

function calcAll() {
	z1a();
	z1b();
	z1c();
}

function printAll() {
	clear();
	console.log("blue - z1a");
	z1a();
	printResult(results.z1a1, blue1);
	printResult(results.z1a2, blue2);
	printResult(results.z1a3, blue3);
	printResult(results.z1a4, blue4);
	console.log("red - z1b");
	z1b();
	printResult(results.z1b1, red1);
	printResult(results.z1b2, red2);
	printResult(results.z1b3, red3);
	console.log("yellow - z1c");
	z1c();
	printResult(results.z1c1, yellow1);
	printResult(results.z1c2, yellow2);
	printResult(results.z1c3, yellow3);
	draw();
}

function drawResult(name) {
	var result = results[name];
	drawSłupki(result.data, colors[Math.floor(Math.random()*colors.length)]);
	console.log()
}

function drawSłupki(data, color) {
	clear();
	var values = [];
	for (var i=0;i<data.length;i++) {
		if (values[data[i]]==undefined) values[data[i]] = 0;
		values[data[i]]++;
	}
	
	for (var i=1;i<values.length;i++) {
		var value = values[i]/tests;
		if (isNaN(value)) {
			value = 0;
		}
		var x = positionToWidth(i);
		var x1 = x-0.5/length;
		var x2 = x+0.5/length
		var y = valueToHeight(value);
		addTriangle([x1, -0.9], [x2, -0.9], [x1, y], color, color, color);
		addTriangle([x2, -0.9], [x2, y], [x1, y], color, color, color);
	}
	draw();
}

calcAll();
printAll();
