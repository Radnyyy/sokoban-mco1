package solver;

import java.util.*;

public class SokoBot {

    int stateCount = 0;
  
  
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
    HashSet<Position> reachableSquares = getReachableSquares(goalPos, mapData, width, height);
    boolean solutionFound = false;
    
    nodes.add(initState);
    
    // Start the search
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
    
    System.out.println("Number of States Explored: " + stateCount);
    return solution;
  }
  
  // Heuristic function to calculate manhattan distance
  public int heuristicFunc(ArrayList<Position> boxPos, ArrayList<Position> goalPos, Position playerPos) {
      int heuristic = 0;
      int minPlayerToBoxDist = Integer.MAX_VALUE;
      boolean[] goalUsed = new boolean[goalPos.size()];

      for (Position box : boxPos) {
          int minDist = Integer.MAX_VALUE;
          int closestGoalIdx = -1;

          for (int i = 0; i < goalPos.size(); i++) {
              if (!goalUsed[i]) {
                  int dist = Math.abs(box.getX() - goalPos.get(i).getX()) + Math.abs(box.getY() - goalPos.get(i).getY());
                  if (dist < minDist) {
                      minDist = dist;
                      closestGoalIdx = i;
                  }
              }
              
          }

          if (closestGoalIdx != -1) {
              goalUsed[closestGoalIdx] = true;
              heuristic += minDist;
          }
          
          int playerToBoxDist = Math.abs(box.getX() - playerPos.getX()) + Math.abs(box.getY() - playerPos.getY());
          minPlayerToBoxDist = Math.min(playerToBoxDist, minPlayerToBoxDist);
      }

      //return heuristic + minPlayerToBoxDist;
      return heuristic;
      
  }
  
  // Get next possible states
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
              
          
          if (isValidBound(newPlayerX, newPlayerY, mapWidth, mapHeight, mapData)) {
              
              int boxIdx = getBoxIndex(boxPos, newPlayerX, newPlayerY);
              
              
                      
              if (boxIdx != -1) {
                  int newBoxX = newPlayerX + moveX[i];
                  int newBoxY = newPlayerY + moveY[i];
                  
                  if (isValidBound(newBoxX, newBoxY, mapWidth, mapHeight, mapData) && getBoxIndex(boxPos, newBoxX, newBoxY) == -1) {
                      ArrayList<Position> newBoxPos = new ArrayList<>();
                      for (Position box : boxPos) {
                            newBoxPos.add(new Position(box.getX(), box.getY()));
                      }
                      
                      
                      
                      if (!isCorner(newBoxX, newBoxY, mapData) && reachableSquares.contains(newBoxPos.get(boxIdx))) {
                        newBoxPos.get(boxIdx).setX(newBoxX);
                        newBoxPos.get(boxIdx).setY(newBoxY);
                        Position newPlayerPos = new Position(newPlayerX, newPlayerY);
                        neighbors.add(new State(newPlayerPos, current.getCost() + 1, heuristicFunc(newBoxPos, goalPos, newPlayerPos), newBoxPos, moves[i], true, current));
                        System.out.println("Generated new state with move: " + moves[i]);
                        stateCount++;
                      }
                  }
              } else if (!undoMove(current, moves[i])) {
                   neighbors.add(new State(new Position(newPlayerX, newPlayerY), current.getCost() + 1, current.getHeuristic(), boxPos, moves[i], false, current));
                   System.out.println("Generated new state with move: " + moves[i]);
                   stateCount++;
              }
          } 
          
      }
      
      return neighbors;
  }
  
  // Get the index of the box
  public int getBoxIndex(ArrayList<Position> boxPos, int playerX, int playerY) {    
      for (int i = 0; i < boxPos.size(); i++) {
          if (boxPos.get(i).getX() == playerX && boxPos.get(i).getY() == playerY)
              return i;
      }
      
      return -1;
  }
  
  // Check if item (player/box) is within the map
  public boolean isValidBound(int xPos, int yPos, int mapWidth, int mapHeight, char[][] mapData) {
      return xPos >= 0 && xPos < mapWidth && yPos >= 0 && yPos < mapHeight && mapData[yPos][xPos] != '#';
  }
  
  // Check if all goals are covered by boxes
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
  
  // Rebuild the optimal path once goal is reached
  public String getPath(State last) {
    
      StringBuilder sb = new StringBuilder();
      
      while (last.getPrevMove() != '\0') {
          sb.append(last.getPrevMove());
          last = last.getParent();
          System.out.println("PathMove: " + last.getPrevMove());
      }
      
      sb.reverse();
      System.out.println("FinalPath: " + sb.toString());
      return sb.toString();
  }
  
  // Corner deadlock detection
  public boolean isCorner(int boxX, int boxY, char[][] mapData) {
    // Check if the box is in a corner where no goal exists
    if (mapData[boxY][boxX] != '.') {
        boolean corner1 = mapData[boxY-1][boxX] == '#' && mapData[boxY][boxX-1] == '#'; // top-left corner
        boolean corner2 = mapData[boxY-1][boxX] == '#' && mapData[boxY][boxX+1] == '#'; // top-right corner
        boolean corner3 = mapData[boxY+1][boxX] == '#' && mapData[boxY][boxX-1] == '#'; // bottom-left corner
        boolean corner4 = mapData[boxY+1][boxX] == '#' && mapData[boxY][boxX+1] == '#'; // bottom-right corner

        return corner1 || corner2 || corner3 || corner4;
    }
    
    return false;
  }
  
  // Check if move is redundant and just leads to an already explored state
  public boolean undoMove(State current, char move) {
    char lastMove = current.getPrevMove();

    if ((lastMove == 'l' && move == 'r') || (lastMove == 'r' && move == 'l') || 
        (lastMove == 'u' && move == 'd') || (lastMove == 'd' && move == 'u')) {
        
        State parent = current.getParent();
        if (parent != null && parent.getPrevMove() == move) {
            return true; 
        }
    }
    
    return false;
  }
  
  
 
  


  
    public HashSet<Position> getReachableSquares(ArrayList<Position> goalPos, char mapData[][], int width, int height) {
        HashSet<Position> reachableSquares = new HashSet<>();
        HashSet<Position> visited = new HashSet<>();
        int[] moveX = {0, 0, -1, 1};
        int[] moveY = {-1, 1, 0, 0};
      
        for (Position goal : goalPos) {
            Queue<Position> queue = new LinkedList<>();
          
            queue.add(goal);
            visited.add(goal);  
            reachableSquares.add(goal); 
          
            while (!queue.isEmpty()) {
                Position current = queue.poll();
                
                int x = current.getX(), y = current.getY();
              
                // Pull boxes from each direction
                for (int i = 0; i < 4; i++) {
                    int boxX = x + moveX[i];
                    int boxY = y + moveY[i];
                  
                    int playerX = x + 2 * moveX[i];
                    int playerY = y + 2 * moveY[i];
                  
                    Position newBoxPos = new Position(boxX, boxY);
                    if (isValidBound(boxX, boxY, width, height, mapData) && isValidBound(playerX, playerY, width, height, mapData) && !visited.contains(newBoxPos)) {
                        reachableSquares.add(newBoxPos);
                        visited.add(newBoxPos);
                        queue.add(newBoxPos);
                    }
                }
            }
        }
        return reachableSquares; 
    }

}
