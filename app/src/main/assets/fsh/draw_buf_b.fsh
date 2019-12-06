// Draw layer - used to hold the finished brushstrokes.
// UI state layer
precision highp float;
uniform vec2                iResolution;
uniform vec4 				iMouse;
uniform sampler2D           iChannel0; //1
varying vec2                vTextureCoord;

const vec2 bufB_mouse_uv  = vec2(0,0);
const vec2 bufB_size_uv   = vec2(3,0);

#define inside(a) (fragCoord.x == a.x+0.5 && fragCoord.y == a.y+0.5)
#define load(a,b) texture2D(b,(a+0.5)/iResolution.xy)
#define save(a,b) if(inside(a)){fragColor=b;return;}

float udBox(vec2 p,vec2 s)
{
    return length(max(abs(p)-s,0.));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    save(bufB_mouse_uv,iMouse);
    fragColor = load(fragCoord-0.5, iChannel0);

    float bufB_size   = load(bufB_size_uv,   iChannel0).x;

    vec2 uv = fragCoord.xy/iResolution.xy;
    vec2 uvm = iMouse.xy/iResolution.xy;

    float a = iResolution.x/iResolution.y;

    vec2 p;

    if (inside(bufB_size_uv))
    {
        // Size slider
   		p = vec2(a-0.28-0.03,-1.0+0.03+0.03);
   	 	if (udBox(p,vec2(0.28,0.03))==0.0) fragColor.x = p.x/(0.28*2.0)+0.5;
        if (fragColor.x == 0.0) { fragColor.x = 0.3; }
    }
}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}