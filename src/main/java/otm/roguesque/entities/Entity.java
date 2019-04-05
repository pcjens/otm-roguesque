package otm.roguesque.entities;

import javafx.scene.image.Image;

public abstract class Entity {

    protected Dungeon dungeon;
    protected int x;
    protected int y;
    protected int maxHealth = 10;
    protected int health = 10;
    protected int attack = 2;
    protected int defense = 1;
    protected String friendlyGroup = "Default";

    protected void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void takeDamage(int attack) {
        this.health -= Math.max(0, attack - this.defense);
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    public boolean move(int deltaX, int deltaY) {
        if (dungeon == null) {
            return false;
        }

        int newX = x + deltaX;
        int newY = y + deltaY;

        if (hitAndCollide(newX, newY)) {
            return false;
        }

        return moveAndCollide(newX, newY);
    }

    private boolean hitAndCollide(int newX, int newY) {
        Entity hitEntity = dungeon.getEntityAt(newX, newY);
        if (hitEntity != null) {
            if (hitEntity.friendlyGroup.equals(this.friendlyGroup)) {
                return true;
            }
            hitEntity.takeDamage(attack);
            if (!hitEntity.isDead()) {
                return true;
            }
        }

        return false;
    }

    private boolean moveAndCollide(int newX, int newY) {
        if (!dungeon.solid(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        } else {
            return false;
        }
    }

    public abstract Image getImage();
}
