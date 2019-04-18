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
public interface CategoryI {
    public String Name = "None";
    public ArrayList<Material> Items = new ArrayList<>();
    
    public ArrayList<Material> getItems();
    public String getName();
    
}
