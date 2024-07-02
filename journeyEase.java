import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.util.LinkedList;
import java.util.HashSet;
import java.time.LocalDate;
import java.time.DayOfWeek;

//import java.util.Arrays;

class Railway
{
	File dataObj, scheduleObj, pnrObj;
	Scanner dataReader, scheduleReader, pnrReader;
	
	public Railway()
	{
		try
		{
			this.dataObj = new File("data/train_info.csv");
			this.scheduleObj = new File("data/train_schedule.csv");
			this.pnrObj = new File("data/PNR.csv");
			
			this.dataReader = new Scanner(dataObj);
			this.scheduleReader = new Scanner(scheduleObj);
			this.pnrReader = new Scanner(pnrObj);
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
	
	private void resetPNRScanner()
	{
		try
		{
			this.pnrReader = new Scanner(pnrObj);
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
	
	public boolean addTrain(LinkedList<LinkedList<String>>[] newTrainInfo)
	{
		if(this.isTrainNumValid(Integer.parseInt(newTrainInfo[0].get(0).get(0))))
			return false;
		
		try
		{
			FileWriter dataWriter = new FileWriter("data/train_info.csv", true);
			FileWriter scheduleWriter = new FileWriter("data/train_schedule.csv", true);
			
			String trainData = "", trainSchedule = "";
			
			for(String data : newTrainInfo[0].get(0))
				trainData += "\"" + data + "\"" + ",";
			trainData = trainData.substring(0, trainData.length() - 1);
			
			dataWriter.write(trainData);
			dataWriter.write(System.lineSeparator());
			dataWriter.close();
			
			for(LinkedList<String> station : newTrainInfo[1])
			{
				trainSchedule = "";
				
				for(int i = 0 ; i < 6 ; i++)
					trainSchedule += station.get(i) + ",";
				
				trainSchedule = trainSchedule.substring(0, trainSchedule.length() - 1);
				
				scheduleWriter.write(trainSchedule);
				scheduleWriter.write(System.lineSeparator());
			}
			
			scheduleWriter.close();
		}
		
		catch(IOException e)
		{
			System.out.println("ERROR!");
			return false;
		}
		
		return true;
	}
	
	public HashSet<Integer> searchTrainsOnDate(String source, String destination, String date)
	{
		HashSet<Integer> trainNums = this.searchTrains(source, destination);
		HashSet<Integer> newTrainNums = new HashSet<Integer>();
		
		for(int num : trainNums)
			if(trainRunsOnDate(date, num))
				newTrainNums.add(num);
		
		return newTrainNums;
	}
	
	public HashSet<Integer> searchTrains(String source, String destination)
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
	
	public boolean trainRunsOnDate(String dateString, int trainNum)
	{
		LocalDate date = LocalDate.parse(dateString);// Format : 2024-01-08
		DayOfWeek day = date.getDayOfWeek();
    	int dayInt = day.getValue();
    	
    	LinkedList<LinkedList<String>>[] trainInfo = this.fetchTrainInfo(trainNum);
    	String dayTrain = trainInfo[0].get(0).get(4);
    	
    	int dayTrainInt = 0;
    	
    	switch(dayTrain)
    	{
    		case "Monday":
    			dayTrainInt = 1;
    			break;
    		
    		case "Tuesday":
    			dayTrainInt = 2;
    			break;
    		
    		case "Wednesday":
    			dayTrainInt = 3;
    			break;
    		
    		case "Thursday":
    			dayTrainInt = 4;
    			break;
    		
    		case "Friday":
    			dayTrainInt = 5;
    			break;
    		
    		case "Saturday":
    			dayTrainInt = 6;
    			break;
    		
    		case "Sunday":
    			dayTrainInt = 7;
    			break;
    	}
    	
    	if(dayTrainInt == dayInt)
    		return true;
    	return false;
	}
	
	public int bookTrain(String username, int trainNum, String date, String classTrain, String from, String to)
	{
		if(!this.trainRunsOnDate(date, trainNum))
			return -1;
		
		Random rand = new Random();
		int randomPNR = -1;
		boolean isNotUnique = true;
		
		while(isNotUnique)
		{
			isNotUnique = false;
			
			randomPNR = rand.nextInt(999999 - 100000 + 1) + 100000; // 6-digit random number
			int pnr;
			while(this.pnrReader.hasNextLine())
			{
				pnr = Integer.parseInt(this.pnrReader.nextLine().split(",")[0]);
				
				if(pnr == randomPNR)
				{
					isNotUnique = true;
					break;
				}
			}
			
			this.resetPNRScanner();
		}
		
		String pnrData = randomPNR + "," + username + "," + trainNum + "," + date + "," + classTrain + "," + from + "," + to;
		LinkedList<LinkedList<String>>[] trainInfo = this.fetchTrainInfo(trainNum);
		
		try
		{
			FileWriter pnrWriter = new FileWriter("data/PNR.csv", true);
			pnrWriter.write(pnrData);
			pnrWriter.write(System.lineSeparator());
			pnrWriter.close();
			
			FileWriter ticketWriter = new FileWriter("data/Tickets/PNR" + randomPNR + ".txt", false);
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|  USERNAME  | " + username);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|    PNR     | " + randomPNR);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("| TRAIN NO.  | " + trainNum);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("| TRAIN NAME | " + trainInfo[0].get(0).get(1));
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|    DATE    | " + date);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|    CLASS   | " + classTrain);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|    FROM    | " + from);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("|    TO      | " + to);
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			
			for(int i = 0 ; i < 8 ; i++)
				ticketWriter.write(System.lineSeparator());
			
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("SCHEDULE");
			ticketWriter.write(System.lineSeparator());
			ticketWriter.write("==========================");
			ticketWriter.write(System.lineSeparator());
			
			for(int i = 1 ; i < trainInfo[1].size() ; i++)
			{
				ticketWriter.write("Station Code   : " + trainInfo[1].get(i).get(1));
				ticketWriter.write(System.lineSeparator());
				ticketWriter.write("Station        : " + trainInfo[1].get(i).get(2));
				ticketWriter.write(System.lineSeparator());
				ticketWriter.write("Arrival Time   : " + trainInfo[1].get(i).get(3));
				ticketWriter.write(System.lineSeparator());
				ticketWriter.write("Departure Time : " + trainInfo[1].get(i).get(4));
				ticketWriter.write(System.lineSeparator());
				ticketWriter.write("Distance Trav. : " + trainInfo[1].get(i).get(5));
				ticketWriter.write(System.lineSeparator());
				ticketWriter.write("==========================");
				ticketWriter.write(System.lineSeparator());
			}
			
			ticketWriter.close();
		}
		
		catch(IOException e)
		{
			return -1;
		}
			
		return randomPNR;
	}
	
	private boolean isPNRValid(int PNR)
	{
		while(this.pnrReader.hasNextLine())
		{
			int pnr = Integer.parseInt(this.pnrReader.nextLine().split(",")[0]);
			
			if(pnr == PNR)
			{
				this.resetPNRScanner();
				return true;
			}
		}
		
		this.resetPNRScanner();
		return false;
	}
	
	public String[] getPNRData(int PNR)
	{
		if(!this.isPNRValid(PNR))
			return new String[7];
		
		String[] pnrData = new String[7];
		while(this.pnrReader.hasNextLine())
		{
			pnrData = this.pnrReader.nextLine().split(",");
			int pnr = Integer.parseInt(pnrData[0]);
			
			if(pnr == PNR)
			{
				this.resetPNRScanner();
				return pnrData;
			}
		}
		
		return pnrData;
	}
	
	public void printPNRData(int PNR)
	{
		if(!this.isPNRValid(PNR))
			return;
		
		String[] pnrData = this.getPNRData(PNR);
		
		System.out.println("PNR       : " + pnrData[0]);
		System.out.println("Username  : " + pnrData[1]);
		System.out.println("Train No. : " + pnrData[2]);
		System.out.println("Date      : " + pnrData[3]);
		System.out.println("Class     : " + pnrData[4]);
		System.out.println("From      : " + pnrData[5]);
		System.out.println("To        : " + pnrData[6]);
	}
	
	public void printTrains(String source, String destination)
	{
		HashSet<Integer> trainNums = this.searchTrains(source, destination);
		
		for(int trainNum : trainNums)
		{
			this.printTrainInfo(trainNum, false);
			System.out.println();
		}
	}
	
	public void printTrainsOnDate(String source, String destination, String date)
	{
		HashSet<Integer> trainNums = this.searchTrainsOnDate(source, destination, date);
		
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
	
	public boolean isTrainNumValid(int trainNum)
	{
		dataReader.nextLine();
		
		while(this.dataReader.hasNextLine())
		{
			String[] data = this.dataReader.nextLine().replace("\"", "").split(",");
			
			if(Integer.parseInt(data[0]) == trainNum)
			{
				this.resetScanner();
				return true;
			}
		}
		
		this.resetScanner();
		return false;
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
		/*LinkedList<LinkedList<String>>[] data = new LinkedList[2];
		data[0] = new LinkedList();
		data[1] = new LinkedList();
		data[0].add(new LinkedList(Arrays.asList("705", "GHY Sp.", "Guwahati", "New Delhi", "Saturday")));
		data[1].add(new LinkedList(Arrays.asList("705", "GHY", "Guwahati", "17:15:00", "17:20:00", "0")));
		data[1].add(new LinkedList(Arrays.asList("705", "NDLS", "Delhi", "10:15:00", "17:20:00", "2017")));
		System.out.println(data[1].get(0));
		
		Railway rail = new Railway();
		rail.printTrainsOnDate("GHY", "NDLS", "2024-07-06");
		System.out.println(rail.addTrain(data));
		System.out.println(rail.isTrainNumValid(705));
		rail.printTrains("GHY", "NDLS");
		rail.printTrainInfo("GHY Sp.", true);
		
		System.out.println(rail.trainRunsOnDate("2024-07-02", 421));
		rail.printTrainsOnDate("GHY", "NDLS", "2024-06-28");
		System.out.println(rail.bookTrain("user123", 12235, "2024-07-05", "2A", "GHY", "NDLS"));
		rail.printPNRData(667024);*/
	}
}
