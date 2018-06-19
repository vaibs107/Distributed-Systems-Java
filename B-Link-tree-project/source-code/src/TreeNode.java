import java.util.Vector;

class TreeNode extends Node {
	protected Vector<Node> pointer;
	protected int maxKey = Integer.MAX_VALUE;
	protected TreeNode nextNode;	

	@SuppressWarnings("unchecked") 
	TreeNode(int x) {
		super(x);
		pointer = new Vector();
		
	}

	public Vector<Node> getPointer() {
		return pointer;
	}

	public void setPointer(Vector<Node> pointer) {
		this.readWriteLock.writeLock().lock();
		this.pointer = pointer;
		this.readWriteLock.writeLock().unlock();
	}

	public int getMaxKey() {
		return maxKey;
	}

	public void setMaxKey(int maxKey) {
		this.maxKey = maxKey;
	}

	public TreeNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(TreeNode nextNode) {
		this.readWriteLock.writeLock().lock();
		this.nextNode = nextNode;
		this.readWriteLock.writeLock().unlock();
	}

	public Node getPointerTo(DataNode x) {
		int i = 0;
		boolean xptrfound = false;
		while(!xptrfound && i < data.size()) {
			if( ((DataNode)data.elementAt(i)).inOrder(x ) ) {
				xptrfound = true;
			}
			else {
				i++;				
			}

		}
		if(!xptrfound && this.getNextNode()!=null ){
			return this.getNextNode().getPointerTo(x);
		}
		return (Node) pointer.elementAt(i);
		
	}

	public Node getPointerAt(int index) {
		return (Node) pointer.elementAt(index);
	}

	boolean search(DataNode dnode) {
		Node next = this.getPointerTo(dnode);
		return next.search(dnode);
	}

	protected void split(DataNode dnode, Node left, Node right) {
		this.readWriteLock.writeLock().lock();
		int splitlocation, insertlocation = 0; 
		if(maxsize%2 == 0) {
			splitlocation = maxsize/2;
		}
		else {
			splitlocation = (maxsize+1)/2 -1;
		}
		boolean dnodeinserted = false;
		for(int i=0; !dnodeinserted && i < data.size(); i++) {
			if( ((DataNode)data.elementAt(i)).inOrder(dnode) ) {
				data.add(i,dnode);
				((TreeNode)this).pointer.remove(i);
				((TreeNode)this).pointer.add(i, left);
				((TreeNode)this).pointer.add(i+1, right);
				dnodeinserted = true;
				insertlocation = i;
			}
		}
		if(!dnodeinserted) {
            insertlocation = data.size();
			data.add(dnode);
			((TreeNode)this).pointer.remove(((TreeNode)this).pointer.size()-1);
			((TreeNode)this).pointer.add(left);
			((TreeNode)this).pointer.add(right);
            
		}
		DataNode mid = (DataNode) data.remove(splitlocation);
		TreeNode newright = new TreeNode(maxsize);
		for(int i=data.size()-splitlocation; i > 0; i--) {
			newright.data.add(this.data.remove(splitlocation));
			newright.pointer.add(this.pointer.remove(splitlocation+1));
		}
		newright.pointer.add(this.pointer.remove(splitlocation+1));
        if(insertlocation < splitlocation) {
            left.setParent(this);
            right.setParent(this);
        }
        else if(insertlocation == splitlocation) {
            left.setParent(this);
            right.setParent(newright);
        }
        else {
            left.setParent(newright);
            right.setParent(newright);
        }
        
        newright.setNextNode( this.getNextNode());
        this.setNextNode(newright);
		this.readWriteLock.writeLock().unlock();
		this.propagate(mid, newright);
	}

	Node insert(DataNode dnode) {
		Node next = this.getPointerTo(dnode);
		
		return next.insert(dnode);
	}
}