var fragCode=`
//fragment shader
precision mediump float;
#define defaultAlpha 1.0

varying vec3 c;

void main(void) {
	gl_FragColor = vec4(c, defaultAlpha);
}
`;