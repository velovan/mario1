
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilterWriter;

import javax.imageio.ImageIO;

public class Player {
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	private int width;
	private int height;
	
	private boolean left;
	private boolean right;
	private boolean jumping;
	private boolean falling;
	
	private double moveSpeed;
	private double maxSpeed;
	private double maxFallingSpeed;
	private double stopSpeed;
    private double jumpStart;
	private double gravity;
	
    private TileMap tileMap;
    
    private boolean topLeft;
    private boolean topRight;
    private boolean bottomLeft;
    private boolean bottomRight;
    
    private Animation animation;
    private BufferedImage[] idleSprites;
    private BufferedImage[] walkingSprites;
    private BufferedImage[] jumpingSprites;
    private BufferedImage[] fallingSprites;
    private boolean facingLeft;
    
    public Player(TileMap tm){
        tileMap = tm;
   
        width = 22;
        height = 22;
       
        moveSpeed = 0.6;
        maxSpeed = 4.2;
        maxFallingSpeed = 12;
        stopSpeed = 0.30;
        jumpStart = -11.0; 
        gravity = 0.64;
   
        try{
        	idleSprites = new BufferedImage[1];
        	jumpingSprites = new BufferedImage[1];
        	fallingSprites    = new BufferedImage[1];
        	walkingSprites  = new BufferedImage[6];
        	
        	idleSprites[0] = ImageIO.read(new File("res/kirbyidle.gif"));
        	jumpingSprites[0] = ImageIO.read(new File("res/kirbyjump.gif"));
        	fallingSprites[0]    = ImageIO.read(new File("res/kirbyfall.gif"));
        	
           BufferedImage image = ImageIO.read(new File("res/kirbywalk.gif"));
           for(int i = 0; i < walkingSprites.length; i++){
        	   walkingSprites[i] = image.getSubimage(i + width * i, 0, width, height);
           }
        }
    
        catch(Exception e){
        	e.printStackTrace();
        }
    animation = new Animation();
    facingLeft = false;
    }
    
    public void setx (int i) { x = i;}
    public void sety (int i) { y = i;}
    
    public void setLeft(boolean b) {left = b; }
    public void setRight(boolean b){right = b; }
    public void setJumping(boolean b){
    	if(!falling){
    		jumping = true;
    	}
    }
    

    public void calculateCorners(double x, double y){
      int leftTile = tileMap.getColTile((int)(x - width / 2));	
      int rightTile = tileMap.getColTile((int)(x + width / 2) - 1);
      int topTile = tileMap.getRowTile((int)(y - height / 2));
      int bottomTile = tileMap.getRowTile((int)(y + height / 2) - 1);
    
      topLeft = tileMap.isBlocked(topTile, leftTile);
      topRight = tileMap.isBlocked(topTile, rightTile);
      bottomLeft = tileMap.isBlocked(bottomTile, leftTile);
      bottomRight = tileMap.isBlocked(bottomTile, rightTile);
    }
    
    public void update(){	
    	
    if(left){
    	dx -= moveSpeed;
    	if(dx < -maxSpeed){
    		dx = -maxSpeed;
    	}
    }
    if(right){
    dx += moveSpeed;
    if(dx > maxSpeed){
    	dx = maxSpeed;
      }
    }
    
    else{
    	if(dx > 0){
    		dx -= stopSpeed;
    		if(dx < 0){
        	    dx = 0;
        	}
    	}
    	
    	if(dx < 0){
    		dx += stopSpeed;
            }
    	if(dx > 0){
    		dx = 0;
    	}
    }
    	
    if(jumping){
     dy = jumpStart;
     jumping = false;
     falling = true;
    }
    if(falling){
    	dy += gravity;
    	if(dy > maxFallingSpeed){
    		dy = maxFallingSpeed;
    	}
    }
    	// check collisions
    	int currCol = tileMap.getColTile((int) x);
    	int currRow = tileMap.getRowTile((int) y);
    
    	double tox = x + dx;
    	double toy = y + dy;
    	
    	double tempx = x;
    	double tempy = y;
    	
    	calculateCorners(x, toy);
    	if(dy < 0){
    		if(topLeft || topRight){
    			dy = 0;
    			tempy = currRow * tileMap.getTileSize() + height / 2;
    		}
    		else{
    			tempy += dy;
    		}
    	}
    	if(dy > 0){
    		if(bottomLeft || bottomRight){
    			dy = 0;
    			falling = false;
    			tempy = (currRow  + 1) * tileMap.getTileSize() - height / 2;
    		}
    		else{
    			tempy += dy;
    		 }
         }	
    	calculateCorners(tox, y);
    	if(dx < 0){
    		if(topLeft || bottomLeft){
    			dx = 0;
    			tempx = currCol * tileMap.getTileSize() + width / 2;
    		}
    		else{
    			tempx += dx;
    		}
    	}
    	if(dx > 0){
    		if(topRight || bottomRight){
    			dx = 0;
    			tempx = (currCol  + 1) * tileMap.getTileSize() - width / 2;
    		}
    		else{
    			tempx += dx;
    		 }
         }
    	if(!falling){
    		calculateCorners(x, y + 1);
    	    if(!bottomLeft && !bottomRight){
    	    	falling = true;
    	    }
    	}
    	
    	x = tempx;
    	y = tempy;
    	
    	//����� ��������  ����� �� player -��
    	tileMap.setX((int)(GamePanel.WIDTH / 2 - x));
    	tileMap.setY((int)(GamePanel.HEIGHT / 2 - y));
    	
    	// Sprite animation
    	if(left || right){
    		animation.setFrames(walkingSprites);
    		animation.setDelay(100);
    	}
    	else{
    		animation.setFrames(idleSprites);
    		animation.setDelay(-1);
    	}
    	if(dy < 0){
    		animation.setFrames(jumpingSprites);
    		animation.setDelay(-1);
    	}
    	if(dy > 0){ 
    		animation.setFrames(fallingSprites);
    		animation.setDelay(-1);
    	}
    	animation.update();
    	
    	if(dx < 0){
    		facingLeft = true;
    	}if(dx > 0){
    		facingLeft = false;
    	}	
    }
    
    public void draw(Graphics2D g){
    	int tx = tileMap.getX();
    	int ty = tileMap.getY();

    	if(facingLeft){
    		g.drawImage(animation.getImage(), (int) (tx + x - width / 2),(int) (ty + y - height / 2), null);
    	}
    	else{
    	    g.drawImage(animation.getImage(), (int) (tx + x - width / 2 + width),(int) (ty + y - height / 2), -width, height, null);
    	}
    }

}
