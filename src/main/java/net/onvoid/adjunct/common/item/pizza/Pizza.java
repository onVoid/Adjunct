package net.onvoid.adjunct.common.item.pizza;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.onvoid.adjunct.common.item.AdjunctItems;

public class Pizza {
    private ItemStack pizzaStack;
    private CompoundNBT nbt;

    public Pizza() {
        this.pizzaStack = new ItemStack(AdjunctItems.PIZZA_ITEM.get(), 1);
        this.nbt = this.pizzaStack.getOrCreateTag();
        this.nbt.putBoolean("cooked", false);
        this.update();
    }

    public Pizza(ItemStack pizzaOrCrustStack){
        if (pizzaOrCrustStack.getItem() instanceof PizzaItem){
            this.pizzaStack = pizzaOrCrustStack;
            this.nbt = this.pizzaStack.getOrCreateTag();
            this.nbt.putBoolean("cooked", false);
        } else if (Topping.is(pizzaOrCrustStack, Topping.CRUST)){
            this.pizzaStack = new ItemStack(AdjunctItems.PIZZA_ITEM.get(), 1);
            this.nbt = this.pizzaStack.getOrCreateTag();
            this.nbt.putBoolean("cooked", false);
            this.nbt.putInt(Topping.CRUST.get(), Topping.getSpecificInt(Topping.CRUST, pizzaOrCrustStack));
        }
        this.update();
    }

    public void update(){
        this.pizzaStack.setTag(this.nbt);
    }

    public Pizza build(){
        this.update();
        return this;
    }

    public ItemStack buildStack(){
        return this.build().getItemStack();
    }

    public ItemStack bakeStack(){
        this.cooked();
        return buildStack();
    }

    public ItemStack getItemStack(){
        return this.pizzaStack;
    }

    public int getNutrition(){
        int nutrition = 2;
        for (Topping type : Topping.values()){
            if (this.has(type)) {
                nutrition += 2;
            }
        }
        return this.isCooked() ? nutrition : nutrition / 2;
    }

    public float getSaturation(){
        float saturation = 0.2f;
        for (Topping type : Topping.values()){
            if (this.has(type)) {
                saturation += 0.05f;
            }
        }
        return this.isCooked() ? saturation : saturation / 2.0f;
    }

    public Pizza cooked(){
        nbt.putBoolean("cooked", true);
        this.update();
        return this;
    }

    public Pizza uncooked(){
        nbt.putBoolean("cooked", false);
        this.update();
        return this;
    }

    public boolean isCooked(){
        return nbt.getBoolean("cooked");
    }

    public Pizza add(Topping type, int topping){
        nbt.putInt(type.get(), topping);
        this.update();
        return this;
    }

    public Pizza add(Topping type, String topping){
        nbt.putInt(type.get(), Topping.fromStr(type, topping));
        this.update();
        return this;
    }

    public Pizza addStack(ItemStack topping){
        for (Topping type : Topping.values()){
            if (topping.getItem().is(Topping.getTag(type))){
                this.add(type, Topping.fromInt(type, Topping.getSpecificInt(type, topping)));
                break;
            }
        }
        return this;
    }

    public boolean has(Topping type){
        // Check if the Pizza has topping in 1 or 2 slot
        if (type.equals(Topping.TOPPING)){
            if (nbt.contains(Topping.TOPPING.get() + "1")){
                return nbt.getInt(Topping.TOPPING.get() + "1") > 0;
            } else if (nbt.contains(Topping.TOPPING.get() + "2")){
                return nbt.getInt(Topping.TOPPING.get() + "2") > 0;
            }
        }
        // Checks if the Pizza has topping type data at all
        if (!nbt.contains(type.get())){
            return false;
        }
        // Checks that the Pizza's specific topping
        return nbt.getInt(type.get()) > 0;
    }

    public boolean has(Topping type, String topping){
        if (type.equals(Topping.TOPPING)){
            if (nbt.contains(Topping.TOPPING.get() + "1")){
                return Topping.fromInt(type, nbt.getInt(Topping.TOPPING.get() + "1")).equals(topping);
            } else if (nbt.contains(Topping.TOPPING.get() + "2")){
                return Topping.fromInt(type, nbt.getInt(Topping.TOPPING.get() + "2")).equals(topping);
            }
            return false;
        }
        if (!nbt.contains(type.get())){
            return false;
        }
        return topping.equals(Topping.fromInt(type, nbt.getInt(type.get())));
    }

    public int get(Topping type){
        if (!this.has(type)){
            return -1;
        }
        return nbt.getInt(type.get());
    }

    public CompoundNBT getNbt() {
        return this.nbt;
    }
}
