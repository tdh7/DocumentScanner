precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
uniform sampler2D iChannel1;

void mainImage(out vec4 fragColor, in vec2 fragCoord){
     vec2 uv = fragCoord.xy / iResolution.xy;
	 vec4 textureColor = texture2D(iChannel0, uv);

     float blueColor = textureColor.b * 63.0;

     vec2 quad1;
     quad1.y = floor(floor(blueColor) / 8.0);
     quad1.x = floor(blueColor) - (quad1.y * 8.0);

     vec2 quad2;
     quad2.y = floor(ceil(blueColor) / 8.0);
     quad2.x = ceil(blueColor) - (quad2.y * 8.0);

     vec2 texPos1;
     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

     vec2 texPos2;
     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

     vec4 newColor1 = texture2D(iChannel1, texPos1);
     vec4 newColor2 = texture2D(iChannel1, texPos2);

     vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
     fragColor = vec4(newColor.rgb, textureColor.w);
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}