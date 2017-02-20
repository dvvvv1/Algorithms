import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.IntPredicate;

import javax.naming.spi.DirStateFactory.Result;

//AI Assignment 3
//Author: Puzhi Yao
//Date: 11 May 2015

public class inference {
	public static int totNumSamples;
	public static int numOfNode; //tot # of node
	public static Vector<Node> nodeData = new Vector<Node>();
	public static Vector<Vector<Query>> target = new Vector<Vector<Query>>();
	public static void main(String[] args) throws IOException{
		totNumSamples = 200000;
		String answerFileName = "";
		//read method
		if(args.length >=1){
			answerFileName = args[2];
		}
		else{
			System.out.println("Arguments Error");
			System.exit(0);
		}
	
		//read train data from text file
		Scanner input1 = new Scanner(System.in);
		File modelFile = new File(args[0]);
		input1 = new Scanner(modelFile);
		
		//read first line and create nodes
		String firstLine = input1.nextLine();
		numOfNode = Integer.parseInt(firstLine);
		for(int i = 0; i < numOfNode; i++){
			Node tempNode = new Node();
			nodeData.add(tempNode);
		}

		//read third line and assign name to all nodes
		String secondLine = input1.nextLine();
		String thirdLine = input1.nextLine();
		int tempIndex1 = 0;
		String tempName = "";
		String nextChar;
		for(int i = 0; i < thirdLine.length(); i++){
			nextChar = thirdLine.substring(i, i+1);
			if(!nextChar.equals(" ")){
				tempName = tempName + nextChar;
			}
			else{
				nodeData.get(tempIndex1).nodeName = tempName;
				System.out.println("Name "+tempIndex1+": "+nodeData.get(tempIndex1).nodeName);
				tempIndex1++;
				tempName = "";
			}
		}
		nodeData.get(tempIndex1).nodeName = tempName;
		System.out.println("Name "+tempIndex1+": "+nodeData.get(tempIndex1).nodeName);
		
		//read graph matrix
		String matrixLine = input1.nextLine(); // skip fourth line
		matrixLine = input1.nextLine(); // start read matrix line
		int tempIndex2 = 0;
		while(!(matrixLine.isEmpty())){
			int tempIndex3 = 0;
			for(int i = 0; i < matrixLine.length(); i++){
				char temp = matrixLine.charAt(i);
				if(temp != ' ')
				{
					if(temp == '1'){
						int tempC = tempIndex3;
						int tempP = tempIndex2;
						nodeData.get(tempIndex2).addchildNode(tempC);
						nodeData.get(tempIndex3).addParentNode(tempP);
					}
					tempIndex3++;
				}
			}
			tempIndex2++;
			matrixLine = input1.nextLine();
		}
		
		//read p_data to each node
		String readPdata = input1.nextLine();;
		int tempProIndex = 0;
		while(input1.hasNextLine() && tempProIndex < numOfNode){
			while(!(readPdata.isEmpty())){
				Scanner pd = new Scanner(readPdata);
				Row tempPRow = new Row();
				while(pd.hasNextDouble()){
					tempPRow.addProValue(pd.nextDouble());
				}
				nodeData.get(tempProIndex).addDataRow(tempPRow);
				readPdata = input1.nextLine();
			}
						
			if(readPdata.isEmpty() && input1.hasNextLine()){
				tempProIndex++;
				readPdata = input1.nextLine();
			}
		}
		System.out.println();
		input1.close();	
		
		// update all states
		for(int i = 0; i < numOfNode; i++){
			Vector<int[]> currentNodePValue = setupState(nodeData, nodeData.get(i).getParentNode(),nodeData.get(i).getDataRow().size(), nodeData.get(i).getDataRow().get(0).getProValue().size());
			for(int j = 0; j < nodeData.get(i).getDataRow().size();j++){
				nodeData.get(i).getDataRow().get(j).addStat(currentNodePValue.get(j));
			}
		}

		//print result
		printModel();

		// read query file
		Scanner input2 = new Scanner(System.in);
		File equryFile = new File(args[1]);
		input2 = new Scanner(equryFile);

		String equryLine = "";
		while(input2.hasNextLine()){		
			equryLine = input2.nextLine();
			if(!equryLine.isEmpty())
			{	
				Vector<Query> tempQV = new Vector<Query>();
				int tempIndex = 0;
				String tempN = "";
				String nextC;
				for(int i = 2; i < equryLine.length(); i++){
					nextC = equryLine.substring(i, i+1);
					if(!nextC.equals(" ") && !nextC.equals("|") && !nextC.equals("=") && !nextC.equals(",") && !nextC.equals(")")){
						tempN = tempN + nextC;
					}
					else if(nextC.equals(" ") || nextC.equals("=") || nextC.equals(")")){
						if(!tempN.equals(""))
						{
							if(tempN.equals("true"))
							{
								tempQV.get(tempIndex).queryState = 1;
								tempIndex++;
								tempN = "";
							}
							else if(tempN.equals("false")){
								tempQV.get(tempIndex).queryState = 0;
								tempIndex++;
								tempN = "";
							}
							else{
								Query tempQ = new Query();
								if(tempIndex == 0){
									tempQ.queryName = tempN;
									tempQ.queryState = 2;
									tempQV.add(tempQ);
									tempN = "";
									tempIndex++;
								}
								else{
									tempQ.queryName = tempN;
									tempQV.add(tempQ);
									tempN = "";
								}						
							}
						}
					}			
				}
				target.add(tempQV);
			}
		}
		System.out.println("Target Size: "+target.size());
		System.out.println("v1 Size: "+target.get(0).size());

		Vector<double[]> allSample1 = new Vector<double[]>();
		Vector<double[]> allSample2 = new Vector<double[]>();


		//likelihood weighting for target 1
		for(int i = 0; i < totNumSamples; i++)
		{
			// Initialization sample states
			double[] sample = new double[numOfNode + 1]; //  index 0 ~ numofNode-1 is status
			sample[numOfNode] = 1;				   // index numofNode is weighting
			for(int j = 0; j < numOfNode; j++){
				sample[j] = -1; 
			}

			//generate random data
			for(int j = 0; j < numOfNode; j++){
				double tempWeighting = 0;
				boolean tempCheck1 = false;
				for(int k = 1; k < target.get(0).size(); k++)
				{	
					if(nodeData.get(j).nodeName.equals(target.get(0).get(k).queryName))
					{
						if(nodeData.get(j).getParentNode().size() == 0)
						{
							for(int m = 0; m < 2; m++)
							{
								if(target.get(0).get(k).queryState == nodeData.get(j).P_data.get(0).stat.get(0)[m])
								{
									tempWeighting = nodeData.get(j).P_data.get(0).pro_value.get(m);
									sample[j] = target.get(0).get(k).queryState;
								}
							}
						}
						else{
							int[] parentState = new int[nodeData.get(j).getParentNode().size()];
							for(int m = 0; m < nodeData.get(j).getParentNode().size(); m++ )
							{
								parentState[m] = (int) sample[nodeData.get(j).getParentNode().get(m)]; 
							}

							for(int m = 0; m < nodeData.get(j).P_data.size(); m++)
							{
								boolean tempChecker2 = true;
								for(int n = 0; n < nodeData.get(j).P_data.get(m).stat.get(0).length; n++)
								{
									if(nodeData.get(j).P_data.get(m).stat.get(0)[n] != parentState[n])
									{
										tempChecker2 = false;
									}
								}
								if(tempChecker2 == true)
								{
									sample[j] = target.get(0).get(k).queryState;
									if(target.get(0).get(k).queryState == 0)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(0);
									}
									else if(target.get(0).get(k).queryState == 1)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(1);
									}
									else if(target.get(0).get(k).queryState == 1)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(2);
									}
								}
							}
						}
						sample[numOfNode] = sample[numOfNode] * tempWeighting;
						tempCheck1 = true;
					}
				}
				if(tempCheck1 == false)
				{
					double randomData = Math.random();
					if(nodeData.get(j).getParentNode().size() == 0)
					{
						if(randomData >= nodeData.get(j).P_data.get(0).pro_value.get(0))
						{
							sample[j] = nodeData.get(j).P_data.get(0).stat.get(0)[1];
						}
						else{
							sample[j] = nodeData.get(j).P_data.get(0).stat.get(0)[0];
						}
					}
					else{
						int[] parentStat = new int[nodeData.get(j).getParentNode().size()];
						for(int k = 0; k < nodeData.get(j).getParentNode().size(); k++)
						{
							parentStat[k] = (int) sample[nodeData.get(j).getParentNode().get(k)];
						}

						for(int k = 0; k < nodeData.get(j).P_data.size(); k++)
						{
							boolean tempCheck3 = true;
							for(int l = 0; l < nodeData.get(j).P_data.get(k).stat.get(0).length; l++)
							{
								if(parentStat[l] != nodeData.get(j).P_data.get(k).stat.get(0)[l])
								{
									tempCheck3 = false;
								}
							}
							if(tempCheck3 == true)
							{
								if(randomData >= nodeData.get(j).P_data.get(k).pro_value.get(0))
								{
									if(nodeData.get(j).P_data.get(k).pro_value.size() == 3)
									{
										if(randomData >= nodeData.get(j).P_data.get(k).pro_value.get(0) + nodeData.get(j).P_data.get(k).pro_value.get(1))
										{
											sample[j] = 2;
										}
										else{
											sample[j] = 1;
										}
									}
									else{
										sample[j] = 1;
									}						
								}
								else{
									sample[j] = 0;
								}
							}
						}
					}
				}
			}
			allSample1.add(sample);
		}


		//likelihood weighting for target 2
		for(int i = 0; i < totNumSamples; i++)
		{
			// Initialization sample states
			double[] sample = new double[numOfNode + 1]; //  index 0 ~ numofNode-1 is status
			sample[numOfNode] = 1;				   // index numofNode is weighting
			for(int j = 0; j < numOfNode; j++){
				sample[j] = -1; 
			}

			//generate random data
			for(int j = 0; j < numOfNode; j++){
				double tempWeighting = 0;
				boolean tempCheck1 = false;
				for(int k = 1; k < target.get(1).size(); k++)
				{	
					if(nodeData.get(j).nodeName.equals(target.get(1).get(k).queryName))
					{
						if(nodeData.get(j).getParentNode().size() == 0)
						{
							for(int m = 0; m < 2; m++)
							{
								if(target.get(1).get(k).queryState == nodeData.get(j).P_data.get(0).stat.get(0)[m])
								{
									tempWeighting = nodeData.get(j).P_data.get(0).pro_value.get(m);
									sample[j] = target.get(1).get(k).queryState;
								}
							}
						}
						else{
							int[] parentState = new int[nodeData.get(j).getParentNode().size()];
							for(int m = 0; m < nodeData.get(j).getParentNode().size(); m++ )
							{
								parentState[m] = (int) sample[nodeData.get(j).getParentNode().get(m)]; 
							}

							for(int m = 0; m < nodeData.get(j).P_data.size(); m++)
							{
								boolean tempChecker2 = true;
								for(int n = 0; n < nodeData.get(j).P_data.get(m).stat.get(0).length; n++)
								{
									if(nodeData.get(j).P_data.get(m).stat.get(0)[n] != parentState[n])
									{
										tempChecker2 = false;
									}
								}
								if(tempChecker2 == true)
								{
									sample[j] = target.get(1).get(k).queryState;
									if(target.get(1).get(k).queryState == 0)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(0);
									}
									else if(target.get(1).get(k).queryState == 1)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(1);
									}
									else if(target.get(1).get(k).queryState == 1)
									{
										tempWeighting = nodeData.get(j).P_data.get(m).pro_value.get(2);
									}
								}
							}
						}
						sample[numOfNode] = sample[numOfNode] * tempWeighting;
						tempCheck1 = true;
					}
				}
				if(tempCheck1 == false)
				{
					double randomData = Math.random();
					if(nodeData.get(j).getParentNode().size() == 0)
					{
						if(randomData >= nodeData.get(j).P_data.get(0).pro_value.get(0))
						{
							sample[j] = nodeData.get(j).P_data.get(0).stat.get(0)[1];
						}
						else{
							sample[j] = nodeData.get(j).P_data.get(0).stat.get(0)[0];
						}
					}
					else{
						int[] parentStat = new int[nodeData.get(j).getParentNode().size()];
						for(int k = 0; k < nodeData.get(j).getParentNode().size(); k++)
						{
							parentStat[k] = (int) sample[nodeData.get(j).getParentNode().get(k)];
						}

						for(int k = 0; k < nodeData.get(j).P_data.size(); k++)
						{
							boolean tempCheck3 = true;
							for(int l = 0; l < nodeData.get(j).P_data.get(k).stat.get(0).length; l++)
							{
								if(parentStat[l] != nodeData.get(j).P_data.get(k).stat.get(0)[l])
								{
									tempCheck3 = false;
								}
							}
							if(tempCheck3 == true)
							{
								if(randomData >= nodeData.get(j).P_data.get(k).pro_value.get(0))
								{
									if(nodeData.get(j).P_data.get(k).pro_value.size() == 3)
									{
										if(randomData >= nodeData.get(j).P_data.get(k).pro_value.get(0) + nodeData.get(j).P_data.get(k).pro_value.get(1))
										{
											sample[j] = 2;
										}
										else{
											sample[j] = 1;
										}
									}
									else{
										sample[j] = 1;
									}
								}
								else{
									sample[j] = 0;
								}
							}
						}
					}
				}
			}
			allSample2.add(sample);
		}

		
		
		int ev1 = -1;
		int evS1 = -1;
		int ev2 = -1;
		int evS2 = -1;
		
		for(int i = 0; i < numOfNode; i++){
			if(nodeData.get(i).nodeName.equals(target.get(0).get(0).queryName))
			{
				ev1 = i;
				evS1 = nodeData.get(i).P_data.get(0).pro_value.size();
			}
			
			if(nodeData.get(i).nodeName.equals(target.get(1).get(0).queryName))
			{
				ev2 = i;
				evS2 = nodeData.get(i).P_data.get(0).pro_value.size();
			}
		}
		double[] result1 = new double[evS1];
		double[] result2 = new double[evS2];
			
		result1 = calculateP(allSample1, ev1, evS1);
		result2 = calculateP(allSample2, ev2, evS2);

		
		// output answer file
		PrintWriter out1 = new PrintWriter(answerFileName);
		out1.println(result1[0]+" "+result1[1]);
		out1.println(result2[0]+" "+result2[1]);
		out1.close();
		System.out.println("Calculation End");

	}
	
	public static double[] calculateP(Vector<double[]> samples, int evidence, int evS){
		double[] result = new double[evS];
		int sampleSize = samples.get(0).length;
		double totWeight = 0;
		double[] weights = new double[evS];
		for(int i = 0; i < evS ; i++){
			weights[i] = 0;
		}
		
		if(evidence == -1)
		{
			for(int i = 0; i < evS; i++){
				result[i] = -1;
			}
		}
		else{
			for(int i = 0; i < samples.size(); i++){
				for(int j = 0; j < evS; j++){
					if(samples.get(i)[evidence] == j)
					{
						weights[j] += samples.get(i)[sampleSize - 1];
					}
				}
				totWeight += samples.get(i)[sampleSize - 1];
			}
		}
		
		for(int i = 0; i < evS; i++){
			result[i] = weights[i]/totWeight;
		}
		return result;
	}
	
	public static Vector<int[]> setupState(Vector<Node> data, Vector<Integer> parent, int rowSize, int columnSize){
		Vector<int[]> resultState = new Vector<int[]>();
		int parentSize = parent.size();
		if(parentSize == 0){
			int[] tempRow = new int[columnSize];
			for(int i = 0; i < columnSize; i++){
				tempRow[i] = i;
			}
			resultState.add(tempRow);
		}
		else{
			for(int i = 0; i < rowSize; i++){
				int[] tempRow = new int[parentSize];
				for(int j = 0; j < parentSize; j++){
					tempRow[j] = 0;			
				}
				if(resultState.size() == 0){
					resultState.add(tempRow);
				}
				else{
					//copy value from previous
					for(int k = 0; k < parentSize; k++){
						tempRow[k] = resultState.get(resultState.size()-1)[k];
					}
					//update last column
					tempRow[parentSize-1]++;
					//update all values
					for(int k = parentSize - 1 ; k > 0; k--){
						if(tempRow[k] >= data.get(parent.get(k)).getDataRow().get(0).getProValue().size()){
							tempRow[k-1]++;
							tempRow[k] = 0;
						}
					}
					resultState.add(tempRow);
				}
			}
		}
		return resultState;
	}
	
	public static void printModel(){
		for(int i = 0; i < numOfNode; i ++){
			for(int j = 0; j < nodeData.get(i).getDataRow().size(); j++){
				for(int k = 0; k < nodeData.get(i).getDataRow().get(j).getStat().get(0).length;k++){
					if(k == nodeData.get(i).getDataRow().get(j).getStat().get(0).length - 1 ){
						System.out.print(nodeData.get(i).getDataRow().get(j).getStat().get(0)[k]+": ");
					}
					else{
						System.out.print(nodeData.get(i).getDataRow().get(j).getStat().get(0)[k]+" ");
					}		
				}
				for(int k = 0; k <nodeData.get(i).getDataRow().get(j).getProValue().size();k++){
					if(k == nodeData.get(i).getDataRow().get(j).getProValue().size() - 1){
						System.out.println(nodeData.get(i).getDataRow().get(j).getProValue().get(k));
					}
					else{
						System.out.print(nodeData.get(i).getDataRow().get(j).getProValue().get(k)+" ");
					}
				}
			}
			System.out.println();
		}
	}
}

class Node{
	public Vector<Integer> parentNode;
	public Vector<Integer> childNode;
	public Vector<Row> P_data;
	
	public String nodeName;
	
	public Node(){
		//Initialization
		parentNode = new Vector<Integer>();
		childNode = new Vector<Integer>();
		P_data = new Vector<Row>();
	}
	public void addParentNode(int num){
		//connect to the Parent Node
		this.parentNode.add(num);
	}
	public void addchildNode(int num){
		//connect to the child Node
		this.childNode.add(num);
	}
	public Vector<Integer> getParentNode(){
		//return parent node
		return this.parentNode;
	}
	public Vector<Integer> getChildNode(){
		//return child node
		return this.childNode;
	}
	public void addDataRow(Row row){
		//add new data row
		this.P_data.add(row);
	}
	public Vector<Row> getDataRow(){
		//return row data
		return this.P_data;
	}
} 

class Row{
	public Vector<int[]> stat;
	public Vector<Double> pro_value;
	
	public Row(){
		//Initialization
		stat = new Vector<int[]>();
		pro_value = new Vector<Double>();
	}
	public void addProValue(Double value){
		//add probability value
		this.pro_value.add(value);
	}
	public void addStat(int[] statValue){
		//add stat value
		this.stat.add(statValue);
	}
	public Vector<int[]> getStat(){
		//return state
		return this.stat;
	}
	public Vector<Double> getProValue(){
		//return probability
		return this.pro_value;
	}
}

class Query{
	public String queryName;
	public int queryState; // 0 for false, 1 for true, 2 for extra use
	
	public Query() {
		// TODO Auto-generated constructor stub
		queryName = "";
		queryState = -1;
	}
}


	
