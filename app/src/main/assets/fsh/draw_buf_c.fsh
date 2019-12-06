// Stroke layer - the current brush stroke is drawn here.
precision highp float;
uniform vec2                iResolution;
uniform vec4 				iMouse;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
uniform sampler2D           iChannel2;
varying vec2                vTextureCoord;

uniform float iClear;
uniform int iFrame;

const vec2 bufB_col_uv = vec2(1,0);

#define inside(a) (fragCoord.x == a.x+0.5 && fragCoord.y == a.y+0.5)
#define load(a,b) texture2D(b,(a+0.5)/iResolution.xy)
#define save(a,b) if(inside(a)){fragColor=b;return;}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    if(iFrame < 2 || iClear == 1.0){
        fragColor = vec4(0.0);
        return;
    }

    vec2 uv = fragCoord.xy/iResolution.xy;
    //vec2 uva = (2.*fragCoord.xy-iResolution.xy)/iResolution.yy;

    vec4 color = texture2D(iChannel0,uv);
    vec4 stroke = texture2D(iChannel1,uv);

    vec3 bufB_col = load(bufB_col_uv, iChannel2).rgb;

    fragColor = color;

    if (iMouse.w>0.0)
    {
        if(stroke.a != 0.0){
            fragColor.rgb = fragColor.rgb*(1.-stroke.a) + stroke.rgb*stroke.a;
            fragColor.a = 1.0;
        }
    }
}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}