package com.mojang.mojam.screen;

import com.mojang.mojam.MojamComponent;

public abstract class Bitmap implements IBitmap, Cloneable {
	
	public int w, h;
	public int[] pixels;
	protected static Class<? extends Bitmap> systemCompatableClass;
	
	/***
	 * Creates an empty Bitmap
	 */
	public Bitmap() {
		w = 0;
		h = 0;
		pixels = null;
	}
	
	/***
	 * Creates a Bitmap the size of w * h
	 * @param w the width
	 * @param h the height
	 */
	public Bitmap(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w*h];
	}
	/***
	 * Creates a Bitmap the size of w * h
	 * with the provided pixels
	 * @param w the width
	 * @param h the height
	 * @param pixels the pixels of the image
	 */
	public Bitmap(int w, int h, int[] pixels) {
		this.w = w;
		this.h = h;
		this.pixels = pixels;
	}
	
	/***
	 * Constructs a Bitmap image based on a
	 * 2 dimensional array
	 * 
	 * @param pixels2D array of pixels [x][y]
	 */
	public Bitmap(int[][] pixels2D) {
		w = pixels2D.length;
		if(w>0){
			h = pixels2D[0].length;
			pixels = new int[w*h];
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					pixels[y*w+x] = pixels2D[x][y];
				}
			}
		} else {
			h = 0;
			pixels = new int[0];
		}
	}
	
	/***
	 * Constructs a Bitmap object based
	 * on the running system's Bitmap implementation.
	 * 
	 * @return the created Bitmap
	 */
	public final static Bitmap getSystemCompatibleBitmap() {
		if(systemCompatableClass == null) setCompatableRenderClass();
		try {
			Bitmap obj = systemCompatableClass.newInstance();
			return obj;
		} catch (Exception e) { 
			System.out.println("STATIC RENDER OBJECT CREATION ERROR!");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/***
	 * Constructs a Bitmap object based
	 * on the running system's Bitmap implementation.
	 * 
	 * @param w the width of the Bitmap
	 * @param h the height of the Bitmap
	 * @return the created Bitmap;
	 */
	public final static Bitmap getSystemCompatibleBitmap(int w, int h) {
		Bitmap obj = getSystemCompatibleBitmap();
		obj.w = w;
		obj.h = h;
		obj.pixels = new int[w*h];
		return obj;
	}
	
	/***
	 * Constructs a Bitmap object based
	 * on the running system's Bitmap implementation.
	 * 
	 * @param pixels2D array of pixels [x][y]
	 * @return the created Bitmap
	 */
	public final static Bitmap getSystemCompatibleBitmap(int[][] pixels2D) {
		Bitmap obj = getSystemCompatibleBitmap();
		obj.w = pixels2D.length;
		if(obj.w>0){
			obj.h = pixels2D[0].length;
			obj.pixels = new int[obj.w*obj.h];
			for(int y = 0; y < obj.h; y++){
				for(int x = 0; x < obj.w; x++){
					obj.pixels[y*obj.w+x] = pixels2D[x][y];
				}
			}
		} else {
			obj.h = 0;
			obj.pixels = new int[0];
		}
		return obj;
	}
	
	//TODO: add more implementations
	private final static void setCompatableRenderClass() {
		com.mojang.mojam.mc.EnumOS2 os = MojamComponent.getOs();
		switch(os) {
		case windows:
		case macos:
		case linux:
		case solaris:
			systemCompatableClass = DesktopBitmap.class;
			break;
		case unknown:
			System.err.println("Unknown System. Exiting...");
			System.exit(1);
			break;
		}
	}

	/***
	 * Constructs a rectangular Bitmap image
	 * based on the current render class.
	 * @param x first x position
	 * @param y first y position
	 * @param x2 second x position/width of Bitmap
	 * @param y2 second y position/height of Bitmap
	 * @param color the color of the rectangle
	 * @return the created Bitmap
	 */
	public static Bitmap rectangleBitmap(int x, int y, int x2, int y2, int color) {
		Bitmap rect = getSystemCompatibleBitmap(x2,y2);
		rect.rectangle(x, y, x2, y2, color);	
		return rect;
	}

	/***
	 * I don't really understand what this does,
	 * but what I do get is that it makes a circle
	 * Bitmap with the radius and there is a 100px
	 * padding on top and bottom?
	 * 
	 * @param radius of the circle
	 * @param color of the circle
	 * @return the created Bitmap
	 */
	public static Bitmap rangeBitmap(int radius, int color) {
		Bitmap circle = getSystemCompatibleBitmap(radius*2+100,radius*2+100);	
		
		circle.circleFill(radius, radius, radius, color);	
		return circle;
	}
	
	/***
	 * Constructs the background Bitmap for a tooltip
	 * of the provided dimensions
	 * @param width
	 * @param height
	 * @return the created Bitmap
	 */
	public static Bitmap tooltipBitmap(int width, int height) {
		int cRadius = 3;
		int color = -16777216; //Color.black.getRGB();
		Bitmap tooltip = getSystemCompatibleBitmap(width+3, height+3);	
		tooltip.fill(0, cRadius, width, height-2*cRadius, color);
		tooltip.fill(cRadius, 0, width-2*cRadius, height, color);
		// draw corner circles
		tooltip.circleFill(cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width-cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width-cRadius, height-cRadius, cRadius, color);
		tooltip.circleFill(cRadius, height-cRadius, cRadius, color);
		
		return tooltip;
	}
	
	/***
	 * Constructs a new Bitmap that is half the size
	 * of the provided one
	 * @param Bitmap the Bitmap to shrink
	 * @return the created Bitmap
	 */
	public static Bitmap shrink(Bitmap Bitmap) {
		Bitmap newbmp = getSystemCompatibleBitmap(Bitmap.w/2, Bitmap.h/2);
		int[] pix = Bitmap.pixels;
		int blarg = 0;
		for (int i = 0; i < pix.length; i++) {
			if(blarg>=newbmp.pixels.length) 
				break;
			if(i%2==0){
				newbmp.pixels[blarg]=pix[i];
				blarg++;
			}
			if(i%Bitmap.w==0) {
				i+=Bitmap.w;
			}
		}
		
		return newbmp;
	}
	
	/***
	 * Constructs a new Bitmap that is scaled to
	 * the provided dimensions based on the provided Bitmap
	 * @param Bitmap the Bitmap to scale
	 * @param width the new width
	 * @param height the new height
	 * @return the created Bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap Bitmap, int width, int height) {
        Bitmap scaledBitmap = getSystemCompatibleBitmap(width, height);

        int scaleRatioWidth = ((Bitmap.w << 16) / width);
        int scaleRatioHeight = ((Bitmap.h << 16) / height);

        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                scaledBitmap.pixels[i++] = Bitmap.pixels[(Bitmap.w * ((y * scaleRatioHeight) >> 16)) + ((x * scaleRatioWidth) >> 16)];
            }
        }

        return scaledBitmap;
    }
	
	/***
	 * Makes a copy of this Bitmap.
	 * 
	 * @return a copy of this Bitmap
	 */
	@Override
	public Bitmap clone() {
		Bitmap copy = getSystemCompatibleBitmap(w,h);
		copy.pixels = pixels.clone();
		return copy;
	}
}