precision mediump float;
uniform vec2                iResolution;
uniform sampler2D           iChannel0;
varying vec2                vTextureCoord;
uniform vec3 brushCol;
uniform float iSize;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	float OuterVig = 1.0; // Position for the Outer vignette

	float InnerVig = 0.3 + iSize * 0.4 / 36.0; // Position for the inner Vignette Ring

	vec2 uv = fragCoord.xy / iResolution.xy;

	vec4 color = texture2D(iChannel0, uv);

	vec2 center = vec2(uv.x,0.5); // Center of Screen

	float dist  = distance(center,uv )*1.414213; // Distance  between center and the current Uv. Multiplyed by 1.414213 to fit in the range of 0.0 to 1.0

	float vig = clamp((OuterVig-dist) / (OuterVig-InnerVig),0.0,1.0); // Generate the Vignette with Clamp which go from outer Viggnet ring to inner vignette ring with smooth steps

    if(vig == 1.0){
    	fragColor = color;
    }
    else{
        color *= vig;
        color.r += (1.0-vig) * brushCol.r;
        color.g += (1.0-vig) * brushCol.g;
        color.b += (1.0-vig) * brushCol.b;
    	fragColor = color;
    }

    center = vec2(0.5,uv.y); // Center of Screen

    dist  = distance(center,uv )*1.414213; // Distance  between center and the current Uv. Multiplyed by 1.414213 to fit in the range of 0.0 to 1.0

    vig = clamp((OuterVig-dist) / (OuterVig-InnerVig),0.0,1.0); // Generate the Vignette with Clamp which go from outer Viggnet ring to inner vignette ring with smooth steps

    if(vig == 1.0){
        fragColor = color;
    }
    else{
        color *= vig;
        color.r += (1.0-vig) * brushCol.r;
        color.g += (1.0-vig) * brushCol.g;
        color.b += (1.0-vig) * brushCol.b;
        fragColor = color;
    }
}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}