var fragCode = `
precision mediump float;

varying vec3 c;

void main(void) {
	gl_FragColor = vec4(c, 1.0);
}
`;