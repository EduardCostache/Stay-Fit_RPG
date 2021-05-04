//the following code was generated by Android Studio
package com.edward_costache.stay_fitrpg;
//the code above was generated by Android Studio

/**
 * Created by Edward Costache
 */
public class User {
    private String username;
    private double level;
    private int strength;
    private int agility;
    private int stamina;
    private int health;

    private boolean isDefending, left;

    //empty constructor needed to FirebaseDatabase
    public User()
    {

    }

    //constructor used when creating a brand new user
    public User(String username) {
        setUsername(username);
        setLevel(1);
        setStrength(1);
        setAgility(1);
        setStamina(1);
        setHealth(50);
    }

    //constructor used when cloning the user data into the game room.
    public User(String username, double level, int strength, int agility, int stamina, int health, boolean isDefending)
    {
        setUsername(username);
        setLevel(level);
        setStrength(strength);
        setAgility(agility);
        setStamina(stamina);
        setHealth(health);
        setDefending(isDefending);
        setLeft(false);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isDefending() {
        return isDefending;
    }

    public void setDefending(boolean defending) {
        isDefending = defending;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
}
