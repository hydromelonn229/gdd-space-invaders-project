package gdd.sprite;

import java.awt.Image;

abstract public class Sprite {

    protected boolean visible;
    protected Image image;
    protected boolean dying;
    protected int visibleFrames = 10;

    protected int x;
    protected int y;
    protected int dx;

    public Sprite() {
        visible = true;
    }

    abstract public void act();

    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        
        // Use safer collision detection with null checks and minimum dimensions
        int thisWidth = (this.getImage() != null) ? this.getImage().getWidth(null) : 16;
        int thisHeight = (this.getImage() != null) ? this.getImage().getHeight(null) : 16;
        int otherWidth = (other.getImage() != null) ? other.getImage().getWidth(null) : 16;
        int otherHeight = (other.getImage() != null) ? other.getImage().getHeight(null) : 16;
        
        // Ensure minimum collision box size to prevent inconsistencies
        thisWidth = Math.max(thisWidth, 16);
        thisHeight = Math.max(thisHeight, 16);
        otherWidth = Math.max(otherWidth, 16);
        otherHeight = Math.max(otherHeight, 16);
        
        return this.getX() < other.getX() + otherWidth
                && this.getX() + thisWidth > other.getX()
                && this.getY() < other.getY() + otherHeight
                && this.getY() + thisHeight > other.getY();
    }

    public void die() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void visibleCountDown() {
        if (visibleFrames > 0) {
            visibleFrames--;
        } else {
            visible = false;
        }
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return this.dying;
    }
}
