//AI Assignment 2
//Author: Puzhi Yao
//Date: 19 April 2015

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;


public class creditrating {
	public static int depth;//number of branches
	public static Vector<int[]> QueueCount = new Vector<int[]>();//save the order of each node
	public static void main(String[] args) throws IOException{

		String method = "arg1";
		String method2 = "arg2";

		//read method
		if(args.length >2){
			method = args[1];//test file
			method2 = args[2];//minLeaf
		}
		else{
			System.out.println("Arguments Error");
			System.exit(0);
		}
		int minLeaf = Integer.valueOf(method2);
		
		//read train data from text file
		java.io.FileReader fr = new FileReader(args[0]);
		BufferedReader reader = new BufferedReader(fr);

		//create vector to store data
		String line1;
		Vector<String> Label = new Vector<String>();
		Vector<double []> Data = new Vector<double []>();

		//read first  line
		//store label into string vector		
		if((line1 = reader.readLine()) != null)
		{
			String[] part = line1.split("\\s");
			for(int i = 0; i < part.length ;i++){
				if(!part[i].equals("")){
					Label.add(part[i]);
				}	
			}
		}

		//read data value
		while((line1 = reader.readLine()) != null){
			//setup
			//WC_TA, RE_TA, EBIT_TA, MVE_BVTD, S_TA
			//Rating:AAA=1, AA=2, A=3, BBB=4, BB=5, B=6, C=7, unknown = 0;
			double [] temp = new double[6];
			String[] part2 = line1.split("\\s");
			int tempIndex = 0;
			for(int i = 0; i< part2.length ;i++)
			{
				if(!part2[i].equals("") && i < part2.length - 1){
					double floatReader = Double.valueOf(part2[i]);
					temp[tempIndex] = floatReader;
					tempIndex++;
				}
				else if(!part2[i].equals("") && i == part2.length - 1){
					if(part2[i].equals("AAA")){
						temp[tempIndex] = 1;
					}
					else if(part2[i].equals("AA")){
						temp[tempIndex] = 2;
					}
					else if(part2[i].equals("A")){
						temp[tempIndex] = 3;
					}
					else if(part2[i].equals("BBB")){
						temp[tempIndex] = 4;
					}
					else if(part2[i].equals("BB")){
						temp[tempIndex] = 5;
					}
					else if(part2[i].equals("B")){
						temp[tempIndex] = 6;
					}
					else if(part2[i].equals("CCC")){
						temp[tempIndex] = 7;
					}
					else {
						temp[tempIndex] = 0;
					}
				}
			}
			Data.add(temp);
		} 
		//read  train data end
		reader.close();
		fr.close();
		
		//read test data 
		java.io.FileReader fr2 = new FileReader(args[1]);
		BufferedReader reader2 = new BufferedReader(fr2);

		//create vector to store data
		String line2;
		Vector<String> Label2 = new Vector<String>();
		Vector<double []> testData = new Vector<double []>();

		//read first  line
		//store label into string vector		
		if((line2 = reader2.readLine()) != null)
		{
			String[] part = line2.split("\\s");
			for(int i = 0; i < part.length ;i++){
				if(!part[i].equals("")){
					Label2.add(part[i]);
				}	
			}
		}

		//read data value
		while((line2 = reader2.readLine()) != null){
			//setup
			//WC_TA, RE_TA, EBIT_TA, MVE_BVTD, S_TA
			double [] temp = new double[5];
			String[] part2 = line2.split("\\s");
			int tempIndex = 0;
			for(int i = 0; i< part2.length ;i++)
			{
				if(!part2[i].equals("") && i < part2.length){
					double floatReader = Double.valueOf(part2[i]);
					temp[tempIndex] = floatReader;
					tempIndex++;
				}
			}
			testData.add(temp);
		} 
		//read  test data end
		reader.close();
		fr.close();

		//DTL algorithm
		Node root = DTL(Data, minLeaf);
		
		//printResult(Data);
		//Vector<Node> queue = new Vector<Node>();
		//int[] first_count = new int[1];
		//first_count[0] = 1;
		//QueueCount.add(first_count);
		//queue.add(root);
		//depth = 1;
		//printDT(queue);
		
		
		//predict new data
		//System.out.println("size of test data: "+testData.size());
		for(int i = 0; i < testData.size(); i++){
			System.out.println(predict(root,testData.get(i)));
		}
		

	}
	
	public static void printResult(Vector<double[]> Data){
		for(int i = 0; i < Data.size(); i++){
			System.out.print(Data.get(i)[0]+" ");
			System.out.print(Data.get(i)[1]+" ");
			System.out.print(Data.get(i)[2]+" ");
			System.out.print(Data.get(i)[3]+" ");
			System.out.print(Data.get(i)[4]+" ");
			if(Data.get(i)[5] == 0){
				System.out.println("unknown");
			}
			else if(Data.get(i)[5] == 1){
				System.out.println("AAA");
			}
			else if(Data.get(i)[5] == 2){
				System.out.println("AA");
			}
			else if(Data.get(i)[5] == 3){
				System.out.println("A");
			}
			else if(Data.get(i)[5] == 4){
				System.out.println("BBB");
			}
			else if(Data.get(i)[5] == 5){
				System.out.println("BB");
			}
			else if(Data.get(i)[5] == 6){
				System.out.println("B");
			}
			else if(Data.get(i)[5] == 7){
				System.out.println("CCC");
			}
		}
	}
	
	public static Node DTL(Vector<double []> Data, int minleaf){
		int N = Data.size();	
		if(N <= minleaf || checkInput(Data) || checkOutput(Data)){
			Node leafNode = new Node();
			if(uniqueMode(Data) != 0){
				leafNode.setLabel(uniqueMode(Data));
			}
			else{
				leafNode.setLabel("unknown");
			}
			return leafNode;
		}
		//copy data
		Vector<double[]> copyQueue = new Vector<double[]>();
		for(int i = 0; i < Data.size(); i++){
			double[] temp = new double[6];
			temp[0] = Data.get(i)[0];
			temp[1] = Data.get(i)[1];
			temp[2] = Data.get(i)[2];
			temp[3] = Data.get(i)[3];
			temp[4] = Data.get(i)[4];
			temp[5] = Data.get(i)[5];
			copyQueue.add(temp);
		}
		
		//find best split attr and value
		double [] tempValue = new double[2];
		tempValue = choose_split(copyQueue);
		
		//crate new node and set split point
		Node leafNode = new Node();
		int tempInt = (int) tempValue[0];
		leafNode.setAttr(tempInt);
		leafNode.setSplitval(tempValue[1]);
		
		//split data and rating according to best split value
		Vector<double []> leftData = splitLeftData(Data, tempValue);
		Vector<double []> rightData = splitRightData(Data, tempValue);
		
		
		//set left node and right node
		leafNode.setLeftNode(DTL(leftData, minleaf));
		leafNode.setRightNode(DTL(rightData, minleaf));
		
		return leafNode;
	}
	
	public static double uniqueMode(Vector<double []> Data){
		//setup counter for unknown, AAA, AA, A, BBB, BB, B, CCC, 
		int [] count = new int[8];
		//initialize count
		for(int i = 0; i < count.length; i++){
			count[i] = 0;
		}
		//count the numbers of each label
		for(int i = 0; i < Data.size(); i++){
			if(Data.get(i)[5] == 1){
				count[1]++;
			}
			else if(Data.get(i)[5] == 2){
				count[2]++;
			}
			else if(Data.get(i)[5] == 3){
				count[3]++;
			}
			else if(Data.get(i)[5] == 4){
				count[4]++;
			}
			else if(Data.get(i)[5] == 5){
				count[5]++;
			}
			else if(Data.get(i)[5] == 6){
				count[6]++;
			}
			else if(Data.get(i)[5] == 7){
				count[7]++;
			}
			else{
				count[0]++;
			}
		}
		//return the most frequent label
		int tempMax = 0;
		boolean draw = false;
		for(int i = 0; i < count.length; i++){
			if(count[i] > count[tempMax]){
				tempMax = i;
				draw = false;
			}
			else if(count[i] == count[tempMax]){
				draw = true;
			}
		}
		
		//if there is no tie in the counter
		if(draw != true){
			return tempMax;
		}
		//if there is a tie
		else{
			return 0;
		}
	}
	
	public static double[] choose_split(Vector<double []> Data){
		//initialize
		//bestAttr & bestSplitVal
		double[] result = new double[2];
		double bestGain = 0;
		
		//find best attr and split value
		for(int i = 0; i < Data.get(0).length - 1; i++){
			//sort attr data
			Vector<double[]> sortedData = sort_Data(Data,i);
			for(int j = 0; j < sortedData.size()-1; j++){
				double temp = sortedData.get(j)[i]+sortedData.get(j+1)[i];
				double tempSplitVal = 0.5*temp;
				double tempGain = calGain(sortedData,i,tempSplitVal);
				if(tempGain > bestGain){
					bestGain = tempGain;
					result[0] = i;//bestAttr
					result[1] = tempSplitVal;//bestSplitVal
				}
			}
		}
		return result;
	}
	
	public static Vector<double[]> sort_Data(Vector<double []> Data, int index){
		
		//use shell sort to sort the data value
		int inner, outer;
		//find initial value of h
		int h = 1;
		while (h <= Data.size() / 3){
			h = h*3 + 1; 
		}

		while(h > 0){
			for(outer = h; outer < Data.size(); outer++ ){
				double[] temp = new double[6];
				temp[0] = Data.get(outer)[0];
				temp[1] = Data.get(outer)[1];
				temp[2] = Data.get(outer)[2];
				temp[3] = Data.get(outer)[3];
				temp[4] = Data.get(outer)[4];
				temp[5] = Data.get(outer)[5];
				inner = outer;
				while(inner  > h - 1 && Data.get(inner - h)[index] > temp[index]){
					Data.get(inner)[0] = Data.get(inner-h)[0];
					Data.get(inner)[1] = Data.get(inner-h)[1];
					Data.get(inner)[2] = Data.get(inner-h)[2];
					Data.get(inner)[3] = Data.get(inner-h)[3];
					Data.get(inner)[4] = Data.get(inner-h)[4];
					Data.get(inner)[5] = Data.get(inner-h)[5];
					inner -= h;
				}
				Data.get(inner)[0] = temp[0];
				Data.get(inner)[1] = temp[1];
				Data.get(inner)[2] = temp[2];
				Data.get(inner)[3] = temp[3];
				Data.get(inner)[4] = temp[4];
				Data.get(inner)[5] = temp[5];
			}
			h = (h - 1) / 3; // decrease h
		}
		return Data;
	}
	
	
	public static double calGain(Vector<double[]> sortedData, int attrNumber, double tempSplitVal){
		double infoCont = 0;
		//calculate entropy
		double[] Ncount = new double[8];//number of AAA,AA,A,BBB,BB,B,CCC,unknown
		//initialize
		for(int i = 0; i < Ncount.length; i++){
			Ncount[i] = 0;
		}
		//count number of each label appeared in data
		for(int i = 0; i < sortedData.size();i++){
			if(sortedData.get(i)[5] == 1){
				Ncount[1]++;
			}
			else if(sortedData.get(i)[5] == 2){
				Ncount[2]++;
			}
			else if(sortedData.get(i)[5] == 3){
				Ncount[3]++;
			}
			else if(sortedData.get(i)[5] == 4){
				Ncount[4]++;
			}
			else if(sortedData.get(i)[5] == 5){
				Ncount[5]++;
			}
			else if(sortedData.get(i)[5] == 6){
				Ncount[6]++;
			}
			else if(sortedData.get(i)[5] == 7){
				Ncount[7]++;
			}
			else {
				Ncount[0]++;
			}
		}
		double[] Pc = new double[8];
		double totSamples = sortedData.size();
		for(int i = 0; i < Pc.length; i++){
			Pc[i] = Ncount[i]/totSamples;
		}
		//calculate information content
		for(int i = 0; i < Pc.length; i++){
			double tempR;
			if(Pc[i] == 0){
				tempR = 0;
			}
			else{
				tempR = Math.log(Pc[i])/Math.log(2);
			}
			infoCont = infoCont - Pc[i]*tempR;
		}
		
		//calculate entropy left
		double[] NcountLeft = new double[8];
		double totLeftSamples = 0;
		for(int i = 0; i < NcountLeft.length; i++){
			NcountLeft[i] = 0;
		}
		for(int i = 0; i < sortedData.size(); i++){
			if(sortedData.get(i)[attrNumber] <= tempSplitVal){
				if(sortedData.get(i)[5] == 1){
					NcountLeft[1]++;
				}
				else if(sortedData.get(i)[5] == 2){
					NcountLeft[2]++;
				}
				else if(sortedData.get(i)[5] == 3){
					NcountLeft[3]++;
				}
				else if(sortedData.get(i)[5] == 4){
					NcountLeft[4]++;
				}
				else if(sortedData.get(i)[5] == 5){
					NcountLeft[5]++;
				}
				else if(sortedData.get(i)[5] == 6){
					NcountLeft[6]++;
				}
				else if(sortedData.get(i)[5] == 7){
					NcountLeft[7]++;
				}
				else {
					NcountLeft[0]++;
				}
				totLeftSamples++;
			}
		}
		double[] PcLeft = new double[8];
		for(int i = 0; i < PcLeft.length; i++){
			PcLeft[i] = NcountLeft[i]/totLeftSamples;
		}
		//calculate information content Left
		double infoContLeft = 0;
		for(int i = 0; i < PcLeft.length; i++){
			double tempRL;
			if(PcLeft[i] == 0){
				tempRL = 0;
			}
			else{
				tempRL = Math.log(PcLeft[i])/Math.log(2);
			}
			infoContLeft = infoContLeft - PcLeft[i]*tempRL;
		}
	
		//calculate entropy right
		double[] NcountRight = new double[8];
		double totRightSamples = 0;
		for(int i = 0; i < NcountRight.length; i++){
			NcountRight[i] = 0;
		}
		for(int i = 0; i < sortedData.size(); i++){
			if(sortedData.get(i)[attrNumber] > tempSplitVal){
				if(sortedData.get(i)[5] == 1){
					NcountRight[1]++;
				}
				else if(sortedData.get(i)[5] == 2){
					NcountRight[2]++;
				}
				else if(sortedData.get(i)[5] == 3){
					NcountRight[3]++;
				}
				else if(sortedData.get(i)[5] == 4){
					NcountRight[4]++;
				}
				else if(sortedData.get(i)[5] == 5){
					NcountRight[5]++;
				}
				else if(sortedData.get(i)[5] == 6){
					NcountRight[6]++;
				}
				else if(sortedData.get(i)[5] == 7){
					NcountRight[7]++;
				}
				else {
					NcountRight[0]++;
				}
				totRightSamples++;
			}
		}
		double[] PcRight = new double[8];
		for(int i = 0; i < PcRight.length; i++){
			PcRight[i] = NcountRight[i]/totRightSamples;
		}
		//calculate information content Right	
		double infoContRight = 0;
		for(int i = 0; i < PcRight.length; i++){
			double tempRR;
			if(PcRight[i] == 0){
				tempRR = 0;
			}
			else{
				tempRR = Math.log(PcRight[i])/Math.log(2);
			}
			infoContRight = infoContRight - PcRight[i]*tempRR;
		}
		
		//calculate information gain
		//information gain = Entropy_before - Entropy_after
		double EntropyAfter = (totLeftSamples/totSamples)*infoContLeft+(totRightSamples/totSamples)*infoContRight;
		
		double information_Gain = infoCont - EntropyAfter;
		
		return information_Gain;
	}
	
	public static Vector<double []> splitLeftData(Vector<double[]> Data, double[] tempValue){
		//tempValue:bestattr, bestsplitval
		Vector<double[]> LeftData = new Vector<double[]>();
		for(int i = 0; i < Data.size(); i++){
			double[] temp = new double[6];
			int tempIndex = (int) tempValue[0];
			if(Data.get(i)[tempIndex] <= tempValue[1]){
				temp[0] = Data.get(i)[0];
				temp[1] = Data.get(i)[1];
				temp[2] = Data.get(i)[2];
				temp[3] = Data.get(i)[3];
				temp[4] = Data.get(i)[4];
				temp[5] = Data.get(i)[5];
				LeftData.add(temp);
			}
		}
		return LeftData;
	}
	
	public static Vector<double []> splitRightData(Vector<double[]> Data, double[] tempValue){
		//tempValue:bestattr, bestsplitval
		Vector<double[]> RightData = new Vector<double[]>();
		for(int i = 0; i < Data.size(); i++){
			double[] temp = new double[6];
			if(Data.get(i)[(int) tempValue[0]] > tempValue[1]){
				temp[0] = Data.get(i)[0];
				temp[1] = Data.get(i)[1];
				temp[2] = Data.get(i)[2];
				temp[3] = Data.get(i)[3];
				temp[4] = Data.get(i)[4];
				temp[5] = Data.get(i)[5];
				RightData.add(temp);
			}
		}
		return RightData;
	}
	
	public static String predict(Node n, double[] Data){
		while(n.getLeftNode() != null && n.getRightNode() != null){
			if(Data[n.getAttr()] <= n.getSplitVal()){
				n = n.getLeftNode();
			}
			else{
				n = n.getRightNode();
			}
		}
		return n.getLabel();
	}
	
	public static void printDT(Vector<Node> queue){
		//print out decision tree
		while(queue.size() > 0){
			Node n = new Node();
			n = queue.get(0);
			if(n.getLeftNode() != null || n.getRightNode() != null){
				//add new node to backup queue
				//using the size of queue to locate branches
				int[] tempCount1 = new int[1];
				tempCount1[0] = 1;
				QueueCount.add(tempCount1);
				System.out.println(depth+". If "+NumToAttr(n.getAttr())+" <= "+n.getSplitVal()+", goto "+(QueueCount.size())+" ,else goto "+(QueueCount.size()+1));
				int[] tempCount2 = new int[1];
				tempCount2[0] = 1;
				QueueCount.add(tempCount2);
				//add new node to queue
				//remove old node
				Node nLeft = new Node();
				Node nRight = new Node();
				nLeft = n.getLeftNode();
				nRight = n.getRightNode();
				queue.add(nLeft);
				queue.add(nRight);
				queue.remove(0);
				depth ++;
			}
			else if(n.getLeftNode() == null && n.getRightNode() == null){
				//when approached leaf
				System.out.println(depth+". Return rating "+n.getLabel());
				queue.remove(0);
				depth ++;
			}
			printDT(queue);
		}
	}
	
	public static String NumToAttr(int i){
		String Result = "UNF";
		if(i == 0){
			Result = "WC TA";
		}
		else if(i == 1){
			Result = "RE_TA";
		}
		else if(i == 2){
			Result = "EBIT_TA";
		}
		else if(i == 3){
			Result = "MVE_BVTD";
		}
		else if(i == 4){
			Result = "S_TA";
		}
		return Result;
	}
	
	public static double round(double d, int decimalPlace) {
		//this function is going to round double decimal number to 4
		//decimal places
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	public static boolean checkInput(Vector<double[]> Data){
		for(int i = 0; i < Data.size(); i++){
			for(int j = i+1; j < Data.size(); j++){
				if(Data.get(i)[0] != Data.get(j)[0] || Data.get(i)[1] != Data.get(j)[1] || Data.get(i)[2] != Data.get(j)[2] || Data.get(i)[3] != Data.get(j)[3] ||Data.get(i)[4] != Data.get(j)[4]){
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkOutput(Vector<double[]> Data){
		for(int i = 0; i < Data.size(); i++){
			for(int j = i+1; j < Data.size(); j++){
				if(Data.get(i)[5] != Data.get(j)[5]){
					return false;
				}
			}
		}
		return true;
	}

}

class Node{
	//WC_TA, RE_TA, EBIT_TA, MVE_BVTD, S_TA
	private int attr;
	private double splitval;
	private String label;
	private Node left;
	private Node right;
	
	public Node(){
		//initialize
		/*
		this.label = "unknown";
		this.left = null;
		this.right = null;
		*/
	}
	
	public void setAttr(int attr){
		//setup attribute value
		this.attr = attr;
	}
	public void setSplitval(double splitval){
		//setup split value
		this.splitval = splitval;
	}
	public void setLabel(double label){
		//setup label
		if(label == 1){
			this.label = "AAA";
		}
		else if(label == 2){
			this.label = "AA";
		}
		else if(label == 3){
			this.label = "A";
		}
		else if(label == 4){
			this.label = "BBB";
		}
		else if(label == 5){
			this.label = "BB";
		}
		else if(label == 6){
			this.label = "B";
		}
		else if(label == 7){
			this.label = "CCC";
		}
		else{
			this.label = "unknown";
		}
	}
	public void setLabel(String label){
		this.label = label;
	}
	public void setLeftNode(Node left){
		//setup left node
		this.left = left;
	}
	public void setRightNode(Node right){
		//setup right node
		this.right = right;
	}
	public int getAttr(){
		//get attribute value
		return this.attr;
	}
	public double getSplitVal(){
		//get split value
		return this.splitval;
	}
	public String getLabel(){
		//get label
		return this.label;
	}
	public Node getLeftNode(){
		//get left node
		return this.left;
	}
	public Node getRightNode(){
		//get right node
		return this.right;
	}
}
