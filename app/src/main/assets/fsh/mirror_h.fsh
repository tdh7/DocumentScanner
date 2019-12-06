precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;

    if(uv.y < 0.5){
        uv.y = 0.5 - uv.y;
    }
    else{
        uv.y -= 0.5;
    }

	fragColor = texture2D(iChannel0, uv);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}