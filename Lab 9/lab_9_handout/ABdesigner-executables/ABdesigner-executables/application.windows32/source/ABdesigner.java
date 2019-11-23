import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import javax.swing.JFileChooser; 
import javax.swing.filechooser.FileNameExtensionFilter; 
import com.esotericsoftware.kryo.*; 
import com.esotericsoftware.*; 
import com.esotericsoftware.minlog.*; 
import java.io.FileOutputStream; 
import java.io.FileInputStream; 
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy; 

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

public class ABdesigner extends PApplet {












ControlP5 cp5;
int v1;

// Constants
static int MODE_A = 0, MODE_B = 1;

// UI Elements
RadioButton abRadio;
Toggle setRegionButton;
Button loadButton;
Button saveButton;
Rectangle regionA;
Rectangle regionB;
Rectangle currentRegion;
Textfield secondsField;

int controlBarHeight = 60;

// STATE
int mode = MODE_A;
boolean drawing = false;
boolean update = false;

PImage imageA, imageB;
String imageApath, imageBpath;

Kryo kryo;

public void setup(){
  
  
  createUserFolder();
  
  kryo = new Kryo();
  kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

  //((DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
  
  cp5 = new ControlP5(this);
  initControlBar();
  regionA = new Rectangle(0,0,0,0);
  regionB = new Rectangle(0,0,0,0);
  currentRegion = regionA;
  
  loadSession();
}

public void initControlBar(){
  // arial font for big toggle buttons
  ControlFont cf1 = new ControlFont(createFont("Arial", 21));
  
  // create Toggle Buttons for A and B
  abRadio = cp5.addRadioButton("radioButton")
         .setPosition(10,10)
         .setSize(40,42)
         .setItemsPerRow(2)
         .setSpacingColumn(5)
         .addItem("A", MODE_A)
         .addItem("B", MODE_B)
         .setNoneSelectedAllowed(false)
         .setArrayValue(new float[]{1f, 0f});
  
  for(Toggle t : abRadio.getItems()) {       
   t.getCaptionLabel().setFont(cf1).getStyle().moveMargin(0,0,0,-30);
  }
  
  // add function buttons
  loadButton      = cp5.addButton("Load Image").setPosition(100, 10).setSize(60, 20);
  setRegionButton = cp5.addToggle("Set region").setPosition(100, 32).setSize(60, 20);
  setRegionButton.getCaptionLabel().getStyle().moveMargin(-18,0,0,8);
  
  saveButton = cp5.addButton("Save").setPosition(165, 10).setSize(40, 42);
  
  secondsField = cp5.addTextfield("Seconds")
   .setPosition(210, 10)
   .setSize(30, 30)
   .setText("5");
   
  //cp5.addTextfield("# Participants")
  // .setPosition(260, 10)
  // .setSize(30, 30)
  // .setText("28");
}

public void draw(){
  //noStroke();
  //rect(0,0,100,60);
  if (update || drawing){
    paintBackground();
    renderRegion(currentRegion);
  }
  
  update = false;
}

public void renderRegion(Rectangle r){
  /* Draws the rectangle */
  colorMode(HSB);
  fill(r.hue, r.s, r.b, 150);
  stroke(0);
  rect(r.x, r.y + controlBarHeight, r.w, r.h);
}

public void update(){
  update = true;
}

public void paintBackground(){
  PImage backImage = mode == MODE_A ? imageA : imageB;
  background(200);
  if (backImage != null)
      image(backImage, 0, 60);
}

// EVENT HANDLING

public void mousePressed(){
  if (drawing && mouseY > 60){
    currentRegion.x = mouseX;
    currentRegion.y = mouseY - controlBarHeight;
    currentRegion.w = 0;
    currentRegion.h = 0;
  }
}

public void mouseDragged(){
  if (drawing){
    currentRegion.w = mouseX-currentRegion.x;
    currentRegion.h = mouseY-currentRegion.y-controlBarHeight;
  }
}

public void controlEvent(ControlEvent evt){
  if (evt.isFrom(abRadio))
    abHandler(evt);
  else if (evt.isFrom(loadButton))
    loadHandler(evt);  
  else if (evt.isFrom(setRegionButton))
    setRegionHandler(evt);
  else if (evt.isFrom(saveButton))
    saveButtonHandler(evt);
    
  println(evt.getName());
}

public void saveButtonHandler(ControlEvent evt){
  saveSession();
}

public void setRegionHandler(ControlEvent evt){
  if (((Toggle)evt.getController()).getState()){
    drawing = true;
    cursor(CROSS);
  } else {
    drawing = false;
    cursor(ARROW);
  }
}

public void loadHandler(ControlEvent evt){
  selectInput("Select an image:", "loadImage");
}

public void resizeImage(PImage im){
  // resize the image to fit screen
  float ratio = (float)im.width/im.height;
  int newWidth, newHeight;
  if (ratio > 1){
    println(ratio);
    println(im.width);
    println(im.height);
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

public void loadImage(File f){
  if (f == null) return;
  String path = f.getAbsolutePath();
  
  PImage im = getImage(path);
  if (mode == MODE_A){ 
    imageA = im;
    imageApath = path;
  }
  else {
    imageB = im;
    imageBpath = path;
  }
  
  update();
}

public void abHandler(ControlEvent evt){
  setRegionButton.setState(false);
  mode = (int)evt.getGroup().getValue();
  if (mode == MODE_A){
    currentRegion = regionA;
  } else {
    currentRegion = regionB;
  }
  update();
}

public String getHomeFolderPath(){
  return System.getProperty("user.home") + File.separator + "ABtester";
}

public void createUserFolder(){
  File homeDir = new File(getHomeFolderPath());
  if (!homeDir.exists())
    homeDir.mkdirs();
}

public void saveSession(){
  int seconds = 5;
  try{
    seconds = Integer.parseInt(secondsField.getText());
  } catch(Exception e){
    e.printStackTrace();
  }
  Session session = new Session(imageApath, imageBpath, regionA, regionB, seconds);
  try {
   String path = getHomeFolderPath() + File.separator + "session.bin";
   Output output = new Output(new FileOutputStream(path));
   kryo.writeClassAndObject(output, session);
   output.close(); 
  } catch (Exception e){
    e.printStackTrace();
  }
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
   secondsField.setText(session.seconds + "");
   
   currentRegion = regionA;
   update();
  } catch (Exception e){
    e.printStackTrace();
  }  catch(Error e){
    e.printStackTrace();
  }
}
  public void settings() {  size(1084, 768); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ABdesigner" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
