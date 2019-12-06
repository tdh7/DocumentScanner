precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
uniform float kernel[25];

highp float threshold = 0.3;
highp float quantizationLevels = 16.;
 
const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec4 smoothColor(vec2 uv){
    
    vec3 origin = texture2D(iChannel0, uv / iResolution.xy).rgb;
    
    vec3 finalColor = vec3(0.0);
    
    int kernelOffset = 0;
    
    for(int i = -2; i < 3; i++){
        for(int j = -2; j < 3; j++){        
        	finalColor += texture2D(iChannel0, (uv + vec2(i, j)) / iResolution.xy).rgb * kernel[kernelOffset];
            kernelOffset++;
        }
    }
    /*vec3 m1_1 = texture2D(iChannel0, uv / iResolution.xy).rgb;
    vec3 m0_0 = texture2D(iChannel0, (uv + vec2(-1, -1)) / iResolution.xy).rgb;
    vec3 m0_1 = texture2D(iChannel0, (uv + vec2(-1, 0)) / iResolution.xy).rgb;
    vec3 m0_2 = texture2D(iChannel0, (uv + vec2(-1, 1)) / iResolution.xy).rgb;
    vec3 m1_0 = texture2D(iChannel0, (uv + vec2(0, -1)) / iResolution.xy).rgb;
    vec3 m1_2 = texture2D(iChannel0, (uv + vec2(0, 1)) / iResolution.xy).rgb;
    vec3 m2_0 = texture2D(iChannel0, (uv + vec2(1, -1)) / iResolution.xy).rgb;
    vec3 m2_1 = texture2D(iChannel0, (uv + vec2(1, 0)) / iResolution.xy).rgb;
    vec3 m2_2 = texture2D(iChannel0, (uv + vec2(1, 1)) / iResolution.xy).rgb;
    
    vec3 finalColor = m0_0 * 1./16. + m0_1 * 1./8. + m0_2 * 1./16.+
        			m1_0 * 1./8. + m1_1 * 1./4. + m1_2 * 1./8.+
        			m2_0 * 1./16. + m2_1 * 1./8. + m2_2 * 1./16.;*/
        
    return vec4(finalColor.r, finalColor.g, finalColor.b,1.0);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy;
    //uv = vec2(uv.x, 1.-uv.y);
    //vec4 textureColor = texture2D(iChannel0, uv / iResolution.xy);
    vec4 textureColor = smoothColor(uv);
    
    /*float bottomLeftIntensity = smoothColor(uv + vec2(1, -1)).r;
    float topRightIntensity = smoothColor(uv + vec2(-1, 1)).r;
    float topLeftIntensity = smoothColor(uv + vec2(-1, -1)).r;
    float bottomRightIntensity = smoothColor(uv + vec2(1, 1)).r;
    float leftIntensity = smoothColor(uv + vec2(0, -1)).r;
    float rightIntensity = smoothColor(uv + vec2(0, 1)).r;
    float bottomIntensity = smoothColor(uv + vec2(1, 0)).r;
    float topIntensity = smoothColor(uv + vec2(-1, 0)).r;*/
    float bottomLeftIntensity = texture2D(iChannel0, (uv + vec2(1, -1)) / iResolution.xy).r;
    float topRightIntensity = texture2D(iChannel0, (uv + vec2(-1, 1)) / iResolution.xy).r;
    float topLeftIntensity = texture2D(iChannel0, (uv + vec2(-1, -1)) / iResolution.xy).r;
    float bottomRightIntensity = texture2D(iChannel0, (uv + vec2(1, 1)) / iResolution.xy).r;
    float leftIntensity = texture2D(iChannel0, (uv + vec2(0, -1)) / iResolution.xy).r;
    float rightIntensity = texture2D(iChannel0, (uv + vec2(0, 1)) / iResolution.xy).r;
    float bottomIntensity = texture2D(iChannel0, (uv + vec2(1, 0)) / iResolution.xy).r;
    float topIntensity = texture2D(iChannel0, (uv + vec2(-1, 0)) / iResolution.xy).r;
    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;
    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;
     
    float mag = length(vec2(h, v));
    
    vec3 tex = textureColor.rgb;
    tex = pow(tex, vec3(0.7, 0.7, 0.7));
    tex *= quantizationLevels;
    tex = floor(tex);
    tex /= quantizationLevels;
    tex = pow(tex, vec3(1./0.7));

     //vec3 posterizedImageColor = floor((textureColor.rgb * quantizationLevels) + 0.5) / quantizationLevels;
     
    float thresholdTest = 1.0 - step(threshold, mag);
    //fragColor = textureColor;
    fragColor = vec4(tex * thresholdTest, textureColor.a);
}
void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}