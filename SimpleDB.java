import java.util.*;
public class SimpleDB {
	//Constructor function, initialize storage space and necessary variable
	public SimpleDB(){
		DBmap = new HashMap<String,Integer>();
		DBcounter = new HashMap<Integer,Integer>();
		RecoveryHistory = new Stack<String>();
		num_block = 0;
	}
	
	//SET function 
	public void SET(String name, int value, boolean history){
		if(DBmap.containsKey(name)){
			int old_value = DBmap.get(name);
			DBcounter.put(DBmap.get(name), DBcounter.get(DBmap.get(name))-1);
			DBmap.put(name, value);
			if(history){
				String recover_command = "SET "+name+" "+Integer.toString(old_value);
				RecoveryHistory.push(recover_command);
			}
		}
		else{
			DBmap.put(name, value);
			if(history){
				String recover_command = "UNSET "+name;
				RecoveryHistory.push(recover_command);
			}
		}
		if(DBcounter.containsKey(value)){
			DBcounter.put(value, DBcounter.get(value)+1);
		}
		else{
			DBcounter.put(value,1);	
		}
		
	}
	
	//GET function
	public void GET(String name){
		if(DBmap.containsKey(name)){
			System.out.println(DBmap.get(name));
		}
		else{
			System.out.println("NULL");
		}
		
	}
	
	//UNSET function
	public void UNSET(String name, boolean history){
		if(DBmap.containsKey(name)){
			int value = DBmap.get(name);
			DBmap.remove(name);
			DBcounter.put(value, DBcounter.get(value)-1);
			if(history){
				String recover_command = "SET "+name+" "+Integer.toString(value);
				RecoveryHistory.push(recover_command);
			}
		}
	}
	
	//NUMEQUALTO function
	public void NUMEQUALTO(int value){
		if(DBcounter.containsKey(value)){
			System.out.println(DBcounter.get(value));
		}
		else{
			System.out.println(0);
		}
	}
	
	//Operation schedule function
	public void run(String[] args){
		Command command = Command.valueOf(args[0]);
		switch(command){
		case SET:
			if(num_block==0){
				SET(args[1],Integer.parseInt(args[2]),false);
			}
			else{
				SET(args[1],Integer.parseInt(args[2]),true);
			}
			return ;
			
		case GET:
			GET(args[1]);
			return;
			
		case UNSET:
			if(num_block==0){
				UNSET(args[1],false);
			}
			else{
				UNSET(args[1],true);
			}
			return;
			
		case NUMEQUALTO:
			NUMEQUALTO(Integer.parseInt(args[1]));
			return ;
			
		case BEGIN:
			num_block++;
			RecoveryHistory.push("BEGIN");
			return;
			
		case ROLLBACK:
			if(RecoveryHistory.isEmpty()){
				System.out.println("NO TRANSACTION");
				return;
			}
			String recover_command = RecoveryHistory.pop();
			while(!recover_command.equals("BEGIN")){
				String[] re_args = recover_command.split(" ");
				if(re_args[0].equals("SET")){
					SET(re_args[1],Integer.parseInt(re_args[2]),false);
				}
				if(re_args[0].equals("UNSET")){
					UNSET(re_args[1],false);
				}
				recover_command = RecoveryHistory.pop();
			}
			num_block--;
			return;
			
		case COMMIT:
			if(num_block==0){
				System.out.println("NO TRANSACTION");
				return;
			}
			RecoveryHistory.clear();
			num_block = 0;
		}
	}
	
	//Input command sanity checking function
	public boolean check(String[] command_args){
		if(command_args[0].equals("")){
			return false;
		}
		Command command = null;
		try{
			command = Command.valueOf(command_args[0]);
		}catch (IllegalArgumentException error){
			System.out.println("COMMAND NOT SUPPOERTED");
			return false;
		}
		switch(command){
		case SET:
			if(command_args.length!=3){
				System.out.println("Invalid number of args for command SET");
				return false;
			}
			int value = 0;
			try{
				value = Integer.parseInt(command_args[2]);
			}catch(NumberFormatException error){
				System.out.println("Invalud value for command SET");
				return false;
			}
			return true;
			
		case GET:
			if(command_args.length!=2){
				System.out.println("Invalid number of args for command GET");
				return false;
			}
			return true;
			
		case UNSET:
			if(command_args.length!=2){
				System.out.println("Invalid number of args for command UNSET");
				return false;
			}
			return true;
			
		case NUMEQUALTO:
			if(command_args.length!=2){
				System.out.println("Invalid number of args for command UNSET");
				return false;
			}
			try{
				value = Integer.parseInt(command_args[1]);
			}catch(NumberFormatException error){
				System.out.println("Invalid value for command NUMEQUALTO");
				return false;
			}
			return true;
			
		case BEGIN:
			if(command_args.length!=1){
				System.out.println("Invalid number of args for command BEGIN");
				return false;
			}
			return true;
			
		case ROLLBACK:
			if(command_args.length!=1){
				System.out.println("Invalid number of args for command ROLLBACK");
				return false;
			}
			return true;
			
		case COMMIT:
			if(command_args.length!=1){
				System.out.println("Invalid number of args for command COMMIT");
				return false;
			}
			return true;
			
		default:
			return true;
		}
	}
	//<name,value> pair mapping
	private HashMap<String,Integer> DBmap;
	//value counter
	private HashMap<Integer,Integer> DBcounter;
	//Command recorder
	private Stack<String> RecoveryHistory;
	//Number of blocks
	private int num_block;
	//Command category
	private static enum Command{
		SET, GET, UNSET, NUMEQUALTO, BEGIN, ROLLBACK, COMMIT;
	}

}
