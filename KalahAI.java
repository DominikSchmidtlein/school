import java.util.ArrayList;
import java.util.Random;
/**
 * Unfortuantely I do not have the rest of the files necessary to play the game,
 * sorry for the inconvenience.
 *
 * The challenge is to create an AI which can simulate future moves and make
 * the best decision based on the findings. The game in question is an old 
 * African game called Kalah. The rules can be found at https://en.wikipedia.org/wiki/Kalah
 * 
 * @author (Dominik Schmidtlein) 
 * @version (a version number or a date)
 */
public class KalahAI implements IKalahAI
{
    GameState gameState; //reference for state that is updated at each call
    Position nextMove; 
    public int DEPTH = 3; // edit this for different depth
    public Position chooseMove(GameState state){
        this.gameState = state; //update local reference of game state
        if(DEPTH == 0){ //implemented for personal testing
            Random r = new Random();
            return this.generateMoves(gameState).get(r.nextInt(this.generateMoves(gameState).size()));
        }
        search(state,DEPTH);
        return nextMove; // search() updates the nextMove variable, thus at this point nextMove is the new best move
    }   
    
    private ArrayList<Position> generateMoves(GameState state){
        ArrayList<Position> moves = new ArrayList<Position>();
        for(int i = 1; i <= state.getHolesPerPlayer(); i++){
                Position p = state.createPosition(state.isTopPlayerTurn(),i); //generate new move
                try{
                    state.checkValidMove(p); //if move is legal, add to moves
                    moves.add(p);
                }
                catch(InvalidMoveException e){
                }
        }
        return moves;
    }
    
    int search(GameState s, int depth){
        if (depth == 0) //if depth is maxed, return the difference in pots as indication of quality of move
            return s.getBeanCount(s.createPosition(this.gameState.isTopPlayerTurn(), Position.POT)) - s.getBeanCount(s.createPosition(!this.gameState.isTopPlayerTurn(), Position.POT));
        else{
            if (this.gameState.isTopPlayerTurn() == s.isTopPlayerTurn()){ //if true, then its the current player's turn in the original board, thus the best move will be found
                Integer maxDifference = null; //keep track of the best end game scores
                if(this.generateMoves(s).isEmpty()){ //if this scenario results in game over
                    int playerBeans = s.getBeanCount(s.createPosition(this.gameState.isTopPlayerTurn(), Position.POT)); //keep running total of all beans on the player side
                    int oppositionBeans = s.getBeanCount(s.createPosition(!this.gameState.isTopPlayerTurn(), Position.POT));// keep running total of beans on opposing side
                    for(int i = 0; i < s.getHolesPerPlayer(); i++){
                        playerBeans += s.getBeanCount(s.createPosition(this.gameState.isTopPlayerTurn(), i)); //add all the beans from the player side to the total
                        oppositionBeans += s.getBeanCount(s.createPosition(!this.gameState.isTopPlayerTurn(), i));// add all the beans from the opposition side to the toatl
                    }
                    if(playerBeans > oppositionBeans) return 5000; //if player won, this is automatically the best move
                    else return -5000; // if player lost, this is the worst move
                }
                
                for(Position p : this.generateMoves(s)){ //loop through all available moves
                    GameState s2 = (GameState) s.clone();  // copy state
                    try{
                        s2.applyMove(p); //make that move on the copied state
                    }
                    catch(InvalidMoveException e){// since all moves have been checked, this should never happen
                    }
                    int beanDifference = search(s2, depth - 1); //search for best move in new state, decrement depth
                    if(maxDifference == null){
                        maxDifference = beanDifference; //if this is first state to be evaluated, automatically set new score
                        if(depth == this.DEPTH){
                            nextMove = p; //if this would be the next move in order to start down the best tree, assign it to nextMove
                        }
                    }
                    else if(beanDifference > maxDifference){
                        maxDifference = beanDifference; //if this tree results in a better bean Difference then update max
                        if(depth == this.DEPTH){
                            nextMove = p; //update best move
                        }
                    }
                }
                
                return maxDifference;
            }
            else{//the turn is of the opposition
                Integer minDifference = null;    
                if(this.generateMoves(s).isEmpty()){
                    int oppositionBeans = s.getBeanCount(s.createPosition(this.gameState.isTopPlayerTurn(), Position.POT)); //oppositions beans are the beans on the side of the current turn player
                    int playerBeans = s.getBeanCount(s.createPosition(!this.gameState.isTopPlayerTurn(), Position.POT));
                    for(int i = 0; i < s.getHolesPerPlayer(); i++){ //update the pots with the beans on each side
                        oppositionBeans += s.getBeanCount(s.createPosition(this.gameState.isTopPlayerTurn(), i));
                        playerBeans += s.getBeanCount(s.createPosition(!this.gameState.isTopPlayerTurn(), i));
                    }
                    if(playerBeans > oppositionBeans) return -5000;//if opposition loses, this is considered a move that the oppositions wouldn't make
                    else return 5000;
                }
                for(Position p : this.generateMoves(s)){ //loop through all available moves
                    GameState s2 = (GameState) s.clone();
                    try{
                        s2.applyMove(p); //make that move on s yielding a state s'
                    }
                    catch(InvalidMoveException e){
                    }
                    int beanDifference = search(s2, depth - 1);
                    if(minDifference == null){
                        minDifference = beanDifference;
                    }
                    else if(beanDifference < minDifference){
                        minDifference = beanDifference;
                    }
                }
                
                return minDifference;
            }
        }
    }
}
