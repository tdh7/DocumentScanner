precision mediump float;

uniform vec2              iResolution;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                vTextureCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
	fragColor = max(texture2D(iChannel0,uv),texture2D(iChannel1,uv+0.002));
}


void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}