package edu.ngtu.game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author tuwap
 */
public class Game2048 extends JPanel{
    enum State {
        start, playing, gameover
    }
    
    //List color for each differrent tile value
    final Color[] colorList = {
        new Color(0xEEE4DA), new Color(0xEDE0C8), new Color(0xF2B179),
        new Color(0xF59563), new Color(0xF67C5F), new Color(0xF65E3B),
        new Color(0xF2B179), new Color(0xEDCF72), new Color(0xEDCC61),
        new Color(0xEDC850), new Color(0x006adb)
    };
    final Color[] valueColor = {
        new Color(0x776E65), new Color(0xF9F6F2)
    };  
    private Color gridColor = new Color(0xBBADA0);
    private Color emptyColor = new Color(0xCDC1B4);
    private Color startColor = new Color(0xFFEBCD);
 
    private Random rand = new Random();
    
    static int score;
    static int best_score;
    private Tile[][] tiles;
    private int side = 4;
    private boolean checkMove;
    private State gamestate = State.start;
    String path = System.getProperty("user.dir")+"\\load.txt";
    
    //Main frame
    public Game2048() {
        setPreferredSize(new Dimension(425, 525));
        setBackground(new Color(0xFAF8EF));
        setFont(new Font("SansSerif", Font.BOLD, 48));
        setFocusable(true);        
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame();
                    repaint();
                }
            }
        });
 
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRight();
                        break;
                }
                repaint();
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                    repaint();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    try {
                        loadGame();
                        JOptionPane.showMessageDialog(null, "Load Game Thành Công", "2048", JOptionPane.INFORMATION_MESSAGE);      
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Không thể load game\nC:\\Users\\Public\\Documents\\2048\\load.txt", 
                                "2048", JOptionPane.ERROR_MESSAGE);
                    }
                    repaint();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    try {                    
                        saveGame();
                        JOptionPane.showMessageDialog(null, "Game Đã Được Lưu", "2048", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Lưu Game KHÔNG Thành Công", "2048", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
                
    }
    
    //Draw grid & tiles
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2d);
    }
 
    void startGame() {
        if (gamestate != State.playing) {
            gamestate = State.playing;
            tiles = new Tile[side][side];
            addRandomTile();
            addRandomTile();
        }
    }
    
    void restartGame() {
        if (gamestate == State.playing) {
            tiles = new Tile[side][side];
            addRandomTile();
            addRandomTile();
            score = 0;
            JOptionPane.showMessageDialog(null, "Game Đã Được Restart", "2048", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void loadGame() throws FileNotFoundException, IOException {
        File file = new File(path); 
        Scanner sc = new Scanner(file);  
        gamestate = State.playing;
        tiles = new Tile[side][side];
        int counter = 0;
        int load_value = 0;
        //Set value all tiles
        for (int row=0; row<side; row++) {
            for (int col=0; col<side; col++) {
                if(sc.hasNextInt() && counter<=side*side) { 
                    load_value = sc.nextInt();
                    if (load_value == 0)
                        continue;
                    tiles[row][col] = new Tile(load_value);  
                    counter++;                    
                }
            }                
        }
        sc.close();
        //read & set score, best_score
        String load_score = Files.readAllLines(Paths.get(path)).get(4);
        score = Integer.parseInt(load_score);
        String load_bestscore = Files.readAllLines(Paths.get(path)).get(5);
        best_score = Integer.parseInt(load_bestscore);
    }
    
    void saveGame() throws IOException {
        FileWriter fw = new FileWriter(path);
        int value = 0;
        //write tile value into text file
        for (int row=0; row<side; row++) {
            for (int col=0; col<side; col++) {                
                if (tiles[row][col] == null) {
                    fw.write(String.valueOf(0)+" ");
                    continue;
                }
                value = tiles[row][col].getValue();                
                fw.write(String.valueOf(value)+" ");
            }
            fw.write(System.getProperty("line.separator"));
        }
        //write score into text file
        fw.write(String.valueOf(score)+System.getProperty("line.separator"));
        fw.write(String.valueOf(best_score));
        fw.close();
    }
    
    void drawGrid(Graphics2D g2d) {
        g2d.setColor(gridColor);
        
        if (gamestate == State.start || gamestate == State.gameover) {            
            g2d.fillRoundRect(12, 12, 400, 500, 20, 20);    //second layer location & height, width
        }        
        
        if (gamestate == State.playing) {
            g2d.fillRoundRect(12, 12, 400, 90, 20, 20);     //info board layer
            g2d.fillRoundRect(12, 112, 400, 400, 20, 20);   //second layer while playing
            
            g2d.setColor(valueColor[0]);
            g2d.fillRoundRect(18, 18, 100, 77, 15, 15);     //score board
            g2d.fillRoundRect(130, 18, 100, 77, 15, 15);    //best score board
            
            g2d.setColor(valueColor[1]);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 15));            
            drawStringCenter(g2d, "SCORE", 100, 18, 44);    //(g2d, string, width of componet, XPos of conponet, YPos of string)
            drawStringCenter(g2d, "BEST", 100, 130, 44);
            
            String s = String.valueOf(score);   
            String best = String.valueOf(best_score);            
            drawStringCenter(g2d, s, 100, 18, 68);
            drawStringCenter(g2d, best, 100, 130, 68);
            g2d.drawString("Nhấn R để chơi lại", 250, 44);
            g2d.drawString("Nhấn S để lưu game", 250, 64);
            g2d.drawString("Nhấn L để tải game", 250, 84);
            for (int row = 0; row<side; row++) {
                for (int col = 0; col<side; col++) {
                    if (tiles[row][col] == null) {
                        g2d.setColor(emptyColor);
                        g2d.fillRoundRect(20 + col * 98, 120 + row * 98, 90, 90, 10, 10);  //third layer
                    } else {
                        drawTile(g2d, row, col);
                    }
                }
            }
        } else {
            g2d.setColor(startColor);
            g2d.fillRoundRect(27, 27, 370, 470, 10, 10);  //third layer
            g2d.setColor(gridColor.darker());
            g2d.setFont(new Font("SansSerif", Font.BOLD, 100));
            drawStringCenter(g2d, "2048", 370, 27, 160);
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 20));             
            g2d.setColor(new Color(0x4AA6B5));
            drawStringCenter(g2d, "Nhấn Phím SPACE Để Bắt Đầu", 370, 27, 340);            
            drawStringCenter(g2d, "Nhấn Phím L Để Tải Game", 370, 27, 370);            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawStringCenter(g2d, "Dùng phím mũi tên để di chuyển các khối", 370, 27, 460);
        }
    }
 
    void drawTile(Graphics2D g2d, int row, int col) {
        int value = tiles[row][col].getValue();
        //Set font size base on tile value
        switch (value) {
            case 2: case 4: case 8: case 16: case 32: case 64:
                g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
                break;
            case 128: case 256: case 512:
                g2d.setFont(new Font("SansSerif", Font.BOLD, 38));
                break;
            case 1024: case 2048:
                g2d.setFont(new Font("SansSerif", Font.BOLD, 32));
                break;
        }
        
        g2d.setColor(colorList[(int) (Math.log(value) / Math.log(2)) - 1]);   //return log 2 of "value"
        g2d.fillRoundRect(20 + col * 98, 120 + row * 98, 90, 90, 10, 10);      //tile background        
        
        if (value < 8) {
            g2d.setColor(valueColor[0]);
        } else {
            g2d.setColor(valueColor[1]);
        }
                
        String s = String.valueOf(value);
        
        FontMetrics fm = g2d.getFontMetrics();
        int asc = fm.getAscent();
        int dec = fm.getDescent(); 

        int x = 20 + col * 98;
        int y = 120 + row * 98 + (asc + (90 - (asc + dec)) / 2);                
        drawStringCenter(g2d, s, 90, x, y);
    }
    
    private void drawStringCenter(Graphics2D g2d, String str, int width, int XPos, int YPos){
        int stringLen = (int) g2d.getFontMetrics().getStringBounds(str, g2d).getWidth();
        int startPos = (width - stringLen) / 2;
        g2d.drawString(str, startPos + XPos, YPos);
    }
 
    private void addRandomTile() {
        int row, col;
        
        do {
            row = rand.nextInt(4); //0-3
            col = rand.nextInt(4);
        } while (tiles[row][col] != null);
 
        int value = 2;
        tiles[row][col] = new Tile(value);
    }
    
    private boolean move(int countdownfrom, int y, int x) {
        boolean moved = false;
        
        for (int i = 0; i<side*side; i++) {
            int j = Math.abs(countdownfrom - i);
            int row = j / side;
            int col = j % side;
            
            if (tiles[row][col] == null)
                continue;
            int nextrow = row + y;
            int nextcol = col + x;
            
            while (nextrow >= 0 && nextrow < side && nextcol >= 0 && nextcol < side) {
                Tile next = tiles[nextrow][nextcol];
                Tile current = tiles[row][col];
                
                if (next == null) {
                    if (checkMove)
                        return true;
                    
                    tiles[nextrow][nextcol] = current;
                    tiles[row][col] = null;
                    row = nextrow;
                    col = nextcol;
                    nextrow += y;
                    nextcol += x;
                    moved = true;
                }
                
                else if (next.canMerge(current)) {
                    if (checkMove)
                        return true;
                    
                    int value = next.mergeWith(current);
                    score += value;
                    tiles[row][col] = null;
                    moved = true;
                    break;
                } else
                    break;
            }
        }
        if (moved) {
            clearTile();
            addRandomTile();
            if (!moveAvailable()) {
                gamestate = State.gameover;                
                if (score > best_score) {
                    best_score = score;
                }
                score = 0;
            }
        }
        return moved;
    }
    
    boolean moveUp() {
        return move(0, -1, 0);
    }
    
    boolean moveDown() {
        return move(side*side - 1, 1, 0);
    }
    
    boolean moveLeft() {
        return move(0, 0, -1);
    }
    
    boolean moveRight() {
        return move(side*side - 1, 0, 1);
    }
    
    void clearTile() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }
    
    boolean moveAvailable() {
        checkMove = true;
        boolean hasMove = moveUp() || moveDown() || moveLeft() || moveRight();
        checkMove = false;
        return hasMove;
    }
}
 
class Tile {
    private boolean merged;
    private int value;
 
    Tile(int tile_value) {
        value = tile_value;
    }
 
    int getValue() {
        return value;
    }
    
    void setValue(int v) {
        this.value = v;
    }
 
    void setMerged(boolean m) {
        merged = m;
    }
 
    boolean canMerge(Tile another) {
        return !merged && another != null && !another.merged && value == another.getValue() && value<2048;
    }
 
    int mergeWith(Tile another) {
        if (canMerge(another)) {
            value *= 2;
            merged = true;
            return value;
        }
        return -1;
    } 
        
}
