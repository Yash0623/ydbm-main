package com.ydbm.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatMessage implements Serializable,Parcelable {

    private String messageText;
    private String messageType;
    private String msgStatus, otherUsreName;
    private UserType userType;
    private String readStatus;
    private long uniqueMsgId;
    private int currentPlayingPosition;
    private boolean isDateChanged = false;
    private boolean isFirstMsg = false;
    private Status messageStatus;
    private String audioFilename;
    private String originalPath;
    private boolean isSelected;
    private boolean isCompressed;
    private int position;
    private String msgQuotedText,msgQuotedId,msgQuotePosition;

    private String mimeType;
    private long messageTime;
    private String messageTimeNew;
    private String audioFilePath;
    private String imgURL;
    private String imgThumb;
    private String videoThumbNail;
    private String profPic;
    private String _id;
    private String dialogId;
    private String isUploaded, isDownloaded;
    Uri audioUri;
    private long dateSent = 0L;

    private String body;
    @Deprecated
    private Integer read;
    private Collection<Integer> readIds;
    private Collection<Integer> deliveredIds;
    private Integer recipientId;
    private Integer senderId;
    private boolean markable = false;
    private boolean delayed = false;
    double latitude;
    String placeName;
    int progress;
    String msgRoomId;
    double longitude;
    private Map<String, String> properties;

    private boolean saveToHistory = false;
    String msgSeenByMembers;
    private String msgType;
    private String tripId;
    private String isFailed;
    private String senderTripTimestamp;
    // private ChatMessageExtension packetExtension;


    protected ChatMessage(Parcel in) {
        messageText = in.readString();
        messageType = in.readString();
        msgStatus = in.readString();
        otherUsreName = in.readString();
        readStatus = in.readString();
        uniqueMsgId = in.readLong();
        currentPlayingPosition = in.readInt();
        isDateChanged = in.readByte() != 0;
        isFirstMsg = in.readByte() != 0;
        audioFilename = in.readString();
        originalPath = in.readString();
        isSelected = in.readByte() != 0;
        isCompressed = in.readByte() != 0;
        position = in.readInt();
        msgQuotedText = in.readString();
        msgQuotedId = in.readString();
        msgQuotePosition = in.readString();
        mimeType = in.readString();
        messageTime = in.readLong();
        messageTimeNew = in.readString();
        audioFilePath = in.readString();
        imgURL = in.readString();
        imgThumb = in.readString();
        videoThumbNail = in.readString();
        profPic = in.readString();
        _id = in.readString();
        dialogId = in.readString();
        isUploaded = in.readString();
        isDownloaded = in.readString();
        audioUri = in.readParcelable(Uri.class.getClassLoader());
        dateSent = in.readLong();
        body = in.readString();
        if (in.readByte() == 0) {
            read = null;
        } else {
            read = in.readInt();
        }
        if (in.readByte() == 0) {
            recipientId = null;
        } else {
            recipientId = in.readInt();
        }
        if (in.readByte() == 0) {
            senderId = null;
        } else {
            senderId = in.readInt();
        }
        markable = in.readByte() != 0;
        delayed = in.readByte() != 0;
        latitude = in.readDouble();
        placeName = in.readString();
        progress = in.readInt();
        msgRoomId = in.readString();
        longitude = in.readDouble();
        saveToHistory = in.readByte() != 0;
        msgSeenByMembers = in.readString();
        msgType = in.readString();
        tripId = in.readString();
        isFailed = in.readString();
        senderTripTimestamp = in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public String getMsgQuotedText() {
        return msgQuotedText;
    }

    public void setMsgQuotedText(String msgQuotedText) {
        this.msgQuotedText = msgQuotedText;
    }

    public String getMsgQuotedId() {
        return msgQuotedId;
    }

    public void setMsgQuotedId(String msgQuotedId) {
        this.msgQuotedId = msgQuotedId;
    }

    public String getMsgQuotePosition() {
        return msgQuotePosition;
    }

    public void setMsgQuotePosition(String msgQuotePosition) {
        this.msgQuotePosition = msgQuotePosition;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getTripId() {
        return tripId;
    }

    public String getSenderTripTimestamp() {
        return senderTripTimestamp;
    }

    public void setSenderTripTimestamp(String senderTripTimestamp) {
        this.senderTripTimestamp = senderTripTimestamp;
    }

    public String getIsFailed() {
        return isFailed;
    }

    public void setIsFailed(String isFailed) {
        this.isFailed = isFailed;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }


    public int getCurrentPlayingPosition() {
        return currentPlayingPosition;
    }

    public void setCurrentPlayingPosition(int currentPlayingPosition) {
        this.currentPlayingPosition = currentPlayingPosition;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public long getUniqueMsgId() {
        return uniqueMsgId;
    }

    public void setUniqueMsgId(long uniqueMsgId) {
        this.uniqueMsgId = uniqueMsgId;
    }


    public int getProgress() {
        return progress;
    }

    public String getMsgRoomId() {
        return msgRoomId;
    }

    public void setMsgRoomId(String msgRoomId) {
        this.msgRoomId = msgRoomId;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isFirstMsg() {
        return isFirstMsg;
    }

    public void setFirstMsg(boolean firstMsg) {
        isFirstMsg = firstMsg;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public boolean isDateChanged() {
        return isDateChanged;
    }

    public void setDateChanged(boolean dateChanged) {
        isDateChanged = dateChanged;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageTimeNew() {
        return messageTimeNew;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setMessageTimeNew(String messageTimeNew) {
        this.messageTimeNew = messageTimeNew;
    }

    public ChatMessage() {
        //    this._id = MongoDBObjectId.get().toString();
    }

    public String getOtherUsreName() {
        return otherUsreName;
    }

    public void setOtherUsreName(String otherUsreName) {
        this.otherUsreName = otherUsreName;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getVideoThumbNail() {
        return videoThumbNail;
    }

    public void setVideoThumbNail(String videoThumbNail) {
        this.videoThumbNail = videoThumbNail;
    }

    public String getOriginalPath() {
        return originalPath;
    }


    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImgURL() {
        return imgURL;
    }

    public Uri getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
    }


    public String getPlaceName() {
        return placeName;
    }

    public String getAudioFilename() {
        return audioFilename;
    }

    public void setAudioFilename(String audioFilename) {
        this.audioFilename = audioFilename;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getId() {
        return this._id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Integer getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public boolean isDelayed() {
        return this.delayed;
    }

    public Integer getRecipientId() {
        return this.recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isMarkable() {
        return this.markable;
    }

    public void setMarkable(boolean markable) {
        this.markable = markable;
    }


    public Collection<Integer> getReadIds() {
        return this.readIds;
    }

    public void setReadIds(Collection<Integer> readIds) {
        this.readIds = readIds;
    }

    public Collection<Integer> getDeliveredIds() {
        return this.deliveredIds;
    }

    public void setDeliveredIds(Collection<Integer> deliveredIds) {
        this.deliveredIds = deliveredIds;
    }

    public Object getProperty(String name) {
        return this.properties == null ? null : this.properties.get(name);
    }

    public Collection<String> getPropertyNames() {
        return this.properties == null ? null : this.properties.keySet();
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public String setProperty(String name, String value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }

        return this.properties.put(name, value);
    }

    public String removeProperty(String name) {
        return this.properties == null ? null : this.properties.remove(name);
    }



    public long getDateSent() {
        return this.dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public void setSaveToHistory(boolean saveToHistory) {
        this.saveToHistory = saveToHistory;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getClass().getSimpleName());
        stringBuilder.append("{").append("id").append("=").append(this.getId()).append(", sender_id").append("=").append(this.getSenderId()).append(", recipient_id").append("=").append(this.getRecipientId()).append(", body").append("=").append(this.getBody()).append(", properties").append("=").append(this.getProperties()).append(", attachments").append("=").append(", dialogId").append("=").append(this.getDialogId()).append(", dateSent").append("=").append(this.getDateSent()).append(", markable").append("=").append(this.isMarkable()).append(", readIds").append("=").append(this.getReadIds()).append(", deliveredIds").append("=").append(this.getDeliveredIds()).append("}");
        return stringBuilder.toString();
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ChatMessage ChatMessage = (ChatMessage) o;
            return this.getId().equals(ChatMessage.getId());
        } else {
            return false;
        }
    }

   /* public ChatMessage(Message smackMessage) {
        String elementName = "extraParams";
        String namespace = "jabber:client";
        ChatMessageExtension extension = (ChatMessageExtension)smackMessage.getExtension(elementName, namespace);
        if(extension != null) {
            this.attachments = extension.getAttachments();
            this.properties = extension.getProperties();
        }

        QBChatMarkersExtension chatMarkersExtension = (QBChatMarkersExtension)smackMessage.getExtension("urn:xmpp:chat-markers:0");
        this.markable = chatMarkersExtension != null && ChatMarker.markable.equals(chatMarkersExtension.getMarker());
        String _packetId = smackMessage.getStanzaId();
        this._id = MongoDBObjectId.isValid(_packetId)?_packetId:String.valueOf(this.getProperty("message_id"));
        this.dialogId = String.valueOf(this.getProperty("dialog_id"));
        String dateSentValue = String.valueOf(this.getProperty("date_sent"));
        if(dateSentValue != null) {
            try {
                this.dateSent = Long.valueOf(dateSentValue).longValue();
            } catch (NumberFormatException var12) {
                try {
                    Double d = Double.valueOf(Double.parseDouble(dateSentValue));
                    this.dateSent = d.longValue();
                } catch (NumberFormatException var11) {
                    Lo.g("Can't parse the 'date_sent' value " + dateSentValue + ": " + var11.getLocalizedMessage());
                }
            }
        }

        DelayInformation delayInformation = (DelayInformation)smackMessage.getExtension("delay", "urn:xmpp:delay");
        if(delayInformation != null) {
            this.delayed = true;
        }

        String senderJid = smackMessage.getFrom();
        if(senderJid != null) {
            if(JIDHelper.INSTANCE.isChatJid(senderJid)) {
                this.senderId = Integer.valueOf(JIDHelper.INSTANCE.parseUserId(senderJid));
            } else {
                this.senderId = JIDHelper.INSTANCE.parseRoomOccupant(senderJid);
            }
        }

        if(this.senderId != null) {
            ArrayList<Integer> senderIdFromMsg = new ArrayList(1);
            senderIdFromMsg.add(this.senderId);
            this.readIds = senderIdFromMsg;
            this.deliveredIds = senderIdFromMsg;
        }

        String recipientJid = smackMessage.getTo();
        if(recipientJid != null) {
            if(JIDHelper.INSTANCE.isChatJid(recipientJid)) {
                this.recipientId = Integer.valueOf(JIDHelper.INSTANCE.parseUserId(recipientJid));
            } else {
                this.recipientId = JIDHelper.INSTANCE.parseRoomOccupant(recipientJid);
            }
        }

        this.body = smackMessage.getBody();
    }

    public void setQBChatUnMarkedMessageExtension(ChatMessageExtension packetExtension) {
        this.packetExtension = packetExtension;
    }

    public void removeQBChatUnMarkedMessageExtension() {
        this.packetExtension = null;
    }

    public Message getSmackMessage() {
        Message asmackMessage = new Message();
        asmackMessage.setStanzaId(this._id);
        asmackMessage.setBody(this.body);
        if(this.markable) {
            QBChatMarkersExtension chatMarkersExtension = new QBChatMarkersExtension(ChatMarker.markable);
            asmackMessage.addExtension(chatMarkersExtension);
        }

        if(this.attachments != null || this.properties != null || this.dateSent > 0L || this.saveToHistory || this.dialogId != null) {
            if(this.packetExtension == null) {
                this.packetExtension = new ChatMessageExtension();
            }

            if(this.attachments != null) {
                this.packetExtension.addAttachments(this.attachments);
            }

            if(this.properties != null) {
                this.packetExtension.setProperties(this.properties);
            }

            if(this.complexProperties != null) {
                this.packetExtension.setComplexProperty(this.complexProperties);
            }

            if(this.dateSent > 0L) {
                this.packetExtension.setProperty("date_sent", String.valueOf(this.dateSent));
            }

            if(this.saveToHistory) {
                this.packetExtension.setProperty("save_to_history", "1");
            }

            if(this.dialogId != null) {
                this.packetExtension.setProperty("dialog_id", this.dialogId);
            }

            asmackMessage.addExtension(this.packetExtension);
        }

        return asmackMessage;
    }

    public static Message buildDeliveredOrReadStatusMessage(boolean delivered, Integer senderId, String originMessageID, String dialogId) {
        Message asmackMessage = new Message();
        asmackMessage.setStanzaId(MongoDBObjectId.get().toString());
        asmackMessage.setType(Type.chat);
        asmackMessage.setTo(JIDHelper.INSTANCE.getJid(senderId.intValue()));
        QBChatMarkersExtension extension = new QBChatMarkersExtension(delivered?ChatMarker.received:ChatMarker.displayed, originMessageID);
        asmackMessage.addExtension(extension);
        if(dialogId != null) {
            ChatMessageExtension packetExtension = new ChatMessageExtension();
            packetExtension.setProperty("dialog_id", dialogId);
            asmackMessage.addExtension(packetExtension);
        }

        return asmackMessage;
    }

    public static Message buildTypingStatusMessage(String to, Type type, ChatState chatState) {
        Message message = new Message();
        message.setStanzaId(MongoDBObjectId.get().toString());
        message.setType(type);
        message.setTo(to);
        ChatStateExtension extension = new ChatStateExtension(chatState);
        message.addExtension(extension);
        return message;
    }*/

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(messageText);
        parcel.writeString(messageType);
        parcel.writeString(msgStatus);
        parcel.writeString(otherUsreName);
        parcel.writeString(readStatus);
        parcel.writeLong(uniqueMsgId);
        parcel.writeInt(currentPlayingPosition);
        parcel.writeByte((byte) (isDateChanged ? 1 : 0));
        parcel.writeByte((byte) (isFirstMsg ? 1 : 0));
        parcel.writeString(audioFilename);
        parcel.writeString(originalPath);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeByte((byte) (isCompressed ? 1 : 0));
        parcel.writeInt(position);
        parcel.writeString(msgQuotedText);
        parcel.writeString(msgQuotedId);
        parcel.writeString(msgQuotePosition);
        parcel.writeString(mimeType);
        parcel.writeLong(messageTime);
        parcel.writeString(messageTimeNew);
        parcel.writeString(audioFilePath);
        parcel.writeString(imgURL);
        parcel.writeString(imgThumb);
        parcel.writeString(videoThumbNail);
        parcel.writeString(profPic);
        parcel.writeString(_id);
        parcel.writeString(dialogId);
        parcel.writeString(isUploaded);
        parcel.writeString(isDownloaded);
        parcel.writeParcelable(audioUri, i);
        parcel.writeLong(dateSent);
        parcel.writeString(body);
        if (read == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(read);
        }
        if (recipientId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(recipientId);
        }
        if (senderId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(senderId);
        }
        parcel.writeByte((byte) (markable ? 1 : 0));
        parcel.writeByte((byte) (delayed ? 1 : 0));
        parcel.writeDouble(latitude);
        parcel.writeString(placeName);
        parcel.writeInt(progress);
        parcel.writeString(msgRoomId);
        parcel.writeDouble(longitude);
        parcel.writeByte((byte) (saveToHistory ? 1 : 0));
        parcel.writeString(msgSeenByMembers);
        parcel.writeString(msgType);
        parcel.writeString(tripId);
        parcel.writeString(isFailed);
        parcel.writeString(senderTripTimestamp);
    }
}
