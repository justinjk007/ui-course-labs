import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import javax.swing.JFileChooser; 
import javax.swing.filechooser.FileNameExtensionFilter; 
import com.esotericsoftware.kryo.*; 
import com.esotericsoftware.minlog.*; 
import java.io.FileOutputStream; 
import java.io.FileInputStream; 
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy; 
import java.io.FileWriter; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Locale; 

import com.esotericsoftware.kryo.*; 
import com.esotericsoftware.kryo.factories.*; 
import com.esotericsoftware.kryo.io.*; 
import com.esotericsoftware.kryo.pool.*; 
import com.esotericsoftware.kryo.serializers.*; 
import com.esotericsoftware.kryo.util.*; 
import com.esotericsoftware.minlog.*; 
import org.objenesis.instantiator.android.*; 
import org.objenesis.instantiator.basic.*; 
import org.objenesis.instantiator.gcj.*; 
import org.objenesis.instantiator.jrockit.*; 
import org.objenesis.instantiator.*; 
import org.objenesis.instantiator.perc.*; 
import org.objenesis.instantiator.sun.*; 
import org.objenesis.*; 
import org.objenesis.strategy.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ABtester extends PApplet {













// Constants
static int MODE_A = 0, MODE_B = 1;

Kryo kryo;

PImage imageA, imageB;
String imageApath, imageBpath;

Rectangle regionA, regionB, currentRegion;

int mode = MODE_A;

boolean testing = false;

PFont coverFont;

long sMoment = 0,
     interval= 5000;
     
FileWriter outputWriter;

SimpleDateFormat dateFormatter;

public void setup(){
  
  createUserFolder();
  
  kryo = new Kryo();
  kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
  //((DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
  
  loadSession();
  
  coverFont = createFont("Arial", 40, true);
  drawCoverScreen();
  
  try{
    outputWriter = new FileWriter(getResultFile(), true);
  } catch(Exception e){
    e.printStackTrace();
  }
  dateFormatter = new SimpleDateFormat("h:mm:ss", new Locale("en", "CA"));
}

public void draw(){
  if (testing){
    if (millis() - sMoment >= interval){
      finishTrial();
      writeResult(mode, false, interval);
    };
  }
}

public void finishTrial(){
  drawCoverScreen();
  testing = false;
}

public void drawCoverScreen(){
  background(200);
  textFont(coverFont);
  textAlign(CENTER, CENTER);
  fill(0);
  text("Press Enter to Start", width/2, height/2);
}

public void keyPressed(){
  if (testing) return;
  
  if (key != ENTER) return;
  
  double r = Math.random();
  
  if (r < 0.5f){    
    mode = MODE_A;
    background(200);
    image(imageA, 0, 0); 
    testing = true;  
    sMoment = millis();
  } else {
    mode = MODE_B;
    background(200);
    image(imageB, 0, 0);
    testing = true;
    sMoment = millis();
  }
}

public void mouseClicked(){
  if (!testing) return;
  
  if (mode == MODE_A && regionA.isInside(mouseX, mouseY) ||
   mode == MODE_B && regionB.isInside(mouseX, mouseY) ){
    
    finishTrial();
    writeResult(mode, true, millis()-sMoment);
   } else {
     finishTrial();
     writeResult(mode, false, millis()-sMoment);
   }

}

public void writeResult(int mode, boolean success, long time){
  String caso = mode == 0 ? "A" : "B";
  String date = dateFormatter.format(new Date(System.currentTimeMillis()));
  String row = String.format("%s,%s,%d,%s\n", caso, success, time, date);
  try {
    outputWriter.write(row);
    outputWriter.flush();
  } catch(Exception e){
    e.printStackTrace();
  }
}

public File getResultFile(){
  String path = getHomeFolderPath() + File.separator + "results.csv";
  File f = new File(path);
  return f;
}

public String getHomeFolderPath(){
  return System.getProperty("user.home") + File.separator + "ABtester";
}

public void createUserFolder(){
  File homeDir = new File(getHomeFolderPath());
  if (!homeDir.exists())
    homeDir.mkdirs();
}

public void resizeImage(PImage im){
  // resize the image to fit screen
  float ratio = (float)im.width/im.height;
  int newWidth, newHeight;
  if (ratio > 1){
    newWidth = width;
    newHeight= (int)(newWidth/ratio);
  } else {
    newHeight = height - 60;
    newWidth = (int)(newHeight*ratio);
  }
  im.resize(newWidth, newHeight);
}

public PImage getImage(String path){ 
  PImage im = loadImage(path);
  
  if (im.width > width || im.height > height - 60)
    resizeImage(im);
  
  return im;
}

public void loadSession(){
  try {
   String path = getHomeFolderPath() + File.separator + "session.bin";
   Input input = new Input(new FileInputStream(path));
   Session session = (Session)kryo.readClassAndObject(input);
   input.close();

   imageApath = session.imageApath;
   imageBpath = session.imageBpath;
   imageA = getImage(imageApath);
   imageB = getImage(imageBpath);
   regionA = session.regionA;
   regionB = session.regionB;
   
   interval = session.seconds * 1000;
   
   currentRegion = regionA;
   
  } catch (Exception e){
    e.printStackTrace();
  }  catch(Error e){
    e.printStackTrace();
  }
}
  public void settings() {  size(1084, 768); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ABtester" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
