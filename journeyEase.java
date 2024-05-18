import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.HashSet;

class Railway
{
	File dataObj, scheduleObj;
	Scanner dataReader, scheduleReader;
	
	public Railway()
	{
		try
		{
			this.dataObj = new File("data/train_info.csv");
			this.scheduleObj = new File("data/train_schedule.csv");
			
			this.dataReader = new Scanner(dataObj);
			this.scheduleReader = new Scanner(scheduleObj);
		}
		
		catch(FileNotFoundException e)
		{
			System.out.println("ERROR : File Not Found!");
			System.exit(0);
		}
	}
	
	private void resetScanner()
	{
		try
		{
			this.dataReader = new Scanner(dataObj);
			this.scheduleReader = new Scanner(scheduleObj);
		}
		
		catch(FileNotFoundException e)
		{
			System.out.println("ERROR : File Not Found!");
			System.exit(0);
		}
	}
	
	private LinkedList<LinkedList<String>>[] fetchTrainInfo(int trainNum)
	{
		LinkedList<LinkedList<String>>[] trainInfo = new LinkedList[2];
		trainInfo[0] = new LinkedList<LinkedList<String>>();
		trainInfo[1] = new LinkedList<LinkedList<String>>();
		trainInfo[0].add(new LinkedList<String>());
		trainInfo[1].add(new LinkedList<String>());
		
		boolean trainFound;
		String[] data = {};
		
		dataReader.nextLine();
		scheduleReader.nextLine();
		
		trainFound = false;
		while(this.dataReader.hasNextLine())
		{
			data = this.dataReader.nextLine().replace("\"", "").split(",");
			int tempNum = Integer.parseInt(data[0]);
			
			if(tempNum == trainNum)
			{
				trainFound = true;
				break;
			}
		}
		
		if(!trainFound)
			return trainInfo;
		
		for(int i = 0 ; i < data.length ; i++)
			trainInfo[0].get(0).add(data[i]);
		
		trainFound = false;
		while(this.scheduleReader.hasNextLine())
		{
			int tempNum;
			data = this.scheduleReader.nextLine().replace("\"", "").split(",");
			tempNum = Integer.parseInt(data[0]);
			
			if(tempNum == trainNum)
			{
				trainFound = true;
				trainInfo[1].add(new LinkedList<String>());
				
				for(int i = 0 ; i < 6 ; i++)
					trainInfo[1].getLast().add(data[i]);
			}
			
			else if(trainFound)
				break;
		}
		
		this.resetScanner();
		return trainInfo;
	}
	
	private LinkedList<LinkedList<String>>[] fetchTrainInfo(String trainName)
	{
		LinkedList<LinkedList<String>>[] trainInfo = new LinkedList[2];
		trainInfo[0] = new LinkedList<LinkedList<String>>();
		trainInfo[1] = new LinkedList<LinkedList<String>>();
		trainInfo[0].add(new LinkedList<String>());
		trainInfo[1].add(new LinkedList<String>());
		
		boolean trainFound;
		int trainNum = -1;
		String[] data = {};
		
		dataReader.nextLine();
		scheduleReader.nextLine();
		
		trainFound = false;
		while(this.dataReader.hasNextLine())
		{
			data = this.dataReader.nextLine().replace("\"", "").split(",");
			
			if(trainName.equals(data[1]))
			{
				trainFound = true;
				trainNum = Integer.parseInt(data[0]);
				break;
			}
		}
		
		if(!trainFound)
			return trainInfo;
		
		for(int i = 0 ; i < data.length ; i++)
			trainInfo[0].get(0).add(data[i]);
		
		trainFound = false;
		while(this.scheduleReader.hasNextLine())
		{
			int tempNum;
			data = this.scheduleReader.nextLine().replace("\"", "").split(",");
			tempNum = Integer.parseInt(data[0]);
			
			if(tempNum == trainNum)
			{
				trainFound = true;
				trainInfo[1].add(new LinkedList<String>());
				
				for(int i = 0 ; i < 6 ; i++)
					trainInfo[1].getLast().add(data[i]);
			}
			
			else if(trainFound)
				break;
		}
		
		this.resetScanner();
		return trainInfo;
	}
	
	private HashSet<Integer> searchTrains(String source, String destination)
	{
		HashSet<Integer> trainNums = new HashSet<Integer>();
		
		dataReader.nextLine();
		scheduleReader.nextLine();
		
		int prevNum = -1;
		boolean sourceFound = false;
		while(this.scheduleReader.hasNextLine())
		{
			String[] data = this.scheduleReader.nextLine().replace("\"", "").split(",");
			
			if(source.equals(data[1]))
				sourceFound = true;
			
			else if(destination.equals(data[1]) && sourceFound)
				trainNums.add(Integer.parseInt(data[0]));
				
			int tempNum = Integer.parseInt(data[0]);
			if(tempNum != prevNum)
				sourceFound = false;
			
			prevNum = tempNum;
		}
		
		this.resetScanner();
		
		return trainNums;
	}
	
	public void printTrains(String source, String destination)
	{
		HashSet<Integer> trainNums = searchTrains(source, destination);
		
		for(int trainNum : trainNums)
		{
			this.printTrainInfo(trainNum, false);
			System.out.println();
		}
	}
	
	
	public void printTrainInfo(int trainNum, boolean printSchedule)
	{
		LinkedList<LinkedList<String>>[] trainInfo = this.fetchTrainInfo(trainNum);
		
		if(trainInfo[0].get(0).size() == 0)
			return;
		
		this.print(trainInfo, printSchedule);
	}
	
	public void printTrainInfo(String trainName, boolean printSchedule)
	{
		LinkedList<LinkedList<String>>[] trainInfo = this.fetchTrainInfo(trainName);
		
		if(trainInfo[0].get(0).size() == 0)
			return;
		
		this.print(trainInfo, printSchedule);
	}
	
	private void print(LinkedList<LinkedList<String>>[] trainInfo, boolean printSchedule)
	{
		System.out.println("Train Number : " + trainInfo[0].get(0).get(0));
		System.out.println("Train Name   : " + trainInfo[0].get(0).get(1));
		System.out.println("Source       : " + trainInfo[0].get(0).get(2));
		System.out.println("Destination  : " + trainInfo[0].get(0).get(3));
		System.out.println("Running Day  : " + trainInfo[0].get(0).get(4));
		
		if(printSchedule)
		{
			System.out.println();
			System.out.println("SCHEDULE");
			
			for(int i = 1 ; i < trainInfo[1].size() ; i++)
			{
				System.out.println();
				System.out.println("Station Code   : " + trainInfo[1].get(i).get(1));
				System.out.println("Station        : " + trainInfo[1].get(i).get(2));
				System.out.println("Arrival Time   : " + trainInfo[1].get(i).get(3));
				System.out.println("Departure Time : " + trainInfo[1].get(i).get(4));
				System.out.println("Distance Trav. : " + trainInfo[1].get(i).get(5));
			}
		}
	}
}

public class journeyEase
{
	public static void main(String[] args)
	{
		Railway rail = new Railway();
		rail.printTrains("GHY", "NDLS");
		rail.printTrainInfo("NDLS-GHY RAJ", true);
	}
}
