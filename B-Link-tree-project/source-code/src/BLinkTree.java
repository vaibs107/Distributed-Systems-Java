import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BLinkTree {

    private static Node tree;
    private static int degree;
    private static boolean debug;
    
    private BLinkTree(int x) {
    	degree = x;
    	tree = new LeafNode(degree);
    	debug = false;
    }
    
    protected static void executeCommand(Command c, BufferedWriter output) throws InvalidCommandException, IOException {
        switch( (int) c.getCommand() ) {
        case 'd':
        	if( c.getXValue() == 1 && !debug) {
        		debug = true;
                System.out.println("ENTERING DEBUG MODE");
        	}
        	else if ( c.getXValue() == 0 && debug) {
        		debug = false;
                System.out.println("EXITTING DEBUG MODE");
        	}
        	else if (c.getXValue() != 0 || c.getXValue() != 1){
        		throw new InvalidCommandException("Invalid Operand with command d. Must be 0 or 1.");
        	}
        case 'p':
        	if(debug) {
                System.out.println("PRINTING TREE");
        	}
        	printTree(output);
            break;
        case 's':
        	if(debug) {
                System.out.println("SEARCHING TREE FOR x = " + c.getXValue());
        	}
        	searchTree(c.getXValue(), output);
        	break;
        case 'i':
        	if(debug) {
                System.out.println("INSERTING x = " + c.getXValue() + " INTO THE TREE");
        	}
        	insertIntoTree(new DataNode(c.getXValue()));
            break;
        }
        if(debug && (int)c.getCommand() != 'p') {
        	printTree(new BufferedWriter(new PrintWriter(System.out)));
        	System.out.println("--->OPERATION COMPLETE");
        }
    }
    
    private static void insertIntoTree(DataNode dnode) {
    	tree = tree.insert(dnode);
	}

	private static void searchTree(int x, BufferedWriter output) throws IOException {
        if( tree.search(new DataNode(x)) ) {
            output.write("FOUND" + System.getProperty("line.separator"));    
        }
        else {
            output.write("NOT FOUND" + System.getProperty("line.separator"));
        }
	}

	@SuppressWarnings("unchecked")
    private static void printTree(BufferedWriter output) throws IOException { 
        Vector<Node> nodeList = new Vector();
        
        nodeList.add(tree);

        boolean done = false;
        while(! done) {
            Vector<Node> nextLevelList = new Vector();
            String toprint = "";
            
            for(int i=0; i < nodeList.size(); i++) {
                Node node = (Node)nodeList.elementAt(i);
                toprint += node.toString() + " ";
                if(node.isLeafNode()) {
                    done = true;
                }
                else
                {
                    for(int j=0; j < node.size()+1 ; j++) {
                        nextLevelList.add( ((TreeNode)node).getPointerAt(j) );
                    }
                }
            }
            output.write(toprint + System.getProperty("line.separator"));
            nodeList = nextLevelList;
        }
	}

	protected static void readDegree(BufferedReader in) {
		try {
        	new BLinkTree(3);
        	
        } catch (Exception e1) {
            System.err.println("degree could not be read... defaulting to order 3");
            new BLinkTree(3);
        }
    }
    
    public static void main(String[] args) throws IOException {
        if(args.length > 1) {
            System.err.println("Syntax error in call sequence, use:\n\tjava BplusTree");
        }
        else {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            int nthreads = NUM_OF_THREADS;
            BLinkTree.readDegree(in);
            
            long start_time = System.currentTimeMillis();
            
            List<MultiThread> threadList = new ArrayList<>();
            for (int i=0; i<nthreads; i++)
            {
            	MultiThread thread = new MultiThread(i);
            	threadList.add(thread);
            	thread.start();
            }
            
            for(MultiThread thread : threadList){
            	try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            in.close();
            
            long end_time = System.currentTimeMillis();
            System.out.println("Total time taken = " + (int)(end_time-start_time) + " msecs");
            System.exit(0);
        }
    }
    
    public static final int NUM_OF_THREADS = 2;
}