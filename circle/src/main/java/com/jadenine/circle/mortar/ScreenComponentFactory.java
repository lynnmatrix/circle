package com.jadenine.circle.mortar;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public interface ScreenComponentFactory {

    Object createComponent(Object... dependencies);
}
