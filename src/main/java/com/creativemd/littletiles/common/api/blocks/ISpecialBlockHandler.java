package com.creativemd.littletiles.common.api.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.littletiles.common.tiles.LittleTileBlock;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISpecialBlockHandler {
	
	public default boolean canBeConvertedToVanilla(LittleTileBlock tile) {
		return true;
	}
	
	public default List<LittleTileBox> getCollisionBoxes(LittleTileBlock tile, List<LittleTileBox> defaultBoxes) {
		return defaultBoxes;
	}
	
	public default boolean onBlockActivated(LittleTileBlock tile, World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	public default void onTileExplodes(LittleTileBlock tile, Explosion explosion) {
		
	}
	
	public default void randomDisplayTick(LittleTileBlock tile, IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
	}
	
	public default boolean isMaterial(LittleTileBlock tile, Material material) {
		return tile.getBlockState().getMaterial() == material;
	}
	
	public default boolean isLiquid(LittleTileBlock tile) {
		return tile.getBlockState().getMaterial().isLiquid();
	}
	
	public default Vec3d modifyAcceleration(LittleTileBlock tile, Entity entityIn, Vec3d motion) {
		return null;
	}
	
	public default LittleTilePreview getPreview(LittleTileBlock tile) {
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public default boolean canBeRenderCombined(LittleTileBlock thisTile, LittleTileBlock tile) {
		return false;
	}
	
	public default boolean shouldCheckForCollision(LittleTileBlock tile) {
		return false;
	}
	
	public default void onEntityCollidedWithBlock(World worldIn, LittleTileBlock tile, BlockPos pos, IBlockState state, Entity entityIn) {
		
	}
	
	public default void flipPreview(Axis axis, LittleTilePreview preview, LittleTileVec doubledCenter) {
		
	}
	
	public default void rotatePreview(Rotation rotation, LittleTilePreview preview, LittleTileVec doubledCenter) {
		
	}
	
}
