package com.example.realisticcattle.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

/** Inheritable secondary phenotype loci. Values are game-scale indices, not veterinary measurements. */
public final class CattleTraits {
    private final int hornShapeA, hornShapeB, hornLengthA, hornLengthB;
    private final int spotSizeA, spotSizeB, spotPlacementA, spotPlacementB;
    private final int foreheadMarkA, foreheadMarkB, muzzleMarkA, muzzleMarkB, legMarkA, legMarkB;
    private final int bodySizeA, bodySizeB, chestA, chestB, legLengthA, legLengthB, musclingA, musclingB;
    private final int milkA, milkB;

    public CattleTraits(int hs1,int hs2,int hl1,int hl2,int ss1,int ss2,int sp1,int sp2,
                        int fm1,int fm2,int mm1,int mm2,int lm1,int lm2,int bs1,int bs2,
                        int ch1,int ch2,int ll1,int ll2,int mu1,int mu2,int my1,int my2) {
        hornShapeA=v(hs1,3); hornShapeB=v(hs2,3); hornLengthA=v(hl1,100); hornLengthB=v(hl2,100);
        spotSizeA=v(ss1,3); spotSizeB=v(ss2,3); spotPlacementA=v(sp1,3); spotPlacementB=v(sp2,3);
        foreheadMarkA=v(fm1,1); foreheadMarkB=v(fm2,1); muzzleMarkA=v(mm1,1); muzzleMarkB=v(mm2,1);
        legMarkA=v(lm1,1); legMarkB=v(lm2,1); bodySizeA=v(bs1,100); bodySizeB=v(bs2,100);
        chestA=v(ch1,100); chestB=v(ch2,100); legLengthA=v(ll1,100); legLengthB=v(ll2,100);
        musclingA=v(mu1,100); musclingB=v(mu2,100); milkA=v(my1,100); milkB=v(my2,100);
    }
    private static int v(int x,int max){return Math.max(0,Math.min(max,x));}
    private static int avg(int a,int b){return (a+b)/2;}
    private static int pick(int a,int b,RandomSource r){return r.nextBoolean()?a:b;}
    public static CattleTraits random(RandomSource r, CattleBreed breed) {
        int milk = breed==CattleBreed.HOLSTEIN?82:breed==CattleBreed.JERSEY?78:breed==CattleBreed.BROWN_SWISS?70:45;
        int muscle = breed==CattleBreed.BELGIAN_BLUE?90:breed==CattleBreed.LIMOUSIN?82:breed==CattleBreed.ABERDEEN_ANGUS?72:55;
        return new CattleTraits(r.nextInt(4),r.nextInt(4),r.nextInt(101),r.nextInt(101),r.nextInt(4),r.nextInt(4),r.nextInt(4),r.nextInt(4),
                r.nextInt(2),r.nextInt(2),r.nextInt(2),r.nextInt(2),r.nextInt(2),r.nextInt(2),range(r,45,85),range(r,40,85),range(r,35,85),range(r,35,85),range(r,35,75),range(r,35,75),range(r,muscle-15,muscle+15),range(r,muscle-15,muscle+15),range(r,milk-15,milk+15),range(r,milk-15,milk+15));
    }
    private static int range(RandomSource r,int min,int max){return min+r.nextInt(max-min+1);}
    public static CattleTraits inherit(CattleTraits m,CattleTraits f,RandomSource r){
        return new CattleTraits(pick(m.hornShapeA,m.hornShapeB,r),pick(f.hornShapeA,f.hornShapeB,r),pick(m.hornLengthA,m.hornLengthB,r),pick(f.hornLengthA,f.hornLengthB,r),
                pick(m.spotSizeA,m.spotSizeB,r),pick(f.spotSizeA,f.spotSizeB,r),pick(m.spotPlacementA,m.spotPlacementB,r),pick(f.spotPlacementA,f.spotPlacementB,r),
                pick(m.foreheadMarkA,m.foreheadMarkB,r),pick(f.foreheadMarkA,f.foreheadMarkB,r),pick(m.muzzleMarkA,m.muzzleMarkB,r),pick(f.muzzleMarkA,f.muzzleMarkB,r),pick(m.legMarkA,m.legMarkB,r),pick(f.legMarkA,f.legMarkB,r),
                pick(m.bodySizeA,m.bodySizeB,r),pick(f.bodySizeA,f.bodySizeB,r),pick(m.chestA,m.chestB,r),pick(f.chestA,f.chestB,r),pick(m.legLengthA,m.legLengthB,r),pick(f.legLengthA,f.legLengthB,r),pick(m.musclingA,m.musclingB,r),pick(f.musclingA,f.musclingB,r),pick(m.milkA,m.milkB,r),pick(f.milkA,f.milkB,r));
    }
    public int hornShape(){return avg(hornShapeA,hornShapeB);} public int hornLength(){return avg(hornLengthA,hornLengthB);}
    public int spotSize(){return avg(spotSizeA,spotSizeB);} public int spotPlacement(){return avg(spotPlacementA,spotPlacementB);}
    public boolean foreheadMark(){return foreheadMarkA+foreheadMarkB>=1;} public boolean muzzleMark(){return muzzleMarkA+muzzleMarkB>=1;} public boolean legMarks(){return legMarkA+legMarkB>=1;}
    public int bodySize(){return avg(bodySizeA,bodySizeB);} public int chestWidth(){return avg(chestA,chestB);} public int legLength(){return avg(legLengthA,legLengthB);}
    public int muscling(){return avg(musclingA,musclingB);} public int milkPotential(){return avg(milkA,milkB);}
    public String describe(){return "Рога: "+shapeName(hornShape())+", длина "+hornLength()+"/100; пятна: "+spotName(spotSize())+", зона "+placementName(spotPlacement())+"; белые отметины: лоб "+yes(foreheadMark())+", морда "+yes(muzzleMark())+", ноги "+yes(legMarks())+"; тело: размер "+bodySize()+"/100, ширина груди "+chestWidth()+"/100, длина ног "+legLength()+"/100, мускулатура "+muscling()+"/100; генетический потенциал молочности "+milkPotential()+"/100";}
    private static String shapeName(int i){return new String[]{"прямые","полумесяц","наружу","спиральные"}[i];}
    private static String spotName(int i){return new String[]{"мелкие","средние","крупные","очень крупные"}[i];}
    private static String placementName(int i){return new String[]{"плечи и бока","круп и бока","равномерно","преимущественно нижняя часть"}[i];}
    private static String yes(boolean b){return b?"есть":"нет";}
    public CompoundTag save(){CompoundTag t=new CompoundTag();String[] n={"HornShapeA","HornShapeB","HornLengthA","HornLengthB","SpotSizeA","SpotSizeB","SpotPlacementA","SpotPlacementB","ForeheadA","ForeheadB","MuzzleA","MuzzleB","LegMarkA","LegMarkB","BodyA","BodyB","ChestA","ChestB","LegLengthA","LegLengthB","MuscleA","MuscleB","MilkA","MilkB"};int[] x={hornShapeA,hornShapeB,hornLengthA,hornLengthB,spotSizeA,spotSizeB,spotPlacementA,spotPlacementB,foreheadMarkA,foreheadMarkB,muzzleMarkA,muzzleMarkB,legMarkA,legMarkB,bodySizeA,bodySizeB,chestA,chestB,legLengthA,legLengthB,musclingA,musclingB,milkA,milkB};for(int i=0;i<n.length;i++)t.putInt(n[i],x[i]);return t;}
    public static CattleTraits load(CompoundTag t){String[] n={"HornShapeA","HornShapeB","HornLengthA","HornLengthB","SpotSizeA","SpotSizeB","SpotPlacementA","SpotPlacementB","ForeheadA","ForeheadB","MuzzleA","MuzzleB","LegMarkA","LegMarkB","BodyA","BodyB","ChestA","ChestB","LegLengthA","LegLengthB","MuscleA","MuscleB","MilkA","MilkB"};int[] x=new int[n.length];for(int i=0;i<n.length;i++)x[i]=t.getInt(n[i]);return new CattleTraits(x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7],x[8],x[9],x[10],x[11],x[12],x[13],x[14],x[15],x[16],x[17],x[18],x[19],x[20],x[21],x[22],x[23]);}
}
