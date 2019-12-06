precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
uniform sampler2D iChannel1;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
    vec4 color1 = texture2D(iChannel0, uv);
    vec4 color2 = texture2D(iChannel1, uv);
    if(color2.a == 0.0){
    	fragColor = color1;
    }
    else{
    	fragColor = color2;
    }
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}