public class PlayerList {
    private ArrayList<Player> players;

    public void retrievePlayersFromDB(){
        //Retrive the players from the database
    }

    public ArrayList<Player> getPlayersSortedBy(Object filter){
        //return the players sorted by a specific method
        return this.players;
    }

    public ArrayList<Player> getFirstNPlayersSortedBy(Object filter, int n){
        return (this.getPlayersSortedBy(filter)).sublist(0, 3);
    }
}
