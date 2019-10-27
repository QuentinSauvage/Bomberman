package parser;

public class MapJson {
	private int width;
	private int height;
	private char content[][];
	
	public MapJson() {
		width = 0;
		height = 0;
		content = new char[width][height];
	}
	
	public MapJson(int width, int height, char[] content) {
		this.width = width;
		this.height = height;
		this.content = new char[height][width];
		int k = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				this.content[i][j] = content[k++];
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public char[][] getContent() {
		return content;
	}
	
	public void setContent(char[] content) {
		int k = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				this.content[i][j] = content[k++];
			}
		}
	}
}
