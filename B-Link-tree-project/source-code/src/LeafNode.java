public class LeafNode extends Node {
	private LeafNode nextNode;
	
	LeafNode(int degree) {
		super(degree);
		nextNode = null;
	}
	
	private void setNextNode(LeafNode next) {
		this.readWriteLock.writeLock().lock();
		nextNode = next;
		this.readWriteLock.writeLock().unlock();
	}
	
	protected LeafNode getNextNode() {
		return nextNode;
	}

	public boolean search(DataNode x) {
		for(int i=0; i < data.size(); i++) {
			if( ((DataNode)data.elementAt(i)).getData() == x.getData() ) {
				return true;
			}
		}
		return false;
	}

	protected void split(DataNode dnode) {
		this.readWriteLock.writeLock().lock();
		boolean dnodeinserted = false;
		for(int i=0; !dnodeinserted && i < data.size(); i++) {
			if( ((DataNode)data.elementAt(i)).inOrder(dnode) ) {
				data.add(i,dnode);
				dnodeinserted = true;
			}
		}
		if(!dnodeinserted) {
			data.add(data.size(), dnode);
		}

		int splitlocation;
		if(maxsize%2 == 0) {
			splitlocation = maxsize/2;
		}
		else {
			splitlocation = (maxsize+1)/2;
		}
				
		LeafNode right = new LeafNode(maxsize);
		
		for(int i = data.size()-splitlocation; i > 0; i--) {
			right.data.add(data.remove(splitlocation));
		}
		
		right.setNextNode(this.getNextNode());
		this.setNextNode(right);
		
		DataNode mid =  (DataNode) data.elementAt(data.size()-1);

		this.readWriteLock.writeLock().unlock();

		this.propagate(mid, right);
	}

	public Node insert(DataNode dnode) {
		if(data.size() < maxsize-1) {
			this.readWriteLock.writeLock().lock();
			boolean dnodeinserted = false;
			int i = 0;
			while(!dnodeinserted && i < data.size()) {
				if( ((DataNode)data.elementAt(i)).inOrder(dnode) ) {
					data.add(i,dnode);
					dnodeinserted = true;
				}
				i++;
			}
			if(!dnodeinserted) {
				data.add(data.size(), dnode);
			}
			this.readWriteLock.writeLock().unlock();

		}
		else {
			this.split(dnode);
		}
		return this.findRoot();
	}
}