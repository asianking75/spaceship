
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    Image starImage;
    int rocketXPos;
    int rocketYPos;
    int rocketXSpeed;
    int rocketYSpeed;
    int rocketHealth;
    int score;
    boolean rocketVisible;
    int numStar = 5;
    int starXPos[] = new int[numStar];
    int starYPos[] = new int[numStar];
    boolean starVisible[] = new boolean[numStar];
    int rocketDir;
    int starhit;
    boolean rocketRight;
    boolean playinitialhit;
    boolean gameOver;
    boolean bombVisable;
    double grow1;
    double grow2;
    boolean Explode;
    
    
    
    int currentLazer;
    int numLazer = 20;
    Lazer lazerArray[] = new Lazer[numLazer];

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();
                    lazerArray[currentLazer].isVisible=true;
                    lazerArray[currentLazer].xPos= rocketXPos;
                    lazerArray[currentLazer].yPos= rocketYPos;
                    
                    if (rocketRight)
                    lazerArray[currentLazer].dir=1;
                    else
                    lazerArray[currentLazer].dir=-1;
                    
                        currentLazer++;
                   
                    
                    if (currentLazer >= numLazer)
                        currentLazer = 0;
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) 
                {
                    rocketYSpeed++;
                } 
                else if (e.VK_DOWN == e.getKeyCode()) 
                {
                    rocketYSpeed--;
                } 
                else if (e.VK_LEFT == e.getKeyCode()) 
                {
                    rocketXSpeed--;
                     
                } 
                else if (e.VK_RIGHT == e.getKeyCode()) 
                {
                    
                    rocketXSpeed++;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                    bombVisable=true;
                    Explode=true;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.black);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.black);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
// draw background////////////////////////////////////////////////////////////////////////////////////////
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
//////////////////////////////////////////////////////////////////////////////////////////////
/////draw Star/////////////////////////////////////////////////////////////////////////////////////
            for (int index=0;index<numStar;index++)
            {
                if (starVisible[index])
                {
                    drawStar(starImage,getX(starXPos[index]),getYNormal(starYPos[index]),0,1,1);
                }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////          
//////draw lazer/////////////////////////////////////////////////////////////////////////////////////////////            
       
            for (int index=0;index<numLazer;index++)
            {
                if (lazerArray[index].isVisible)
                {
                    g.setColor(Color.black);
                    drawlazer(getX(lazerArray[index].xPos),getYNormal(lazerArray[index].yPos),0,lazerArray[index].dir,1);
                }
            }
        
        
//////////////////////////////////////////////////////////////////////////////////////////////////
/////////draw Bomb//////////////////////////////////////////////////////////////////////////////
            if (bombVisable)
              {
                    g.setColor(Color.yellow);
                    drawBomb(getX(rocketXPos),getYNormal(rocketYPos),0.0,grow1,grow2 );
              }
//////////////////////////////////////////////////////////////////////////////////////////////////
/////////draw rocket//////////////////////////////////////////////////////////////////////////////
       if (rocketVisible)
               {
        if (rocketRight)
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        else
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1,1.0 );
               }
//////////////////////////////////////////////////////////////////////////////////////////////////

//////draw score//////////////////////////////////////////////////////////////////////////////////
       g.setColor (Color. white);
        g.setFont ( new Font ("Impact", Font.PLAIN,12));
        g.drawString ("Score : " + score ,getX(0) ,getY(20));
//////////////////////////////////////////////////////////////////////////////////////////////////
//////draw Health//////////////////////////////////////////////////////////////////////////////////
       g.setColor (Color. white);
        g.setFont ( new Font ("Impact", Font.PLAIN,12));
        g.drawString ("Health : " + rocketHealth ,getX(0) ,getY(40));
//////////////////////////////////////////////////////////////////////////////////////////////////
///////draw GameOver//////////////////////////////////////////////////////////////////////////////
        if (gameOver)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Comic Sans MS",Font.PLAIN,30));
            g.drawString("Game Over",100,250);
        }        
///////////////////////////////////////////////////////////////////////////////////////////////////        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.yellow);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public void drawBomb(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawlazer(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillRect(-5,-5,20,5);
     
        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ///////////////////////////////////////////////////////////////////////////
    
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
     ///////////////////////////////////////////////////////////////////////////
    
    public void drawStar(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = starImage.getWidth(this);
        int height = starImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketXSpeed=0;
        rocketYSpeed=0;
        for (int index=0;index<numStar;index++)
        {
        starXPos[index] = (int)(Math.random() * (getWidth2() -30) + 15);
        starYPos[index] = (int)(Math.random() * (getHeight2()/4) + getHeight2()/2);
        starVisible[index]=true;
        }
        rocketRight=true;
        rocketDir = 1;
        currentLazer = 0;
        playinitialhit=true;
        starhit= -1;
        rocketHealth=4;
        rocketVisible=true;
        score=0;
        grow1=.1;
        grow2=.1;
        bombVisable=false;
        Explode=false;
        for(int i=0;i<numLazer;i++)
        {
            lazerArray[i] = new Lazer(); 
            //initialize the lazer
        }
        gameOver = false;
     
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
            reset();
            bgSound = new sound("starWars.wav");
        }
        if (gameOver)
            return;
       
            for (int count=0;count<numLazer;count++)
           {
               if (lazerArray[count].isVisible)
               {
                   lazerArray[count].xPos += 8 *lazerArray[count].dir;
                   if (lazerArray[count].xPos >= getWidth2()||lazerArray[count].xPos <= 0)
                       lazerArray[count].isVisible = false;
               }                  
           }     
        
       
if (bgSound.donePlaying)
{
    bgSound = new sound("starWars.wav");
}
rocketYPos+=rocketYSpeed;
if (rocketYPos>getHeight2())
            {
                rocketYPos=getHeight2();
                rocketYSpeed=0;
            }
            else if (rocketYPos<0)
            {
                rocketYPos=0;
                rocketYSpeed=0;
            }
for (int index=0;index<numStar;index++)
        {
                starXPos[index] -= rocketXSpeed;
                
            if (starXPos[index]<3)
            {
                starXPos[index]= getWidth2();
                starYPos[index]= (int)(Math.random() * (getHeight2()));
            }
            
            else if (starXPos[index]>getWidth2())
            {
                starXPos[index]= 3;
                starYPos[index]= (int)(Math.random() * (getHeight2()));
            }
            
            if (rocketXSpeed>0)
            {
                rocketRight= true;
            }
            
            else if (rocketXSpeed<0)
            {
                rocketRight=false;
            }
            
            if (rocketYSpeed>=5)
            {
                rocketYSpeed=5;
            }
            else if (rocketYSpeed<=-5)
            {
                rocketYSpeed=-5;
            }
            if (rocketXSpeed>=10)
            {
                rocketXSpeed=10;
            }
            else if (rocketXSpeed<=-10)
            {
                rocketXSpeed=-10;
            }
            if (bombVisable)
            {
                grow1+=.1;
                grow2+=.1;
            }
            if(grow1>=10||grow2>=10)
            {
                grow1=.1;
                grow2=.1;
                bombVisable=false;
            }
    
        }
boolean didHitStar=false;
for (int index=0;index<numStar;index++)
            {
                
               
                    if (starXPos[index]-18 < rocketXPos && 
                        starXPos[index]+18 > rocketXPos &&
                        starYPos[index]-18 < rocketYPos &&
                        starYPos[index]+18 > rocketYPos)
                    {
                        didHitStar=true;
                        if (starhit == -1)
                        {
                        zsound = new sound("ouch.wav");
                        starhit=index;
                        if(starVisible[index])
                        rocketHealth -=1;
                        // the star im hitting currently
                        }
                        
                    }
                    
            }
if (!didHitStar)
{
    starhit=-1;
}
if (rocketHealth==0)
{
    gameOver=true;
}

if (Explode)
{
    for (int index=0;index<numStar;index++)
            {
                int radius=100;
                double dist = Math.sqrt((rocketXPos-starXPos[index])*(rocketXPos-starXPos[index])+(rocketYPos-starYPos[index])*(rocketYPos-starYPos[index]));        
               if (radius>dist)
               {
                   starYPos[index] = (int)(Math.random()*getHeight2());
                   starXPos[index] = getWidth2();
               }
              
            }
}

            




   
for (int count=0;count<numLazer;count++)
        {
            for (int index=0;index<numStar;index++)
            {
                if (starVisible[index] && lazerArray[count].isVisible )
                {
                    if (starXPos[index]-18 < lazerArray[count].xPos && 
                        starXPos[index]+18 > lazerArray[count].xPos &&
                        starYPos[index]-18 < lazerArray[count].yPos &&
                        starYPos[index]+18 > lazerArray[count].yPos)
                    {
                        if (rocketRight)
                        {
                            starYPos[index] = (int)(Math.random()*getHeight2());
                            starXPos[index] = getWidth2();
                        }
                        else
                        {
                            starYPos[index] = (int)(Math.random()*getHeight2());
                            starXPos[index] = 0;
                        }
                        lazerArray[count].isVisible = false;
                        score+=1;
                    }
                }
            }
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
class Lazer
{
    public int xPos;
    public int yPos;
    public boolean isVisible;
    public  int dir;
}