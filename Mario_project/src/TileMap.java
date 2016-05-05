
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystemAlreadyExistsException;

import javax.imageio.ImageIO;

import tile.Tile;

public class TileMap {

	private int x;
	private int y;

	private int tileSize;
	private int[][] map;
	private int mapWidth;
	private int mapHeight;

	private BufferedImage tileset;
	private Tile[][] tiles;

	private int minx;
	private int miny;
	private int maxx = 0;
	private int maxy = 0;

	public TileMap(String s, int tileSize) {
		this.tileSize = tileSize;
		try {
			BufferedReader br = new BufferedReader(new FileReader(s));
			mapWidth = Integer.parseInt(br.readLine());
			mapHeight = Integer.parseInt(br.readLine());
			map = new int[mapHeight][mapWidth];
			minx = GamePanel.WIDTH - mapWidth * tileSize;
			miny = GamePanel.HEIGHT - mapHeight * tileSize;
			String resourses = "\\s+";
			for (int row = 0; row < mapHeight; row++) {
				String line = br.readLine();
				String[] top = line.split(resourses);
				for (int col = 0; col < mapWidth; col++) {
					map[row][col] = Integer.parseInt(top[col]);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void loadTiles(String s) {
		try {
			tileset = ImageIO.read(new File(s));
			int numTiles = (tileset.getWidth() + 1) / (tileSize + 1);
			tiles = new Tile[2][numTiles];
			BufferedImage subImage;
			for (int col = 0; col < numTiles; col++) {
				subImage = tileset.getSubimage(col + tileSize * col, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subImage, false);
				subImage = tileset.getSubimage(col + tileSize * col, tileSize + 1, tileSize, tileSize);
				tiles[1][col] = new Tile(subImage, true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getColTile(int x) {
		return x / tileSize;
	}

	public int getRowTile(int y) {
		return y / tileSize;
	}

	public int getTile(int row, int col) {
		return map[row][col];
	}

	public int getTileSize() {
		return tileSize;
	}

	public boolean isBlocked(int row, int col) {
		int rc = map[row][col];
		int r = rc / tiles[0].length;
		int c = rc % tiles[0].length;
		return tiles[r][c].isBlocked();
	}

	public void setX(int xx) {
		x = xx;
		if (x < minx) {
			x = minx;
		}
		if (x > maxx) {
			x = maxx;
		}
	}

	public void setY(int yy) {
		y = yy;
		if (y < miny) {
			y = miny;
		}
		if (y > maxy) {
			y = maxy;
		}
	}

	public void update() {

	}

	public void draw(Graphics2D g) {
		for (int row = 0; row < mapHeight; row++) {
			for (int col = 0; col < mapWidth; col++) {
				int rc = map[row][col];
				int r = rc / tiles[0].length;
				int c = rc % tiles[0].length;
				g.drawImage(tiles[r][c].getImage(), x + col * tileSize, y + row * tileSize, null);
			}
		}
	}

}