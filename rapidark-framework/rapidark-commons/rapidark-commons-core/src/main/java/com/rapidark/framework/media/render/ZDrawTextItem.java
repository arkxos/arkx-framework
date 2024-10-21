package com.rapidark.framework.media.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;

import com.alibaba.simpleimage.render.DrawTextItem;
import com.rapidark.framework.commons.util.StringUtil;

public class ZDrawTextItem
  extends DrawTextItem
{
  protected float x;
  protected float y;
  protected Color outlineColor;
  protected int outlineSize;
  protected float opicty = 1.0F;
  protected boolean underline = false;
  
  public ZDrawTextItem(String text, Color fontColor, Font font, float opicty, float x, float y)
  {
    super(text, fontColor, null, font, 0);
    this.opicty = opicty;
    this.x = x;
    this.y = y;
  }
  
  public ZDrawTextItem(String text, Color fontColor, Color fontShadowColor, Font font, float opicty, float x, float y)
  {
    super(text, fontColor, fontShadowColor, font, 0);
    this.opicty = opicty;
    this.x = x;
    this.y = y;
  }
  
  public ZDrawTextItem(String text, Color fontColor, Color outlineColor, int outlineSize, Font font, float opicty, float x, float y, boolean underline)
  {
    super(text, fontColor, null, font, 0);
    this.outlineColor = outlineColor;
    this.outlineSize = outlineSize;
    this.opicty = opicty;
    this.x = x;
    this.y = y;
    this.underline = underline;
  }
  
  public ZDrawTextItem(String text, Color fontColor, Color fontShadowColor, Color outlineColor, int outlineSize, Font font, float x, float y, boolean underline)
  {
    super(text, fontColor, fontShadowColor, font, 0);
    this.outlineColor = outlineColor;
    this.outlineSize = outlineSize;
    this.x = x;
    this.y = y;
    this.underline = underline;
  }
  
  public void drawText(Graphics2D graphics, int width, int height)
  {
    if (StringUtil.isEmpty(this.text)) {
      return;
    }
    if ((this.x <= 0.0F) || (this.y <= 0.0F)) {
      return;
    }
    if (this.opicty < 1.0F) {
      graphics.setComposite(AlphaComposite.getInstance(10, this.opicty));
    }
    graphics.setFont(this.defaultFont);
    if (this.underline)
    {
      AttributedString as = new AttributedString(this.text);
      as.addAttribute(TextAttribute.FONT, this.defaultFont);
      as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      graphics.setColor(this.fontColor);
      graphics.drawString(as.getIterator(), this.x, this.y);
    }
    if (this.outlineColor != null)
    {
      FontRenderContext frc = graphics.getFontRenderContext();
      TextLayout tl = new TextLayout(this.text, this.defaultFont, frc);
      Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(this.x, this.y));
      graphics.setColor(this.outlineColor);
      graphics.setStroke(new BasicStroke(this.outlineSize));
      graphics.draw(sha);
    }
    else if (this.fontShadowColor != null)
    {
      graphics.setColor(this.fontShadowColor);
      graphics.drawString(this.text, this.x + getShadowTranslation(this.defaultFont.getSize()), this.y + getShadowTranslation(this.defaultFont.getSize()));
    }
    graphics.setColor(this.fontColor);
    graphics.drawString(this.text, this.x, this.y);
    graphics.dispose();
  }
}
