precision highp float;

uniform vec2                iResolution;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                vTextureCoord;

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    vec2 uv = fragCoord.xy/iResolution.xy;
    vec4 stroke = texture2D(iChannel0, uv);
    vec4 camera = texture2D(iChannel1, uv);

    if(stroke.a == 0.0){
    	fragColor = camera;
    }
    else{
    	fragColor = stroke;
    }
}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}