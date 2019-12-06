precision mediump float;
uniform vec2                iResolution;
uniform sampler2D           iChannel0;
varying vec2                vTextureCoord;

void mainImage(out vec4 c, vec2 p)
{
	c = texture2D(iChannel0, p.xy / iResolution.xy);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}