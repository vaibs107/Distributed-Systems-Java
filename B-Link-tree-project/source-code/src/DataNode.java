public class DataNode {
    private Integer data;
    
    DataNode() {
        data = null;
    }   
    public String toString() {
		return data.toString();
	}
	public DataNode(int x) {
        data = x;
    }
    public int getData() {
        return data.intValue();
    }   
    public boolean inOrder(DataNode dnode) {
        return (dnode.getData() <= this.data.intValue());
    }
}
