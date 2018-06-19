import java.io.BufferedReader;
import java.io.IOException;

class Command {
	int xvalue;
	char command;
	
	Command() {
		command = 0;
		xvalue = 0;
	}
	
	public String toString() {
		return "command = " + command + " x = " + xvalue;
	}

	public void readCommand(BufferedReader in) throws InvalidCommandException, IOException, NumberFormatException {
        
		boolean readcommand = false;
	
		while(!readcommand && in.ready()) {
			command = (char)in.read();
        	if(command == '#') {
                System.out.println( in.readLine() );
        	}
        	else if(this.validCommand()) {
        		if(this.commandWithArgument()) {
        			xvalue = Integer.parseInt(in.readLine().trim());
        		}
        		else {
        			xvalue = 0;
        			in.readLine();
        		}
        		readcommand = true;
        	}
        	else {
        		in.readLine();
        		throw new InvalidCommandException(command);
        	}
		}
	}
	
	public char getCommand() {
		return command;
	}
	
	public int getXValue() {
		return xvalue;
	}
	
	private boolean validCommand() {
		return commandWithArgument() || commandWithoutArgument();
	}
	private boolean commandWithArgument() {
		return this.command == 'i' || this.command == 's' || this.command == 'd';
	}
	private boolean commandWithoutArgument() {
		return this.command == 'p' || this.command == 'q';
	}
}