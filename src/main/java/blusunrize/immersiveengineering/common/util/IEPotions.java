package blusunrize.immersiveengineering.common.util;

import blusunrize.immersiveengineering.api.IEApi;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class IEPotions
{
	public static Potion flammable;
	public static Potion slippery;
	public static Potion conductive;
	public static Potion sticky;
	public static Potion stunned;

	public static void init()
	{
		flammable = new IEPotion(new ResourceLocation("ie.flammable"), true,0x8f3f1f,0, false,0, true,true).setPotionName("immersiveengineering.potion.flammable");
		slippery = new IEPotion(new ResourceLocation("ie.slippery"), true,0x171003,0, false,1, true,true).setPotionName("immersiveengineering.potion.slippery");
		conductive = new IEPotion(new ResourceLocation("ie.conductive"), true,0x690000,0, false,2, true,true).setPotionName("immersiveengineering.potion.conductive");
		sticky = new IEPotion(new ResourceLocation("ie.sticky"), true,0x9c6800,0, false,3, true,true).setPotionName("immersiveengineering.potion.sticky").registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, Utils.generateNewUUID().toString(), -0.50000000298023224D, 2);
		stunned = new IEPotion(new ResourceLocation("ie.stunned"), true,0x624a98,0, false,4, true,true).setPotionName("immersiveengineering.potion.stunned");
		
		IEApi.potions = new Potion[]{flammable,slippery,conductive,sticky,stunned};
	}

	public static class IEPotion extends Potion
	{
		static ResourceLocation tex = new ResourceLocation("immersiveengineering","textures/gui/potioneffects.png");
		final int tickrate;
		final boolean halfTickRateWIthAmplifier;
		boolean showInInventory = true;
		boolean showInHud = true;
		public IEPotion(ResourceLocation resource, boolean isBad, int colour, int tick, boolean halveTick, int icon, boolean showInInventory, boolean showInHud)
		{
			super(isBad, colour);
			this.setPotionName("potion." + resource.getResourcePath());
			this.showInInventory = showInInventory;
			this.showInHud = showInHud;
			this.tickrate = tick;
			this.halfTickRateWIthAmplifier = halveTick;
			this.setIconIndex(icon%8, icon/8);

			REGISTRY.register(-1, resource, this);
		}

		@Override
		public boolean shouldRender(PotionEffect effect)
		{
			return showInInventory;
		}
		@Override
		public boolean shouldRenderInvText(PotionEffect effect)
		{
			return showInInventory;
		}
		@Override
		public boolean shouldRenderHUD(PotionEffect effect)
		{
			return showInHud;
		}

		@Override
		public int getStatusIconIndex()
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
			return super.getStatusIconIndex();
		}
		@Override
		public boolean isReady(int duration, int amplifier)
		{
			if(tickrate<0)
				return false;
			int k = tickrate >> amplifier;
			return k <= 0 || duration % k == 0;
		}
		@Override
		public void performEffect(EntityLivingBase living, int amplifier)
		{
			if(this==IEPotions.slippery)
			{
				if(living.onGround)
					living.moveRelative(0,1, 0.005F);
				EntityEquipmentSlot hand = living.getRNG().nextBoolean()?EntityEquipmentSlot.MAINHAND:EntityEquipmentSlot.OFFHAND;
				if(!living.worldObj.isRemote && living.getRNG().nextInt(300)==0 && living.getItemStackFromSlot(hand)!=null)
				{
					EntityItem dropped = living.entityDropItem(living.getItemStackFromSlot(hand).copy(), 1);
					dropped.setPickupDelay(20);
					living.setItemStackToSlot(hand, null);
				}
			}
		}
	}
}