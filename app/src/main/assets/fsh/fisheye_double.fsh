precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;

uniform vec4 iMouse;
uniform float iSize;

float radius;
float strength;
vec2 center;
vec2 center2;


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	radius = 0.1 + iSize * 0.07 / 36.0;
	strength = (iMouse.y/iResolution.y + 0.5);
	center = vec2(iMouse.x / iResolution.x,iMouse.y / iResolution.y);
	center2 = vec2(center.x + radius,center.y);
	vec2 coord2 = fragCoord.xy / iResolution.xy;
	coord2 -= center2;
	float distance2 = length(coord2);
	if (distance2 < radius) {
		float percent = distance2 / radius;
		if (strength > 0.0) {
			coord2 *= mix(1.0, smoothstep(0.0, radius / distance2, percent), strength * 0.75);
		} else {
			coord2 *= mix(1.0, pow(percent, 1.0 + strength * 0.75) * radius / distance2, 1.0 - percent);
		}
	}
	coord2 += center2;
 	fragColor = vec4( texture2D(iChannel0, coord2).xyz, 1.0);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}
