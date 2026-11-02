package com.example.realisticcattle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import java.util.ArrayList;
import java.util.List;

/** Read-only graphical herdbook with ancestry cards and a scrollable family tree. */
public class PedigreeScreen extends Screen {
    private final String animal, breed, ancestry, tree, traits, genome;
    private final boolean purebred;
    private int page;
    private int scroll;
    private final String[] tabs = {"Родословная", "Родовое дерево", "Генетика"};

    public PedigreeScreen(String animal, String breed, boolean purebred, String ancestry,
                          String tree, String traits, String genome) {
        super(Component.literal("Племенная книга"));
        this.animal=animal; this.breed=breed; this.purebred=purebred;
        this.ancestry=ancestry; this.tree=tree; this.traits=traits; this.genome=genome;
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int left=(width-310)/2, top=(height-220)/2;
        g.fill(left,top,left+310,top+220,0xEE30271F);
        g.fill(left+3,top+3,left+307,top+217,0xFFDDC69A);
        g.fill(left+8,top+8,left+302,top+35,0xFF63452F);
        g.drawCenteredString(font,"ПЛЕМЕННАЯ КНИГА",width/2,top+17,0xFFFFE8B0);
        for(int i=0;i<tabs.length;i++){
            int x=left+10+i*98; int color=page==i?0xFF80603D:0xFFB89A70;
            g.fill(x,top+42,x+94,top+61,color);
            g.drawCenteredString(font,tabs[i],x+47,top+48,0xFFFFFFFF);
        }
        if(page==0) renderCard(g,left,top);
        else if(page==1) renderTree(g,left,top);
        else renderGenetics(g,left,top);
        g.drawCenteredString(font,"Esc — закрыть   •   Колесо мыши — прокрутка",width/2,top+203,0xFF493A2C);
        super.render(g,mouseX,mouseY,partialTick);
    }

    private void renderCard(GuiGraphics g,int l,int t){
        g.fill(l+12,t+70,l+91,t+151,0xFF9B8769);
        // stylized cow portrait placeholder, deliberately vector-drawn so no missing asset dependency
        g.fill(l+31,t+91,l+72,t+127,0xFFF2EBDD); g.fill(l+25,t+85,l+78,t+111,0xFFF2EBDD);
        g.fill(l+32,t+90,l+45,t+103,0xFF28231F); g.fill(l+58,t+108,l+70,t+121,0xFF28231F);
        g.fill(l+25,t+80,l+37,t+91,0xFFB78D76); g.fill(l+66,t+80,l+78,t+91,0xFFB78D76);
        g.drawString(font,animal,l+101,t+73,0xFF30271F,false);
        g.drawString(font,"Порода: "+breed,l+101,t+89,0xFF493A2C,false);
        g.drawString(font,"Статус: "+(purebred?"Чистокровный":"Помесный"),l+101,t+105,purebred?0xFF28652D:0xFF8B4B20,false);
        g.drawString(font,"Состав породы:",l+14,t+160,0xFF30271F,false);
        drawWrapped(g,ancestry,l+14,t+173,280,0xFF493A2C,2);
    }
    private void renderTree(GuiGraphics g,int l,int t){
        g.drawString(font,"РОДСТВЕННЫЕ СВЯЗИ • ДО 4 ПОКОЛЕНИЙ",l+14,t+70,0xFF30271F,false);
        drawWrapped(g,tree,l+16,t+87,278,0xFF493A2C,12);
    }
    private void renderGenetics(GuiGraphics g,int l,int t){
        g.drawString(font,"НАСЛЕДУЕМЫЕ ПРИЗНАКИ",l+14,t+70,0xFF30271F,false);
        drawWrapped(g,traits,l+14,t+85,280,0xFF493A2C,5);
        g.drawString(font,"ОСНОВНОЙ ГЕНОТИП",l+14,t+151,0xFF30271F,false);
        drawWrapped(g,genome,l+14,t+166,280,0xFF493A2C,2);
    }
    private void drawWrapped(GuiGraphics g,String text,int x,int y,int maxWidth,int color,int maxLines){
        List<FormattedCharSequence> lines=font.split(Component.literal(text.replace("; ","\n")),maxWidth);
        int start=Math.min(scroll,Math.max(0,lines.size()-maxLines));
        for(int i=0;i<Math.min(maxLines,lines.size()-start);i++)g.drawString(font,lines.get(start+i),x,y+i*10,color,false);
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        int l=(width-310)/2,t=(height-220)/2;
        if(y>=t+42&&y<=t+61){int tab=(int)((x-(l+10))/98);if(tab>=0&&tab<3){page=tab;scroll=0;return true;}}
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseScrolled(double x,double y,double delta){scroll=Math.max(0,scroll+(delta>0?-1:1));return true;}
    @Override public boolean isPauseScreen(){return false;}
}
