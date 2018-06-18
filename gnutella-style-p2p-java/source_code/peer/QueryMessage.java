package peer;

import java.io.Serializable;

public class QueryMessage implements Serializable{
	final private String msgId;
	final private String fileName;
	private int ttl=0;
	public QueryMessage(String msgId, String fileName, int ttl) {
		super();
		this.msgId = msgId;
		this.fileName = fileName;
		this.ttl = ttl;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public String getMsgId() {
		return msgId;
	}
	public String getFileName() {
		return fileName;
	}
	
	public void incrementTtl(){
		this.ttl++;
	}
	
	public void decrementTtl(){
		this.ttl--;
	}
	
	protected QueryMessage duplicate(){
        return new QueryMessage(this.msgId, this.fileName, this.ttl);
    }
}
