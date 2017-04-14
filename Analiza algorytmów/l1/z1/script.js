function makeVertShader() {
	var vertShader = gl.createShader(gl.VERTEX_SHADER);
	gl.shaderSource(vertShader,vertCode);
	gl.compileShader(vertShader);
	
	if(!gl.getShaderParameter(vertShader, gl.COMPILE_STATUS)) {
		var error = gl.getShaderInfoLog(vertShader);
		console.log(vertCode);
		console.log("VERT SHADER ERROR\n"+error);
	}
	return vertShader;
}

function makeFragShader() {
	var fragShader = gl.createShader(gl.FRAGMENT_SHADER);
	gl.shaderSource(fragShader,fragCode);
	gl.compileShader(fragShader);
	
	if(!gl.getShaderParameter(fragShader, gl.COMPILE_STATUS)) {
		var error = gl.getShaderInfoLog(fragShader);
		console.log(fragCode);
		console.log("FRAG SHADER ERROR\n"+error);
	}
	return fragShader;
}

function bindBuffers(shader) {
	gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
	var coord = gl.getAttribLocation(shader, "position");
	gl.vertexAttribPointer(coord,2,gl.FLOAT,false,0,0);
	gl.enableVertexAttribArray(coord);

	gl.bindBuffer(gl.ARRAY_BUFFER, cBuffer);
	var color = gl.getAttribLocation(shader, "color");
	gl.vertexAttribPointer(color,3,gl.FLOAT,false,0,0);
	gl.enableVertexAttribArray(color);
}

function createShader() {
	var shader = gl.createProgram();
	gl.attachShader(shader, makeVertShader());
	gl.attachShader(shader, makeFragShader());
	gl.linkProgram(shader);
	gl.useProgram(shader);
	
	bindBuffers(shader);
	gl.viewport(0, 0, canvas.width, canvas.height);
}

function buf1() {
	gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(v1), gl.STREAM_DRAW);
	gl.bindBuffer(gl.ARRAY_BUFFER, cBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(c1), gl.STREAM_DRAW);
}

function buf2() {
	gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(v2), gl.STREAM_DRAW);
	gl.bindBuffer(gl.ARRAY_BUFFER, cBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(c2), gl.STREAM_DRAW);
}

function buf3() {
	gl.bindBuffer(gl.ARRAY_BUFFER, vBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(v3), gl.STREAM_DRAW);
	gl.bindBuffer(gl.ARRAY_BUFFER, cBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(c3), gl.STREAM_DRAW);
}

function draw() {
	gl.clearColor(1, 1, 1, 1);
	gl.clear(gl.DEPTH_BUFFER_BIT);
	gl.clear(gl.COLOR_BUFFER_BIT);
	if (v1.length > 0) {
		buf1();
		gl.drawArrays(gl.TRIANGLES, 0, v1.length/2);
	}
	if (v2.length > 0) {
		buf2();
		gl.drawArrays(gl.LINES, 0, v2.length/2);
	}
	if (v3.length > 0) {
		buf3();
		gl.drawArrays(gl.POINTS, 0, v3.length/2);
	}
}

function addTriangle(p1, p2, p3, pc1, pc2, pc3) {
	v1.push(p1[0]);
	v1.push(p1[1]);
	v1.push(p2[0]);
	v1.push(p2[1]);
	v1.push(p3[0]);
	v1.push(p3[1]);
	
	c1.push(pc1[0]);
	c1.push(pc1[1]);
	c1.push(pc1[2]);
	c1.push(pc2[0]);
	c1.push(pc2[1]);
	c1.push(pc2[2]);
	c1.push(pc3[0]);
	c1.push(pc3[1]);
	c1.push(pc3[2]);
}

function addLine(p1, p2, pc1, pc2) {
	v2.push(p1[0]);
	v2.push(p1[1]);
	v2.push(p2[0]);
	v2.push(p2[1]);
	
	c2.push(pc1[0]);
	c2.push(pc1[1]);
	c2.push(pc1[2]);
	c2.push(pc2[0]);
	c2.push(pc2[1]);
	c2.push(pc2[2]);
}

function addPoint(p1, pc1) {
	v3.push(p1[0]);
	v3.push(p1[1]);
	
	c3.push(pc1[0]);
	c3.push(pc1[1]);
	c3.push(pc1[2]);
}

function clear() {
	v1 = [];
	v2 = [];
	v3 = [];
	c1 = [];
	c2 = [];
	c3 = [];
	
	addLine([-0.9, -0.95], [-0.9, 0.95], black, black);
	for (var i=0;i<4;i++) {
		var y = i*0.45-0.45;
		addLine([-0.95, y], [-0.85, y], black, black);
	}
	addLine([-0.95, -0.9], [0.95, -0.9], black, black);
	for (var i=0;i<length/5;i++) {
		var x = (5+i*5)*1.8/length-0.9;
		addLine([x, -0.95], [x, -0.85], black, black);
	}
	
}

clear();
createShader();
draw();