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

function printAll() {
	clear();
	var options = ['k2', 'k3', 'k10', 'k20', 'k50', 'k100', 'k200', 'k400', 'k1000'];
	for (var i=0;i<options.length;i++) {
		drawResult(options[i], i);
	}
	draw();
}

printAll();
