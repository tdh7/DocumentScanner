package nahuy.fithcmus.magiccam.presentation.entities.shader_kit;

/**
 * Created by huy on 6/14/2017.
 */

public class MyGLChannelIndex {
    private String header;
    private int channelId;

    public MyGLChannelIndex(String content) {
        this.header = content.substring(0, 1);
        this.channelId = Integer.parseInt(content.substring(1));
    }

    public ChannelIndexType getChannelIndexType(){
        if(header.equals("B")){
            return ChannelIndexType.BUFFER;
        }
        else if(header.equals("M")){
            return ChannelIndexType.MAIN;
        }
        else if(header.equals("F")){
            return ChannelIndexType.FILTER;
        }
        else{
            return ChannelIndexType.EMPTY;
        }
    }

    public int getChannelId() {
        return channelId;
    }

    public enum ChannelIndexType{
        BUFFER,
        MAIN,
        FILTER,
        EMPTY
    }
}
