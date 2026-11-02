package com.example.realisticcattle.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persistent ancestry snapshot with a bounded, serializable family tree. */
public final class CattleLineage {
    private final String sireName, damName, sireBreed, damBreed, sireGenes, damGenes;
    private final Map<String, Double> ancestry;
    private final int generations;
    private final CattleLineage sireLine, damLine;

    public CattleLineage(String sireName, String damName, String sireBreed, String damBreed, String sireGenes, String damGenes,
                         Map<String, Double> ancestry, int generations) {
        this(sireName, damName, sireBreed, damBreed, sireGenes, damGenes, ancestry, generations, null, null);
    }
    private CattleLineage(String sireName, String damName, String sireBreed, String damBreed, String sireGenes, String damGenes,
                          Map<String, Double> ancestry, int generations, CattleLineage sireLine, CattleLineage damLine) {
        this.sireName=clean(sireName); this.damName=clean(damName); this.sireBreed=clean(sireBreed); this.damBreed=clean(damBreed);
        this.sireGenes=clean(sireGenes); this.damGenes=clean(damGenes); this.ancestry=new LinkedHashMap<>(ancestry);
        this.generations=Math.max(0,generations); this.sireLine=sireLine; this.damLine=damLine;
    }
    private static String clean(String s) { return s == null || s.isBlank() ? "Неизвестно" : s; }
    public static CattleLineage founder(CattleBreed breed) {
        Map<String,Double> map=new LinkedHashMap<>(); map.put(breed.name(),100.0);
        return new CattleLineage("Неизвестно","Неизвестно","Неизвестно","Неизвестно","—","—",map,0);
    }
    public static CattleLineage child(String sireName,CattleBreed sireBreed,CattleGenome sireGenome,CattleLineage sireLine,
                                     String damName,CattleBreed damBreed,CattleGenome damGenome,CattleLineage damLine) {
        Map<String,Double> map=new LinkedHashMap<>(); addHalf(map,sireLine); addHalf(map,damLine);
        return new CattleLineage(sireName,damName,sireBreed.name(),damBreed.name(),sireGenome.describe(),damGenome.describe(),map,
                1+Math.max(sireLine.generations,damLine.generations),sireLine,damLine);
    }
    private static void addHalf(Map<String,Double> out,CattleLineage line) {
        double total=line.ancestry.values().stream().mapToDouble(Double::doubleValue).sum(); if(total<=0)return;
        line.ancestry.forEach((k,v)->out.merge(k,v*.5/total,Double::sum));
    }
    public Map<String,Double> ancestry(){return Map.copyOf(ancestry);} public int generations(){return generations;}
    public String sireName(){return sireName;} public String damName(){return damName;}
    public String sireBreed(){return sireBreed;} public String damBreed(){return damBreed;}
    public String sireGenes(){return sireGenes;} public String damGenes(){return damGenes;}
    public double percentage(String breed){return ancestry.getOrDefault(breed,0.0);}
    public String ancestryText(){return ancestry.entrySet().stream().sorted((a,b)->Double.compare(b.getValue(),a.getValue()))
            .map(e->e.getKey()+" "+Math.round(e.getValue()*10.0)/10.0+"%").reduce((a,b)->a+"; "+b).orElse("Нет данных");}
    public String familyTreeText(int depth) {
        StringBuilder b=new StringBuilder(); appendTree(b,"",depth,0); return b.toString();
    }
    private void appendTree(StringBuilder b,String prefix,int depth,int level) {
        if(level>=depth)return;
        if(sireLine!=null){ b.append(prefix).append("├─ Отец: ").append(sireName).append(" [").append(sireBreed).append("] — ").append(sireGenes).append('\n'); sireLine.appendTree(b,prefix+"│  ",depth,level+1); }
        else b.append(prefix).append("├─ Отец: ").append(sireName).append('\n');
        if(damLine!=null){ b.append(prefix).append("└─ Мать: ").append(damName).append(" [").append(damBreed).append("] — ").append(damGenes).append('\n'); damLine.appendTree(b,prefix+"   ",depth,level+1); }
        else b.append(prefix).append("└─ Мать: ").append(damName).append('\n');
    }
    public CompoundTag save(){return save(0);}
    private CompoundTag save(int depth){
        CompoundTag t=new CompoundTag(); t.putString("SireName",sireName);t.putString("DamName",damName);t.putString("SireBreed",sireBreed);t.putString("DamBreed",damBreed);
        t.putString("SireGenes",sireGenes);t.putString("DamGenes",damGenes);t.putInt("Generations",generations);
        ListTag list=new ListTag();ancestry.forEach((k,v)->{CompoundTag e=new CompoundTag();e.putString("Breed",k);e.putDouble("Value",v);list.add(e);});t.put("Ancestry",list);
        if(depth<4 && sireLine!=null)t.put("SireLine",sireLine.save(depth+1)); if(depth<4 && damLine!=null)t.put("DamLine",damLine.save(depth+1)); return t;
    }
    public static CattleLineage load(CompoundTag t,CattleBreed fallback){return load(t,fallback,0);}
    private static CattleLineage load(CompoundTag t,CattleBreed fallback,int depth){
        Map<String,Double> map=new LinkedHashMap<>();ListTag list=t.getList("Ancestry",10);
        for(int i=0;i<list.size();i++){CompoundTag e=list.getCompound(i);map.put(e.getString("Breed"),Math.max(0,e.getDouble("Value")));}
        if(map.isEmpty())map.put(fallback.name(),100.0);
        CattleLineage sire=null,dam=null;
        if(depth<4 && t.contains("SireLine",10))sire=load(t.getCompound("SireLine"),fallback,depth+1);
        if(depth<4 && t.contains("DamLine",10))dam=load(t.getCompound("DamLine"),fallback,depth+1);
        return new CattleLineage(t.getString("SireName"),t.getString("DamName"),t.getString("SireBreed"),t.getString("DamBreed"),
                t.getString("SireGenes"),t.getString("DamGenes"),map,t.getInt("Generations"),sire,dam);
    }
}
