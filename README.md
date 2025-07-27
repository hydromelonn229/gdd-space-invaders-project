# Space Invaders - Java Game

A modern implementation of the classic Space Invaders arcade game built with Java Swing, featuring enhanced graphics, multiple scenes, boss battles, and dynamic gameplay mechanics.

## 👥 Development Team

**Team Name**: [NPCs]

### Team Members
- **Khant Min Lwin** - Student ID: [6530203] 
- **Zwe La Pyae** - Student ID: [6530207] 
- **Thet Myat Noe Thwin** - Student ID: [6530213] 

## 🎮 Game Overview

This Space Invaders game features two challenging scenes with time-based progression, a scoring system, power-ups, and an epic boss battle. Players must survive increasingly difficult waves of alien enemies while collecting power-ups to enhance their ship's capabilities.

## 🚀 Features

### Core Gameplay
- **Two-Scene Campaign**: Progress through Scene 1 (5 minutes) and Scene 2 (3 minutes + boss fight)
- **Time-Based Progression**: Survive specific durations to advance rather than kill-based objectives
- **Dynamic Enemy Spawning**: Random alien spawns with varying difficulty
- **Boss Battle**: Epic final boss with 100 health and multiple attack patterns
- **Collision Detection**: Precise hitbox system for accurate gameplay

### Visual & Audio
- **Animated Sprites**: Player ship, enemies, and boss with frame-by-frame animation
- **Starfield Background**: Multi-layered scrolling star effects
- **Visual Effects**: Explosion animations and particle effects
- **Sound Design**: Background music, laser sounds, and explosion effects
- **Dashboard UI**: Real-time display of score, time, upgrades, and boss health

### Power-Up System
- **Speed Upgrades**: Increase player movement speed (max 4 upgrades)
- **Multi-Shot**: Increase maximum simultaneous shots (max 4 upgrades)
- **Random Spawning**: Power-ups appear randomly throughout gameplay

### Enemy Types
- **Alien1 (Shooter)**: Standard enemies that fire projectiles (10 points)
- **Alien2 (Kamikaze)**: Aggressive enemies that dive at the player (20 points)
- **Boss**: Large enemy with 100 health, zig-zag movement, and spread-shot attacks

## 🎯 Game Objectives

### Scene 1
- **Duration**: 5 minutes
- **Objective**: Survive the alien assault
- **Progression**: Automatic advancement to Scene 2 after 5 minutes

### Scene 2
- **Phase 1**: Survive for 3 minutes with increased alien spawns
- **Phase 2**: Boss spawns after 3 minutes
- **Victory Condition**: Defeat the boss to win the game

## 🕹️ Controls

- **Left Arrow**: Move player ship left
- **Right Arrow**: Move player ship right
- **Spacebar**: Fire laser shots

## 🏗️ Technical Implementation

### Architecture
- **Language**: Java
- **Framework**: Java Swing for GUI and graphics
- **Design Pattern**: Object-oriented with sprite inheritance hierarchy
- **Audio System**: Custom SoundEffect class for efficient audio management

### Key Components
- **Scene Management**: Modular scene system (TitleScene, Scene1, Scene2)
- **Sprite System**: Base Sprite class with specialized enemies, player, and projectiles
- **Collision Detection**: Rectangle-based intersection detection with custom hitboxes
- **Animation System**: Frame-based sprite animation with configurable timing
- **Power-Up Framework**: Upgradeable player stats with visual feedback

### File Structure
```
src/
├── gdd/
│   ├── Main.java                 # Application entry point
│   ├── Game.java                 # Main game controller
│   ├── Global.java               # Game constants and configuration
│   ├── AudioPlayer.java          # Background music management
│   ├── SoundEffect.java          # Sound effects system
│   ├── scene/                    # Game scenes
│   │   ├── TitleScene.java       # Main menu
│   │   ├── Scene1.java           # First level
│   │   └── Scene2.java           # Final level with boss
│   ├── sprite/                   # Game objects
│   │   ├── Sprite.java           # Base sprite class
│   │   ├── Player.java           # Player ship
│   │   ├── Enemy.java            # Base enemy class
│   │   ├── Alien1.java           # Shooting enemy
│   │   ├── Alien2.java           # Kamikaze enemy
│   │   ├── Boss.java             # Final boss
│   │   ├── Shot.java             # Player projectiles
│   │   ├── Bomb.java             # Enemy projectiles
│   │   └── Explosion.java        # Visual effects
│   └── powerup/                  # Power-up system
│       ├── PowerUp.java          # Base power-up class
│       ├── SpeedUp.java          # Speed enhancement
│       └── MultiBullet.java      # Shot upgrade
├── audio/                        # Sound assets
└── images/                       # Visual assets
```

## 🎵 Assets

### Audio Files
- Background music for different scenes
- Laser firing sound effects
- Explosion sound effects

### Visual Assets
- Player ship sprites (3-frame animation)
- Enemy alien sprites (multiple types)
- Boss sprites (2-frame animation)
- Projectile and explosion sprites
- Power-up icons

## 🏆 Scoring System

- **Alien1 (Shooter)**: 10 points per kill
- **Alien2 (Kamikaze)**: 20 points per kill
- **Boss Defeat**: Victory condition (game completion)

## 📄 References

This project is based on and enhanced from the original [Space Invaders](https://github.com/janbodnar/Java-Space-Invaders) repository by Jan Bodnar, with significant modifications and improvements for educational purposes.

---

**Enjoy defending Earth from the alien invasion! 🛸👾**