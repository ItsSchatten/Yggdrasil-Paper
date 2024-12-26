package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.buttons.impl.*;

/**
 * Helper class to quickly get a {@link Button} builder.
 */
public final class Buttons {

    /**
     * An {@link AnimatedButton} builder.
     *
     * @return Returns a new {@link AnimatedButtonImpl} builder via {@link AnimatedButtonImpl#builder()}.
     */
    public static AnimatedButtonImpl.Builder animated() {
        return AnimatedButtonImpl.builder();
    }

    /**
     * An {@link AnimatedCommandButton} builder.
     *
     * @return Returns a new {@link AnimatedCommandButtonImpl} builder via {@link AnimatedCommandButtonImpl#builder()}.
     */
    public static AnimatedCommandButtonImpl.Builder animatedCommand() {
        return AnimatedCommandButtonImpl.builder();
    }

    /**
     * An {@link AnimatedMenuTriggerButton} builder.
     *
     * @return Returns a new {@link AnimatedMenuTriggerButtonImpl} builder via {@link AnimatedMenuTriggerButtonImpl#builder()}.
     */
    public static AnimatedMenuTriggerButtonImpl.Builder animatedMenuTrigger() {
        return AnimatedMenuTriggerButtonImpl.builder();
    }

    /**
     * An {@link AnimatedSimpleButton} builder.
     *
     * @return Returns a new {@link AnimatedSimpleButtonImpl} builder via {@link AnimatedSimpleButtonImpl#builder()}.
     */
    public static AnimatedSimpleButtonImpl.Builder animatedSimple() {
        return AnimatedSimpleButtonImpl.builder();
    }

    /**
     * A {@link Button} builder.
     *
     * @return Returns a new {@link ButtonImpl} builder via {@link ButtonImpl#builder()}.
     */
    public static ButtonImpl.Builder button() {
        return ButtonImpl.builder();
    }

    /**
     * A {@link CommandButton} builder.
     *
     * @return Returns a new {@link CommandButtonImpl} builder via {@link CommandButtonImpl#builder()}.
     */
    public static CommandButtonImpl.Builder command() {
        return CommandButtonImpl.builder();
    }

    /**
     * A {@link DynamicButton} builder.
     *
     * @return Returns a new {@link DynamicButtonImpl} builder via {@link DynamicButtonImpl#builder()}.
     */
    public static DynamicButtonImpl.Builder dynamic() {
        return DynamicButtonImpl.builder();
    }

    /**
     * A {@link SimpleButton} builder.
     *
     * @return Returns a new {@link SimpleButtonImpl} builder via {@link SimpleButtonImpl#builder()}.
     */
    public static SimpleButtonImpl.Builder simple() {
        return SimpleButtonImpl.builder();
    }

    /**
     * A {@link MenuTriggerButton} builder.
     *
     * @return Returns a new {@link MenuTriggerButtonImpl} builder via {@link MenuTriggerButtonImpl#builder()}.
     */
    public static MenuTriggerButtonImpl.Builder menuTrigger() {
        return MenuTriggerButtonImpl.builder();
    }

}
