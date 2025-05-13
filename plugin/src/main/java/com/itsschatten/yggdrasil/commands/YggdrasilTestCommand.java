package com.itsschatten.yggdrasil.commands;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.UtilityItems;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.Buttons;
import com.itsschatten.yggdrasil.menus.types.PaginatedMenu;
import com.itsschatten.yggdrasil.menus.types.StandardMenu;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public final class YggdrasilTestCommand extends BrigadierCommand {

    final List<String> lists = new ArrayList<>(List.of("Hi", "Hello", "Bye", "Greetings", "Farewell", "Another String", "more", "another"));

    public YggdrasilTestCommand() {
        super("The main test command for Yggdrasil.", List.of("ytest"));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command() {
        return literal("ygg-test")
                .then(literal("menus")
                        .executes(context -> {
                            new TestMenu2().displayTo(MenuHolder.wrap(context.getSource()));
                            return SUCCESS;
                        })
                );
    }


    class TestMenu2 extends StandardMenu<MenuHolder> {

        public TestMenu2() {
            super(null, "Menu", 36);
        }

        @Override
        public List<Button<MenuHolder>> makeButtons() {
            return List.of(Buttons.menuTrigger().menu((menuHolder, clickType) -> new TestMenu(this))
                            .item(ItemCreator.of(Material.BARRIER).name("Click to 2!").supplier())
                            .position(0, 0)
                            .build()
            );
        }
    }

    class TestMenu extends PaginatedMenu<MenuHolder, String> {

        public TestMenu(Menu<MenuHolder> menu) {
            super(menu, "Test Menu", 36, new ArrayList<>(lists));
            setHideNav(true);
        }

        @Override
        public @Nullable @Unmodifiable List<InventoryPosition> getPlaceablePositions() {
            return InventoryPosition.ofRow(2);
        }

        @Override
        public ItemCreator convertToStack(String object) {
            return ItemCreator.of(Material.MAP).name(object).build();
        }

        @Override
        public void onClickPageItem(MenuHolder user, String object, @NotNull ClickType click) {
            if (click.isRightClick()) {
                removeValue(object);
            }
        }

        @Override
        public void drawExtra() {
            fill(UtilityItems.makeFiller(Material.BLACK_STAINED_GLASS_PANE));
        }

        @Override
        public List<Button<MenuHolder>> makeButtons() {
            return List.of(
                    Buttons.button().item(ItemCreator.of(Material.BIRCH_LOG).name("POOP").lore("This is smthn").supplier())
                            .position(0, 3)
                            .onClick((holder, menu, click) -> {
                                refresh();
                            })
                            .build(),
                    Buttons.button().item(ItemCreator.of(Material.PAPER).name("Add a string").supplier())
                            .position(0, 4)
                            .onClick((holder, menu, click) -> {
                                if (click.isRightClick()) {
                                    cleanUpdatePages(lists);
                                    return;
                                }
                                addValue("A string to add.");
                            })
                            .build()
            );
        }
    }

}
