package com.example.lasills.baseballscoreboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String[] homeTeam, guestTeam;
    private int currentBatter=0;
    private boolean[] runSequence;
    private int homeScore=0, homeScore1=0, homeScore2=0, guestScore=0, guestScore1=0, guestScore2=0;
    private int inning=1;
    private int outs=0, balls=0, strike=0;
    private boolean currentTeam=true; //true==home, false==guest

    //THERES STILL A LOT OF WORK TO DO WITH RUNSEQUENCES AND ALSO CONFIRMING WHICH BUTTONS GO WITH
    //WHICH JAVA FUNCTIONS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create teams
        homeTeam=new String[25];
        for(int i=0; i<25; i++){
            homeTeam[i]="Player "+(i+1);
        }
        guestTeam=new String[25];
        for(int i=0; i<25; i++){
            guestTeam[i]="Player "+(i+1);
        }
        //runSequence
        runSequence=new boolean[4];
        for(int i=0; i<4; i++){
            runSequence[i]=false;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //increments score, used for scoring purposes
    public void homeScoreInc(){
        homeScore++;
        homeScore2++;
        if(homeScore2==10){
            homeScore2=0;
            homeScore1++;
        }
        updateScore(0);
    }
    public void guestScoreInc(){
        guestScore++;
        guestScore2++;
        if(guestScore2==10){
            guestScore2=0;
            guestScore1++;
        }
        updateScore(1);
    }
    //increments specific parts of score, used for manual input
    public void homeScore1Inc(){
        homeScore++;
        homeScore1++;
        if(homeScore1==10) {
            homeScore1 = 0;
            homeScore-=10;
        }
        updateScore(0);
    }
    public void homeScore2Inc(){
        homeScore+=10;
        homeScore2++;
        if(homeScore2==10) {
            homeScore2 = 0;
            homeScore-=100;
        }
        updateScore(1);
    }
    public void guestScore1Inc(){
        guestScore++;
        guestScore1++;
        if(guestScore1==10) {
            guestScore-=10;
            guestScore1 = 0;
        }
        updateScore(1);
    }
    public void guestScore2Inc(){
        guestScore+=10;
        guestScore2++;
        if(guestScore2==10) {
            guestScore-=100;
            guestScore2 = 0;
        }
        updateScore(1);
    }

    //team == 0 = home; team == 1 = guest
    public void updateScore(int team){
        if(team == 0) {
            Button scoreButton =  (Button) findViewById(R.id.homeScoreButton1);
            scoreButton.setText(""+homeScore1);
            scoreButton = (Button) findViewById(R.id.homeScoreButton2);
            scoreButton.setText(""+homeScore2);
        }
        else{
            Button scoreButton = (Button) findViewById(R.id.visitorsScoreButton1);
            scoreButton.setText(""+guestScore1);
            scoreButton = (Button) findViewById(R.id.visitorsScoreButton2);
            scoreButton.setText(""+guestScore2);
        }
    }
    /*
     ***********BALL COUNTING SYSTEM*****************
     */
    //ball counter
    public void ballInc(){
        balls++;
        if(balls==4){
            nextBatter();
            if(currentTeam){
                homeScoreInc();
            }
            else{
                guestScoreInc();
            }
        }
    }
    //if the display button is pressed, cycle through balls
    public void manBallInc(){
        balls++;
        if(balls==4)
            balls=0;
    }
    /*
            **********STRIKE COUNTING SYSTEM*****************
     */
    public void strikeInc(){
        /* if the user presses the batter strike button, strikeButton, strikeInc() is called
        IF it is strike 3, increment outs with outInc()
        OTHERWISE, just increment strike
        ALWAYS update strike display using updateStrikeCounter()
         */
        if(runSequence[0]){
            batterTag();
        }
        if(strike++==3) {
            outInc();
        }
        updateStrikeCounter();
    }
    public void manStrikeInc(){
        /* if the user clicks the strike display, strikeButton2, manStrikeInc() is called
        IF it is strike 3, resets to 0
        OTHERWISE just increment strike
        ALWAYS update strike display using updateStrikeCounter()
         */
        if(strike++==3)
            strike=0;
        updateStrikeCounter();
    }
    public void updateStrikeCounter(){
        /* update strikeButton2 when called from either strikeInc(), manStrikeInc(), or nextBatter()
        IF there is one strike, display one circle, "o"
        IF there are two strike, display two circles, "o o"
        IF the third strike is recorded, reset to zero, display nothing, ""
         */
        Button strikeCounter = (Button) findViewById(R.id.strikeButton2);
        if(strike==0)
            strikeCounter.setText("");
        if(strike==1)
            strikeCounter.setText("o");
        if(strike==2)
            strikeCounter.setText("o o");
    }
    /*
            ************OUT COUNTING SYSTEM***********************
     */
    public void outInc(){
        /* if three strikes are recorded, strikeInc() calls outInc()
        IF it is out 3, the team switches, switchField()
        OTHERWISE, the next batter is called, nextBatter()
        updateOutCounter() is ALWAYS called
     */
        outs++;
        if(outs==3){
            switchField();
        }
        else
            nextBatter();
        updateOutCounter();
    }
    public void manOutInc(){
        /* if the user clicks the out display, outButton, manOutInc() is called
            the out display, outButton, cycles through 1, 2, or 3, strikes, without consequence
            updateOutCounter() is ALWAYS called
         */
        outs++;
        if(outs==3)
            outs=0;
        updateOutCounter();
    }
    public void updateOutCounter(){
        /* updates outButton if called by either outInc(), manOutInc(), or nextBatter()
            IF there is one out, display one circle, "o"
            IF there are two outs, display two circles, "o o"
            IF the third out is recorded, reset to zero, display nothing, ""
         */
        Button outCounter = (Button) findViewById(R.id.outButton);
        if(outs==0)
            outCounter.setText("");
        if(outs==1)
            outCounter.setText("o");
        if(outs==2)
            outCounter.setText("o o");
    }
    /*
            *************HITTING RUNNING AND TAGGING OUT******************
     */
    public void batterHit(){
        /* begins run sequence for batter and all players on bases once hitButton is clicked
            IF runSequence is all false, this is the first batter to potentially run
            IF runSequence has any true values, those bases are occupied and must be issued commands
                in reversing order (3->2->1->0)
            IF runSequence[0] is false, this is when the button displays 'HIT'
            IF runSequence[0] is true, this is when the button displays 'RUN'
         */
        if (!runSequence[0]) { //UH OH CHECK OUT THE STRIKE BUTTON
            Button hitButton = (Button) findViewById(R.id.hitButton);
            Button outButton = (Button) findViewById(R.id.strikeButton);
            Button ballButton = (Button) findViewById(R.id.ballButton2);
            hitButton.setText("RUN");
            outButton.setText("OUT");
            ballButton.setText("--");

            runSequence[0]=true;
            runSequence();
            //ballButton.setText("");
        }
        else{
            batterRun();
        }
    }
    public void batterRun(){ //CHECK OUT THE NEXT BATTER
        /* runs the current batter to first base and imports a new batter
         */
        TextView homePlate = (TextView) findViewById(R.id.homeBase);
        TextView firstBase = (TextView) findViewById(R.id.firstBase);
        firstBase.setText(homePlate.getText());
        runSequence[1]=true;
        nextBatter();
        Button hitButton = (Button) findViewById(R.id.hitButton);
        Button outButton = (Button) findViewById(R.id.strikeButton);
        Button ballButton = (Button) findViewById(R.id.ballButton2);
        hitButton.setText("HIT");
        outButton.setText("STRIKE");
        ballButton.setText("--");
        runSequence[0]=false;
    }
    public void batterTag(){
        /* tags the current batter and imports a new one, called from strikeInc() once runSequence[0]
            is true
         */
        TextView homePlate = (TextView) findViewById(R.id.homeBase);
        nextBatter();
        Button hitButton = (Button) findViewById(R.id.hitButton);
        Button outButton = (Button) findViewById(R.id.strikeButton);
        Button ballButton = (Button) findViewById(R.id.ballButton2);
        hitButton.setText("HIT");
        outButton.setText("STRIKE");
        ballButton.setText("--");
        runSequence[0]=false;
        outInc();
    }
    public void runSequence(){
        /* begins run sequence by highlighting appropriate buttons to be engaged with next
         */
        if(runSequence[3]){
            Button runButton = (Button) findViewById(R.id.thirdBaseRunButton);
            Button outButton = (Button) findViewById(R.id.thirdBaseOutButton);
            runButton.setText("!RUN!");
            outButton.setText("!OUT!");
        }
        else if(runSequence[2]){
            Button runButton = (Button) findViewById(R.id.secondBaseRunButton);
            Button outButton = (Button) findViewById(R.id.secondBaseOutButton);
            runButton.setText("!RUN!");
            outButton.setText("!OUT!");
        }
        else if(runSequence[1]){
            Button runButton = (Button) findViewById(R.id.firstBaseRunButton);
            Button outButton = (Button) findViewById(R.id.firstBaseOutButton);
            runButton.setText("!RUN!");
            outButton.setText("!OUT!");
        }
        else if(runSequence[0]){
            Button runButton = (Button) findViewById(R.id.hitButton);
            Button outButton = (Button) findViewById(R.id.strikeButton);
            runButton.setText("!RUN!");
            outButton.setText("!OUT!");
        }
    }
    public void firstBaseRun(){
        /* runs the current batter to second base
         */
        TextView secondBase = (TextView) findViewById(R.id.secondBase);
        TextView firstBase = (TextView) findViewById(R.id.firstBase);
        secondBase.setText(firstBase.getText());
        firstBase.setText("Empty");
        Button runButton = (Button) findViewById(R.id.firstBaseRunButton);
        Button outButton = (Button) findViewById(R.id.firstBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        runSequence[1]=false;
        runSequence();
    }
    public void firstBaseOut(){
        /* tags the current first base runner and counts an out
         */
        TextView firstBase = (TextView) findViewById(R.id.firstBase);
        firstBase.setText("Empty");
        Button runButton = (Button) findViewById(R.id.firstBaseRunButton);
        Button outButton = (Button) findViewById(R.id.firstBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        outInc();
        runSequence[1]=false;
        runSequence();
    }
    public void secondBaseRun(){
        /* runs the current batter to third base
         */
        TextView secondBase = (TextView) findViewById(R.id.secondBase);
        TextView thirdBase = (TextView) findViewById(R.id.thirdBase);
        thirdBase.setText(secondBase.getText());
        secondBase.setText("Empty");
        Button runButton = (Button) findViewById(R.id.secondBaseRunButton);
        Button outButton = (Button) findViewById(R.id.secondBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        runSequence[2]=false;
        runSequence();

    }
    public void secondBaseOut(){
        /* tags the current first base runner and counts an out
         */
        TextView secondBase = (TextView) findViewById(R.id.secondBase);
        secondBase.setText("Empty");
        Button runButton = (Button) findViewById(R.id.secondBaseRunButton);
        Button outButton = (Button) findViewById(R.id.secondBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        outInc();
        runSequence[2]=false;
        runSequence();
    }
    public void thirdBaseRun() {
        /* runs the current batter home
         */
        TextView thirdBase = (TextView) findViewById(R.id.thirdBase);
        Button runButton = (Button) findViewById(R.id.secondBaseRunButton);
        Button outButton = (Button) findViewById(R.id.secondBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        if (currentTeam) {
            homeScoreInc();
        } else
            guestScoreInc();
        runSequence[3]=false;
        runSequence();
    }
    public void thirdBaseOut(){
        /* tags the current first base runner and counts an out
         */
        TextView thirdBase = (TextView) findViewById(R.id.thirdBase);
        thirdBase.setText("Empty");
        Button runButton = (Button) findViewById(R.id.thirdBaseRunButton);
        Button outButton = (Button) findViewById(R.id.thirdBaseOutButton);
        runButton.setText("RUN");
        outButton.setText("OUT");
        outInc();
        runSequence[3]=false;
        runSequence();
    }


    /*
            *************BATTERS, TEAMS, AND INNINGS******************
            ************NOT YET IMPLEMENTED FULLY**********************
     */
    public void nextBatter(){
        /* if called by outInc() or switchField(), resets scoreboard and pushes next batter
        ALWAYS sets balls and strike to 0, and calls both updateOutCounter() and updateStrikeCounter()
        ALWAYS calls the next batter for the current batting team to home plate
         */
        balls=0;
        strike=0;
        currentBatter++;
        TextView homePlate = (TextView) findViewById(R.id.homeBase);
        updateOutCounter();
        updateStrikeCounter();
        if(currentTeam){
            homePlate.setText(""+homeTeam[currentBatter]);
        }
        else{
            homePlate.setText(""+guestTeam[currentBatter]);
        }
    }
    public void switchField(){
        /* if called by outInc(), switches currentTeam and pushes the next batter
        ALWAYS negatives boolean currentTeam
        IF currentTeam is now home, increment inning, inningInc()
        ALWAYS calls nextBatter()
        IF currentTeam is guest team, currentTeam==false, increment innings, inningInc()
         */
        currentTeam=!currentTeam;
        currentBatter=-1;
        if(currentTeam)
            inningInc();
        nextBatter();
    }
    public void inningInc(){
        /* if called by switchField(), updates inning and calls updateInningCounter()
        IF it is now inning 10, end the game with gameEnd()
        OTHERWISE calls updateInningCounter()
         */
        if(inning++==10){
            gameEnd();
        }
        else
            updateInningCounter();
    }
    public void manInningInc(){
        /* if user clicks inning display, inningsButton, updates inning
        IF it is now inning 10, set inning to 1
        ALWAYS calls updateInningCounter()
         */
        if(inning++==10) {
            inning = 1;
        }
        updateInningCounter();
    }
    public void updateInningCounter(){
        /* if called by inningInc() or manInningInc(), updates inningDisplay to represent currentInning
        ALWAYS updates inningsButton to display int value inning
         */
        Button inningCounter = (Button) findViewById(R.id.inningsButton);
        inningCounter.setText(""+inning);
    }
    /*
     *****************THE GAME END*******************
     */
    public void gameEnd(){

    }

}
