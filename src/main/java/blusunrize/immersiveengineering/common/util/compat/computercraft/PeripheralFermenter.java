package blusunrize.immersiveengineering.common.util.compat.computercraft;

import blusunrize.immersiveengineering.api.energy.DieselHandler;
import blusunrize.immersiveengineering.api.energy.DieselHandler.FermenterRecipe;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityFermenter;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PeripheralFermenter extends IEPeripheral
{

	public PeripheralFermenter(World w, int _x, int _y, int _z)
	{
		super(w, _x, _y, _z);
	}

	@Override
	public String getType()
	{
		return "IE:fermenter";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[]{"getRecipe", "getInpuStack", "getOutputStack", "getFluid", "getEmptyCannisters", "getFilledCannisters", "getEnergyStored", "getMaxEnergyStored", "getProgress"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException
	{
		TileEntityFermenter te = (TileEntityFermenter) getTileEntity(TileEntityFermenter.class);
		if (te==null)
			throw new LuaException("The fermenter was removed");
		switch (method)
		{
		case 0://recipe
			if (arguments.length!=1||!(arguments[0] instanceof Double))
				throw new LuaException("Wrong amount of arguments, needs one integer");
			int slot = (int) (double)arguments[0];
			if (slot<0||slot>8)
				throw new LuaException("Input slots are numbers 0-8");
			FermenterRecipe recipe = DieselHandler.findFermenterRecipe(te.getStackInSlot(slot));
			if (recipe!=null)
				return new Object[]{saveStack(te.getStackInSlot(slot)), saveStack(recipe.output), saveFluidStack(recipe.fluid), recipe.time};
			else
				return new Object[]{"No recipe found"};
		case 1://Input stack
			if (arguments.length!=1||!(arguments[0] instanceof Double))
				throw new LuaException("Wrong amount of arguments, needs one integer");
			slot = (int) (double)arguments[0];
			if (slot<0||slot>8)
				throw new LuaException("Input slots are numbers 0-8");
			return new Object[]{saveStack(te.getStackInSlot(slot))};
		case 2://output item stack
			return new Object[]{saveStack(te.getStackInSlot(11))};
		case 3://fluid tank
			return new Object[]{saveFluidTank(te.tank)};
		case 4://empty cannisters
			return new Object[]{saveStack(te.getStackInSlot(9))};
		case 5://full cannisters
			return new Object[]{saveStack(te.getStackInSlot(10))};
		case 6://energy stored
			return new Object[]{te.energyStorage.getEnergyStored()};
		case 7://max energy stored
			return new Object[]{te.energyStorage.getMaxEnergyStored()};
		case 8://progress
			return new Object[]{te.tick};
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer)
	{}

	@Override
	public void detach(IComputerAccess computer)
	{}
	@Override
	protected TileEntity getTileEntity(Class<? extends TileEntity> type)
	{

		TileEntityFermenter te = (TileEntityFermenter) super.getTileEntity(type);
		return te;
	}
}
