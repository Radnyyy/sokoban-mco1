package solver;

import java.util.*;

public class SokoBot {

    int stateCount = 0;
    DeadlockDetector dl = new DeadlockDetector();
  
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */
    
    // Default solution and player position
    String solution = "dddd";   
    Position playerPos = new Position(0, 0);
    
    
    ArrayList<Position> initBoxPos = new ArrayList<>();    // Each index of the arraylist represents a box
    ArrayList<Position> goalPos = new ArrayList<>();   // Each index of the arraylist represents a goal
    
    // Get positions of boxes, goals, and player
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (mapData[i][j] == '.') {
                goalPos.add(new Position(j, i));
            } if (itemsData[i][j] == '$') {
                initBoxPos.add(new Position(j, i));
            } if (itemsData[i][j] == '@') {
                playerPos.setX(j);
                playerPos.setY(i);
            }
        }
    }
    
    State initState = new State(playerPos, 0, heuristicFunc(initBoxPos, goalPos, playerPos), initBoxPos, '\0', false, null);
    
    
    PriorityQueue<State> nodes = new PriorityQueue<>((State s1, State s2) -> s1.getTotalCost() - s2.getTotalCost());
    HashSet<State> visited = new HashSet<>();
    HashSet<Position> reachableSquares = dl.getReachableSquares(goalPos, mapData, width, height);
    boolean solutionFound = false;
    
    nodes.add(initState);
    
    // Start the A* search
    while (!nodes.isEmpty() && !solutionFound) {
        State current = nodes.poll();
        
        if (goalReached(current.getBoxPos(), goalPos)) {
            solution = getPath(current);
            solutionFound = true;
        }
        
        if (!solutionFound) {
            visited.add(current);

            List<State> neighbors = getNeighborStates(current, width, height, mapData, goalPos, reachableSquares);

            for (State s : neighbors) {
                if(!visited.contains(s)) {
                    nodes.add(s);
                }
            }
        }
    }
    
    System.out.println("Number of States Generated: " + stateCount);
    return solution;
  }
  
  /**
   * Heuristic function based on manhattan distance from each box to a goal
   * @param boxPos list of box positions
   * @param goalPos list of goal positions
   * @param playerPos current player position
   * @return 
   */
  public int heuristicFunc(ArrayList<Position> boxPos, ArrayList<Position> goalPos, Position playerPos) {
        int heuristic = 0;
        boolean[] goalUsed = new boolean[goalPos.size()];
        boolean[] boxUsed = new boolean[boxPos.size()];
        ArrayList<int[]> distances = new ArrayList<>(); // Each element will be {distance, boxIndex, goalIndex}

        // Calculate distances between each box and each goal
        for (int i = 0; i < boxPos.size(); i++) {
            for (int j = 0; j < goalPos.size(); j++) {
                int distance = Math.abs(boxPos.get(i).getX() - goalPos.get(j).getX()) +
                               Math.abs(boxPos.get(i).getY() - goalPos.get(j).getY());
                distances.add(new int[] {distance, i, j}); // Store the distance, box index, and goal index
            }
        }

        // Sort distances by the distance value (ascending order)
        distances.sort((a, b) -> a[0] - b[0]);

        // Try to match boxes to goals
        for (int[] entry : distances) {
            int distance = entry[0];
            int boxIndex = entry[1];
            int goalIndex = entry[2];

            // If neither the box nor the goal has been used yet, match them
            if (!boxUsed[boxIndex] && !goalUsed[goalIndex]) {
                heuristic += distance;      // Add the distance to the heuristic
                boxUsed[boxIndex] = true;   // Mark the box as used
                goalUsed[goalIndex] = true; // Mark the goal as used
            }
        }

        // If any boxes are left unmatched, match them to their closest unused goal
        for (int i = 0; i < boxPos.size(); i++) {
            if (!boxUsed[i]) {
                int closestGoalDist = Integer.MAX_VALUE;
                int closestGoalIndex = -1;
                for (int j = 0; j < goalPos.size(); j++) {
                    if (!goalUsed[j]) {
                        int distance = Math.abs(boxPos.get(i).getX() - goalPos.get(j).getX()) +
                                       Math.abs(boxPos.get(i).getY() - goalPos.get(j).getY());
                        if (distance < closestGoalDist) {
                            closestGoalDist = distance;
                            closestGoalIndex = j;
                        }
                    }
                }
                // Match the unmatched box with the closest unused goal
                heuristic += closestGoalDist;
                boxUsed[i] = true;
                goalUsed[closestGoalIndex] = true;
            }
        }

      //return heuristic + minPlayerToBoxDist;
      return heuristic;
      
  }
  
  /**
   * Generate the next possible states from the current state.
   * @param current current state
   * @param mapWidth map width
   * @param mapHeight map height
   * @param mapData map data
   * @param goalPos list of goal positions
   * @param reachableSquares pre-calculated set of reachable squares
   * @return list of neighboring states
   */
  public List<State> getNeighborStates(State current, int mapWidth, int mapHeight, char[][] mapData, ArrayList<Position> goalPos, HashSet<Position> reachableSquares) {
      List<State> neighbors = new ArrayList<>();
      ArrayList<Position> boxPos = current.getBoxPos();
      
      // Up, Down, Left, Right
      int[] moveX = {0, 0, -1, 1}; 
      int[] moveY = {-1, 1, 0, 0};
      char moves[] = {'u', 'd', 'l', 'r'};
      
      for (int i = 0; i < 4; i++) {
          int newPlayerX = current.getPlayerPos().getX() + moveX[i];
          int newPlayerY = current.getPlayerPos().getY() + moveY[i];
              
          
          if (dl.isValidBound(newPlayerX, newPlayerY, mapWidth, mapHeight, mapData)) {
              
              int boxIdx = getBoxIndex(boxPos, newPlayerX, newPlayerY);
              
              
                      
              if (boxIdx != -1) {
                  int newBoxX = newPlayerX + moveX[i];
                  int newBoxY = newPlayerY + moveY[i];
                  
                  if (dl.isValidBound(newBoxX, newBoxY, mapWidth, mapHeight, mapData) && getBoxIndex(boxPos, newBoxX, newBoxY) == -1) {
                      ArrayList<Position> newBoxPos = new ArrayList<>();
                      for (Position box : boxPos) {
                            newBoxPos.add(new Position(box.getX(), box.getY()));
                      }
                      
                      
                      
                      if (reachableSquares.contains(newBoxPos.get(boxIdx))) {
                        newBoxPos.get(boxIdx).setX(newBoxX);
                        newBoxPos.get(boxIdx).setY(newBoxY);
                        
                        if (!dl.isFrozen(new Position(newBoxX, newBoxY), mapData, reachableSquares, new HashSet<>(newBoxPos), new HashSet<>())) {
                            Position newPlayerPos = new Position(newPlayerX, newPlayerY);
                            neighbors.add(new State(newPlayerPos, current.getCost() + 1, heuristicFunc(newBoxPos, goalPos, newPlayerPos), newBoxPos, moves[i], true, current));
                            stateCount++;
                          
                        }
                      }
                  }
              } else if (!dl.undoMove(current, moves[i])) {
                   neighbors.add(new State(new Position(newPlayerX, newPlayerY), current.getCost() + 1, current.getHeuristic(), boxPos, moves[i], false, current));
                   stateCount++;
              }
          } 
          
      }
      
      return neighbors;
  }
  
  /**
   * Checks the index of the box within the boxPos array list based on a given
   * position
   * @param boxPos list of box positions
   * @param posX given x coordinate
   * @param posY given y coordinate
   * @return 
   */
  public int getBoxIndex(ArrayList<Position> boxPos, int posX, int posY) {    
      for (int i = 0; i < boxPos.size(); i++) {
          if (boxPos.get(i).getX() == posX && boxPos.get(i).getY() == posY)
              return i;
      }
      
      return -1;
  }
  
  
  
  /**
   * Checks if all goals are covered by boxes
   * @param boxPos list of box positions
   * @param goalPos list of goal positions
   * @return true if all goals are covered by boxes, false otherwise
   */
  public boolean goalReached(ArrayList<Position> boxPos, ArrayList<Position> goalPos) {
    for (int i = 0; i < boxPos.size(); i++) {
        boolean goalFound = false;
        
        for (int j = 0; j < goalPos.size() && !goalFound; j++) {
            if (boxPos.get(i).getX() == goalPos.get(j).getX() && boxPos.get(i).getY() == goalPos.get(j).getY()) {
                goalFound = true;
            }
        }
        
        if (!goalFound) {
            return false;
        }
        
        
    }
       System.out.println("goal found");
       return true;
  }
  
  /**
   * Rebuild the optimal path once goal is reached
   * @param last the last/goal state
   * @return the solution string
   */
  public String getPath(State last) {
    
      StringBuilder sb = new StringBuilder();
      
      while (last.getPrevMove() != '\0') {
          sb.append(last.getPrevMove());
          last = last.getParent();
      }
      
      sb.reverse();
      System.out.println("FinalPath: " + sb.toString());
      return sb.toString();
  }
  
  

}