precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;

mat4 rgba2sepia =
mat4
(
0.393, 0.349, 0.272, 0,
0.769, 0.686, 0.534, 0,
0.189, 0.168, 0.131, 0,
0,     0,     0,     1
);

// Sepia without time
void sepia( inout vec3 color, float adjust ) {
    color.r = min(1.0, (color.r * (1.0 - (0.607 * adjust))) + (color.g * (0.769 * adjust)) + (color.b * (0.189 * adjust)));
    color.g = min(1.0, (color.r * (0.349 * adjust)) + (color.g * (1.0 - (0.314 * adjust))) + (color.b * (0.168 * adjust)));
    color.b = min(1.0, (color.r * (0.272 * adjust)) + (color.g * (0.534 * adjust)) + (color.b * (1.0 - (0.869 * adjust))));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
    vec3 color = texture2D(iChannel0, uv).rgb;
    sepia(color, 0.75);
	fragColor = vec4(color, 1.0);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}