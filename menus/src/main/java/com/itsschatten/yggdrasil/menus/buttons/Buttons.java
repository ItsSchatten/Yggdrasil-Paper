package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.buttons.impl.*;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import lombok.experimental.UtilityClass;

/**
 * Helper class to quickly get a {@link Button} builder.
 */
@UtilityClass
public final class Buttons {

    /**
     * An {@link AnimatedButton} builder.
     *
     * @return Returns a new {@link AnimatedButtonImpl} builder via {@link AnimatedButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> AnimatedButtonImpl.Builder<T> animated() {
        return AnimatedButtonImpl.builder();
    }

    /**
     * An {@link AnimatedCommandButton} builder.
     *
     * @return Returns a new {@link AnimatedCommandButtonImpl} builder via {@link AnimatedCommandButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> AnimatedCommandButtonImpl.Builder<T> animatedCommand() {
        return AnimatedCommandButtonImpl.builder();
    }

    /**
     * An {@link AnimatedMenuTriggerButton} builder.
     *
     * @return Returns a new {@link AnimatedMenuTriggerButtonImpl} builder via {@link AnimatedMenuTriggerButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> AnimatedMenuTriggerButtonImpl.Builder<T> animatedMenuTrigger() {
        return AnimatedMenuTriggerButtonImpl.builder();
    }

    /**
     * An {@link AnimatedSimpleButton} builder.
     *
     * @return Returns a new {@link AnimatedSimpleButtonImpl} builder via {@link AnimatedSimpleButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> AnimatedSimpleButtonImpl.Builder<T> animatedSimple() {
        return AnimatedSimpleButtonImpl.builder();
    }

    /**
     * A {@link Button} builder.
     *
     * @return Returns a new {@link ButtonImpl} builder via {@link ButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> ButtonImpl.Builder<T> button() {
        return ButtonImpl.builder();
    }

    /**
     * A {@link CommandButton} builder.
     *
     * @return Returns a new {@link CommandButtonImpl} builder via {@link CommandButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> CommandButtonImpl.Builder<T> command() {
        return CommandButtonImpl.builder();
    }

    /**
     * A {@link DynamicButton} builder.
     *
     * @return Returns a new {@link DynamicButtonImpl} builder via {@link DynamicButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> DynamicButtonImpl.Builder<T> dynamic() {
        return DynamicButtonImpl.builder();
    }

    /**
     * A {@link SimpleButton} builder.
     *
     * @return Returns a new {@link SimpleButtonImpl} builder via {@link SimpleButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> SimpleButtonImpl.Builder<T> simple() {
        return SimpleButtonImpl.builder();
    }

    /**
     * A {@link MenuTriggerButton} builder.
     *
     * @return Returns a new {@link MenuTriggerButtonImpl} builder via {@link MenuTriggerButtonImpl#builder()}.
     */
    public static <T extends MenuHolder> MenuTriggerButtonImpl.Builder<T> menuTrigger() {
        return MenuTriggerButtonImpl.builder();
    }

}
