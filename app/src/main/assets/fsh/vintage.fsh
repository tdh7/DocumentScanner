precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
uniform sampler2D iChannel1;
uniform float iGlobalTime;

//#define B_W 1
#define SEPIA

#define VIGNETTE

#define BLEND_IMG1

vec4 blend(vec3 first, vec3 second, float alpha){
	return vec4(mix(first, second, alpha), 1.0);
}

vec4 applyB_W(vec4 m_input){
	
    vec4 m_output;
    
    float grayScale = dot(m_input.rgb, vec3(0.21, 0.72, 0.07));
    
    m_output = vec4(vec3(grayScale), 1.0);
    
    return m_output;
    
}

vec4 applySepia(vec4 m_input, float toneRed, float toneGreen, float toneBlue){
	
    vec4 m_output;
    
    vec4 b_w = applyB_W(m_input);
    
    vec3 sepiaTone = vec3(toneRed, toneGreen, toneBlue);
    
    m_output = vec4(b_w.xyz * sepiaTone, 1.0);
    
    return m_output;
    
}

vec4 applyVignette(vec4 m_input, vec2 fragCoord){

    vec4 m_output;
    
    vec2 position = fragCoord - vec2(0.5);
    
    //position.x *= iResolution.x / iResolution.y;
    
    float lengthFromCenter = length(position);
    
    float r = 0.5;
    
    float softness = 0.15;
    
    vec4 colorWithVignette = vec4(m_input.xyz * vec3(smoothstep(r, r - softness, lengthFromCenter)), 1.0);
    
    m_output = blend(m_input.xyz, colorWithVignette.xyz, 0.2);
    
    return m_output;
    
}

vec4 applyBackgroundImg(vec4 m_input, vec4 blending_img){
	
    vec4 m_output;
    
    m_output = blend(m_input.xyz, blending_img.xyz, 0.176);
    
    return m_output;
    
}

vec4 createHorLine(vec4 m_input, vec2 uv){
	float scanline = sin(uv.y*800.0)*0.08;
	m_input -= scanline;
    return m_input;
}

vec4 createVerLine(vec4 m_input, vec2 uv){
	float scanline = sin(uv.x*800.0)*0.08;
	m_input -= scanline;
    return m_input;
}

vec4 createRandomHorLine(vec4 m_input, vec2 uv){

    float positionOfLine = sin(3.14*uv.x + 0.7*iGlobalTime);
    float positionOfLine2 = sin(3.14*uv.x - 0.3*iGlobalTime);
    float positionOfLine3 = sin(3.14*uv.x + 0.2*iGlobalTime);

    
    if(abs(uv.x - positionOfLine) < 0.0005)
        return vec4(1.0);
    
    if(abs(uv.x - positionOfLine2) < 0.0005)
        return vec4(1.0);
    
    if(abs(uv.x - positionOfLine3) < 0.0005)
        return vec4(1.0);
    
    return m_input;
    
}

vec4 createRandomNoise(vec4 m_input){
	return vec4(1.0);
}

// Distorted
// change these values to 0.0 to turn off individual effects
float vertJerkOpt = 1.0;
float vertMovementOpt = 1.0;
float bottomStaticOpt = 1.0;
float scalinesOpt = 1.0;
float rgbOffsetOpt = 1.0;
float horzFuzzOpt = 1.0;

// Noise generation functions borrowed from: 
// https://github.com/ashima/webgl-noise/blob/master/src/noise2D.glsl

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float staticV(vec2 uv) {
    float staticHeight = snoise(vec2(9.0,iGlobalTime*1.2+3.0))*0.3+5.0;
    float staticAmount = snoise(vec2(1.0,iGlobalTime*1.2-6.0))*0.1+0.3;
    float staticStrength = snoise(vec2(-9.75,iGlobalTime*0.6-3.0))*2.0+2.0;
	return (1.0-step(snoise(vec2(5.0*pow(iGlobalTime,2.0)+pow(uv.x*7.0,1.2),pow((mod(iGlobalTime,100.0)+100.0)*uv.y*0.3+3.0,staticHeight))),staticAmount))*staticStrength;
}

vec4 distortedTV(vec4 m_input, vec2 uv){
	float jerkOffset = (1.0-step(snoise(vec2(iGlobalTime*1.3,5.0)),0.8))*0.05;
	
	float fuzzOffset = snoise(vec2(iGlobalTime*15.0,uv.y*80.0))*0.003;
	float largeFuzzOffset = snoise(vec2(iGlobalTime*1.0,uv.y*25.0))*0.004;
    
    float vertMovementOn = (1.0-step(snoise(vec2(iGlobalTime*0.2,8.0)),0.4))*vertMovementOpt;
    float vertJerk = (1.0-step(snoise(vec2(iGlobalTime*1.5,5.0)),0.6))*vertJerkOpt;
    float vertJerk2 = (1.0-step(snoise(vec2(iGlobalTime*5.5,5.0)),0.2))*vertJerkOpt;
    float yOffset = abs(sin(iGlobalTime)*4.0)*vertMovementOn+vertJerk*vertJerk2*0.3;
    float y = mod(uv.y+yOffset,1.0);
    
	
	float xOffset = (fuzzOffset + largeFuzzOffset) * horzFuzzOpt;
    
    float staticVal = 0.0;
   
    for (float y = -1.0; y <= 1.0; y += 1.0) {
        float maxDist = 5.0/200.0;
        float dist = y/200.0;
    	staticVal += staticV(vec2(uv.x,uv.y+dist))*(maxDist-abs(dist))*1.5;
    }
        
    staticVal *= bottomStaticOpt;
	
	float red 	=   m_input.r+staticVal;
	float green = 	m_input.g+staticVal;
	float blue 	=	m_input.b+staticVal;
	
	vec3 color = vec3(red,green,blue);
	float scanline = sin(uv.y*800.0)*0.04*scalinesOpt;
	color -= scanline;
    
    return vec4(color, 1.0);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
    
    vec4 vTexColor = texture2D(iChannel0, uv);
    
    vec4 finalColor;
    
    // Black & White version.
    vec4 blend_stage1, vignette_stage;
    
    // Blend stage 1
    #ifdef B_W
    	finalColor = applyB_W(vTexColor);
    #endif
    
    #ifdef SEPIA
    	finalColor = applySepia(vTexColor, 1.2, 1.0, 0.8);
    #endif
    
    // Vignette Stage
    #ifdef VIGNETTE
    	finalColor = applyVignette(finalColor, uv); 
    #endif
    
    // Blend stage 2
    #ifdef BLEND_IMG1
    	vec4 blendImg = texture2D(iChannel1, uv);
    	finalColor = applyBackgroundImg(finalColor, blendImg); 
    #endif
    
    // Noise stage
    
    // Create Line
    finalColor = distortedTV(finalColor, uv);
    //createHorLine(finalColor, uv);//createRandomHorLine(finalColor, uv);
    
	fragColor = finalColor;
    
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}