public class ScannableCode{
    private UUID scannableCodeId;
    private CodeLocation codeLocation;
    private HashInfo hashInfo;
    private ArrayList<Comment> comments;

    public ScannableCode(CodeLocation codeLocation, HashInfo hashInfo){
        this.codeLocation = codeLocation;
        this.hashInfo = hashInfo;
        this.scannableCodeId = UUID.randomUUID();

        this.comments = new ArrayList<>();
    }

    public void addComment(Comment newComment){
        this.comments.add(newComment);
    }

    public CodeLocation getCodeLocation(){
        return this,codeLocation;
    }

    public HashInfo getHashInfo(){
        return this.hashInfo;
    }

}