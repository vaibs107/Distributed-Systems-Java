package peer;

import java.util.ArrayList;

public class AssociativeArray<T> extends ArrayList<T>{

	private static final long serialVersionUID = 1L;
	public int maxSize;

	public AssociativeArray(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	public boolean add(T obj) {
		if(super.size()>=maxSize){
			super.remove(0);
		}
		return super.add(obj);
	}
	
	
}
