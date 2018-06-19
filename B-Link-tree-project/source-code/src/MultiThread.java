import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MultiThread implements Runnable{
	protected int tid;
	protected Thread t;
	
	public MultiThread(int id)
	{
		this.tid = id;
	}
	
	public void run()
	{
		try {
            System.out.println("Thread completed: " + tid);

			String inputfileName = "blinktree" + tid + ".inp";
			String outputfileName = "blinktree" + tid + ".out";
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter output = new BufferedWriter( new FileWriter(new File(outputfileName)) );            
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(inputfileName)));
            } catch (FileNotFoundException e) {
                System.err.println("Error: specified file not found (defaulting to standard input)");
            }
            
            Command c = new Command(); 
            while(c.getCommand() != 'q') {
                try {
                    c.readCommand(in);
                    BLinkTree.executeCommand(c, output);
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (InvalidCommandException e) {
                    System.err.println(e.getMessage());
                    System.out.println("Valid Query-Modes:\n\ti x - insert x into tree\n\ts x - find x in tree\n\tp   - print tree\n\tq   - quit");
                }
                catch (NumberFormatException e) {
                	System.err.println("This type of command requires a integer operand");
                    System.out.println("Valid Query-Modes:\n\ti x - insert x into tree\n\ts x - find x in tree\n\tp   - print tree\n\tq   - quit");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            output.close();
            in.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start()
	{
         t = new Thread(this);
         t.start();

	}
	public void join() throws InterruptedException{
		t.join();
	}
}