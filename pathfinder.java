
//Author: Puzhi Yao
//Student ID: 1205593
//Coding Date: 22 Mar 2015
//AI Assignment 1

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
enum Status { UNEXPLORED, EXPLORED };


public class pathfinder {
	public static void main(String[] args) throws IOException{
		
		//read map information from text file
		//java.io.FileReader fr = new FileReader("E:/JavaSet/JavaProject/AIAssignment1/src/map.txt");

		java.io.FileReader fr = new FileReader(args[0]);
		BufferedReader reader = new BufferedReader(fr);
		String method = "arg1";
		String method2 = "arg2";
		
		//read method
		if(args.length >2){
			method = args[1];
			method2 = args[2];
		}
		else if(args.length == 2){
			method = args[1];
		}
		else{
			System.out.println("No Arguments");
			System.exit(0);
		}
		
		//read first and second line
		//store the map size and start/end point
		String line1;
		int[] sizeOfMap = new int[2];
		int[] startPoint = new int[2];
		int[] endPoint = new int[2];
		
		//read rest data and store in matrix
		for(int i = 0; i < 3; i++)
		{
			if((line1 = reader.readLine()) != null)
			{
				for(int j = 0; j < 2; j++)
				{
					String[] part = line1.split("\\s");
					Integer intReader = Integer.valueOf(part[j]);
					if(i == 0)
					{
						sizeOfMap[j] = intReader;//store integers into list
					}
					else if(i == 1)
					{
						startPoint[j] = intReader;//store integers into list
					}
					else if(i == 2)
					{
						endPoint[j] = intReader;//store integers into list
					}
				}
			}
		}
		//print pre-setting
		//System.out.println(sizeOfMap[0]+" "+sizeOfMap[1]);
		//System.out.println(startPoint[0]+" "+startPoint[1]);
		//System.out.println(endPoint[0]+" "+endPoint[1]);

		
		//create a map of nodes
		mapNode[][] nodes = new mapNode[sizeOfMap[1]][sizeOfMap[0]];
		
		//read map information
		int x_index = 0;
		int y_index = 0;
		while((line1 = reader.readLine()) != null){
			//setup index
			String[] part2 = line1.split("\\s");
			for(int i = 0; i< sizeOfMap[1];i++)
			{
				mapNode tempNode = new mapNode();
				if(part2[i].equals("X"))
				{
					tempNode.setNoGoZone(true);
					tempNode.setElevation(999999999);//set elevation to infinite
				}
				else
				{
					Integer intReader = Integer.valueOf(part2[i]);//reader all integer
					tempNode.setRoad(true);
					tempNode.setElevation(intReader);
				}
				
				//store the position of each node 
				Vector<int[]> initialPath = new Vector<int[]>();
				int [] initialPosition = new int[2];
				initialPosition[0] = i;
				initialPosition[1] = y_index;
				initialPath.add(initialPosition);
				tempNode.setTotPath(initialPath);
				nodes[i][y_index] = tempNode;
			}
			y_index++;
		}
		reader.close();
		fr.close();
			
		// connect each node (Using Linked List to connect the
		// the top, bottom, left and right nodes of current node
		for (int i = 0; i < sizeOfMap[1]; i++) {
			for (int j = 0; j < sizeOfMap[0]; j++) {
				// connect the top left node
				if (i == 0 && j == 0) {
					nodes[i][j].topNode = null;// top node does not exist
					nodes[i][j].bottomNode = nodes[i][j + 1];// connect bottom
																// node to
																// current node
					nodes[i][j].leftNode = null;// left node does not exist
					nodes[i][j].rightNode = nodes[i + 1][j];// connect right
															// node to current
															// node
				}
				// connect the bottom left node
				else if (i == 0 && j == sizeOfMap[0] - 1) {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = null;
					nodes[i][j].leftNode = null;
					nodes[i][j].rightNode = nodes[i + 1][j];
				}
				// connect the top right node
				else if (i == sizeOfMap[1] - 1 && j == 0) {
					nodes[i][j].topNode = null;
					nodes[i][j].bottomNode = nodes[i][j + 1];
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = null;
				}
				// connect the bottom right node
				else if (i == sizeOfMap[1] - 1
						&& j == sizeOfMap[0] - 1) {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = null;
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = null;
				}
				// connect the left edge nodes
				else if (i == 0 && j != 0 && j != sizeOfMap[0] - 1) {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = nodes[i][j + 1];
					nodes[i][j].leftNode = null;
					nodes[i][j].rightNode = nodes[i + 1][j];
				}
				// connect the right edge nodes
				else if (i == sizeOfMap[1] - 1 && j != 0
						&& j != sizeOfMap[0] - 1) {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = nodes[i][j + 1];
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = null;
				}
				// connect the top edge nodes
				else if (i != 0 && i != sizeOfMap[1] - 1 && j == 0) {
					nodes[i][j].topNode = null;
					nodes[i][j].bottomNode = nodes[i][j + 1];
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = nodes[i + 1][j];
				}
				// connect the bottom edge nodes
				else if (i != 0 && i != sizeOfMap[1] - 1
						&& j == sizeOfMap[0] - 1) {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = null;
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = nodes[i + 1][j];
				}
				// connect all rest nodes
				else {
					nodes[i][j].topNode = nodes[i][j - 1];
					nodes[i][j].bottomNode = nodes[i][j + 1];
					nodes[i][j].leftNode = nodes[i - 1][j];
					nodes[i][j].rightNode = nodes[i + 1][j];
				}
				// update the weight on the edge between each node
				nodes[i][j].updateTopWeight();// update the top edge weight
				nodes[i][j].updateBottomWeight();// update the bottom edge weight										
				nodes[i][j].updateLeftWeight();// update the Left edge weight
				nodes[i][j].updateRightWeight();// update the Right edge weight
			}
		}
		
		//method
		Vector<int []> result;
		Vector<int []> result2;
		startPoint[0] = startPoint[0] - 1;
		startPoint[1] = startPoint[1] - 1;
		endPoint[0] = endPoint[0] - 1;
		endPoint[1] = endPoint[1] - 1;
		
		int temp;
		temp = startPoint[0];
		startPoint[0] = startPoint[1];
		startPoint[1] = temp;
		
		temp = endPoint[0];
		endPoint[0] = endPoint[1];
		endPoint[1] = temp;
		
		//test case
		//endPoint[0] = 1;
		//endPoint[1] = 1;
		
		//Main Method for searching the path
		//print original map
		//System.out.println("print out the map");
		//printMap(nodes);
		if(method.equals("bfs")){
			result2 = BFS(startPoint,endPoint, nodes);
			if(result2 == null)
			{
				System.out.println("null");
			}
			else{
			updateResultOnMap(result2,nodes);
			}
		}
		else if(method.equals("ucs")){
			//System.out.println("UCS Shortest Path: ");
			result = UCS(startPoint, endPoint, nodes);
			if(result == null)
			{
				System.out.println("null");
			}
			else{
			updateResultOnMap(result,nodes);
			}
		}
		else if(method.equals("astar")){
			//System.out.println("A* Path:");
			result = aStar(startPoint, endPoint, nodes, method2);
			if(result == null)
			{
				System.out.println("null");
			}
			else{
			updateResultOnMap(result,nodes);
			}
		}		
	}
	
	public static Vector<int []> aStar(int[] startNode,int[] endNode, mapNode[][] nodes, String method){
		//This function using A* manhattan or A* euclidean method to find shortest path
		//between two given node.
		
		//initial all nodes' status to UN-visited
				for(int i = 0; i < nodes.length; i++)
				{
					for(int j = 0; j < nodes[i].length;j++)
					{
						nodes[i][j].setTotCost(999999999);//set the initial distance to INF
						nodes[i][j].gridPosition[0] = i;//save current node x position
						nodes[i][j].gridPosition[1] = j;//save current node y position
						nodes[i][j].setNodeVistied(false);//set all nodes are not visited yet
					}
				}
		
		Vector<int []> shortestPath;
		Vector<mapNode> closeSet;//the set of nodes already evaluated
		closeSet = new Vector<mapNode>();
		Vector<mapNode> openSet;//the set of nodes to be evaluated
		openSet = new Vector<mapNode>();
		float bestCost;
		//Add the start node into open set
		mapNode start = new mapNode();
		nodes[startNode[0]][startNode[1]].setHeuristics(0);
		nodes[startNode[0]][startNode[1]].setTotCost(0);
		start = nodes[startNode[0]][startNode[1]];
		openSet.add(start);
		
		while(openSet.size() != 0)
		{
			//sort openSet from lowest cost to highest
			int j;
			for(int i = 1; i < openSet.size(); i++){
				j = i;
				while(j > 0 && openSet.get(j-1).getTotCost() > openSet.get(j).getTotCost()){
					Collections.swap(openSet, j, j-1);
					j = j-1;
				}
			}
			
			//the node in openset having the lowest cost
			mapNode tempNode = new mapNode();
			tempNode = openSet.get(0);
			
			//End case if the condition have approached
			if(tempNode.gridPosition[0] == endNode[0] && tempNode.gridPosition[1] == endNode[1]){
				shortestPath = nodes[endNode[0]][endNode[1]].getTotPath();
				if(shortestPath.size() == 1)
				{
					return null;
				}
				shortestPath.add(endNode);
				// No result was found -- only the end node
				return shortestPath;
			}
			
			mapNode tempTopNode = new mapNode();
			mapNode tempBottomNode = new mapNode();
			mapNode tempLeftNode = new mapNode();
			mapNode tempRightNode = new mapNode();
			
			//remove current from open set
			openSet.remove(0);
			//add current to close set
			closeSet.add(tempNode);
			
			//update Top node information from original nodes matrix
			if(tempNode.topNode != null)
			{
				tempTopNode = nodes[tempNode.topNode.gridPosition[0]][tempNode.topNode.gridPosition[1]];
			}
			else
			{
				tempTopNode = null;
			}
			
			//update Bottom node information from original nodes matrix
			if(tempNode.bottomNode != null)
			{
				tempBottomNode = nodes[tempNode.bottomNode.gridPosition[0]][tempNode.bottomNode.gridPosition[1]];
			}
			else
			{
				tempBottomNode = null;
			}
			
			//update Left node information from original nodes matrix
			if(tempNode.leftNode != null)
			{
				tempLeftNode = nodes[tempNode.leftNode.gridPosition[0]][tempNode.leftNode.gridPosition[1]];
			}
			else
			{
				tempLeftNode = null;
			}
			
			//update Right node information from original nodes matrix
			if(tempNode.rightNode != null)
			{
				tempRightNode = nodes[tempNode.rightNode.gridPosition[0]][tempNode.rightNode.gridPosition[1]];
			}
			else
			{
				tempRightNode = null;
			}
			
			//Manhattan, Euclidean method
			if(method.equals("manhattan")){
				//update neighbor nodes
				//update top Node
				if(tempTopNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+manhattan(tempTopNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempTopNode.getOwnElevation()){
						if(tempTopNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempTopNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+manhattan(tempTopNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempTopNode) == false && tempTopNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempTopNode.setHeuristics(hValue);
						tempTopNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempTopNode.setTotPath(tempPath);
						tempTopNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.topNode.gridPosition[0]][tempNode.topNode.gridPosition[1]] = tempTopNode;
					}
					if(checkCloseSet(closeSet,tempTopNode) == false && checkOpenSet(openSet, tempTopNode) == false){
						openSet.add(tempTopNode);
					}
				}
				
				//update bottom node
				if(tempBottomNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+manhattan(tempBottomNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempBottomNode.getOwnElevation()){
						if(tempBottomNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempBottomNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+manhattan(tempBottomNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempBottomNode) == false && tempBottomNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempBottomNode.setHeuristics(hValue);
						tempBottomNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempBottomNode.setTotPath(tempPath);
						tempBottomNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.bottomNode.gridPosition[0]][tempNode.bottomNode.gridPosition[1]] = tempBottomNode;
					}
					if(checkCloseSet(closeSet,tempBottomNode) == false && checkOpenSet(openSet, tempBottomNode) == false){
						openSet.add(tempBottomNode);
					}
				}
				
				//update Left node
				if(tempLeftNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost =hValue+manhattan(tempLeftNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempLeftNode.getOwnElevation()){
						if(tempLeftNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempLeftNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+manhattan(tempLeftNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempLeftNode) == false && tempLeftNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempLeftNode.setHeuristics(hValue);
						tempLeftNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempLeftNode.setTotPath(tempPath);
						tempLeftNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.leftNode.gridPosition[0]][tempNode.leftNode.gridPosition[1]] = tempLeftNode;
					}
					if(checkCloseSet(closeSet,tempLeftNode) == false && checkOpenSet(openSet, tempLeftNode) == false){
						openSet.add(tempLeftNode);
					}
				}
				
				//update Right node
				if(tempRightNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+manhattan(tempRightNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempRightNode.getOwnElevation()){
						if(tempRightNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempRightNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+manhattan(tempRightNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempRightNode) == false && tempRightNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempRightNode.setHeuristics(hValue);
						tempRightNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempRightNode.setTotPath(tempPath);
						tempRightNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.rightNode.gridPosition[0]][tempNode.rightNode.gridPosition[1]] = tempRightNode;
					}
					if(checkCloseSet(closeSet,tempRightNode) == false && checkOpenSet(openSet, tempRightNode) == false){
						openSet.add(tempRightNode);
					}
				}
			}
			
			//Manhattan, Euclidean method
			else if(method.equals("euclidean")){
				//update neighbor nodes
				//update top Node
				if(tempTopNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+Euclidean(tempTopNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempTopNode.getOwnElevation()){
						if(tempTopNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempTopNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+Euclidean(tempTopNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempTopNode) == false && tempTopNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempTopNode.setHeuristics(hValue);
						tempTopNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempTopNode.setTotPath(tempPath);
						tempTopNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.topNode.gridPosition[0]][tempNode.topNode.gridPosition[1]] = tempTopNode;
					}
					if(checkCloseSet(closeSet,tempTopNode) == false && checkOpenSet(openSet, tempTopNode) == false){
						openSet.add(tempTopNode);
					}
				}
				
				//update bottom node
				if(tempBottomNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+Euclidean(tempBottomNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempBottomNode.getOwnElevation()){
						if(tempBottomNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempBottomNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+Euclidean(tempBottomNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempBottomNode) == false && tempBottomNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempBottomNode.setHeuristics(hValue);
						tempBottomNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempBottomNode.setTotPath(tempPath);
						tempBottomNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.bottomNode.gridPosition[0]][tempNode.bottomNode.gridPosition[1]] = tempBottomNode;
					}
					if(checkCloseSet(closeSet,tempBottomNode) == false && checkOpenSet(openSet, tempBottomNode) == false){
						openSet.add(tempBottomNode);
					}
				}
				
				//update Left node
				if(tempLeftNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost =hValue+Euclidean(tempLeftNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempLeftNode.getOwnElevation()){
						if(tempLeftNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempLeftNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+Euclidean(tempLeftNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempLeftNode) == false && tempLeftNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempLeftNode.setHeuristics(hValue);
						tempLeftNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempLeftNode.setTotPath(tempPath);
						tempLeftNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.leftNode.gridPosition[0]][tempNode.leftNode.gridPosition[1]] = tempLeftNode;
					}
					if(checkCloseSet(closeSet,tempLeftNode) == false && checkOpenSet(openSet, tempLeftNode) == false){
						openSet.add(tempLeftNode);
					}
				}
				
				//update Right node
				if(tempRightNode != null){
					float hValue = tempNode.getHeuristics()+1;
					float nextCost = hValue+Euclidean(tempRightNode.gridPosition, endNode, nodes);
					if(tempNode.getOwnElevation() < tempRightNode.getOwnElevation()){
						if(tempRightNode.getOwnElevation() == 999999999){
							nextCost = 999999999;
						}
						else{
							hValue = tempNode.getHeuristics()+1+(tempRightNode.getOwnElevation()-tempNode.getOwnElevation());
							nextCost = hValue+Euclidean(tempRightNode.gridPosition, endNode, nodes);
						}
					}
					if(checkCloseSet(closeSet,tempRightNode) == false && tempRightNode.getTotCost() > nextCost){
						//This node is not in closeSet or openSet
						//it is the first time that program has arrived this node
						//update cost
						tempRightNode.setHeuristics(hValue);
						tempRightNode.setTotCost(nextCost);
						Vector<int[]> tempPath = new Vector<int[]>();
						//record the path
						for(int m = 0; m < tempNode.getTotPath().size(); m++)
						{
							int[] tempXY = new int[2];
							tempXY = tempNode.getTotPath().get(m);
							tempPath.add(tempXY);
						}
						//check new node to see if it exists in path
						if(checkPositionInPath(tempPath, tempNode.gridPosition[0], tempNode.gridPosition[1]) == false)
						{
							int[] tempXY2 = new int[2];
							tempXY2 = tempNode.gridPosition;
							tempPath.add(tempXY2);
						}				
						//assign new path to the node
						tempRightNode.setTotPath(tempPath);
						tempRightNode.setStatus(Status.EXPLORED);
						//update data back to original matrix
						nodes[tempNode.rightNode.gridPosition[0]][tempNode.rightNode.gridPosition[1]] = tempRightNode;
					}
					if(checkCloseSet(closeSet,tempRightNode) == false && checkOpenSet(openSet, tempRightNode) == false){
						openSet.add(tempRightNode);
					}
				}
			}
	
		}
		shortestPath = nodes[endNode[0]][endNode[1]].getTotPath();
		if(shortestPath.size() == 1)
		{
			return null;
		}
		shortestPath.add(endNode);
		// No result was found -- only the end node
		return shortestPath;	
	}
	
	public static boolean checkOpenSet(Vector<mapNode> openSet, mapNode tempNode){
		//This function will check given node to see if it is in the openSet
		boolean check = false;
		for(int i = 0; i < openSet.size(); i++){
			if(openSet.get(i).gridPosition[0] == tempNode.gridPosition[0] && openSet.get(i).gridPosition[1] == tempNode.gridPosition[1]){
				check = true;
			}
		}
		return check;
	}
	
	public static boolean checkCloseSet(Vector<mapNode> closeSet, mapNode tempNode){
		//This function will check given node to see if it is in the closeSet
		boolean check = false;
		for(int i = 0; i < closeSet.size(); i++){
			if(closeSet.get(i).gridPosition[0] == tempNode.gridPosition[0] && closeSet.get(i).gridPosition[1] == tempNode.gridPosition[1]){
				check = true;
			}
		}
		return check;
	}
	
	
	public static float manhattan(int [] start, int [] end, mapNode[][] nodes){
		//A* manhattan distance
		float distance = 0;
		distance = (float)(Math.abs(start[0]-end[0])+Math.abs(start[1]-end[1]));
		return distance;
	}
	
	public static float Euclidean(int [] start, int [] end, mapNode[][] nodes){
		//A* Euclidean distance
		float distance = 0.0f;
		float a = (start[0]-end[0])*(start[0]-end[0]);
		float b = (start[1]-end[1])*(start[1]-end[1]);
		distance = (float) Math.sqrt(a+b);	
		return distance;
	}
	
	
	public static boolean checkResult(Vector<int []> result, int x, int y){
		//This function will check the position of node to see
		//if it is in the result vector
		boolean check = false;
		for(int i = 0; i < result.size(); i++){
			if(result.get(i)[0] == x && result.get(i)[1] == y)
			{
				check = true;
			}
		}
		return check;
	}
	
	
	public static void updateResultOnMap(Vector<int []> result, mapNode[][] nodes){
		//This function will update result node on map and print new map
		// print out the map
		int sizeOfMap1 = nodes.length;
		int sizeOfMap2 = nodes[0].length;
		for (int i = 0; i < sizeOfMap2; i++) {
			for (int j = 0; j < sizeOfMap1; j++) {
				if (j == sizeOfMap1-1) {
					if (nodes[j][i].getOwnElevation() == 999999999) {
						System.out.println("X");
					} else if (checkResult(result, j, i)) {
						System.out.println("*");
					} else {
						System.out.println(nodes[j][i].getOwnElevation());
					}
				} else {
					if (nodes[j][i].getOwnElevation() == 999999999) {
						System.out.print("X ");
					} else if (checkResult(result, j, i)) {
						System.out.print("* ");
					} else {
						System.out.print(nodes[j][i].getOwnElevation() + " ");
					}
				}
			}
		}
	}
	
	
	public static void printMap(mapNode[][] nodes){
		//This function will print out the map
		int sizeOfMap = nodes.length;
		for (int i = 0; i < sizeOfMap; i++) {
			for (int j = 0; j < sizeOfMap; j++) {
				if (j == 9) {
					if(nodes[j][i].getOwnElevation() == 999999999){
						System.out.println("X");
					}
					else if(nodes[j][i].getOwnElevation() == -1){
						System.out.println("*");
					}
					else{
						System.out.println(nodes[j][i].getOwnElevation());
					}
				} else {
					if(nodes[j][i].getOwnElevation() == 999999999){
						System.out.print("X ");
					}
					else if(nodes[j][i].getOwnElevation() == -1){
						System.out.print("* ");
					}
					else{
						System.out.print(nodes[j][i].getOwnElevation()+" ");
					}
				}
			}
		}
	}
	
	public static Vector<int []> BFS(int[] startPosition,int[] unexploreNode, mapNode[][] nodes){
		//This function is using BFS method to obtain the path 
		//between any two given nodes.
		
		Vector<int []> shortestPath;
		
		//initial all nodes' status to UN-visited
		for(int i = 0; i < nodes.length; i++)
		{
			for(int j = 0; j < nodes[i].length;j++)
			{
				nodes[i][j].setTotDistance(999999999);//set the initial distance to INF
				nodes[i][j].gridPosition[0] = i;//save current node x position
				nodes[i][j].gridPosition[1] = j;//save current node y position
				nodes[i][j].setNodeVistied(false);//set all nodes are not visited yet
			}
		}
		
		//create start node and add it into the execution queue
		mapNode tempNode = new mapNode();
		tempNode = nodes[startPosition[0]][startPosition[1]];
		Vector<mapNode> tempQueue = new Vector<mapNode>();
		tempQueue.add(tempNode);
		//main loop: check all nodes on the map
		while(tempQueue.size() != 0)
		{
			//create four nearby nodes
			mapNode tempTopNode = new mapNode();
			mapNode tempBottomNode = new mapNode();
			mapNode tempLeftNode = new mapNode();
			mapNode tempRightNode = new mapNode();
				
			//update Top node information from original nodes matrix
			if(tempQueue.get(0).topNode != null)
			{
				tempTopNode = nodes[tempQueue.get(0).topNode.gridPosition[0]][tempQueue.get(0).topNode.gridPosition[1]];
			}
			else
			{
				tempTopNode = null;
			}
			
			//update Bottom node information from original nodes matrix
			if(tempQueue.get(0).bottomNode != null)
			{
				tempBottomNode = nodes[tempQueue.get(0).bottomNode.gridPosition[0]][tempQueue.get(0).bottomNode.gridPosition[1]];
			}
			else
			{
				tempBottomNode = null;
			}
			
			//update Left node information from original nodes matrix
			if(tempQueue.get(0).leftNode != null)
			{
				tempLeftNode = nodes[tempQueue.get(0).leftNode.gridPosition[0]][tempQueue.get(0).leftNode.gridPosition[1]];
			}
			else
			{
				tempLeftNode = null;
			}
			
			//update Right node information from original nodes matrix
			if(tempQueue.get(0).rightNode != null)
			{
				tempRightNode = nodes[tempQueue.get(0).rightNode.gridPosition[0]][tempQueue.get(0).rightNode.gridPosition[1]];
			}
			else
			{
				tempRightNode = null;
			}
				
			//start re-calculate distance of each node
			//check the top node and update new distance
			if(tempTopNode != null)
			{
				if(tempTopNode.getNodeVisited() == false && tempTopNode.getOwnElevation() != 999999999 && tempTopNode.getStatus() != Status.EXPLORED )
				{
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}
					//assign new path to the node
					tempTopNode.setTotPath(tempPath);
					//update data back to original matrix
					tempTopNode.setStatus(Status.EXPLORED);
					nodes[tempTopNode.gridPosition[0]][tempTopNode.gridPosition[1]] = tempTopNode;
				}
				
				if(tempTopNode.gridPosition[0] == unexploreNode[0] && tempTopNode.gridPosition[1] == unexploreNode[1]){
					//print out the end node
					//print out the total distance between two points
					//print out the total number node of path
					//System.out.println("End Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
					//System.out.println("Total Distance: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotDistance());
					//System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
					shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
					shortestPath.add(unexploreNode);
					return shortestPath;	
				}
				
				if(nodes[tempTopNode.gridPosition[0]][tempTopNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempTopNode.gridPosition[0], tempTopNode.gridPosition[1]) == false && tempTopNode.getOwnElevation() != 999999999)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempTopNode);
				}
			}
			
			//check the bottom node and update new distance
			if(tempBottomNode != null)
			{
				if(tempBottomNode.getNodeVisited() == false && tempBottomNode.getOwnElevation() != 999999999 && tempBottomNode.getStatus() != Status.EXPLORED)
				{
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if it exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}				
					//assign new path to the node
					tempBottomNode.setTotPath(tempPath);
					tempBottomNode.setStatus(Status.EXPLORED);
					//update data back to original matrix
					nodes[tempQueue.get(0).bottomNode.gridPosition[0]][tempQueue.get(0).bottomNode.gridPosition[1]] = tempBottomNode;
				}
				if(tempBottomNode.gridPosition[0] == unexploreNode[0] && tempBottomNode.gridPosition[1] == unexploreNode[1]){
					//System.out.println("End Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
					//System.out.println("Total Distance: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotDistance());
					//System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
					shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
					shortestPath.add(unexploreNode);
					return shortestPath;
				}
				
				if(nodes[tempBottomNode.gridPosition[0]][tempBottomNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempBottomNode.gridPosition[0], tempBottomNode.gridPosition[1]) == false && tempBottomNode.getOwnElevation() != 999999999)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempBottomNode);
				}
			}
			
			//check the left node and update new distance
			if(tempLeftNode != null)
			{
				if(tempLeftNode.getNodeVisited() == false && tempLeftNode.getOwnElevation() != 999999999 && tempLeftNode.getStatus() != Status.EXPLORED)
				{
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if it exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}				
					//assign new path to the node
					tempLeftNode.setTotPath(tempPath);
					tempLeftNode.setStatus(Status.EXPLORED);
					//update data back to original matrix
					nodes[tempQueue.get(0).leftNode.gridPosition[0]][tempQueue.get(0).leftNode.gridPosition[1]] = tempLeftNode;
				}
				if(tempLeftNode.gridPosition[0] == unexploreNode[0] && tempLeftNode.gridPosition[1] == unexploreNode[1]){
					//System.out.println("End Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
					//System.out.println("Total Distance: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotDistance());
					//System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
					shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
					shortestPath.add(unexploreNode);
					return shortestPath;	
				}
				
				if(nodes[tempLeftNode.gridPosition[0]][tempLeftNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempLeftNode.gridPosition[0], tempLeftNode.gridPosition[1]) == false && tempLeftNode.getOwnElevation() != 999999999)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempLeftNode);
				}
			}
			
			//check the right node and update new distance
			if(tempRightNode != null)
			{
				if(tempRightNode.getNodeVisited() == false && tempRightNode.getOwnElevation() != 999999999 && tempRightNode.getStatus() != Status.EXPLORED)
				{
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check to see if new node existed in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}
					//assign new path to the node
					tempRightNode.setTotPath(tempPath);
					tempRightNode.setStatus(Status.EXPLORED);
					//update data back to original matrix
					nodes[tempQueue.get(0).rightNode.gridPosition[0]][tempQueue.get(0).rightNode.gridPosition[1]] = tempRightNode;
				}
				if(tempRightNode.gridPosition[0] == unexploreNode[0] && tempRightNode.gridPosition[1] == unexploreNode[1]){
					//System.out.println("End Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
					//System.out.println("Total Distance: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotDistance());
					//System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
					shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
					shortestPath.add(unexploreNode);
					return shortestPath;
				}
				
				if(nodes[tempRightNode.gridPosition[0]][tempRightNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempRightNode.gridPosition[0], tempRightNode.gridPosition[1]) == false && tempRightNode.getOwnElevation() != 999999999)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempRightNode);
				}
			}
			//set current node to visited node and
			//remove current node from execution queue
			tempQueue.get(0).setNodeVistied(true);
			nodes[tempQueue.get(0).gridPosition[0]][tempQueue.get(0).gridPosition[1]].setNodeVistied(true);
			tempQueue.remove(0);	
		}
		//print out the end node
		//print out the total distance between two points
		//print out the total number node of path
		System.out.println("End 5 Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
		System.out.println("Total Distance: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotDistance());
		System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
		shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
		shortestPath.add(unexploreNode);
		return shortestPath;		
	}
	
	
	
	public static Vector<int []> UCS(int[] startPosition,int[] unexploreNode, mapNode[][] nodes){
		//This function is using UCS method to update the distance
		//and weight of each node and edge to obtain the shortest path 
		//between any two given nodes.
		
		Vector<int []> shortestPath;
		
		//initial all nodes' distance to INF
		for(int i = 0; i < nodes.length; i++)
		{
			for(int j = 0; j < nodes[i].length;j++)
			{
				nodes[i][j].setTotDistance(999999999);//set the initial distance to INF
				nodes[i][j].gridPosition[0] = i;//save current node x position
				nodes[i][j].gridPosition[1] = j;//save current node y position
				nodes[i][j].setNodeVistied(false);//set all nodes are not visited yet
			}
		}
		
		//set the start point total distance to 0
		nodes[startPosition[0]][startPosition[1]].setTotDistance(0);
		//create start node and add it into the execution queue
		mapNode tempNode = new mapNode();
		tempNode = nodes[startPosition[0]][startPosition[1]];
		Vector<mapNode> tempQueue = new Vector<mapNode>();
		tempQueue.add(tempNode);
		//main loop: check all nodes on the map
		while(tempQueue.size() != 0)
		{
			//create four nearby nodes
			mapNode tempTopNode = new mapNode();
			mapNode tempBottomNode = new mapNode();
			mapNode tempLeftNode = new mapNode();
			mapNode tempRightNode = new mapNode();
				
			//update Top node information from original nodes matrix
			if(tempQueue.get(0).topNode != null)
			{
				tempTopNode = nodes[tempQueue.get(0).topNode.gridPosition[0]][tempQueue.get(0).topNode.gridPosition[1]];
			}
			else
			{
				tempTopNode = null;
			}
			
			//update Bottom node information from original nodes matrix
			if(tempQueue.get(0).bottomNode != null)
			{
				tempBottomNode = nodes[tempQueue.get(0).bottomNode.gridPosition[0]][tempQueue.get(0).bottomNode.gridPosition[1]];
			}
			else
			{
				tempBottomNode = null;
			}
			
			//update Left node information from original nodes matrix
			if(tempQueue.get(0).leftNode != null)
			{
				tempLeftNode = nodes[tempQueue.get(0).leftNode.gridPosition[0]][tempQueue.get(0).leftNode.gridPosition[1]];
			}
			else
			{
				tempLeftNode = null;
			}
			
			//update Right node information from original nodes matrix
			if(tempQueue.get(0).rightNode != null)
			{
				tempRightNode = nodes[tempQueue.get(0).rightNode.gridPosition[0]][tempQueue.get(0).rightNode.gridPosition[1]];
			}
			else
			{
				tempRightNode = null;
			}
				
			//start re-calculate distance of each node
			//check the top node and update new distance
			if(tempTopNode != null)
			{
				if(tempTopNode.getTotDistance()>tempQueue.get(0).getTotDistance()+tempQueue.get(0).getTopWeight())
				{
					//update new distance to the top node 
					tempTopNode.setTotDistance(tempQueue.get(0).getTotDistance()+tempQueue.get(0).getTopWeight());	
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}
					//assign new path to the node
					tempTopNode.setTotPath(tempPath);
					//update data back to original matrix
					nodes[tempTopNode.gridPosition[0]][tempTopNode.gridPosition[1]] = tempTopNode;
				}
				
				if(nodes[tempTopNode.gridPosition[0]][tempTopNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempTopNode.gridPosition[0], tempTopNode.gridPosition[1]) == false)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempTopNode);
				}
			}
			//check the bottom node and update new distance
			if(tempBottomNode != null)
			{
				if(tempBottomNode.getTotDistance()>tempQueue.get(0).getTotDistance()+tempQueue.get(0).getBottomWeight())
				{
					tempBottomNode.setTotDistance(tempQueue.get(0).getTotDistance()+tempQueue.get(0).getBottomWeight());
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if it exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}				
					//assign new path to the node
					tempBottomNode.setTotPath(tempPath);
					//update data back to original matrix
					nodes[tempQueue.get(0).bottomNode.gridPosition[0]][tempQueue.get(0).bottomNode.gridPosition[1]] = tempBottomNode;
				}
				if(nodes[tempBottomNode.gridPosition[0]][tempBottomNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempBottomNode.gridPosition[0], tempBottomNode.gridPosition[1]) == false)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempBottomNode);
				}
			}
			//check the left node and update new distance
			if(tempLeftNode != null)
			{
				if(tempLeftNode.getTotDistance()>tempQueue.get(0).getTotDistance()+tempQueue.get(0).getLeftWeight())
				{
					tempLeftNode.setTotDistance(tempQueue.get(0).getTotDistance()+tempQueue.get(0).getLeftWeight());
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check new node to see if it exists in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}				
					//assign new path to the node
					tempLeftNode.setTotPath(tempPath);
					//update data back to original matrix
					nodes[tempQueue.get(0).leftNode.gridPosition[0]][tempQueue.get(0).leftNode.gridPosition[1]] = tempLeftNode;
				}
				if(nodes[tempLeftNode.gridPosition[0]][tempLeftNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempLeftNode.gridPosition[0], tempLeftNode.gridPosition[1]) == false)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempLeftNode);
				}
			}
			//check the right node and update new distance
			if(tempRightNode != null)
			{
				if(tempRightNode.getTotDistance()>tempQueue.get(0).getTotDistance()+tempQueue.get(0).getRightWeight())
				{
					tempRightNode.setTotDistance(tempQueue.get(0).getTotDistance()+tempQueue.get(0).getRightWeight());
					Vector<int[]> tempPath = new Vector<int[]>();
					//record the path
					for(int m = 0; m < tempQueue.get(0).getTotPath().size(); m++)
					{
						int[] tempXY = new int[2];
						tempXY = tempQueue.get(0).getTotPath().get(m);
						tempPath.add(tempXY);
					}
					//check to see if new node existed in path
					if(checkPositionInPath(tempPath, tempQueue.get(0).gridPosition[0], tempQueue.get(0).gridPosition[1]) == false)
					{
						int[] tempXY2 = new int[2];
						tempXY2 = tempQueue.get(0).gridPosition;
						tempPath.add(tempXY2);
					}
					//assign new path to the node
					tempRightNode.setTotPath(tempPath);
					//update data back to original matrix
					nodes[tempQueue.get(0).rightNode.gridPosition[0]][tempQueue.get(0).rightNode.gridPosition[1]] = tempRightNode;
				}
				if(nodes[tempRightNode.gridPosition[0]][tempRightNode.gridPosition[1]].getNodeVisited()== false && checkNodeInQueue(tempQueue, tempRightNode.gridPosition[0], tempRightNode.gridPosition[1]) == false)
				{
					//check un-visited nodes and add new node into execution queue
					tempQueue.add(tempRightNode);
				}
			}
			//set current node to visited node and
			//remove current node from execution queue
			tempQueue.get(0).setNodeVistied(true);
			nodes[tempQueue.get(0).gridPosition[0]][tempQueue.get(0).gridPosition[1]].setNodeVistied(true);
			tempQueue.remove(0);	
		}
		//print out the end node
		//print out the total distance between two points
		//print out the total number node of path
		//End point not Found
		//System.out.println("End Point: "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[0]+1)+" "+(nodes[unexploreNode[0]][unexploreNode[1]].gridPosition[1]+1));
		//System.out.println("Total number of nodes: "+nodes[unexploreNode[0]][unexploreNode[1]].getTotPath().size());
		shortestPath = nodes[unexploreNode[0]][unexploreNode[1]].getTotPath();
		shortestPath.add(unexploreNode);
		return shortestPath;		
	}
	
	
	public static boolean checkPositionInPath(Vector<int[]> path, int x, int y){
		//This function is used to check that
		//the give node if it exist in path
		boolean positionChecker = false;
		for(int i = 0; i < path.size(); i++)
		{
			if(path.get(i)[0] == x && path.get(i)[1] == y)
			{
				positionChecker = true;
			}
		}
		return positionChecker;
	}
	
	public static boolean checkNodeInQueue(Vector<mapNode> tempQueue, int x, int y){
		//This function is used to check that
		//the give node if it exist in execution queue
		boolean nodeChecker = false;
		for(int i = 0; i < tempQueue.size(); i++)
		{
			if(tempQueue.get(i).gridPosition[0] == x && tempQueue.get(i).gridPosition[1] == y)
			{
				nodeChecker = true;
			}
		}
		return nodeChecker;
	}
	
}
	
class mapNode{
		private Status status;//explored or unexplored
	    private Boolean noGoZone;
	    private Boolean road;
	    private int ownElevation;
	    private int topWeight;//the elevation weight of current node to other nodes
	    private int bottomWeight;
	    private int leftWeight;
	    private int rightWeight;
	    private int totDistance;//the total distance between start point to this point
	    private boolean nodeVisited = false;//visited node property
	    private Vector<int[]> path;//store the shortest path from start point to this point
	    private int weightOfRoad = 1;
	    private int weightOfMapEdge = 9999999;//wall or not node there
	    private int weightOfNoGoZone = 9999999;//Obstacle
	    private float f_cost;//total cost
	    private float heuristics;//h value
	    
	    
	    //store the position of this point
	    public int[] gridPosition = new int[2];
	    
	    //four nodes connect to top, bottom, left and right nearby nodes
	    public mapNode topNode;
	    public mapNode bottomNode;
	    public mapNode leftNode;
	    public mapNode rightNode;
	    
	    public mapNode() {
	    	//node initialization
	        this.status = status.UNEXPLORED;
	        this.noGoZone = false;
	        this.road = false;
	        this.f_cost = 9999999;
	    }
	    //read the no go zone status
	    public Boolean isNoGoZone() {
	        return this.noGoZone;
	    }
	    //set the no go zone status
	    public void setNoGoZone(Boolean bool) {
	        this.noGoZone = bool;
	    }
	    //read the road status
	    public Boolean isRoad() {
	    	return this.road;
	    }
	    //set the road status
	    public void setRoad(Boolean bool) {
	    	this.road = bool;
	    }
	    //read the node status
	    public Status getStatus() {
	        return status;
	    }
	    //set the node status
	    public void setStatus(Status status) {
	        this.status = status;
	    }
	    //update the top weight of current node
	    public void updateTopWeight(){
	    	if(this.topNode != null)
	    	{
	    		if(this.topNode.isNoGoZone() == true)
	        	{
	    			//set edge weight to 9999999 if the next node is NO go zone or
	    			//boundary node
	        		this.topWeight = weightOfNoGoZone;
	        	}
	        	else if(this.topNode.road == true && this.topNode.isNoGoZone() == false)
	        	{
	        		//set edge weight to 1 if the next node is a road node
	        		this.topWeight = this.topNode.getOwnElevation();
	        	}
	    	}
	    	else
	    	{
	    		//set edge weight to INF if the next node is NULL
	    		this.topWeight = weightOfMapEdge;
	    	}
	    }
	    //update the bottom weight of current node
	    public void updateBottomWeight(){
	    	if(this.bottomNode != null)
	    	{
	    		if( this.bottomNode.isNoGoZone() == true)
	        	{
	    			//set edge weight to 9999 if the next node is NO go zone or
	    			//boundary node
	        		this.bottomWeight = weightOfNoGoZone;
	        	}
	        	else if(this.bottomNode.isRoad() == true && this.bottomNode.isNoGoZone() == false)
	        	{
	        		//set edge weight to 1 if the next node is a road node
	        		this.bottomWeight = this.bottomNode.getOwnElevation();
	        	}
	    	}
	    	else
	    	{
	    		//set edge weight to INF if the next node is NULL
	    		this.bottomWeight = weightOfMapEdge;
	    	}
	    }
	    
	    //update the left weight of current node
	    public void updateLeftWeight(){
	    	if(this.leftNode != null)
	    	{
	    		if( this.leftNode.isNoGoZone() == true)
	        	{
	    			//set edge weight to 9999 if the next node is NO go zone or
	    			//boundary node
	        		this.leftWeight = weightOfNoGoZone;
	        	}
	        	else if(this.leftNode.isRoad() == true && this.leftNode.isNoGoZone() == false)
	        	{
	        		//set edge weight to 1 if the next node is a road node
	        		this.leftWeight = this.leftNode.getOwnElevation();
	        	}
	    	}
	    	else
	    	{
	    		//set edge weight to INF if the next node is NULL
	    		this.leftWeight = weightOfMapEdge;
	    	}
	    }
	    
	    //update the right weight of current node
	    public void updateRightWeight(){
	    	if(this.rightNode != null)
	    	{
	    		if( this.rightNode.isNoGoZone() == true)
	        	{
	    			//set edge weight to 9999 if the next node is NO go zone or
	    			//boundary node
	        		this.rightWeight = weightOfNoGoZone;
	        	}
	        	else if(this.rightNode.isRoad() == true && this.rightNode.isNoGoZone() == false)
	        	{
	        		//set edge weight to 1 if the next node is a road node
	        		this.rightWeight = this.rightNode.getOwnElevation();
	        	}
	    	}
	    	else
	    	{
	    		//set edge weight to INF if the next node is NULL
	    		this.rightWeight = weightOfMapEdge;
	    	}
	    }
	    
	    //read the top weight of current node
	    public int getTopWeight(){
	    	return this.topWeight;
	    }
	    //read the bottom weight of current node
	    public int getBottomWeight(){
	    	return this.bottomWeight;
	    }
	    //read the left weight of current node
	    public int getLeftWeight(){
	    	return this.leftWeight;
	    }
	    //read the right weigt of current node
	    public int getRightWeight(){
	    	return this.rightWeight;
	    }
	    
	    //read the total distance
	    public int getTotDistance(){
	    	return this.totDistance;
	    }
	    //set total distance
	    public void setTotDistance(int n){
	    	this.totDistance = n;
	    }
	    //read the path
	    public Vector<int[]> getTotPath(){
	    	return this.path;
	    }
	    //set path
	    public void setTotPath(Vector<int []> path){
	    	this.path = path;
	    }
	    //check if this node has been visited
	    public boolean getNodeVisited(){
	    	return this.nodeVisited;
	    }
	    //set the node visited property
	    public void setNodeVistied(boolean nodeVisit){
	    	this.nodeVisited = nodeVisit;
	    }
	    public void setElevation(int elevation){
	    	this.ownElevation = elevation;
	    }
	    public int getOwnElevation(){
	    	return this.ownElevation;
	    }
	    public void setTotCost(float cost){
	    	this.f_cost = cost;
	    }
	    public float getTotCost(){
	    	return this.f_cost;
	    }
	    public void setHeuristics(float h){
	    	this.heuristics = h;
	    }
	    public float getHeuristics(){
	    	return this.heuristics;
	    }
}

