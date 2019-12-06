precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 uResolution;
uniform float uGlobalTime;
uniform vec2 uMouse;
uniform sampler2D iChannel0;

// Swirl effect parameters
float radius;
float angle = 0.8;
vec2 center;

vec4 PostFX(vec2 uv)
{
  float radius = uResolution.x * 1.0 / 4.0;
  float angle = sin(uGlobalTime);   //-1.+2.*
  vec2 center = uMouse;//vec2(uResolution.x,uResolution.y) * 0.5;

  vec2 texSize = vec2(uResolution.x,uResolution.y);
  vec2 tc = uv * texSize;
  tc -= center;
  float dist = length(tc);// *sin(uGlobalTime/5.
  if (dist < radius)
  {
    float percent = (radius - dist) / radius;
    float theta = percent * percent * angle * 8.0;
    float s = sin(theta/2.);
    float c = cos(sin(theta/2.));
    tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
  }
  tc += center;
  vec3 color  = texture2D(iChannel0,(tc / texSize)).rgb;
  // vec3 color2 = texture2D(iChannel0,(tc / texSize)).rgb;
  // vec3 colmix = mix(color,color2,sin(uGlobalTime*.5));
  return vec4(color, 1.0);
}

void main()
{
  vec2 uv = vTextureCoord;
  gl_FragColor = PostFX(uv);
}