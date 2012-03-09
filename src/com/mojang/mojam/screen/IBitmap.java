package com.mojang.mojam.screen;

//TODO: more accurate javadocs
public interface IBitmap {
	
	/***
	 * Clears the bitmap by filling
	 * with the specified color
	 * @param color the color to fill with
	 */
	void clear(int color);
	
	/***
	 * Combines the provided bitmap with the 
	 * current one using the implementations
	 * raster operator. The combination is
	 * relative to the the x,y position
	 * depending on the implementation.
	 * 
	 * default: x,y refer to the new location
	 * of the top right of the bitmap
	 * 
	 * @param bitmap the bitmap to be combined with this
	 * @param x the relative, combinative x
	 * @param y the relative, combinative y
	 */
	void blit(Bitmap bitmap, int x, int y);
	
	/***
	 * Blends the RGBA channels of the two
	 * pixels provided and returns the result.
	 * 
	 //TODO: Why isn't this static?
	 * 
	 * @param backgroundColor pixel one
	 * @param pixelToBlendColor pixel two
	 * @return the blended pixel
	 */
	int blendPixels(int backgroundColor, int pixelToBlendColor);
	
	/***
	 * Performs the blit function with
	 * the ability to only blit a portion
	 * of the bitmap as defined by width
	 * and height.
	 * 
	 * See IBitmap.blit for more details
	 * 
	 * @param bitmap the bitmap to be combined with this
	 * @param x the relative, combinative x
	 * @param y the relative, combinative y
	 * @param width of the region
     * @param height of the region
	 */
	void blit(Bitmap bitmap, int x, int y, int width, int height);
	
	/***
	 * Performs the blit function with
	 * the alpha channel allowing for
	 * semi-transparent bitmap combinations.
	 * 
	 * See IBitmap.blit for more details
	 * 
	 * @param bitmap the bitmap to be combined with this
	 * @param x the relative, combinative x
	 * @param y the relative, combinative y
	 * @param alpha range from 0x00 (transparent) to 0xff (opaque)
	 */
    void alphaBlit(Bitmap bitmap, int x, int y, int alpha);
    
    /***
     * Performs the blit function; this
     * blends the provided color with the 
     * provided bitmap
     * 
     * See IBitmap.blit for more details
     * 
     * @param bitmap the bitmap to be combined with this
	 * @param x the relative, combinative x
	 * @param y the relative, combinative y
     * @param color the overlay color
     */
    void colorBlit(Bitmap bitmap, int x, int y, int color);
    
    /***
     * Fills a semi-transparent colored region on this bitmap
     * 
     * See IBitmap.fill
     * 
     * @param x the relative, combinative x
	 * @param y the relative, combinative y
     * @param width of the region
     * @param height of the region
     * @param color to fill the region
     * @param alpha range from 0x00 (transparent) to 0xff (opaque)
     */
    public void alphaFill(int x, int y, int width, int height, int color, int alpha);
    
    /***
     * Fills a colored region
     * with the provided information
     * 
     * @param x the relative, combinative x
	 * @param y the relative, combinative y
     * @param width of the region
     * @param height of the region
     * @param color to fill the region
     */
    void fill(int x, int y, int width, int height, int color);
    
    /***
     * Ensures that the provided Rect
     * is within this bitmap's area
     * 
     * @param blitArea the Rect to verify
     */
    void adjustBlitArea(Rect blitArea);
    
    /***
     * Draws a colored rectangle
     * outline with the provided
     * information
     * 
     * @param x the relative, combinative x
	 * @param y the relative, combinative y
     * @param width of the region
     * @param height of the region
     * @param color of the outline
     */
    void rectangle(int x, int y, int width, int height, int color);
    
    /***
     * Sets a pixel to the provided
     * color
     * 
     * @param x pixel location
	 * @param y pixel location
     * @param color to set the pixel
     */
    void setPixel(int x, int y, int color);
    
    /***
     * Draws a colored circle outline with
     * the provided information 
     * 
     * @param centerX x pixel location of the center of the circle
     * @param centerY y pixel location of the center of the circle
     * @param radius of the circle in pixels
     * @param color of the outline
     */
	void circle(int centerX, int centerY, int radius, int color);
	
	/***
	 * Draws a colored circle with
     * the provided information 
     * 
	 * @param centerX x pixel location of the center of the circle
     * @param centerY y pixel location of the center of the circle
     * @param radius of the circle in pixels
	 * @param color to fill the region
	 */
	void circleFill(int centerX, int centerY, int radius, int color);
	
	/***
	 * Draws a horizontal line from
	 * (x1,y) to (x2,y) with the
	 * provided color
	 * 
	 * @param x1 starting x pixel (of this bitmap)
	 * @param x2 ending x pixel (of this bitmap)
	 * @param y y pixel location (of this bitmap)
	 * @param color of the horizontal line
	 */
	void horizonalLine(int x1, int x2, int y, int color);
}