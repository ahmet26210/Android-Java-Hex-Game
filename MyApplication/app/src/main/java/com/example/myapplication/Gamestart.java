package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import static android.graphics.Color.*;

public class Gamestart extends AppCompatActivity {
    RelativeLayout relativeLayout;
    private Button[][] buttons;
    private Button[] buttons1;
    private int moves[][];
    private Cell[][] temp_arrey;//2D dynamic array
    private char[][] hexCells;//2D dynamic array
    private EditText editTextfilename;
    private int  MaximizingPlayer = 1;
    private int  MinimizingPlayer = -1;
    private int[][]  trB;
    private int[][]  trW;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);
        control=' ';control1=' ';
        lastp_x=0;lastp_y=0;
        tempi=0;tempj=0;
        counter=0;
        currentPlayer='x';
        controll=false;
        controll1=false;
        counter1=0;
        fullcell=0;
        lastpoint=0;
        endgame=false;
        Score=0;
        count=0;
        Intent i=getIntent();
        size=i.getIntExtra("game_size",0);
        is_valid();
        gametype=i.getBooleanExtra("game_type",false);
        level=i.getIntExtra("level_game",1);
        whofirst=i.getBooleanExtra("whofirst",false);
        relativeLayout=new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        if(gametype==false) setControl1('c');
        if(gametype==true) setControl1('u');
        if(whofirst==true){
            check=true;
        }
        temp_arrey=new Cell[getSize()][getSize()];
        moves=new int[size*size][2];
        for (int j=0;j<getSize();j++){
            for (int k=0;k<getSize();k++){
                temp_arrey[j][k]=new Cell();
                temp_arrey[j][k].setcell('*');
            }
        }
        trB=new int[100][100];
        trW=new int[100][100];
        int temp1,temp2,space2=0,buttonlength=0;
        int temp,width=0,length=0;
        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxlength=displayMetrics.heightPixels;
        maxwidth = displayMetrics.widthPixels;
        int buttonwidth =2*maxwidth/(3*size-1);
        int y = (maxlength-maxwidth)/ 2;
        //Toast.makeText(getApplicationContext(),"width,height "+size+" "+maxlength,Toast.LENGTH_SHORT).show();
        buttons = new Button[size][size];
        buttons1= new Button[4];
        for (int j=0;j<size;j++){
            temp=width;
            for (int k=0;k<size;k++) {
                buttons[j][k]=new Button(this);
                buttons[j][k].setLayoutParams(new RelativeLayout.LayoutParams(buttonwidth-5,buttonwidth-5));
                buttons[j][k].setX(width);
                buttons[j][k].setY(y);
                buttons[j][k].setText("*");
                buttons[j][k].setId(100+size*j+k);
                buttons[j][k].setBackgroundColor(LTGRAY);
                buttons[j][k].setOnClickListener(getOnClick());
                width+=buttonwidth;
                relativeLayout.addView(buttons[j][k]);
            }
            y+=buttonwidth;
            width=temp+buttonwidth/2;
        }
        buttons1[0] = new Button(this);
        buttons1[0].setLayoutParams(new RelativeLayout.LayoutParams(200,200));
        buttons1[0].setId(1);
        buttons1[0].setY(100);
        buttons1[0].setText("UNDO");
        buttons1[0].setX(25);
        buttons1[0].setBackgroundColor(MAGENTA);
        buttons1[0].setOnClickListener(getOnClick());
        relativeLayout.addView(buttons1[0]);
        buttons1[1] = new Button(this);
        buttons1[1].setLayoutParams(new RelativeLayout.LayoutParams(200,200));
        buttons1[1].setId(2);
        buttons1[1].setY(100);
        buttons1[1].setText("SAVE");
        buttons1[1].setX(250);
        buttons1[1].setBackgroundColor(RED);
        buttons1[1].setOnClickListener(getOnClick());
        relativeLayout.addView(buttons1[1]);
        buttons1[2] = new Button(this);
        buttons1[2].setLayoutParams(new RelativeLayout.LayoutParams(200,200));
        buttons1[2].setId(3);
        buttons1[2].setY(100);
        buttons1[2].setText("LOAD");
        buttons1[2].setX(475);
        buttons1[2].setBackgroundColor(BLUE);
        buttons1[2].setOnClickListener(getOnClick());
        relativeLayout.addView(buttons1[2]);
        buttons1[3] = new Button(this);
        buttons1[3].setLayoutParams(new RelativeLayout.LayoutParams(200,200));
        buttons1[3].setId(4);
        buttons1[3].setY(100);
        buttons1[3].setText("RESET");
        buttons1[3].setX(700);
        buttons1[3].setBackgroundColor(CYAN);
        buttons1[3].setOnClickListener(getOnClick());
        relativeLayout.addView(buttons1[3]);
        setContentView(relativeLayout);
    }
    class Cell//INNER CLASS
    {
        private char charcell;//WHİCH ENUM HAS THIS CELL
        private int column;
        private int row;
        public Cell(){
            setcell('*');
        }//UCTER
        public int getColumn() 						{return column;}
        public int  getRow() 						{return row;}
        public char getCell() 						{return charcell;}
        public void setcell(char _charcell) 		{charcell=_charcell;}//ENUM
        public void setRow(int _row) 				{row=_row;}//ENUM
        public void setColumn(int _column) 			{column=_column;}//ENUM
    };
    private View.OnClickListener getOnClick(){
        return new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case 1:
                        undo_game();
                        break;
                    case 2:
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(Gamestart.this);
                        builder3.setTitle("HEXGAME");
                        builder3.setMessage("Please Enter File Name! Example:file.txt");
                        final EditText filename=new EditText(Gamestart.this);
                        filename.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder3.setView(filename);
                        builder3.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file_name=filename.getText().toString();
                                save_file();
                            }
                        });
                        builder3.show();
                        break;
                    case 3:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(Gamestart.this);
                        builder2.setTitle("HEXGAME");
                        builder2.setMessage("Please Enter File Name! Example:file.txt");
                        final EditText filename1=new EditText(Gamestart.this);
                        filename1.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder2.setView(filename1);
                        builder2.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file_name=filename1.getText().toString();
                                for (int k =0;k<size;k++){
                                    for(int l=0;l<size;l++){
                                        if(buttons[k][l].getId()==100+10*k+l){
                                            buttons[k][l].setVisibility(v.INVISIBLE);
                                            buttons[k][l]=null;
                                        }
                                    }
                                }
                                load_file();
                            }
                        });
                        builder2.show();
                        break;
                    case 4:
                        reset_game();
                        break;
                    default:
                        boolean end_game1=false,end_game2=false;
                        if(getControl1()=='u'){
                            if(check==false){
                                setCurrentPlayer('x');
                                for (int i=0;i<getSize();i++) {
                                    for (int j=0;j<getSize();j++) {
                                        if(v.getId()==buttons[i][j].getId()){
                                            Toast.makeText(getApplicationContext(),"x,y "+i+" "+j,Toast.LENGTH_SHORT).show();
                                            play(i,j);
                                        }
                                    }
                                }
                                end_game1=end_game_User1();
                                if(end_game1==true){
                                    Toast.makeText(getApplicationContext(),"USER1 İS WİN!!",Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Gamestart.this);
                                    builder1.setTitle("HEXGAME");
                                    builder1.setMessage("User1 is win restart again or check moves with dismiss");
                                    builder1.setNegativeButton("DISMISS", null);
                                    builder1.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reset_game();
                                        }
                                    });
                                    builder1.show();
                                }
                                if(controll==false) {
                                    check = true;
                                    check1 = false;
                                }
                            }
                            else if(check1==false){
                                setCurrentPlayer('o');
                                for (int i=0;i<getSize();i++) {
                                    for (int j=0;j<getSize();j++) {
                                        if(v.getId()==buttons[i][j].getId()){
                                            Toast.makeText(getApplicationContext(),"x,y "+i+" "+j,Toast.LENGTH_SHORT).show();
                                            play(i,j);
                                        }
                                    }
                                }
                                end_game2=end_game_User2();
                                if(end_game2==true){
                                    Toast.makeText(getApplicationContext(),"USER2 İS WİN!!",Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Gamestart.this);
                                    builder.setTitle("HEXGAME");
                                    builder.setMessage("User2 is win restart again or check moves with dismiss");
                                    builder.setNegativeButton("DISMISS", null);
                                    builder.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reset_game();
                                        }
                                    });
                                    builder.show();
                                }
                                if(controll1==false) {
                                    check1 = true;
                                    check = false;
                                }
                            }
                        }
                        else if(getControl1()=='c'){
                            setCurrentPlayer('x');
                            for (int i=0;i<getSize();i++) {
                                for (int j=0;j<getSize();j++) {
                                    if(v.getId()==buttons[i][j].getId()){
                                        Toast.makeText(getApplicationContext(),"x,y "+i+" "+j,Toast.LENGTH_SHORT).show();
                                        play(i,j);
                                    }
                                }
                                end_game1=end_game_User1();
                                if(end_game1==true){
                                    Toast.makeText(getApplicationContext(),"USER1 İS WİN!!",Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Gamestart.this);
                                    builder.setTitle("HEXGAME");
                                    builder.setMessage("User1 is win restart again or check moves with dismiss");
                                    builder.setNegativeButton("DISMISS", null);
                                    builder.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reset_game();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                            playAI();
                            end_game2=end_game_User2();
                            if(end_game2==true){
                                Toast.makeText(getApplicationContext(),"COMPUTER İS WİN!!",Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Gamestart.this);
                                    builder1.setTitle("HEXGAME");
                                    builder1.setMessage("COMPUTER is win restart again or check moves with dismiss");
                                    builder1.setNegativeButton("DISMISS", null);
                                    builder1.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reset_game();
                                        }
                                    });
                                    builder1.show();
                            }
                        }
                        break;
                }
            }
        };
    }
    public void play(int number1,int number2){
        if(getCurrentPlayer()=='x'){//'x'
            if(buttons[number1][number2].getText()=="*") {
                buttons[number1][number2].setText("x");
                buttons[number1][number2].setBackgroundColor(BLUE);
                moves[getlastpoint()][0] = number2;
                moves[getlastpoint()][1] = number1;
                lastpoint++;//counter for moves
                controll=false;
            }
            else{
                controll=true;
                Toast.makeText(getApplicationContext(),"This place is not empty!",Toast.LENGTH_SHORT).show();
            }
        }
        else if(getCurrentPlayer()=='o'){//'o'
            if(buttons[number1][number2].getText()=="*") {
                buttons[number1][number2].setBackgroundColor(RED);
                buttons[number1][number2].setText("o");
                moves[getlastpoint()][0] = number2;
                moves[getlastpoint()][1] = number1;
                lastpoint++;
                controll1=false;
            }
            else{
                controll1=true;
                Toast.makeText(getApplicationContext(),"This place is not empty!",Toast.LENGTH_SHORT).show();
            }
        }
    }//This function make move for both of 'x' and 'o'
    public void undo_game(){
        if(lastpoint!=0 && lastpoint!=1){
            buttons[moves[lastpoint - 1][1]][moves[lastpoint - 1][0]].setText("*");
            buttons[moves[lastpoint - 1][1]][moves[lastpoint - 1][0]].setBackgroundColor(LTGRAY);
            lastpoint--;
            buttons[moves[lastpoint - 1][1]][moves[lastpoint - 1][0]].setText("*");
            buttons[moves[lastpoint - 1][1]][moves[lastpoint - 1][0]].setBackgroundColor(LTGRAY);
            lastpoint--;
            for (int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    int color = ((ColorDrawable)buttons[i][j].getBackground()).getColor();
                    if(color==MAGENTA){
                        if(buttons[i][j].getText()=="O") {
                            buttons[i][j].setBackgroundColor(RED);
                            buttons[i][j].setText("o");
                        }
                        else if(buttons[i][j].getText()=="X"){
                            buttons[i][j].setBackgroundColor(BLUE);
                            buttons[i][j].setText("x");
                        }
                    }
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"There is no moves to make undo",Toast.LENGTH_SHORT).show();
        }
    }
    public void save_file(){
        int i,j;
        try{
            FileOutputStream save_load_file=openFileOutput(getFilename(),MODE_PRIVATE);
            OutputStreamWriter outputFile=new OutputStreamWriter(save_load_file);
            outputFile.write(getSize()+"\n");
            for(i=0;i<getSize();i++){
                for (j=0;j<getSize();j++) {
                    outputFile.write(buttons[i][j].getText()+"\n");
                }
                outputFile.write("\n");
            }
            if(getControl1()=='u'){
                outputFile.write("USERVSUSER\n");
            }
            else if(getControl1()=='c'){
                outputFile.write("USERVSCOMPUTER\n");
            }
            outputFile.write(getCurrentPlayer()+"\n");
            outputFile.write("---------------MOVES----------------\n");
            outputFile.write(lastpoint+"\n");
            for (int k = 0; k < getlastpoint(); ++k)
            {
                outputFile.write("(");
                outputFile.write(moves[k][0]+"");
                outputFile.write(",");
                outputFile.write(moves[k][1]+"");
                outputFile.write(")");
                outputFile.write("\n");
            }
            outputFile.flush();
            outputFile.close();
            Toast.makeText(getApplicationContext(),"Successfully Saved!",Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void is_valid(){
        boolean valid=false;
        while(valid==false){
            if(getSize()<6){
                Toast.makeText(getApplicationContext(),"Please enter the board size ,the size can be bigger than 5*5 ",Toast.LENGTH_SHORT).show();
                setSize(6);
            }
            valid=true;
        }
    }
    public void boardArray(){
        hexCells=new char[size][size];
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                CharSequence text = buttons[row][col].getText();
                if ("*".equals(text)) {
                    hexCells[row][col] = '.';
                } else if ("x".equals(text)) {
                    hexCells[row][col] = 'o';
                } else if ("o".equals(text)) {
                    hexCells[row][col] = 'x';
                }
            }
        }
    }
    public void load_file(){
        File file=getApplicationContext().getFileStreamPath(getFilename());
        char data=' ';
        char free;
        boolean end_game2=false;
        boolean end_game1=false;
        int control=0,control1=0,lastp;
        int num1,num2;
        int i=0,j=0;
        int size2;
        char currentplayer=' ';
        int temp,width=0;
        String line,str=" ",size1;
        try {
            BufferedReader myReader =new BufferedReader(new InputStreamReader(openFileInput(getFilename())));
            while (true){//to reach end of file
                if (control1 == 0) {
                    for (int k=0;k<size*size;k++){
                        moves[k][0]=0;
                        moves[k][1]=0;
                    }
                    size1 = myReader.readLine();
                    size2 = Integer.parseInt(size1);
                    setSize(size2);
                    buttons = new Button[size][size];
                    int buttonwidth = 2 * maxwidth / (3 * size - 1);
                    int y = (maxlength - maxwidth) / 2;
                    for (int l = 0; l < size; l++) {
                        temp = width;
                        for (int k = 0; k < size; k++) {
                            buttons[l][k] = new Button(this);
                            buttons[l][k].setLayoutParams(new RelativeLayout.LayoutParams(buttonwidth - 5, buttonwidth - 5));
                            buttons[l][k].setX(width);
                            buttons[l][k].setY(y);
                            buttons[l][k].setText("*");
                            buttons[l][k].setId(100 + 10 * l + k);
                            buttons[l][k].setBackgroundColor(LTGRAY);
                            buttons[l][k].setOnClickListener(getOnClick());
                            width += buttonwidth;
                            relativeLayout.addView(buttons[l][k]);
                        }
                        y += buttonwidth;
                        width = temp + buttonwidth / 2;
                    }
                    temp_arrey = new Cell[getSize()][getSize()];
                    for (int k = 0; k < getSize(); k++) {
                        for (int l = 0; l < getSize(); l++) {
                            temp_arrey[k][l] = new Cell();
                        }
                    }
                    control1++;
                }
                    line = myReader.readLine();//we are reading the data from file
                    data = line.charAt(0);
                    if(String.valueOf(data)=="o" || String.valueOf(data)=="x" ) {
                        buttons[i][j].setText(String.valueOf(data));
                    }
                    if (data == 'x' || data=='X') {
                        buttons[i][j].setBackgroundColor(BLUE);
                        buttons[i][j].setText("x");
                    } else if (data == 'o' || data=='O') {
                        buttons[i][j].setBackgroundColor(RED);
                        buttons[i][j].setText("o");
                    }
                    if (j == getSize() - 1 && i == getSize() - 1) {
                        line = myReader.readLine();
                        str = myReader.readLine();
                        line=myReader.readLine();
                        Toast.makeText(getApplicationContext(),""+str,Toast.LENGTH_SHORT).show();
                        line = myReader.readLine();
                        //Toast.makeText(getApplicationContext(),""+line,Toast.LENGTH_SHORT).show();
                        currentplayer = line.charAt(0);
                        line = myReader.readLine();
                        lastp =Integer.parseInt(line);
                        lastpoint = lastp;
                        for (int m = 0; m < lastpoint; ++m) {
                            line = myReader.readLine();
                            free= line.charAt(1);
                            num1=Character.getNumericValue(free);
                            free= line.charAt(3);
                            num2=Character.getNumericValue(free);
                            moves[m][0] = num1;
                            moves[m][1] = num2;
                        }
                        break;
                    } else if (j == getSize() - 1) {
                        line = myReader.readLine();
                        i++;
                        j = 0;
                    } else {
                        j++;
                    }
            }
            control = 1;
                if (str.equals("USERVSUSER")) {
                    setControl1('u');
                } else if (str.equals("USERVSCOMPUTER")) {
                    setControl1('c');
                }
            myReader.close();
            end_game1=end_game_User1();
            if(end_game1==true){
                Toast.makeText(getApplicationContext(),"USER1 İS WİN!!",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Gamestart.this);
                builder1.setTitle("HEXGAME");
                builder1.setMessage("User1 is win restart again or check moves with dismiss");
                builder1.setNegativeButton("DISMISS", null);
                builder1.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset_game();
                    }
                });
                builder1.show();
            }
            end_game2=end_game_User2();
            if(end_game2==true){
                Toast.makeText(getApplicationContext(),"USER2 İS WİN!!",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(Gamestart.this);
                builder.setTitle("HEXGAME");
                builder.setMessage("User2 is win restart again or check moves with dismiss");
                builder.setNegativeButton("DISMISS", null);
                builder.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset_game();
                    }
                });
                builder.show();
            }
        }
        catch (IOException e){
            Toast.makeText(getApplicationContext(),"FILE DOES NOT EXIST!",Toast.LENGTH_SHORT).show();
        }
    }
    public void reset_game(){
        lastp_x=0;lastp_y=0;
        tempi=0;tempj=0;
        counter=0;
        currentPlayer='x';
        counter1=0;
        fullcell=0;
        lastpoint=0;
        Score=0;
        count=0;
        for (int i = 0; i < getlastpoint(); i++)
        {
            moves[i][0]=0;
            moves[i][1]=0;
        }
        for (int i = 0; i < getSize(); ++i)
        {
            for (int j = 0; j < getSize(); ++j)
            {
                buttons[i][j].setText("*");
                buttons[i][j].setBackgroundColor(LTGRAY);
            }
        }
        for (int i = 0; i < getSize(); ++i)
        {
            for (int j = 0; j < getSize(); ++j)
            {
                temp_arrey[i][j].setcell('*');
            }
        }
    }
    public void iterate_func1(){
        int i,j;
        for(i=0;i<getSize();i++){
            for(j=0;j<getSize();j++){
                temp_arrey[i][j].setcell('*');//TO CONTROL IN CHECK FUNCTION NOT GO BACK WAS FILLED  BEFORE
            }
        }
    }//FOR TEMP_ARREY VECTOR
    public boolean end_game_User2(){
        int i=0,j=0;
        boolean control=false;
        for(j=0;j<getSize();j++){
            if(buttons[i][j].getText()=="o"){//we find first point and starting the recursion with finding o words
                iterate_func1();
                setTempi(i);
                setTempj(j);
                control=control_moves_o();
                if(control){
                    up_words_o();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean end_game_User1(){
        int i,j=0;
        boolean control=false;
        for(i=0;i<getSize();i++){
            if(buttons[i][j].getText()=="x"){//we find first point and starting the recursion with finding x words
                iterate_func1();// temp_arrey is create for control the not returning to passing point with recursion
                setTempi(i);
                setTempj(j);
                control=control_moves_x();//lastp_x and lastp_y created for computer game
                if(control){//if game end the connected words is up
                    up_words_x();
                    return true;// to end the game
                }
            }
        }
        return false;
    }

   public boolean control_moves_x(){
		if(getTempj()+1==size){
			setLastp_x(getTempi()+1);//we keep the last x point to end game
			setLastp_y(getTempj());
			return true;
		}
		if(getTempi()!=0){
			if(buttons[getTempi()-1][getTempj()].getText()=="x"){//kuzey
				if(temp_arrey[getTempi()-1][getTempj()].getCell()!='x'){//to avoid of going back to and avoid infinite loop
					temp_arrey[getTempi()-1][getTempj()].setcell('x');
					setTempi(getTempi()-1);
					return control_moves_x(); //recursive to around existing x values
				}
			}
		}
		if(getTempi()!=size-1){
			if(buttons[getTempi()+1][getTempj()].getText()=="x"){//güney
				if(temp_arrey[getTempi()+1][getTempj()].getCell()!='x'){
					temp_arrey[getTempi()+1][getTempj()].setcell('x');
					setTempi(getTempi()+1);
					return control_moves_x();
				}
			}
		}
		if(getTempi()!=0 && getTempj()!=size-1){
			if(buttons[getTempi()-1][getTempj()+1].getText()=="x"){//kuzeydoğu
				if(temp_arrey[getTempi()-1][getTempj()+1].getCell()!='x'){
					temp_arrey[getTempi()-1][getTempj()+1].setcell('x');
					setTempi(getTempi()-1);
					setTempj(getTempj()+1);
					return control_moves_x();
				}
			}
		}
		if(getTempj()!=size-1){
			if(buttons[getTempi()][getTempj()+1].getText()=="x"){//doğu
				if(temp_arrey[getTempi()][getTempj()+1].getCell()!='x'){
					temp_arrey[getTempi()][getTempj()+1].setcell('x');
					setTempj(getTempj()+1);
					return control_moves_x();
				}
			}
		}
		if(getTempj()!=0){
			if(buttons[getTempi()][getTempj()-1].getText()=="x"){//batı
				if(temp_arrey[getTempi()][getTempj()-1].getCell()!='x'){
					temp_arrey[getTempi()][getTempj()-1].setcell('x');
					setTempj(getTempj()-1);
					return control_moves_x();
				}
			}
		}
		if(getTempi()!=size-1 && getTempj()!=0){
			if(buttons[getTempi()+1][getTempj()-1].getText()=="x"){//güneybatı
				if(temp_arrey[getTempi()+1][getTempj()-1].getCell()!='x'){
					temp_arrey[getTempi()+1][getTempj()-1].setcell('x');
					setTempi(getTempi()+1);
					setTempj(getTempj()-1);
					return control_moves_x();
				}
			}
		}
		return false;
	}
    public boolean control_moves_o(){
        if(getTempi()+1==size){
            setLastp_x(getTempi()+1);//we keep the last O point to end game
            setLastp_y(getTempj());
            return true;
        }
        if(getTempi()!=0){
            if(buttons[getTempi()-1][getTempj()].getText()=="o"){//kuzey
                if(temp_arrey[getTempi()-1][getTempj()].getCell()!='o'){//to avoid of going back to and avoid infinite loop
                    temp_arrey[getTempi()-1][getTempj()].setcell('o');
                    setTempi(getTempi()-1);
                    return control_moves_o(); //recursive to around existing x values
                }
            }
        }
        if(getTempi()!=size-1){
            if(buttons[getTempi()+1][getTempj()].getText()=="o"){//güney
                setTempi(getTempi()+1);
                return control_moves_o();
            }
        }
        if(getTempi()!=0 && getTempj()!=size-1){
            if(buttons[getTempi()-1][getTempj()+1].getText()=="o"){//kuzeydoğu
                if(temp_arrey[getTempi()-1][getTempj()+1].getCell()!='o'){
                    temp_arrey[getTempi()-1][getTempj()+1].setcell('o');
                    setTempi(getTempi()-1);
                    setTempj(getTempj()+1);
                    return control_moves_o();
                }
            }
        }
        if(getTempj()!=size-1){
            if(buttons[getTempi()][getTempj()+1].getText()=="o"){//doğu
                if(temp_arrey[getTempi()][getTempj()+1].getCell()!='o'){
                    temp_arrey[getTempi()][getTempj()+1].setcell('o');
                    setTempj(getTempj()+1);
                    return control_moves_o();
                }
            }
        }
        if(getTempj()!=0){
            if(buttons[getTempi()][getTempj()-1].getText()=="o"){//batı
                if(temp_arrey[getTempi()][getTempj()-1].getCell()!='o'){
                    temp_arrey[getTempi()][getTempj()-1].setcell('o');
                    setTempj(getTempj()-1);
                    return control_moves_o();
                }
            }
        }
        if(getTempi()!=size-1 && getTempj()!=0){
            if(buttons[getTempi()+1][getTempj()-1].getText()=="o"){//güneybatı
                if(temp_arrey[getTempi()+1][getTempj()-1].getCell()!='o'){
                    temp_arrey[getTempi()+1][getTempj()-1].setcell('o');
                    setTempi(getTempi()+1);
                    setTempj(getTempj()-1);
                    return control_moves_o();
                }
            }
        }
        return false;
    }
    public void up_words_o(){
        if(getLastp_x()!=0){
            if(buttons[getLastp_x()-1][getLastp_y()].getText()=="o"){//kuzey
                if(buttons[getLastp_x()-1][getLastp_y()].getText()!="O"){//to avoid the going back and to avoid infinite loop
                    buttons[getLastp_x()-1][getLastp_y()].setText("O");
                    buttons[getLastp_x()-1][getLastp_y()].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    up_words_o();//recursive to around existing x values
                }												//this function will end if the all connected x values are up
            }
        }
        if(getLastp_x()!=0 && getLastp_y()!=size-1){
            if(buttons[getLastp_x()-1][getLastp_y()+1].getText()=="o"){//kuzeydoğu
                if(buttons[getLastp_x()-1][getLastp_y()+1].getText()!="O"){
                    buttons[getLastp_x()-1][getLastp_y()+1].setText("O");
                    buttons[getLastp_x()-1][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    setLastp_y(getLastp_y()+1);
                    up_words_o();
                }
            }
        }
        if(getLastp_y()!=0){
            if(buttons[getLastp_x()][getLastp_y()-1].getText()=="o"){//batı
                if(buttons[getLastp_x()][getLastp_y()-1].getText()!="O"){
                    buttons[getLastp_x()][getLastp_y()-1].setText("O");
                    buttons[getLastp_x()][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_y(getLastp_y()-1);
                    up_words_o();
                }
            }
        }
        if(getLastp_y()!=size-1){
            if(buttons[getLastp_x()][getLastp_y()+1].getText()=="o"){//doğu
                if(buttons[getLastp_x()][getLastp_y()+1].getText()!="O"){
                    buttons[getLastp_x()][getLastp_y()+1].setText("O");
                    buttons[getLastp_x()][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_y(getLastp_y()+1);
                    up_words_o();
                }
            }
        }
        if(getLastp_x()!=size){
            if(buttons[getLastp_x()+1][getLastp_y()].getText()=="o"){//güney
                if(buttons[getLastp_x()+1][getLastp_y()].getText()!="O"){
                    buttons[getLastp_x()+1][getLastp_y()].setText("O");
                    buttons[getLastp_x()+1][getLastp_y()].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    up_words_o();
                }
            }
        }
        if(getLastp_x()!=size && getLastp_y()!=0){
            if(buttons[getLastp_x()+1][getLastp_y()-1].getText()=="o"){//güneybatı
                if(buttons[getLastp_x()+1][getLastp_y()-1].getText()!="O"){
                    buttons[getLastp_x()+1][getLastp_y()-1].setText("O");
                    buttons[getLastp_x()+1][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    setLastp_y(getLastp_y()-1);
                    up_words_o();
                }
            }
        }
        if(getLastp_x()!=0 && getLastp_y()!=0){
            if(buttons[getLastp_x()-1][getLastp_y()-1].getText()=="o"){//kuzeybatı
                if(buttons[getLastp_x()-1][getLastp_y()-1].getText()!="O"){
                    buttons[getLastp_x()-1][getLastp_y()-1].setText("O");
                    buttons[getLastp_x()-1][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    setLastp_y(getLastp_y()-1);
                    up_words_o();
                }
            }
        }
        if(getLastp_x()!=size && getLastp_y()!=size-1){
            if(buttons[getLastp_x()+1][getLastp_y()+1].getText()=="o"){//güneydoğu
                if(buttons[getLastp_x()+1][getLastp_y()+1].getText()!="O"){
                    buttons[getLastp_x()+1][getLastp_y()+1].setText("O");
                    buttons[getLastp_x()+1][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    setLastp_y(getLastp_y()+1);
                    up_words_o();
                }
            }
        }
    }
    public void up_words_x(){
        Toast.makeText(getApplicationContext(),"lastpx"+getLastp_x()+getLastp_y(),Toast.LENGTH_SHORT).show();
        if(getLastp_x()!=0){
            if(buttons[getLastp_x()-1][getLastp_y()].getText()=="x"){//kuzey
                if(buttons[getLastp_x()-1][getLastp_y()].getText()!="X"){//to avoid the going back and to avoid infinite loop
                    buttons[getLastp_x()-1][getLastp_y()].setText("X");
                    buttons[getLastp_x()-1][getLastp_y()].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    up_words_x();//recursive to around existing x values
                }												//this function will end if the all connected x values are up
            }
        }
        if(getLastp_x()!=0 && getLastp_y()!=size-1){
            if(buttons[getLastp_x()-1][getLastp_y()+1].getText()=="x"){//kuzeydoğu
                if(buttons[getLastp_x()-1][getLastp_y()+1].getText()!="X"){
                    buttons[getLastp_x()-1][getLastp_y()+1].setText("X");
                    buttons[getLastp_x()-1][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    setLastp_y(getLastp_y()+1);
                    up_words_x();
                }
            }
        }
        if(getLastp_y()!=0){
            if(buttons[getLastp_x()][getLastp_y()-1].getText()=="x"){//batı
                //Toast.makeText(getApplicationContext(),"BATI"+getLastp_y(),Toast.LENGTH_SHORT).show();
                if(buttons[getLastp_x()][getLastp_y()-1].getText()!="X"){
                    //Toast.makeText(getApplicationContext(),"BATIB"+getLastp_y(),Toast.LENGTH_SHORT).show();
                    buttons[getLastp_x()][getLastp_y()-1].setText("X");
                    buttons[getLastp_x()][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_y(getLastp_y()-1);
                    up_words_x();
                }
            }
        }
        if(getLastp_y()!=size){
            Toast.makeText(getApplicationContext(),"DOĞU"+getLastp_y(),Toast.LENGTH_SHORT).show();
            if(buttons[getLastp_x()][getLastp_y()+1].getText()=="x"){//doğu
                Toast.makeText(getApplicationContext(),"DOĞU"+getLastp_y(),Toast.LENGTH_SHORT).show();
                if(buttons[getLastp_x()][getLastp_y()+1].getText()!="X"){
                    //Toast.makeText(getApplicationContext(),"DOĞUB"+getLastp_y(),Toast.LENGTH_SHORT).show();
                    buttons[getLastp_x()][getLastp_y()+1].setText("X");
                    buttons[getLastp_x()][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_y(getLastp_y()+1);
                    up_words_x();
                }
            }
        }
        if(getLastp_x()!=size-1){
            if(buttons[getLastp_x()+1][getLastp_y()].getText()=="x"){//güney
                if(buttons[getLastp_x()+1][getLastp_y()].getText()!="X"){
                    buttons[getLastp_x()+1][getLastp_y()].setText("X");
                    buttons[getLastp_x()+1][getLastp_y()].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    up_words_x();
                }
            }
        }
        if(getLastp_x()!=size-1 && getLastp_y()!=0){
            if(buttons[getLastp_x()+1][getLastp_y()-1].getText()=="x"){//güneybatı
                if(buttons[getLastp_x()+1][getLastp_y()-1].getText()!="X"){
                    buttons[getLastp_x()+1][getLastp_y()-1].setText("X");
                    buttons[getLastp_x()+1][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    setLastp_y(getLastp_y()-1);
                    up_words_x();
                }
            }
        }
        if(getLastp_x()!=0 && getLastp_y()!=0){
            if(buttons[getLastp_x()-1][getLastp_y()-1].getText()=="x"){//kuzeybatı
                if(buttons[getLastp_x()-1][getLastp_y()-1].getText()!="X"){
                    buttons[getLastp_x()-1][getLastp_y()-1].setText("X");
                    buttons[getLastp_x()-1][getLastp_y()-1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()-1);
                    setLastp_y(getLastp_y()-1);
                    up_words_x();
                }
            }
        }
        if(getLastp_x()!=size-1 && getLastp_y()!=size-1){
            if(buttons[getLastp_x()+1][getLastp_y()+1].getText()=="x"){//güneydoğu
                if(buttons[getLastp_x()+1][getLastp_y()+1].getText()!="X"){
                    buttons[getLastp_x()+1][getLastp_y()+1].setText("X");
                    buttons[getLastp_x()+1][getLastp_y()+1].setBackgroundColor(MAGENTA);
                    setLastp_x(getLastp_x()+1);
                    setLastp_y(getLastp_y()+1);
                    up_words_x();
                }
            }
        }
    }
    private void playAI(){
        boardArray();
        int [] move = BestMove(MaximizingPlayer);
        hexCells[move[0]][move[1]]='x';
        buttons[move[0]][move[1]].setBackgroundColor(RED);
        buttons[move[0]][move[1]].setText("o");
        moves[lastpoint][1]=move[0];
        moves[lastpoint][0]=move[1];
        lastpoint++;
    }
    private int [] BestMove(int player){
        int minval = Integer.MAX_VALUE; // INF
        int maxval = Integer.MIN_VALUE; // -INF

        int[] bestMove = new int[2];
        int i, j;
        int moveVal;
        bestMove[0] = -1;
        bestMove[1] = -1;
        if (player == MaximizingPlayer) {
            for (i = 0; i < size; i++)
                for (j = 0; j < size; j++) {
                    if (hexCells[i][j] == '.') {
                        hexCells[i][j] = 'x'; // it was 'w'
                        //moveVal = minimax(level, false);
                        moveVal=minimax(level,maxval,minval,false);
                        hexCells[i][j] = '.';
                        if (moveVal > maxval) {
                            bestMove[0] = i;
                            bestMove[1] = j;
                            maxval = moveVal;
                        }
                    }
                }
        }
        else {                             /* player == MinimizingPlayer */
            for (i = 0; i < size; i++)
                for (j = 0; j < size; j++) {
                    if ( hexCells[i][j] == '.') {
                        hexCells[i][j] = 'o'; // it was 'b'
                        //moveVal = minimax(level, true);
                        moveVal=minimax(level,minval,maxval,true);
                        hexCells[i][j] = '.';
                        if (moveVal < minval) {
                            bestMove[0] = i;
                            bestMove[1] = j;
                            minval = moveVal;
                        }
                    }
                }
        }

        return bestMove;
    }
    int minimax(int depth, int alpha,int beta,boolean maxTurn) {
        if (depth == 0 || winnerFound()!=0)
            return staticEvaluation((maxTurn) ? 1: 0);

        if (BoardNotFull() == 0)
            return 0;
        int i,j;
        int [] temp;
        if (maxTurn) {
            int maxEval = -Integer.MAX_VALUE;
            for (int x = 0; x < size; x++){
                for (int y = 0; y < size; y++){
                    if (hexCells[x][y] == '.') {
                        hexCells[x][y] = 'x';
                        int eval =minimax(depth - 1,alpha,beta, false);
                        maxEval = maximum(maxEval, eval);
                        alpha = maximum(alpha,eval);
                        hexCells[x][y] = '.';
                        if(beta <= alpha)
                            break;
                    }
                }
                if(beta<=alpha)
                    break;
            }
            return maxEval;
        } else {/* minimizing player */
            int minEval = Integer.MAX_VALUE;
            for (int x = 0; x < size; x++){
                for (int y = 0; y < size; y++) {
                    if (hexCells[x][y] == '.') {
                        hexCells[x][y] = 'o';
                        int eval = minimax(depth - 1,alpha,beta, true);
                        minEval = minimum(minEval, eval);
                        beta = minimum(beta,eval);
                        hexCells[x][y] = '.';
                        if(beta<=alpha) break;
                    }

                }
                if(beta<=alpha) break;

            }
            return minEval;
        }
    }
    private int winnerFound(){
        int[] s = new int[1];
        transformBlackMatrix();                    /* update trB matrix */
        transformWhiteMatrix();                    /* update trW matrix */
        if (WhiteWon(size,trW,s)!=0)
            return MaximizingPlayer;

        if (BlackWon(size, trB, s)!=0)
            return MinimizingPlayer;

        return 0;
    }
    private void transformBlackMatrix(){ /* for Black */
        /* returns a matrix of integers where (1) is the source node */
        /* and the rest of the elements with 'b' are initialized with 3 */
        /* if the case is none of the above then A[i][j] = 0 */
        /* free the memory when check for winner is completed */
        int i, j;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (j == 0 && hexCells[i][j] == 'o')
                    trB[i][j] = 1;    /* source nodes are j = 0 with 'b' */
                else if (hexCells[i][j] == 'o')
                    trB[i][j] = 3;
                else
                    trB[i][j] = 0;
            }

    }
    void transformWhiteMatrix() /* for  White */
        /* input array B is the array of chars with 'b', 'w' and '.' */
        /* returns a matrix of integers where (1) is the source node */
        /* and the rest of the elements with 'w' are initialized with 3 */
        /* if the case is none of the above then A[i][j] = 0 */
        /* free the memory when check for winner is completed */
    { int i, j;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (i == 0 && hexCells[i][j] == 'x')
                    trW[i][j] = 1;    /* source nodes are i = 0 with 'w' */
                else if (hexCells[i][j] == 'x')
                    trW[i][j] = 3;
                else
                    trW[i][j] = 0;
            }

    }
    int WhiteWon(int n, int[][] matrix, int[] seq)
        /* returns 1 if path was found else reutns 0 */
        /* seq is the max sequence of white pawns */
    {
        int i, j;
        int flag = 0;
        int[][] visited= new int [n][n];          /* keep track of already visited indexes */


        for (i = 0; i < n; i++) /* initialize visited (array) with false */
            for (j = 0; j < n; j++)      /* since we don't know the paths */
                visited[i][j] = 0;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (matrix[i][j] == 1 && visited[i][j] == 0) /* if source cell */
                    if (PathSearchWhite(i, j, n, matrix, visited) == 1) {
                        /* search */
                        flag = 1;                          /* path was found */
                    }

            }

        int diff = maxi(visited, n) - mini(visited, n);
        if (diff <= 0)
            seq[0] = 0;
        else
            seq[0] = diff + 1;


        visited = null;

        if (flag == 1)
            return 1;
        else
            return 0;  /* path was not found */

    }
    int PathSearchWhite(int i, int j, int n, int[][] matrix, int[][] visited)
    {
        if (valid(i, j, n)==1 && matrix[i][j] != 0 && visited[i][j] == 0) {
            /* terminal cases */
            visited[i][j] = 1;                            /* cell visited */
            if (matrix[i][j] == 3 && i == n-1)          /* if cell is the */
                return 1;        /* destination(the other end) return true */

            // traverse north
            /* if path is found in this direction */
            if (PathSearchWhite(i-1, j, n, matrix, visited) == 1)
                return 1;                                   /* return true */

            // traverse west
            /* if path is found in this direction */
            if (PathSearchWhite(i, j-1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse south
            /* if path is found in this direction */
            if (PathSearchWhite(i+1, j, n, matrix, visited)== 1)
                return 1;                                  /* return true */

            // traverse east
            /* if path is found in this direction */
            if (PathSearchWhite(i, j+1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse northeast
            /* if path is found in this direction */
            if (PathSearchWhite(i-1, j+1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse southwest
            /* if path is found in this direction */
            if (PathSearchWhite(i+1, j-1, n, matrix, visited)== 1)
                return 1;                                  /* return true */


        }

        return 0;                                   /* no path was found */
    }
    int valid(int i, int j, int n)
    { if ((i >= 0) && (i < n) &&
            (j >= 0) && (j < n) )
        return 1;
    else
        return 0;
    }
    int maxi(int[][] visited, int n)
    { int i, j, max = -1;
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (visited[i][j] == 1)
                    max = i;

        return max;
    }
    int mini(int[][] visited, int n)
    { int i, j, min;
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (visited[i][j] == 1)
                    return min = i;

        return -1;

    }
    int BlackWon(int n, int[][] matrix, int[] seq)
        /* returns 1 if path was found else reutns 0 */
    { int i, j;
        int[][] visited;          /* keep track of already visited indexes */
        int flag = 0;
        visited = new int[n][n];

        for (i = 0; i < n; i++) /* initialize visited (array) with false */
            for (j = 0; j < n; j++)      /* since we don't know the paths */
                visited[i][j] = 0;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (matrix[i][j] == 1 && visited[i][j] == 0) /* if source cell */
                    if (PathSearchBlack(i, j, n, matrix, visited) == 1 ) {
                        flag = 1; /* path was found */
                    }

            }

        int diff = maxj(visited, n) - minj(visited, n);
        if (diff <= 0)
            seq[0] = 0;
        else
            seq[0] = diff + 1;


        visited = null;
        if (flag == 1)
            return 1;   /* path was found */
        else
            return 0;   /* path was not found */

    }
    int PathSearchBlack(int i, int j, int n, int[][] matrix, int[][] visited)
        /* searches for path from right to left */
    {
        if (valid(i, j, n)!=0 && matrix[i][j] != 0 && visited[i][j] == 0) {
            /* terminal cases */
            visited[i][j] = 1;                            /* cell visited */
            if (matrix[i][j] == 3 && j == n-1)          /* if cell is the */
                return 1;        /* destination(the other end) return true */

            // traverse north
            /* if path is found in this direction */
            if (PathSearchBlack(i-1, j, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse west
            /* if path is found in this direction */
            if (PathSearchBlack(i, j-1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse south
            /* if path is found in this direction */
            if (PathSearchBlack(i+1, j, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse east
            /* if path is found in this direction */
            if (PathSearchBlack(i, j+1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse northeast
            /* if path is found in this direction */
            if (PathSearchBlack(i-1, j+1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse southwest
            /* if path is found in this direction */
            if (PathSearchBlack(i+1, j-1, n, matrix, visited)==1)
                return 1;                                   /* return true */


        }

        return 0;                                   /* no path was found */
    }
    int maxj(int[][] visited, int n)
        /* returns the greater j co-ordinate where last 1 was found */
        /* in "visited"  array */
    { int i, j, max = -1;
        for (j = 0; j < n; j++)
            for (i = 0; i < n; i++)
                if (visited[i][j] == 1)
                    max = j;

        return max;
    }
    int minj(int[][] visited, int n)
    { int i, j, min;
        for (j = 0; j < n; j++)
            for (i = 0; i < n; i++)
                if (visited[i][j] == 1)
                    return min = j;

        return -1;


    }
    int staticEvaluation( int maxTurn)
        /* evaluates a board-state */
    { int winner = 0;
        int seqW = 0;
        int seqB = 0;
        int[] s = new int[1];
        int k;
        transformWhiteMatrix();
        transformBlackMatrix();

        for (k = 0; k < size; k++) {
            changeW(size, trW, k);
            if (WhiteWon(size, trW, s) != 0) {
                /* if the two sides are connected */
                if (k == 0) {
                    winner = MaximizingPlayer;
                    break; }

            }
            else                      /* if White in not the winner, then */
                seqW = maximum(seqW, s[0]);       /* save the maximum sequence */
        }




        for (k = 0; k < size; k++) {
            changeB(size, trB, k);
            if (BlackWon(size, trB, s) != 0) {
                /* if the two sides are connected */
                if (k == 0) {
                    winner = MinimizingPlayer;
                    break; }

            }
            else                      /* if Black in not the winner, then */
                seqB = maximum(seqB, s[0]);       /* save the maximum sequence */
        }



        int scoreW = seqW* size / 2;
        int scoreB = seqB* size / 2;



        if (winner == MaximizingPlayer)
            return 100;


        if (winner == MinimizingPlayer)
            return -100;

        if (maxTurn != 0)
            return scoreW - scoreB  + CenterVal( maxTurn) + DirVal( maxTurn)/2 +2* BridgeVal( maxTurn);
        else return scoreW - scoreB  - CenterVal(maxTurn) - DirVal( maxTurn)/2 - 2*BridgeVal( maxTurn);


    }
    int CenterVal( int maxTurn)
    { int i, j, counter = 0;
        int val = 0;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                if (hexCells[i][j] != '.')         /* number of filled cells */
                    counter++;

        if (counter < size/2 ) {
            for (i = size/2 - 1; i < size/2; i++)
                for (j = size/2 - 1; j < size/2; j++)
                    if (hexCells[i][j] == 'o' && maxTurn==0)
                        val++;
                    else if (hexCells[i][j] == 'x' && maxTurn!=0)
                        val++;

            return val;
        }
        else
            return 0;


    }

    void changeW(int n, int[][] matr, int k)                    /* White */
        /* updates transformed matrix with new sources for different k, */
        /* where k is a source row */
    { int i, j;
        if (k == 0)      /* when k = 0 we dont need to change the matrix */
            return;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (i == k && matr[i][j] == 3) /* if was (non-source) cell */
                    matr[i][j] = 1;                           /* new source */
                else if (matr[i][j] == 3)
                    continue;
                else {                            /* old sources must be 0 */
                    matr[i][j] = 0; }
            }
    }
    int maximum(int a, int b)
    { return (a >= b) ? a : b;
    }
    void changeB(int n, int[][] matr, int k)                    /* Black */
        /* updates transformed matrix with new sources for different k, */
        /* where k is a source column */
    { int i, j;

        if (k == 0)     /* when k = 0, we dont need to change the matrix */
            return;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (j == k && matr[i][j] == 3) /* if was (non-source) cell */
                    matr[i][j] = 1;                           /* new source */
                else if (matr[i][j] == 3)
                    continue;
                else {                            /* old sources must be 0 */
                    matr[i][j] = 0;  }
            }
    }
    int DirVal( int maxTurn)
    { int i, j, val = 0;
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (maxTurn!=0 && hexCells[i][j] == 'x') {
                    if (valid(i+1, j, size)==1 && valid(i-1, j, size)==1)
                        if (hexCells[i+1][j] == 'x' || hexCells[i-1][j] == 'x')
                            val++;
                }
                else if (maxTurn==0 && hexCells[i][j] == 'o') {
                    if (valid(i, j+1, size)!=0 && valid(i, j-1, size)!=0)
                        if (hexCells[i][j+1] == 'o' || hexCells[i][j-1] == 'o')
                            val++;
                }
            }
        }
        return val;
    }

    int BridgeVal( int maxTurn)
    { int i, j, bridge = 0;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (maxTurn==0 && hexCells[i][j] == 'o' && surrounded_ratio_b(i, j) >= 1) {
                    if (valid(i-1, j+2, size)==1 && hexCells[i-1][j+2] == 'o') bridge++;
                    if (valid(i+1, j-2, size)==1 && hexCells[i+1][j-2] == 'o') bridge++;
                    if (valid(i-1, j-1, size)==1 && hexCells[i-1][j-1] == 'o') bridge++;
                    if (valid(i+2, j-1, size)==1 && hexCells[i+2][j-1] == 'o') bridge++;
                    if (valid(i+1, j+1, size)==1 && hexCells[i+1][j+1] == 'o') bridge++;
                }
                else if (hexCells[i][j] == 'x' && surrounded_ratio_w(i, j) >= 2) {
                    if (valid(i-1, j+2, size)==1 && hexCells[i-1][j+2] == 'x') bridge++;
                    if (valid(i+1, j-2, size)==1 && hexCells[i+1][j-2] == 'x') bridge++;
                    if (valid(i-1, j-1, size)==1 && hexCells[i-1][j-1] == 'x') bridge++;
                    if (valid(i+2, j-1, size)==1 && hexCells[i+2][j-1] == 'x') bridge++;
                    if (valid(i+1, j+1, size)==1 && hexCells[i+1][j+1] == 'x') bridge++;
                }

            }

        return bridge;


    }
    int surrounded_ratio_b(int i, int j)
    { int r = 0;
        if (valid(i, j+1,size)==1 && hexCells[i][j+1] == 'x') r++;
        if (valid(i, j-1,size)==1 && hexCells[i][j-1] == 'x') r++;
        if (valid(i-1, j+1,size) == 1 && hexCells[i-1][j+1] == 'x') r++;
        return r;
    }
    int surrounded_ratio_w(int i, int j)
    { int r = 0;
        if (valid(i-1, j,size)==1 && hexCells[i-1][j] == 'o') r++;
        if (valid(i+1, j,size)==1 && hexCells[i+1][j] == 'o') r++;
        if (valid(i+1, j-1,size)==1 && hexCells[i+1][j-1] == 'o') r++;
        return r;
    }
    int BoardNotFull()
    { for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
            if (hexCells[i][j] == '.')
                return 1; /* there is at least one cell available */

        return 0; /* board is full */
    }
    int minimum(int a, int b)
    { return (a <= b) ? a : b;
    }
    private boolean controll1;
    private boolean controll;
    private int maxwidth;
    private int level;
    private int maxlength;
    private String file_name;
    private char control,control1;
    private int lastp_x,lastp_y;
    private int lastpoint;
    private int tempi,tempj;
    private int counter;
    private char currentPlayer;
    private int counter1;
    private boolean check=false;
    private boolean whofirst;
    private boolean check1=false;
    private boolean endgame;
    private int fullcell;//RETURN FILLED CELL SAVED MARKEDCOUNT
    private int Score;
    private int count;
    private void setSize(int _size)				{size=_size;}
    private void setControl(char _control)		{control=_control;}
    private void setControl1(char _control1)	{control1=_control1;}
    private void setFilename(String _file_name){file_name=_file_name;}
    private void setLastp_x(int _lastp_x)		{lastp_x=_lastp_x;}
    private void setLastp_y(int _lastp_y)		{lastp_y=_lastp_y;}
    private void setTempi(int _tempi)			{tempi=_tempi;}
    private void setTempj(int _tempj)			{tempj=_tempj;}
    private void setCounter(int _counter)		{counter=_counter;}
    private void setCounter1(int _counter1)		{counter1=_counter1;}
    private void setCurrentPlayer(char _currentPLayer)	{currentPlayer=_currentPLayer;}
    private int getSize()					{return size;}//THIS RETURNS THE SIZE OF BOARD
    private char getControl()				{return control;}
    private char getControl1()				{return control1;}
    private String getFilename()				{return file_name;}
    private int getLastp_x()					{return lastp_x;}//FOR CONTROLLİNG THE COMPUTER MOVE AND CHECK END GAME FUNCTİON
    private int getLastp_y()					{return lastp_y;}//FOR CONTROLLİNG THE COMPUTER MOVE AND CHECK END GAME FUNCTİON
    private int getlastpoint()				{return lastpoint;}//FOR CONTROLLİNG THE COMPUTER MOVE AND CHECK END GAME FUNCTİON
    private int getTempi()					{return tempi;}//I FIND FİRST X OR FİRST O ON BOARD TO CHECK END GAME FUNCTİON
    private int getTempj()					{return tempj;}
    private void setLastpoint(int _lastpoint)			{lastpoint=_lastpoint;}
    private int getCounter()					{return counter;}//TO CONTROL COMPUTER GAME WHİCH MOVE IT WILL MAKE
    private int getfullcell()				{return fullcell;}//WE SAVE THE FİLLED CELL OF ALL GAME
    private void setfullcell(int _fullcell)	{fullcell=_fullcell;}
    private boolean getEndgame()				{return endgame;}
    private int getCounter1()				{return counter1;}
    private char getCurrentPlayer()			{return currentPlayer;}//WE GET THE CURRENT PLAYER TO CHECK PLAY FUNCTİON WHİCH ONE MAKE MOVE
    private int getCountUser()				{return count;}
    private int getScore()					{return Score;}
    private int size;
    private boolean gametype;
}