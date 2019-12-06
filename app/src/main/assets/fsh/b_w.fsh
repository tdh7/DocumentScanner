precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D iChannel0;

vec4 blend(vec3 first, vec3 second, float alpha){
	return vec4(mix(first, second, alpha), 1.0);
}

void main() {
    vec4 tc = texture2D(iChannel0, vTextureCoord);
    float bw = tc.r * 0.2 + tc.g * 0.88;
    bw += (1.0 - bw) * tc.b;
    gl_FragColor = vec4(vec3(bw), 1.0);
}