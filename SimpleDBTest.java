import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class SimpleDBTest {
	
	public static void main(String[] args) throws IOException{
		SimpleDB Dbase = new SimpleDB();
		BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
		String command = null;
		while(true){
			command = input.readLine().trim();
			if(command.equals("END")){
				break;
			}
			String[] command_args = command.split(" ");
			if(Dbase.check(command_args)){
				Dbase.run(command_args);
			}
		}
	}
}
