package cn.wildfirechat.message;

import android.graphics.BitmapFactory;
import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import cn.wildfirechat.message.core.ContentTag;
import cn.wildfirechat.message.core.MessageContentType;
import cn.wildfirechat.message.core.MessagePayload;
import cn.wildfirechat.message.core.PersistFlag;

/**
 * Created by heavyrain lee on 2017/12/6.
 */

@ContentTag(type = MessageContentType.ContentType_Sticker, flag = PersistFlag.Persist_And_Count)
public class StickerMessageContent extends MediaMessageContent {
    public int width;
    public int height;

    public StickerMessageContent() {
    }

    public StickerMessageContent(String localPath) {
        this.localPath = localPath;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localPath, options);
        height = options.outHeight;
        width = options.outWidth;
    }


    @Override
    public MessagePayload encode() {
        MessagePayload payload = new MessagePayload();
        payload.searchableContent = "[动态表情]";
        payload.mediaType = MessageContentMediaType.FILE;

        payload.remoteMediaUrl = remoteUrl;
        payload.localMediaPath = localPath;

        try {
            JSONObject objWrite = new JSONObject();
            objWrite.put("x", width);
            objWrite.put("y", height);
            payload.binaryContent = objWrite.toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        remoteUrl = payload.remoteMediaUrl;
        localPath = payload.localMediaPath;

        try {
            if (payload.binaryContent != null) {
                JSONObject jsonObject = new JSONObject(new String(payload.binaryContent));
                width = jsonObject.optInt("x");
                height = jsonObject.optInt("y");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String digest() {
        return "[动态表情]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.localPath != null ? this.localPath : "");
        dest.writeString(this.remoteUrl != null ? this.remoteUrl : "");
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected StickerMessageContent(Parcel in) {
        this.localPath = in.readString();
        this.remoteUrl = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<StickerMessageContent> CREATOR = new Creator<StickerMessageContent>() {
        @Override
        public StickerMessageContent createFromParcel(Parcel source) {
            return new StickerMessageContent(source);
        }

        @Override
        public StickerMessageContent[] newArray(int size) {
            return new StickerMessageContent[size];
        }
    };
}