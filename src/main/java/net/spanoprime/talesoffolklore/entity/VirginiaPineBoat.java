package net.spanoprime.talesoffolklore.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public class VirginiaPineBoat extends Boat
{
    public VirginiaPineBoat(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public Type getVariant() {
        return Type.OAK; // You can customize this or use a custom one.
    }
}
