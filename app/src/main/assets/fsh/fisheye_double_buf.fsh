precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;

uniform vec4 iMouse;
uniform float iSize;

float radius;
float strength;
vec2 center;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	radius = 0.1 + iSize * 0.07 / 36.0;
	strength = (iMouse.y/iResolution.y + 0.5);
	center = vec2(iMouse.x / iResolution.x,iMouse.y / iResolution.y);

    center.x -= radius;
	vec2 coord = fragCoord.xy / iResolution.xy;
	coord -= center;
	float distance = length(coord);

	if (distance < radius) {
		float percent = distance / radius;
		if (strength > 0.0) {
			coord *= mix(1.0, smoothstep(0.0, radius / distance, percent), strength * 0.75);
		} else {
			coord *= mix(1.0, pow(percent, 1.0 + strength * 0.75) * radius / distance, 1.0 - percent);
		}
    }

	coord += center;

 	fragColor = vec4( texture2D(iChannel0, coord).xyz, 1.0);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}
