precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D iChannel0;
void main() {
    gl_FragColor = texture2D(iChannel0, vTextureCoord);
}