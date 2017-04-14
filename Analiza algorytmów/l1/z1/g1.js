var vertCode=`
//pixel shader

attribute vec2 position;
attribute vec3 color;

varying vec3 c;

void main(void) {
	c = color;
	gl_Position = vec4(position, 0.0, 1.0);
	gl_PointSize = 4.0;
}
`;


