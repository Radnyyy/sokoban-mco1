/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author user
 */
public class DeadlockDetector {
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
  
  // 2x2 deadlock detection
  public boolean is2x2(int boxX, int boxY, char[][] mapData) {
    boolean topLeft = mapData[boxY-1][boxX] == '$' && mapData[boxY][boxX-1] == '$' && mapData[boxY-1][boxX-1] == '$';
    boolean topRight = mapData[boxY-1][boxX] == '$' && mapData[boxY][boxX+1] == '$' && mapData[boxY-1][boxX+1] == '$';
    boolean bottomLeft = mapData[boxY+1][boxX] == '$' && mapData[boxY][boxX-1] == '$' && mapData[boxY+1][boxX-1] == '$';
    boolean bottomRight = mapData[boxY+1][boxX] == '$' && mapData[boxY][boxX+1] == '$' && mapData[boxY+1][boxX+1] == '$';

    return topLeft || topRight || bottomLeft || bottomRight;
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
    
 public boolean isFrozen(Position boxPos, char[][] mapData, HashSet<Position> reachableSquares, Set<Position> allBoxPositions, Set<Position> checkedBoxes) {
    // Check if the box is on a goal
    if (mapData[boxPos.getY()][boxPos.getX()] == '.') {
        return false;
    }

    // If the box has already been checked, return true (it is considered frozen)
    if (checkedBoxes.contains(boxPos)) {
        return true;
    }

    

    // Check if the box is blocked vertically and horizontally.
    boolean blockedVertically = isBlockedVertically(boxPos, mapData, reachableSquares, allBoxPositions, checkedBoxes);
    boolean blockedHorizontally = isBlockedHorizontally(boxPos, mapData, reachableSquares, allBoxPositions, checkedBoxes);

   
    return blockedVertically && blockedHorizontally;
}

public boolean isBlockedVertically(Position boxPos, char[][] mapData, HashSet<Position> reachableSquares, Set<Position> allBoxPositions, Set<Position> checkedBoxes) {

    // Mark the box as being processed 
    checkedBoxes.add(boxPos);
    
    Position above = new Position(boxPos.getX(), boxPos.getY() - 1);
    Position below = new Position(boxPos.getX(), boxPos.getY() + 1);

    // Check for walls above and below
    if (mapData[boxPos.getY() + 1][boxPos.getX()] == '#' || mapData[boxPos.getY() - 1][boxPos.getX()] == '#') {
        return true;
    }

    // Check if box is surrounded by 2 dead squares on opposite sides
    if ((!reachableSquares.contains(above) && !reachableSquares.contains(below))) {
        return true;
    }

    // Recursively check if the boxes above or below are blocked vertically.
    if (allBoxPositions.contains(above) && !checkedBoxes.contains(above)) {
        if (isBlockedHorizontally(above, mapData, reachableSquares, allBoxPositions, checkedBoxes)) {
            return true;
        }
    }

    if (allBoxPositions.contains(below) && !checkedBoxes.contains(below)) {
        if (isBlockedHorizontally(below, mapData, reachableSquares, allBoxPositions, checkedBoxes)) {
            return true;
        }
    }

    return false;
}

public boolean isBlockedHorizontally(Position boxPos, char[][] mapData, HashSet<Position> reachableSquares, Set<Position> allBoxPositions, Set<Position> checkedBoxes) {

    // Mark the box as being processed 
    checkedBoxes.add(boxPos);
    
    Position left = new Position(boxPos.getX() - 1, boxPos.getY());
    Position right = new Position(boxPos.getX() + 1, boxPos.getY());

    // Check for walls left and right
    if (mapData[boxPos.getY()][boxPos.getX() + 1] == '#' || mapData[boxPos.getY()][boxPos.getX() - 1] == '#') {
        return true;
    }

    // Check if the box is surrounded by dead squares on both sides
    if (!reachableSquares.contains(left) && !reachableSquares.contains(right)) {
        return true;
    }

    // Recursively check if the boxes to the left or right are blocked horizontally.
    if (allBoxPositions.contains(left) && !checkedBoxes.contains(left)) {
        if (isBlockedVertically(left, mapData, reachableSquares, allBoxPositions, checkedBoxes)) {
            return true;
        }
    }

    if (allBoxPositions.contains(right) && !checkedBoxes.contains(right)) {
        if (isBlockedVertically(right, mapData, reachableSquares, allBoxPositions, checkedBoxes)) {
            return true;
        }
    }

    return false;
}

// Check if item (player/box) is within the map
  public boolean isValidBound(int xPos, int yPos, int mapWidth, int mapHeight, char[][] mapData) {
      return xPos >= 0 && xPos < mapWidth && yPos >= 0 && yPos < mapHeight && mapData[yPos][xPos] != '#';
  }
}
