precision mediump float;

uniform vec2                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                vTextureCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 px = 3.0/vec2(640.0,360.0);
	vec2 uv = fragCoord.xy / iResolution.xy;
    vec4 tex = texture2D(iChannel0,uv);
    float newG = min(tex.g,max(tex.r,tex.b));
    float d = abs(tex.g - newG);
    tex.g = newG * 0.9;
    if (d > 0.0)
    {
        //px*= sin(iGlobalTime+uv.yx*3.0)*.35;
        uv -= 0.5*px;
        vec4 tex2 = texture2D(iChannel1,uv);
        uv += px;
        tex2 += texture2D(iChannel1,uv);
        uv.x -= px.x -.018 *sin(iGlobalTime*4.1+tex2.r);
        uv.y += px.y +.015 * cos(iGlobalTime*4.1+tex2.g);
        tex2 += texture2D(iChannel1,uv);
        uv.y -= px.y;
        tex2 += texture2D(iChannel1,uv);
        tex2 /= 4.013;
        tex2 = clamp(tex2*1.02-0.012,0.0,1.0);
        tex = max(clamp(tex*(1.0-d),0.0,1.0),mix(tex,tex2,smoothstep(-0.3,0.23,d)));
     }

	fragColor = tex;
}


void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}