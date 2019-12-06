precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D iChannel0;
void main() {
    vec4 color = texture2D(iChannel0, vTextureCoord);
    vec4 navi = 1.0 - color;
    gl_FragColor = navi;
}