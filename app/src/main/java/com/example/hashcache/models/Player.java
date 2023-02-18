public class Player{
    private UUID playerId;
    private String username;
    private ContactInfo contactInfo;
    private PlayerPreferences playerPreferences;
    private PlayerWallet playerWallet;

    //Constructor for new players only
    public Player(String username){
        this.playerId = UUID.randomUUID();
        this.username = username;
        this.contactInfo = new ContactInfo();
        this.playerPreferences = new PlayerPreferences();
        this.playerWallet = new PlayerWallet();
    }

    public void updateUserName(String newUserName){
        this.username = newUserName;
    }

    public UUID getPlayerId(){
        return this.playerId;
    }

    public String getUsername(){
        return this.username;
    }

    public ContactInfo getContactInfo(){
        return this.contactInfo;
    }

    public PlayerPreferences getPlayerPreferences(){
        return this.playerPreferences;
    }

    public PlayerWallet getPlayerWallet(){
        return this.playerWallet;
    }
}