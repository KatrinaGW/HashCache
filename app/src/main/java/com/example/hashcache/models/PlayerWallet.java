public class PlayerWallet{
    private HashMap<String, Image> scannableCodes;

    public PlayerWallet(){
        this.scannableCodes = new HashMap<String, Image>();
    }

    public void addScannableCode(String scannableCodeId){
        this.scannableCodes.put(scannableCodeId, null);
    }

    public void addScannableCode(String scannableCodeId, Image locationImage){
        this.scannableCodes.put(scannableCodeId, locationImage);
    }

    public Image getScannableCodeLocationImage(String scannableCodeId){
        return this.scannableCodes.get(scannableCodeId);
    }

    //Do we need to get the scannablecode from here? Since I'm assuming we're just connecting to
    //DB and can get it somewhere else. Assume id is already known if the user gets here - doesn't
    //seem to be a point in adding that method
}