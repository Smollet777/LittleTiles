package com.creativemd.littletiles.common.utils.compression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.creativemd.creativecore.common.utils.type.HashMapList;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.tiles.LittleTile;
import com.creativemd.littletiles.common.tiles.preview.LittlePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.utils.grid.LittleGridContext;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LittleNBTCompressionTools {
	
	public static NBTTagList writeTiles(List<LittleTile> tiles) {
		HashMapList<Class<? extends LittleTile>, LittleTile> groups = new HashMapList<>();
		
		for (LittleTile tile : tiles)
			groups.add(tile.getClass(), tile);
		
		NBTTagList list = new NBTTagList();
		
		for (Iterator<ArrayList<LittleTile>> iterator = groups.values().iterator(); iterator.hasNext();) {
			ArrayList<LittleTile> classList = iterator.next();
			
			while (classList.size() > 0) {
				LittleTile grouping = classList.remove(0);
				NBTTagCompound groupNBT = null;
				
				for (Iterator iterator2 = classList.iterator(); iterator2.hasNext();) {
					LittleTile littleTile = (LittleTile) iterator2.next();
					if (grouping.canBeNBTGrouped(littleTile)) {
						if (groupNBT == null)
							groupNBT = grouping.startNBTGrouping();
						grouping.groupNBTTile(groupNBT, littleTile);
						iterator2.remove();
					}
				}
				
				if (groupNBT == null) {
					NBTTagCompound nbt = new NBTTagCompound();
					grouping.saveTile(nbt);
					list.appendTag(nbt);
				} else {
					groupNBT.setBoolean("group", true);
					list.appendTag(groupNBT);
				}
			}
			
		}
		
		return list;
	}
	
	public static List<LittleTile> readTiles(NBTTagList list, TileEntityLittleTiles te) {
		ArrayList<LittleTile> tiles = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			if (nbt.getBoolean("group")) {
				LittleTile create = LittleTile.CreateEmptyTile(nbt.getString("tID"));
				if (create != null) {
					List<NBTTagCompound> nbts = create.extractNBTFromGroup(nbt);
					for (int j = 0; j < nbts.size(); j++) {
						LittleTile tile = LittleTile.CreateandLoadTile(te, te.getWorld(), nbts.get(j));
						if (tile != null)
							tiles.add(tile);
					}
				}
			} else {
				LittleTile tile = LittleTile.CreateandLoadTile(te, te.getWorld(), nbt);
				if (tile != null)
					tiles.add(tile);
			}
		}
		return tiles;
	}
	
	public static NBTTagList writePreviews(LittlePreviews previews) {
		HashMapList<String, LittleTilePreview> groups = new HashMapList<>();
		
		for (Iterator iterator = previews.iterator(); iterator.hasNext();) {
			LittleTilePreview preview = (LittleTilePreview) iterator.next();
			groups.add(preview.getTypeID(), preview);
		}
		
		NBTTagList list = new NBTTagList();
		
		for (Iterator<ArrayList<LittleTilePreview>> iterator = groups.values().iterator(); iterator.hasNext();) {
			ArrayList<LittleTilePreview> classList = iterator.next();
			
			while (classList.size() > 0) {
				LittleTilePreview grouping = classList.remove(0);
				NBTTagCompound groupNBT = null;
				
				for (Iterator iterator2 = classList.iterator(); iterator2.hasNext();) {
					LittleTilePreview preview = (LittleTilePreview) iterator2.next();
					if (grouping.canBeNBTGrouped(preview)) {
						if (groupNBT == null)
							groupNBT = grouping.startNBTGrouping();
						grouping.groupNBTTile(groupNBT, preview);
						iterator2.remove();
					}
				}
				
				if (groupNBT == null) {
					NBTTagCompound nbt = new NBTTagCompound();
					grouping.writeToNBT(nbt);
					list.appendTag(nbt);
				} else {
					groupNBT.setBoolean("group", true);
					list.appendTag(groupNBT);
				}
			}
		}
		
		return list;
	}
	
	public abstract static class PreviewCompressionHandler {
		
		public abstract List<NBTTagCompound> extractNBTFromGroup(NBTTagCompound nbt);
		
	}
	
	public static void registerPreviewCompressionHandler(String id, PreviewCompressionHandler handler) {
		if (id.equals(""))
			defaultHandler = handler;
		handlers.put(id, handler);
	}
	
	private static PreviewCompressionHandler defaultHandler = null;
	private static HashMap<String, PreviewCompressionHandler> handlers = new HashMap<>();
	
	public static PreviewCompressionHandler ordinaryPreviewHandler = new PreviewCompressionHandler() {
		
		@Override
		public List<NBTTagCompound> extractNBTFromGroup(NBTTagCompound nbt) {
			List<NBTTagCompound> tags = new ArrayList<>();
			NBTTagList list = nbt.getTagList("boxes", 11);
			NBTTagCompound copy = new NBTTagCompound();
			for (Iterator<String> iterator = nbt.getKeySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				if (!key.equals("boxes") && !key.equals("group"))
					copy.setTag(key, nbt.getTag(key).copy());
			}
			
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound second = copy.copy();
				second.setIntArray("bBox", list.getIntArrayAt(i));
				tags.add(second);
			}
			return tags;
		}
	};
	
	static {
		registerPreviewCompressionHandler("", ordinaryPreviewHandler);
	}
	
	public static LittlePreviews readPreviews(LittlePreviews previews, NBTTagList list) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			if (nbt.getBoolean("group")) {
				PreviewCompressionHandler handler = handlers.get(nbt.getString("tID"));
				if (handler == null)
					handler = defaultHandler;
				
				List<NBTTagCompound> nbts = handler.extractNBTFromGroup(nbt);
				for (int j = 0; j < nbts.size(); j++) {
					LittleTilePreview preview = LittleTilePreview.loadPreviewFromNBT(nbts.get(j));
					if (preview != null)
						previews.addWithoutCheckingPreview(preview);
				}
			} else {
				LittleTilePreview preview = LittleTilePreview.loadPreviewFromNBT(nbt);
				if (preview != null)
					previews.addWithoutCheckingPreview(preview);
			}
		}
		return previews;
	}
	
	public static LittlePreviews readPreviews(LittleGridContext context, NBTTagList list) {
		return readPreviews(new LittlePreviews(context), list);
	}
	
}
