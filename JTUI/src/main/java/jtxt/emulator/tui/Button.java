package jtxt.emulator.tui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.util.Glyphs;

public class Button extends Decorator implements Interactable,
  FocusableComponent {
  /**
   * The actions to perform whenever this button receives a notification that it has
   * been pressed by a client.
   */
  private List<Callable> callbacks;

  public Button(
    String text,
    Color textColor,
    Object parameters)
  {
    super(new Border(Border.Type.DASHED, Color.WHITE, new TextBox(
      parameters,
      Glyphs.escape(textColor) + text,
      TextBox.Position.CENTER)));
    callbacks = new ArrayList<>();
  }

  public void addCallback(Callable callback) {
    callbacks.add(callback);
  }

  @Override
  public void setBounds(Region bounds) {
    component.setBounds(bounds);
  }

  @Override
  public Region getBounds() {
    return component.getBounds();
  }

  @Override
  public boolean clicked(Location clickLocation) {
    for (Callable callback : callbacks) { callback.dispatch(); }

    component.fg = new Color(
      255,
      0,
      0);
    update();

    /*
     * We can't do anything with keyboard input, so yield control of the
     * input.
     */
    return false;
  }

  @Override
  public void onFocus() { /* ... */ }

  @Override
  public void focusChanged() { /* ... */ }
}
