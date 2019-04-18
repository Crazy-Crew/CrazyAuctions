/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.badbones69.crazyauctions.api;

import org.bukkit.Material;

import java.util.ArrayList;

/**
 *
 * @author black
 */
public class Search implements CategoryI{
    private String Name;
    private ArrayList<Material> Items;
    
    public Search(String search){
        this.Name = search;
        this.Items = new ArrayList<>();
        for(Material ma : Material.values()){                    
            if(ma.toString().contains(search.toUpperCase())){
                Items.add(ma);
            }
                
        }
    }
    
    @Override
    public String getName() {
        return this.Name;
    }
    
    @Override
    public ArrayList<Material> getItems() {
        return this.Items;
    }
}
