precision mediump float;
varying vec2 vTextureCoord;
uniform float uGlobalTime;
uniform vec2 uResolution;
uniform sampler2D iChannel0;
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	const float minTileSize = 1.0;
	const float maxTileSize = 32.0;
	const float textureSamplesCount = 3.0;
	const float textureEdgeOffset = 0.005;
	const float borderSize = 1.0;
	const float speed = 1.0;

	float time = pow(sin(uGlobalTime * speed), 2.0);
	float tileSize = minTileSize + floor(time * (maxTileSize - minTileSize));
	tileSize += mod(tileSize, 2.0);
	vec2 tileNumber = floor(fragCoord / tileSize);

	vec4 accumulator = vec4(0.0);
	for (float y = 0.0; y < textureSamplesCount; ++y)
	{
		for (float x = 0.0; x < textureSamplesCount; ++x)
		{
			vec2 textureCoordinates = (tileNumber + vec2((x + 0.5)/textureSamplesCount, (y + 0.5)/textureSamplesCount)) * tileSize / uResolution.xy;
			//textureCoordinates.y = 1.0 - textureCoordinates.y;
			textureCoordinates = clamp(textureCoordinates, 0.0 + textureEdgeOffset, 1.0 - textureEdgeOffset);
			accumulator += texture2D(iChannel0, textureCoordinates);
	   }
	}

	fragColor = accumulator / vec4(textureSamplesCount * textureSamplesCount);

}
void main() {
    mainImage(gl_FragColor, vTextureCoord * uResolution);
}