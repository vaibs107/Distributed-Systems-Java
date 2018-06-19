import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract class Node {
	protected Vector<DataNode> data;
	protected  Node parent;
	protected int maxsize;
	protected Node nextNode;	
	
	protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public boolean isLeafNode() {
	    return this.getClass().getName().trim().equals("LeafNode");
	}

	abstract Node insert(DataNode dnode);
	abstract boolean search(DataNode x);

	protected boolean isFull() {
		return data.size() == maxsize-1;
	}
	
	public DataNode getDataAt(int index) {
		return (DataNode) data.elementAt(index);
	}
	
	protected void propagate(DataNode dnode, Node right) {
		if(parent == null) {
			TreeNode newparent = new TreeNode(maxsize);
			
			newparent.readWriteLock.writeLock().lock();
			newparent.data.add(dnode);
			newparent.pointer.add(this);
			newparent.pointer.add(right);
			
			newparent.readWriteLock.writeLock().unlock();
			this.setParent(newparent);
			right.setParent(newparent);
		}
		else {
			if( ! parent.isFull() ) {
				parent.readWriteLock.writeLock().lock();
				boolean dnodeinserted = false;
				for(int i = 0; !dnodeinserted && i < parent.data.size(); i++) {
					if( ((DataNode)parent.data.elementAt(i)).inOrder(dnode) ) {
						parent.data.add(i,dnode);
						((TreeNode)parent).pointer.add(i+1, right);
						dnodeinserted = true;
					}
				}
				if(!dnodeinserted) {
					parent.data.add(dnode);
					((TreeNode)parent).pointer.add(right);
				}
				parent.readWriteLock.writeLock().unlock();
				right.setParent(this.parent);
			}
			else {
                ((TreeNode)parent).split(dnode, this, right);
			}
		}
	}
	
	public int size() {
		return data.size();
	}

	@SuppressWarnings("unchecked") Node(int degree) {
	    parent = null;
	    
	    data = new Vector();
	    maxsize = degree;
	}

	public String toString() {
		this.readWriteLock.writeLock().lock();
		String s = "";
		for(int i=0; i < data.size(); i++) {
			s += ((DataNode)data.elementAt(i)).toString() + " ";
		}
		
		this.readWriteLock.writeLock().unlock();
		if(this.getNextNode() !=null){
			return s + "->";
		}
		else{
			return s + "#";
		}
	}

	protected Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.readWriteLock.writeLock().lock();
		this.nextNode = nextNode;
		this.readWriteLock.writeLock().lock();
	}

	protected Node findRoot() {
		Node node = this;
		
		while(node.parent != null) {
			node = node.parent;
		}
		
		return node;
	}

	protected void setParent(Node newparent) {
		this.readWriteLock.writeLock().lock();
		this.parent = newparent;
		this.readWriteLock.writeLock().unlock();
	}
} 