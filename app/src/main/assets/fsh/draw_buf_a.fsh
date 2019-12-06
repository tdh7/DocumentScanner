precision highp float;
uniform vec2                iResolution;
uniform vec4 				iMouse;
uniform sampler2D           iChannel0;
varying vec2                vTextureCoord;

uniform vec3 brushCol;
uniform float iSize;

const vec2 bufB_mouse_uv = vec2(0,0);
const vec2 bufB_soft_uv  = vec2(20.,0);

#define inside(a) (fragCoord.x == a.x+0.5 && fragCoord.y == a.y+0.5)
#define load(a,b) texture2D(b,(a+0.5)/iResolution.xy)
#define save(a,b) if(inside(a)){fragColor=b;return;}

#define T iGlobalTime
#define PI 3.1415926359
#define TAU (PI*2.0)

vec2 sdLine(vec2 p, vec2 a, vec2 b, float r)
{
    vec2 ab = b-a;
    float t = dot(p-a,ab)/dot(ab,ab);
    //if (t<0.0 || t > 1.0) { return 0.; }
    return vec2(length(p-a-ab*clamp(t,0.,1.))-r,t);
}

float udBox(vec2 p,vec2 s)
{
    return length(max(abs(p)-s,0.));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy/iResolution.xy;
    vec2 uva = (2.*fragCoord.xy-iResolution.xy)/iResolution.yy;
    float a = iResolution.x/iResolution.y;

    if (fragCoord.x+fragCoord.y < 1.) {
        //store old mouse position in top left pixel for smoothed line drawing
        // fragColor = vec4(iMouse.xy,clamp(iMouse.w*1e4,0.,1.),0.);
        fragColor = vec4(0.0);
        return;
    }

    fragColor = load(fragCoord-0.5, iChannel0);

    vec3  bufB_col   = brushCol;
    vec4  bufB_mouse = load(bufB_mouse_uv, iChannel0);
    float bufB_size  = iSize * 0.03 / 36.0;
    float bufB_soft  = load(bufB_soft_uv,  iChannel0).x;

    vec2 mouse_current = iMouse.xy/iResolution.yy;
    vec2 mouse_last    = bufB_mouse.xy/iResolution.yy;
    vec2 mouse_aspect  = (2.*iMouse.xy-iResolution.xy)/iResolution.yy;
    vec2 dmouse        = mouse_current-mouse_last;

    if (bufB_mouse.w<1.0) { mouse_last = mouse_current; }

    vec2 stroke = sdLine(fragCoord.xy/iResolution.yy, mouse_current, mouse_last, bufB_size);

    fragColor = vec4(0.0);

    if (iMouse.w > 0.0  && stroke.x < 0.0)
    {
        vec4 col = fragColor;

        fragColor.rgb = bufB_col;
        fragColor.a = smoothstep(stroke.x,stroke.x+0.1*bufB_soft+0.005,0.0);
    }

}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}